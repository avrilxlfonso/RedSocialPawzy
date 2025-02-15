package es.fempa.acd.redsocialpawzy.repository;

import es.fempa.acd.redsocialpawzy.model.Post;
import es.fempa.acd.redsocialpawzy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository interface for managing Post entities.
 */
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * Finds posts by the associated user.
     *
     * @param user the user to find posts for
     * @return a list of posts for the specified user
     */
    List<Post> findByUser(User user);

    /**
     * Finds all posts with their associated likes.
     *
     * @return a list of posts with their likes
     */
    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes")
    List<Post> findAllWithLikes();

    /**
     * Finds posts by the username of the associated user.
     *
     * @param username the username to find posts for
     * @return a list of posts for the specified username
     */
    List<Post> findByUserUsername(String username);

    /**
     * Finds the author of a post by the post ID.
     *
     * @param postId the ID of the post
     * @return the user who authored the post
     */
    @Query("SELECT p.user FROM Post p WHERE p.id = :postId")
    User findAuthorByPostId(@Param("postId") Long postId);
}