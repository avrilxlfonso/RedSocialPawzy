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

/**
 * Configuración de seguridad para la aplicación.
 * Define reglas de autenticación y autorización, gestión de sesiones y configuración del login/logout.
 */
@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    /**
     * Constructor que inyecta el servicio de detalles de usuario personalizado.
     *
     * @param userDetailsService Servicio de autenticación de usuarios.
     */
    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Configura la cadena de filtros de seguridad (Security Filter Chain).
     * Aquí se establecen las reglas de acceso a diferentes rutas y la configuración de sesiones.
     *
     * @param http Objeto de configuración de seguridad de Spring Security.
     * @return Configuración de seguridad de la aplicación.
     * @throws Exception En caso de error en la configuración.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // 🔹 Deshabilita CSRF (Cross-Site Request Forgery) para pruebas locales
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers( // 🔹 Rutas públicas accesibles sin autenticación
                                "/", // Página de inicio
                                "/index.html",
                                "/auth/login", // Página de inicio de sesión
                                "/auth/register", // Página de registro
                                "/politica-cookies",
                                "/privacidad",
                                "/condiciones"
                        ).permitAll()
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/uploads/**").permitAll() // 🔹 Permite acceso a archivos estáticos
                        .anyRequest().authenticated() // 🔹 Cualquier otra solicitud requiere autenticación
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // 🔹 Mantiene sesión activa
                        .maximumSessions(1) // 🔹 Solo permite una sesión por usuario
                        .expiredUrl("/auth/login?expired") // 🔹 Redirige si la sesión expira
                )
                .formLogin(login -> login
                        .loginPage("/auth/login") // 🔹 Página personalizada de inicio de sesión
                        .defaultSuccessUrl("/posts", true) // 🔹 Redirige a "/posts" después de iniciar sesión exitosamente
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout") // 🔹 URL para cerrar sesión
                        .logoutSuccessUrl("/") // 🔹 Redirige a la página de inicio tras cerrar sesión
                        .invalidateHttpSession(true) // 🔹 Invalida la sesión al cerrar sesión
                        .deleteCookies("JSESSIONID") // 🔹 Elimina la cookie de sesión
                        .permitAll()
                );

        return http.build();
    }

    /**
     * Configuración del codificador de contraseñas.
     * Se usa BCrypt para almacenar contraseñas de manera segura.
     *
     * @return Instancia de BCryptPasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura el gestor de autenticación, estableciendo el proveedor de autenticación DAO.
     * Usa el servicio de detalles de usuario y el codificador de contraseñas para autenticar a los usuarios.
     *
     * @param authenticationConfiguration Configuración de autenticación de Spring Security.
     * @return El AuthenticationManager configurado.
     * @throws Exception En caso de error en la configuración.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); // 🔹 Usa el servicio de detalles de usuario
        authProvider.setPasswordEncoder(passwordEncoder()); // 🔹 Usa BCrypt para verificar contraseñas

        return new ProviderManager(authProvider);
    }
}
