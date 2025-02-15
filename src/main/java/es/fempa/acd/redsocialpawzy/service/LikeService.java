package es.fempa.acd.redsocialpawzy.service;

import es.fempa.acd.redsocialpawzy.model.Like;
import es.fempa.acd.redsocialpawzy.model.Post;
import es.fempa.acd.redsocialpawzy.model.User;
import es.fempa.acd.redsocialpawzy.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class for managing Like entities.
 */
@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    /**
     * Toggles the like status for a given user and post.
     * If the user has already liked the post, the like is removed.
     * If the user has not liked the post, a new like is added.
     *
     * @param user the user who is liking or unliking the post
     * @param post the post to be liked or unliked
     */
    public void toggleLike(User user, Post post) {
        Optional<Like> existingLike = likeRepository.findByUserAndPost(user, post);

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get()); // If already liked, remove the like
        } else {
            Like like = new Like(user, post);
            likeRepository.save(like); // If not liked, add a new like
        }
    }

    /**
     * Counts the number of likes for a given post.
     *
     * @param post the post to count likes for
     * @return the number of likes for the specified post
     */
    public int countLikes(Post post) {
        return likeRepository.countByPost(post);
    }
}