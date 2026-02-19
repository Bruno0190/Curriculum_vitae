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
        // Cerca l'utente per email (che usiamo come username)
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        User user = userOptional.get();
        // Qui puoi aggiungere ruoli/authorities se servono, per ora lasciamo vuoto
        return new DatabaseUserDetails(
            user.getId(),
            user.getEmail(), // username = email
            user.getPassword(),
            Collections.emptySet() // authorities vuoto per ora
        );
    }
}
