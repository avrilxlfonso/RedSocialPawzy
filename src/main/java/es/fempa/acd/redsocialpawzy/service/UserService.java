package es.fempa.acd.redsocialpawzy.service;

import es.fempa.acd.redsocialpawzy.model.User;
import es.fempa.acd.redsocialpawzy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    public User registerUser(String username, String email, String password) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // 🔹 Encriptar la contraseña
        userRepository.save(user);

        // 🔹 Iniciar sesión automáticamente después del registro
        autoLogin(email, password);

        return user;
    }

    public void autoLogin(String email, String password) {
        UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken(email, password);

        SecurityContextHolder.getContext().setAuthentication(
                authenticationManager.authenticate(authRequest)
        );
    }


    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public Optional<User> findByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            System.out.println("✅ Usuario encontrado en la base de datos: " + user.get().getEmail());
        } else {
            System.out.println("❌ ERROR: El usuario con email '" + email + "' NO existe en la base de datos.");
        }

        return user;
    }
}
