package es.fempa.acd.redsocialpawzy.service;

import es.fempa.acd.redsocialpawzy.model.User;
import es.fempa.acd.redsocialpawzy.repository.PostRepository;
import es.fempa.acd.redsocialpawzy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class for managing User entities.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PostRepository postRepository;

    /**
     * Finds the author of a post by the post ID.
     *
     * @param postId the ID of the post
     * @return the User who authored the post
     */
    public User findByPostId(Long postId) {
        return postRepository.findAuthorByPostId(postId);
    }

    /**
     * Registers a new user with the given username, email, and password.
     * The password is encoded before saving.
     * Automatically logs in the user after registration.
     *
     * @param username the username of the new user
     * @param email the email of the new user
     * @param password the password of the new user
     * @return the registered User entity
     */
    public User registerUser(String username, String email, String password) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // Encrypt the password
        userRepository.save(user);

        // Automatically log in after registration
        autoLogin(email, password);

        return user;
    }

    /**
     * Finds a user by their ID.
     *
     * @param id the ID of the user
     * @return an Optional containing the found user, or empty if not found
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Automatically logs in a user with the given email and password.
     *
     * @param email the email of the user
     * @param password the password of the user
     */
    public void autoLogin(String email, String password) {
        UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken(email, password);

        SecurityContextHolder.getContext().setAuthentication(
                authenticationManager.authenticate(authRequest)
        );
    }

    /**
     * Verifies if the raw password matches the encoded password.
     *
     * @param rawPassword the raw password to verify
     * @param encodedPassword the encoded password to compare against
     * @return true if the passwords match, false otherwise
     */
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * Finds a user by their email.
     *
     * @param email the email of the user
     * @return an Optional containing the found user, or empty if not found
     */
    public Optional<User> findByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            System.out.println("✅ User found in the database: " + user.get().getEmail());
        } else {
            System.out.println("❌ ERROR: User with email '" + email + "' does NOT exist in the database.");
        }

        return user;
    }

    public void updateProfileImage(Long userId, String imageUrl) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setProfileImageUrl(imageUrl);
            userRepository.save(user);
        }
    }
}