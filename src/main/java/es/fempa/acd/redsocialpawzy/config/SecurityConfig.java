package es.fempa.acd.redsocialpawzy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/favicon.ico").permitAll()
                        .requestMatchers("/", "/index.html", "/login.html", "/register.html", "/feed.html", "/profile.html").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/img/**").permitAll()                        
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/politica-cookies.html", "/politica-cookies.html", "/privacidad.html", "/condiciones.html").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/static/**").permitAll()                                              
                        .requestMatchers("/auth/login", "/auth/register", "/auth/logout", "/auth/user").permitAll()
                        .requestMatchers("/posts/**", "/auth/**").authenticated()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)) // Manejo de sesión
                .formLogin(login -> login.disable())
                .httpBasic(basic -> basic.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
