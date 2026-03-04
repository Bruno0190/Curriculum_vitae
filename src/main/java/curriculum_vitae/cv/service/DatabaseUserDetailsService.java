package curriculum_vitae.cv.service;

import curriculum_vitae.cv.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import curriculum_vitae.cv.repository.UserRepository;
import java.util.*;

@Service
public class DatabaseUserDetailsService implements UserDetailsService{

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        System.out.println("[DEBUG] DatabaseUserDetailsService.loadUserByUsername chiamato con email: " + email);
        Optional<User> userOptional = userRepository.findByEmail(email);
        System.out.println("[DEBUG] userRepository.findByEmail(...) trovato? " + userOptional.isPresent());
        if (userOptional.isEmpty()) {
            System.out.println("[DEBUG] Nessun utente trovato per questa email!");
            throw new UsernameNotFoundException("User not found");
        }
        User user = userOptional.get();
        System.out.println("[DEBUG] Utente trovato: id=" + user.getId() + ", email=" + user.getEmail());
        return new DatabaseUserDetails(
            user.getId(),
            user.getEmail(), // username = email
            user.getPassword(),
            Collections.emptySet() // authorities vuoto per ora
        );
    }
}
