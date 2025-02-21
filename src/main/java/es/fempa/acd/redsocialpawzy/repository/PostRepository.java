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

    List<Post> findByUser(User user);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.privatePost = false")
    List<Post> findAllPublicPostsWithLikes(); // Solo devuelve posts públicos

    List<Post> findByUserUsernameAndPrivatePostFalse(String username); // Solo devuelve posts públicos de un usuario

    @Query("SELECT p.user FROM Post p WHERE p.id = :postId")
    User findAuthorByPostId(@Param("postId") Long postId);

    @Query("SELECT p FROM Post p WHERE p.user = :user AND p.privatePost = false")
    List<Post> findPublicPostsByUser(@Param("user") User user);

}
