package curriculum_vitae.cv.controller;

import java.util.HashMap;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import curriculum_vitae.cv.model.*;
import curriculum_vitae.cv.repository.CurriculumRepository;
import curriculum_vitae.cv.repository.UserRepository;
import curriculum_vitae.cv.service.TranslationService;


@Controller
public class TranslationController {


    @Autowired
    private CurriculumRepository curriculumRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TranslationService translationService;

    @PostMapping("/curriculums/translate/{id}")
    public String translateCurriculum(@PathVariable Long id, @RequestParam("languages") String lang, Model model,
                                      @AuthenticationPrincipal UserDetails userDetails) {

        Optional<Curriculum> curriculumOpt = curriculumRepository.findById(id);
        if (curriculumOpt.isEmpty()) {
            return "redirect:/?error=cv_not_found";
        }

        Curriculum curriculum = curriculumOpt.get();
        populateShowModel(model, curriculum, lang, userDetails);
        return "curriculums/show";
    }

    @PostMapping("/curriculums/translate/{id}/save")
    public String saveTranslationLanguage(@PathVariable Long id, @RequestParam("languages") String lang,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/users/login?error=not_authenticated";
        }

        Optional<Curriculum> curriculumOpt = curriculumRepository.findById(id);
        if (curriculumOpt.isEmpty()) {
            return "redirect:/?error=cv_not_found";
        }

        Optional<User> userOpt = userRepository.findByEmail(userDetails.getUsername());
        if (userOpt.isEmpty()) {
            return "redirect:/?error=user_not_found";
        }

        Curriculum curriculum = curriculumOpt.get();
        if (!curriculum.getUser().getId().equals(userOpt.get().getId())) {
            return "redirect:/?error=not_authorized";
        }

        curriculum.setPreferredLanguage(lang);
        curriculumRepository.save(curriculum);
        return "redirect:/curriculums/show/" + curriculum.getId() + "?success=language_saved";
    }

    private void populateShowModel(Model model, Curriculum curriculum, String lang, UserDetails userDetails) {
        Map<String, String> tradotto = new HashMap<>();
        boolean translationError = false;
        if (lang != null && !lang.isBlank() && !"it".equalsIgnoreCase(lang)) {
            tradotto = translationService.translateCurriculum(curriculum, lang);
            translationError = Boolean.parseBoolean(tradotto.getOrDefault("_translation_error", "false"));
        }

        boolean isOwner = false;
        if (userDetails != null) {
            Optional<User> user = userRepository.findByEmail(userDetails.getUsername());
            if (user.isPresent() && curriculum.getUser().getId().equals(user.get().getId())) {
                isOwner = true;
            }
        }

        model.addAttribute("tradotto", tradotto);
        model.addAttribute("translationError", translationError);
        model.addAttribute("curriculum", curriculum);
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("currentLanguage", lang);
    }
}
