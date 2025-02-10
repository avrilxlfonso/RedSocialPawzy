package es.fempa.acd.redsocialpawzy.config;

import es.fempa.acd.redsocialpawzy.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // 🔹 Desactivar CSRF solo para pruebas locales
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers( // 🔹 Rutas públicas
                                "/", // 🔹 Permitir acceso al index sin autenticación
                                "/index.html",
                                "/auth/login",
                                "/auth/register",
                                "/politica-cookies",
                                "/privacidad",
                                "/condiciones"
                        ).permitAll()
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/uploads/**").permitAll() // 🔹 Permitir archivos estáticos
                        .anyRequest().authenticated() // 🔹 Cualquier otra petición requiere autenticación
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // 🔹 Mantener sesión activa
                        .maximumSessions(1) // 🔹 Solo una sesión por usuario
                        .expiredUrl("/auth/login?expired") // 🔹 Redirigir si la sesión expira
                )
                .formLogin(login -> login
                        .loginPage("/auth/login") // 🔹 Página de login personalizada
                        .defaultSuccessUrl("/posts", true) // 🔹 Si inicia sesión, se queda en `/`
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout") // 🔹 URL para cerrar sesión
                        .logoutSuccessUrl("/") // 🔹 Después del logout, volver al index
                        .invalidateHttpSession(true) // 🔹 Invalidar sesión
                        .deleteCookies("JSESSIONID") // 🔹 Borrar cookies
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return new ProviderManager(authProvider);
    }
}
