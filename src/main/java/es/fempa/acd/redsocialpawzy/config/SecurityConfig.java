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
                .csrf(csrf -> csrf.disable()) // Desactivamos CSRF para pruebas locales
                .authorizeHttpRequests(auth -> auth
                        // 🔹 Permitir acceso sin autenticación a estas rutas
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/auth/login",
                                "/auth/register",
                                "/auth/logout",
                                "/politica-cookies",
                                "/privacidad",
                                "/condiciones"
                        ).permitAll()
                        // 🔹 Permitir acceso a archivos estáticos (CSS, JS, imágenes)
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/static/**").permitAll()
                        // 🔹 Cualquier otra petición debe estar autenticada
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .formLogin(login -> login.disable()) // Deshabilitamos el login de Spring Security
                .httpBasic(basic -> basic.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
