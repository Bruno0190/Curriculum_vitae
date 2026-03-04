package curriculum_vitae.cv.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.http.SessionCreationPolicy;

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
                .requestMatchers("/", "/curriculums", "/curriculums/show/**", "/users/create", "/users/login", "/css/**", "/js/**", "/images/**").permitAll()
                // Pagine protette: solo autenticati
                .requestMatchers("/curriculums/create", "/curriculums/edit/**", "/curriculums/delete/**").authenticated()
                .anyRequest().permitAll()
            )
            // Form login
            .formLogin(form -> form
                .loginPage("/users/login")
                .loginProcessingUrl("/login")
                .usernameParameter("email")
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            // Logout
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .permitAll()
            )
            // Sessione sempre creata
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
            )
            // Remember-me opzionale (cookie persistente)
            .rememberMe(remember -> remember
                .key("cv-remember-me-key")
                .tokenValiditySeconds(7 * 24 * 60 * 60) // 7 giorni
            );
        return http.build();
    }
}