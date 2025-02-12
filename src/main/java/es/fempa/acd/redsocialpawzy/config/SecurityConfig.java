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
 * Configuration class for Spring Security.
 */
@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    /**
     * Constructor for SecurityConfig.
     *
     * @param userDetailsService the custom user details service
     */
    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Configures the security filter chain.
     *
     * @param http the HttpSecurity object
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for local testing
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers( // Public routes
                                "/", // Allow access to index without authentication
                                "/index.html",
                                "/auth/login",
                                "/auth/register",
                                "/politica-cookies",
                                "/privacidad",
                                "/condiciones"
                        ).permitAll()
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/uploads/**").permitAll() // Allow static files
                        .anyRequest().authenticated() // Any other request requires authentication
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // Keep session active
                        .maximumSessions(1) // Only one session per user
                        .expiredUrl("/auth/login?expired") // Redirect if session expires
                )
                .formLogin(login -> login
                        .loginPage("/auth/login") // Custom login page
                        .defaultSuccessUrl("/posts", true) // Redirect to `/posts` on successful login
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout") // URL to log out
                        .logoutSuccessUrl("/") // Redirect to index after logout
                        .invalidateHttpSession(true) // Invalidate session
                        .deleteCookies("JSESSIONID") // Delete cookies
                        .permitAll()
                );

        return http.build();
    }

    /**
     * Configures the password encoder.
     *
     * @return the password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the authentication manager.
     *
     * @param authenticationConfiguration the authentication configuration
     * @return the authentication manager
     * @throws Exception if an error occurs
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return new ProviderManager(authProvider);
    }
}