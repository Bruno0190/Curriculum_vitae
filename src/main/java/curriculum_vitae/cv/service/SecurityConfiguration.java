package curriculum_vitae.cv.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfiguration {

    // Password encoder per criptare le password
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configurazione della sicurezza
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Pagine pubbliche
                .requestMatchers("/", "/curriculums", "/curriculums/{id}", "/register", "/login").permitAll()
                // Pagine protette: solo autenticati
                .requestMatchers("/curriculums/create", "/curriculums/edit/**", "/curriculums/delete/**").authenticated()
            )
            // Form login
            .formLogin(form -> form
                .loginPage("/login") // puoi creare una pagina custom
                .permitAll()
            )
            // Logout
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .permitAll()
            );
        return http.build();
    }
}