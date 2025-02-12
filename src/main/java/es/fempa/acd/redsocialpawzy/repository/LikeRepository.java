package es.fempa.acd.redsocialpawzy.repository;

import es.fempa.acd.redsocialpawzy.model.Like;
import es.fempa.acd.redsocialpawzy.model.Post;
import es.fempa.acd.redsocialpawzy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing Like entities.
 */
@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    /**
     * Finds a like by the associated user and post.
     *
     * @param user the user who liked the post
     * @param post the post that was liked
     * @return an Optional containing the found like, or empty if not found
     */
    Optional<Like> findByUserAndPost(User user, Post post);

    /**
     * Counts the number of likes for a given post.
     *
     * @param post the post to count likes for
     * @return the number of likes for the specified post
     */
    int countByPost(Post post);
}