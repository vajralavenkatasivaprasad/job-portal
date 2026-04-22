package com.jobportal.config;

import com.jobportal.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .userDetailsService(userDetailsService)
            .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**", "/h2-console/**"))
            .headers(h -> h.frameOptions(f -> f.sameOrigin()))  // H2 console frames
            .authorizeHttpRequests(auth -> auth
                // Public pages
                .requestMatchers("/", "/home", "/jobs", "/jobs/**", "/register", "/login", "/css/**", "/js/**", "/uploads/**", "/h2-console/**").permitAll()
                // Student-only pages
                .requestMatchers("/student/**").hasRole("STUDENT")
                // Employer-only pages
                .requestMatchers("/employer/**").hasRole("EMPLOYER")
                // Admin-only pages
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // REST APIs
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/student/**").hasRole("STUDENT")
                .requestMatchers("/api/employer/**").hasRole("EMPLOYER")
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler((req, res, auth) -> {
                    var authorities = auth.getAuthorities();
                    if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                        res.sendRedirect("/admin/dashboard");
                    } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_EMPLOYER"))) {
                        res.sendRedirect("/employer/dashboard");
                    } else {
                        res.sendRedirect("/student/dashboard");
                    }
                })
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            );

        return http.build();
    }
}
