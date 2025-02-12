package es.fempa.acd.redsocialpawzy.repository;

import es.fempa.acd.redsocialpawzy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository interface for managing User entities.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their email.
     *
     * @param email the email to find the user by
     * @return an Optional containing the found user, or empty if not found
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds a user by their username.
     *
     * @param username the username to find the user by
     * @return an Optional containing the found user, or empty if not found
     */
    Optional<User> findByUsername(String username);
}