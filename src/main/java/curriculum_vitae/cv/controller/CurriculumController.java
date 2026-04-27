
package curriculum_vitae.cv.controller;

import curriculum_vitae.cv.model.Curriculum;
import curriculum_vitae.cv.model.Contacts;
import curriculum_vitae.cv.model.Education;
import curriculum_vitae.cv.model.Experience;
import curriculum_vitae.cv.model.Job;
import curriculum_vitae.cv.model.JobTask;
import curriculum_vitae.cv.model.Language;
import curriculum_vitae.cv.model.Projects;
import curriculum_vitae.cv.model.Skill;
import curriculum_vitae.cv.repository.CurriculumRepository;
import curriculum_vitae.cv.model.User;
import curriculum_vitae.cv.repository.UserRepository;
import curriculum_vitae.cv.service.FileStorage;
import curriculum_vitae.cv.service.TranslationService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

@Controller
public class CurriculumController {

    @Autowired
    private CurriculumRepository curriculumRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TranslationService translationService;

    @Autowired
    private FileStorage fileStorage;

    // 1. Homepage: mostra tutti i curriculum
    @GetMapping("/")
    public String getIndex(@RequestParam(name = "name", required = false) String name, Model model) {
        List<Curriculum> curriculums;
        if (name != null && !name.trim().isBlank()) {
            curriculums = curriculumRepository.findByUserNameContainingIgnoreCase(name.trim());
        } else {
            curriculums = curriculumRepository.findAll();
        }
        model.addAttribute("curriculums", curriculums);
        return "index";
    }

    @GetMapping("/curriculums/edit")
    public String getMyCurriculumEdit(@AuthenticationPrincipal UserDetails userDetails) {
        String authenticatedEmail = getAuthenticatedEmail(userDetails);
        if (authenticatedEmail == null) {
            return "redirect:/users/login?error=not_authenticated";
        }

        Optional<User> user = userRepository.findByEmail(authenticatedEmail);
        if (user.isEmpty()) {
            return "redirect:/users/create?error=not_registered";
        }

        Curriculum curriculum = curriculumRepository.findByUser(user.get());
        if (curriculum == null) {
            return "redirect:/curriculums/create?from=edit_no_cv";
        }

        return "redirect:/curriculums/edit/" + curriculum.getId();
    }

    // 2. Form creazione curriculum (solo autenticati)
    @GetMapping("/curriculums/create")
    public String getCreate(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        String authenticatedEmail = getAuthenticatedEmail(userDetails);
        if (authenticatedEmail == null) {
            return "redirect:/users/login?error=not_authenticated";
        }
        Optional<User> user = userRepository.findByEmail(authenticatedEmail);
        if (user.isEmpty()) {
            return "redirect:/users/create?error=not_registered";
        }
        Curriculum cv_existing = curriculumRepository.findByUser(user.get());
        if (cv_existing != null) {
            return "redirect:/?error=curriculum_exists";
        }
        Curriculum curriculum = new Curriculum();
        curriculum.setUser(user.get());
        curriculum.setContacts(new Contacts());
        model.addAttribute("curriculum", curriculum);
        return "curriculums/create";
    }

    // 3. Submit creazione curriculum (solo autenticati)
    @PostMapping("/curriculums/create")
    public String postCreate(
            @Valid @ModelAttribute("curriculum") Curriculum curriculum,
            BindingResult bindingResult,
            @RequestParam(value = "profiles_file", required = false) MultipartFile profileFile,
            @RequestParam(value = "certificates_files", required = false) MultipartFile[] certificateFiles,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (bindingResult.hasErrors()) {
            return "curriculums/create";
        }
        String authenticatedEmail = getAuthenticatedEmail(userDetails);
        if (authenticatedEmail == null) {
            return "redirect:/users/login?error=not_authenticated";
        }
        // Associa curriculum all'utente loggato
        Optional<User> user = userRepository.findByEmail(authenticatedEmail);
        if (user.isPresent()) {
            curriculum.setUser(user.get());
        } else {
            return "redirect:/users/create?error=not_registered";
        }
        if (curriculum.getContacts() == null) {
            curriculum.setContacts(new Contacts());
        }

        if (profileFile != null && !profileFile.isEmpty()) {
            String uploadedUrl = fileStorage.storeFile(profileFile, "profiles");
            if (uploadedUrl == null) {
                bindingResult.reject("curriculum.image.upload.failed", "Errore nel caricamento della foto profilo.");
                return "curriculums/create";
            }
            curriculum.setImage_profile_Url(uploadedUrl);
        }

        if (certificateFiles != null && curriculum.getEducations() != null) {
            int count = Math.min(certificateFiles.length, curriculum.getEducations().size());
            for (int i = 0; i < count; i++) {
                MultipartFile certificateFile = certificateFiles[i];
                if (certificateFile == null || certificateFile.isEmpty()) {
                    continue;
                }
                String uploadedUrl = fileStorage.storeFile(certificateFile, "certificates");
                if (uploadedUrl == null) {
                    bindingResult.reject("curriculum.certificate.upload.failed", "Errore nel caricamento del certificato.");
                    return "curriculums/create";
                }
                curriculum.getEducations().get(i).setImage_certificate_Url(uploadedUrl);
            }
        }

        prepareCurriculumRelationships(curriculum);
        try {
            curriculumRepository.save(curriculum);
        } catch (DataIntegrityViolationException ex) {
            String detail = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();
            bindingResult.reject("curriculum.save.failed", "Unable to save curriculum. Possible too-long field (often projects.description/url). DB: " + detail);
            return "curriculums/create";
        }
        return "redirect:/";
    }

    // 4. Show curriculum (tutti, ma pulsanti extra solo per proprietario)
    @GetMapping("/curriculums/show/{id}")
    public String getShow(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Curriculum> curriculumOpt = curriculumRepository.findById(id);
        if (curriculumOpt.isEmpty()) {
            return "redirect:/?error=cv_not_found";
        }
        Curriculum curriculum = curriculumOpt.get();
        boolean isOwner = false;
        String authenticatedEmail = getAuthenticatedEmail(userDetails);
        if (authenticatedEmail != null) {
            Optional<User> user = userRepository.findByEmail(authenticatedEmail);
            if (user.isPresent() && curriculum.getUser().getId().equals(user.get().getId())) {
                isOwner = true;
            }
        }
        model.addAttribute("curriculum", curriculum);
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("currentLanguage", curriculum.getPreferredLanguage());
        Map<String, String> tradotto = new HashMap<>();
        boolean translationError = false;
        if (curriculum.getPreferredLanguage() != null
            && !curriculum.getPreferredLanguage().isBlank()
            && !"it".equalsIgnoreCase(curriculum.getPreferredLanguage())) {
            tradotto = translationService.translateCurriculum(curriculum, curriculum.getPreferredLanguage());
            translationError = Boolean.parseBoolean(tradotto.getOrDefault("_translation_error", "false"));
        }
        model.addAttribute("tradotto", tradotto);
        model.addAttribute("translationError", translationError);
        return "curriculums/show";
    }

    // 5. Form modifica curriculum (solo proprietario)
    @GetMapping("/curriculums/edit/{id}")
    public String getEdit(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        String authenticatedEmail = getAuthenticatedEmail(userDetails);
        if (authenticatedEmail == null) {
            // Non autenticato: reindirizza a login con messaggio
            return "redirect:/users/login?error=not_authenticated";
        }

        Optional<Curriculum> curriculumOpt = curriculumRepository.findById(id);
        if (curriculumOpt.isEmpty()) {
            return "redirect:/?error=cv_not_found";
        }

        Curriculum curriculum = curriculumOpt.get();

        if (!curriculum.getUser().getEmail().equals(authenticatedEmail)) {
            // Utente autenticato ma non proprietario: reindirizza a homepage con messaggio
            return "redirect:/?error=not_authorized";
        }

        model.addAttribute("curriculum", curriculum);
        return "curriculums/edit";
    }

    // 6. Submit modifica curriculum (solo proprietario)
    @PostMapping("/curriculums/edit/{id}")
    public String postEdit(
            @PathVariable Long id,
            @Valid @ModelAttribute("curriculum") Curriculum curriculum,
            BindingResult bindingResult,
            @RequestParam(value = "profiles_file", required = false) MultipartFile profileFile,
            @RequestParam(value = "certificates_files", required = false) MultipartFile[] certificateFiles,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (bindingResult.hasErrors()) {
            return "curriculums/edit";
        }
        String authenticatedEmail = getAuthenticatedEmail(userDetails);
        if (authenticatedEmail == null) {
            return "redirect:/users/login?error=not_authenticated";
        }

        Optional<Curriculum> existingCurriculumOpt = curriculumRepository.findById(id);
        if (existingCurriculumOpt.isEmpty()) {
            return "redirect:/?error=cv_not_found";
        }

        Curriculum existingCurriculum = existingCurriculumOpt.get();
        if (!existingCurriculum.getUser().getEmail().equals(authenticatedEmail)) {
            return "redirect:/?error=not_authorized";
        }

        curriculum.setId(existingCurriculum.getId());
        curriculum.setUser(existingCurriculum.getUser());
        curriculum.setPreferredLanguage(existingCurriculum.getPreferredLanguage());

        // Gestione foto profilo
        if (profileFile != null && !profileFile.isEmpty()) {
            String uploadedUrl = fileStorage.storeFile(profileFile, "profiles");
            if (uploadedUrl == null) {
                bindingResult.reject("curriculum.image.upload.failed", "Errore nel caricamento della foto profilo.");
                return "curriculums/edit";
            }
            curriculum.setImage_profile_Url(uploadedUrl);
        }

        String incomingUrl = curriculum.getImage_profile_Url();
        String existingUrl = existingCurriculum.getImage_profile_Url();
        if (incomingUrl == null || incomingUrl.isBlank()) {
            // Nessuna nuova foto: mantieni la precedente
            curriculum.setImage_profile_Url(existingUrl);
        } else if (!incomingUrl.equals(existingUrl)) {
            // Nuova foto diversa: elimina il vecchio file
            fileStorage.deleteFile(existingUrl);
        }

        if (curriculum.getContacts() == null) {
            curriculum.setContacts(existingCurriculum.getContacts() != null ? existingCurriculum.getContacts() : new Contacts());
        }

        if (certificateFiles != null && curriculum.getEducations() != null) {
            int count = Math.min(certificateFiles.length, curriculum.getEducations().size());
            for (int i = 0; i < count; i++) {
                MultipartFile certificateFile = certificateFiles[i];
                if (certificateFile == null || certificateFile.isEmpty()) {
                    continue;
                }
                String uploadedUrl = fileStorage.storeFile(certificateFile, "certificates");
                if (uploadedUrl == null) {
                    bindingResult.reject("curriculum.certificate.upload.failed", "Errore nel caricamento del certificato.");
                    return "curriculums/edit";
                }

                String oldCertificateUrl = null;
                if (existingCurriculum.getEducations() != null && existingCurriculum.getEducations().size() > i) {
                    oldCertificateUrl = existingCurriculum.getEducations().get(i).getImage_certificate_Url();
                }

                curriculum.getEducations().get(i).setImage_certificate_Url(uploadedUrl);
                if (oldCertificateUrl != null && !oldCertificateUrl.isBlank() && !oldCertificateUrl.equals(uploadedUrl)) {
                    fileStorage.deleteFile(oldCertificateUrl);
                }
            }
        }

        prepareCurriculumRelationships(curriculum);
        try {
            curriculumRepository.save(curriculum);
        } catch (DataIntegrityViolationException ex) {
            String detail = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();
            bindingResult.reject("curriculum.save.failed", "Unable to save curriculum. Possible too-long field (often projects.description/url). DB: " + detail);
            return "curriculums/edit";
        }
        return "redirect:/curriculums/edit/" + id;
    }

    // 7. Delete curriculum (solo proprietario)
    @GetMapping("/curriculums/delete/{id}")
    public String getDelete(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        String authenticatedEmail = getAuthenticatedEmail(userDetails);
        if (authenticatedEmail == null) {
            // Non autenticato: reindirizza a login con messaggio
            return "redirect:/users/login?error=not_authenticated";
        }

        Optional<Curriculum> curriculumOpt = curriculumRepository.findById(id);
        if (curriculumOpt.isEmpty()) {
            return "redirect:/?error=cv_not_found";
        }

        Curriculum curriculum = curriculumOpt.get();

        if (!curriculum.getUser().getEmail().equals(authenticatedEmail)) {
            // Utente autenticato ma non proprietario: reindirizza a homepage con messaggio
            return "redirect:/?error=not_authorized";
        }

        curriculumRepository.delete(curriculum);
        return "redirect:/";
    }

    private String getAuthenticatedEmail(UserDetails userDetails) {
        if (userDetails != null) {
            return userDetails.getUsername();
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String username = authentication.getName();
        if (username == null || username.isBlank() || "anonymousUser".equals(username)) {
            return null;
        }

        return username;
    }

    private void prepareCurriculumRelationships(Curriculum curriculum) {
        if (curriculum.getContacts() != null) {
            curriculum.getContacts().setCurriculum(curriculum);
        }

        for (Skill skill : curriculum.getSkills()) {
            skill.setCurriculum(curriculum);
        }

        for (Projects project : curriculum.getProjects()) {
            project.setCurriculum(curriculum);
        }

        for (Experience experience : curriculum.getExperiences()) {
            experience.setCurriculum(curriculum);

            for (Job job : experience.getJobs()) {
                job.setExperience(experience);

                if (job.getJobTask() == null) {
                    continue;
                }

                for (JobTask task : job.getJobTask()) {
                    task.setJob(job);
                }
            }
        }

        for (Education education : curriculum.getEducations()) {
            education.setCurriculum(curriculum);
        }

        for (Language language : curriculum.getLanguages()) {
            language.setCurriculum(curriculum);
        }
    }

}
