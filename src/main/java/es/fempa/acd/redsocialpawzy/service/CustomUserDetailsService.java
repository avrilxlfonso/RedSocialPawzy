package es.fempa.acd.redsocialpawzy.service;

import es.fempa.acd.redsocialpawzy.model.User;
import es.fempa.acd.redsocialpawzy.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class for managing custom user details.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Constructs a CustomUserDetailsService with the specified UserRepository.
     *
     * @param userRepository the user repository to use for user data access
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads the user details by the given email.
     *
     * @param email the email of the user to load
     * @return the UserDetails of the user
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Normalizes the received email: trims spaces and converts to lowercase
        String normalizedEmail = email.trim().toLowerCase();

        Optional<User> optionalUser = userRepository.findByEmail(normalizedEmail);

        if (optionalUser.isEmpty()) {
            System.out.println("❌ Usuario no encontrado: " + normalizedEmail);
            throw new UsernameNotFoundException("Usuario no encontrado con email: " + normalizedEmail);
        }

        User user = optionalUser.get();
        System.out.println("✅ Usuario encontrado: " + user.getEmail());

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }

}