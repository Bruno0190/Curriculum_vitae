package curriculum_vitae.cv.controller;
import curriculum_vitae.cv.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import curriculum_vitae.cv.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.validation.*;
import org.springframework.validation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Controller
@RequestMapping("/users")
public class UserController {


	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	// Mostra form registrazione
	@GetMapping("/create")
	public String showRegistrationForm(Model model) {
		model.addAttribute("user", new User());
		return "users/create";
	}

	// Gestisce submit registrazione
	@PostMapping("/create")
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			return "users/create";
		}

		// Codifica password prima di salvare
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		userRepository.save(user);
		return "redirect:/login";
	}

	// Mostra form modifica profilo
	@GetMapping("/edit/{id}")
	public String showEditForm(@PathVariable("id") Long id, Model model) {
		
        User user = userRepository.findById(id).orElseThrow();

		model.addAttribute("user", user);
		return "users/edit";
	}

	// Gestisce submit modifica profilo
	@PostMapping("/edit/{id}")
	public String updateUser(@PathVariable("id") Long id, @Valid @ModelAttribute("user") User userForm, BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			return "users/edit";
		}

		User user = userRepository.findById(id).orElseThrow();
		user.setName(userForm.getName());
		user.setEmail(userForm.getEmail());
		// Codifica password prima di salvare
		user.setPassword(passwordEncoder.encode(userForm.getPassword()));

		userRepository.save(user);

		return "redirect:/login";
	}

	// Mostra pagina login custom (opzionale)
	@GetMapping("/login")
	public String showLoginForm() {
		return "users/login";
	}

	// Logout (Spring Security gestisce, ma puoi mostrare una pagina di conferma)
	@GetMapping("/logout-success")
	public String logoutSuccess() {
		return "users/logout-success";
	}
}
