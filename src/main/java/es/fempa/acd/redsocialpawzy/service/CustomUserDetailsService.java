package es.fempa.acd.redsocialpawzy.service;

import es.fempa.acd.redsocialpawzy.model.User;
import es.fempa.acd.redsocialpawzy.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Normaliza el email recibido: quita espacios y conviértelo a minúsculas
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
