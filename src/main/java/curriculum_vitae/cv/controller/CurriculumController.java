
package curriculum_vitae.cv.controller;

import curriculum_vitae.cv.model.Curriculum;
import curriculum_vitae.cv.repository.CurriculumRepository;
import curriculum_vitae.cv.model.User;
import curriculum_vitae.cv.repository.UserRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

@Controller
public class CurriculumController {

    @Autowired
    private CurriculumRepository curriculumRepository;

    @Autowired
    private UserRepository userRepository;

    // 1. Homepage: mostra tutti i curriculum
    @GetMapping("/")
    public String getIndex(Model model) {
        model.addAttribute("curriculums", curriculumRepository.findAll());
        return "index";
    }

    // 2. Form creazione curriculum (solo autenticati)
    @GetMapping("/curriculums/create")
    public String getCreate(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            System.out.println("[DEBUG] userDetails è null: non autenticato");
            return "redirect:/users/login?error=not_authenticated";
        }
        System.out.println("[DEBUG] userDetails.getUsername(): " + userDetails.getUsername());
        Optional<User> user = userRepository.findByEmail(userDetails.getUsername());
        System.out.println("[DEBUG] userRepository.findByEmail(...) trovato? " + user.isPresent());
        if (user.isEmpty()) {
            System.out.println("[DEBUG] Utente autenticato ma non presente nel DB");
            return "redirect:/users/create?error=not_registered";
        }
        Curriculum cv_existing = curriculumRepository.findByUser(user.get());
        System.out.println("[DEBUG] Curriculum già esistente per utente? " + (cv_existing != null));
        if (cv_existing != null) {
            return "redirect:/?error=curriculum_exists";
        }
        model.addAttribute("curriculum", new Curriculum());
        return "curriculums/create";
    }

    // 3. Submit creazione curriculum (solo autenticati)
    @PostMapping("/curriculums/create")
    public String postCreate(@Valid @ModelAttribute("curriculum") Curriculum curriculum, BindingResult bindingResult, @AuthenticationPrincipal UserDetails userDetails) {
        if (bindingResult.hasErrors()) {
            return "curriculums/create";
        }
        // Associa curriculum all'utente loggato
        Optional<User> user = userRepository.findByEmail(userDetails.getUsername());
        if (user.isPresent()) {
            curriculum.setUser(user.get());
        }
        curriculumRepository.save(curriculum);
        return "redirect:/";
    }

    // 4. Show curriculum (tutti, ma pulsanti extra solo per proprietario)
    @GetMapping("/curriculums/show/{id}")
    public String getShow(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Curriculum> curriculumOpt = curriculumRepository.findById(id);
        if (curriculumOpt.isEmpty()) {
            return "redirect:/?error=not_found";
        }
        Curriculum curriculum = curriculumOpt.get();
        boolean isOwner = false;
        if (userDetails != null) {
            Optional<User> user = userRepository.findByEmail(userDetails.getUsername());
            if (user.isPresent() && curriculum.getUser().getId().equals(user.get().getId())) {
                isOwner = true;
            }
        }
        model.addAttribute("curriculum", curriculum);
        model.addAttribute("isOwner", isOwner);
        return "curriculums/show";
    }

    // 5. Form modifica curriculum (solo proprietario)
    @GetMapping("/curriculums/edit/{id}")
    public String getEdit(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
    
        if (userDetails == null) {
            // Non autenticato: reindirizza a login con messaggio
            return "redirect:/users/login?error=not_authenticated";
        }

        Curriculum curriculum = curriculumRepository.findById(id).orElseThrow();

        if (!curriculum.getUser().getEmail().equals(userDetails.getUsername())) {
            // Utente autenticato ma non proprietario: reindirizza a homepage con messaggio
            return "redirect:/?error=not_authorized";
        }

        model.addAttribute("curriculum", curriculum);
        return "curriculums/edit";
    }

    // 6. Submit modifica curriculum (solo proprietario)
    @PostMapping("/curriculums/edit/{id}")
    public String postEdit(@PathVariable Long id, @Valid @ModelAttribute("curriculum") Curriculum curriculum, BindingResult bindingResult, @AuthenticationPrincipal UserDetails userDetails) {
        if (bindingResult.hasErrors()) {
            return "curriculums/edit";
        }
        // ...controllo proprietà e update...
        return "redirect:/";
    }

    // 7. Delete curriculum (solo proprietario)
    @GetMapping("/curriculums/delete/{id}")
    public String getDelete(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            // Non autenticato: reindirizza a login con messaggio
            return "redirect:/users/login?error=not_authenticated";
        }

        Curriculum curriculum = curriculumRepository.findById(id).orElseThrow();

        if (!curriculum.getUser().getEmail().equals(userDetails.getUsername())) {
            // Utente autenticato ma non proprietario: reindirizza a homepage con messaggio
            return "redirect:/?error=not_authorized";
        }

        curriculumRepository.delete(curriculum);
        return "redirect:/";
    }

}
