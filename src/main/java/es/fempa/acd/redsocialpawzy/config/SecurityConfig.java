package es.fempa.acd.redsocialpawzy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Deshabilita CSRF para pruebas
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/favicon.ico").permitAll()
                        .requestMatchers("/", "/index.html", "/login.html", "/register.html", "/feed.html").permitAll() // Permitir acceso público
                        .requestMatchers("/css/**", "/js/**", "/img/**").permitAll() // Archivos estáticos
                        .requestMatchers("/auth/login", "/auth/register", "/auth/logout").permitAll() // Rutas públicas
                        .anyRequest().authenticated() // Rutas protegidas
                )
                .formLogin(login -> login.disable()) // Deshabilitar login de Spring Security
                .httpBasic(basic -> basic.disable()); // Deshabilitar autenticación básica

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
