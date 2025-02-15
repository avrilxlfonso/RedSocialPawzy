package es.fempa.acd.redsocialpawzy.service;

import es.fempa.acd.redsocialpawzy.model.Post;
import es.fempa.acd.redsocialpawzy.model.User;
import es.fempa.acd.redsocialpawzy.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing Post entities.
 */
@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    /**
     * Retrieves a list of posts for a given user.
     *
     * @param user the user to retrieve posts for
     * @return a list of posts for the specified user
     */
    public List<Post> getPostsByUser(User user) {
        return postRepository.findByUser(user);
    }

    /**
     * Deletes a post by its ID.
     *
     * @param postId the ID of the post to delete
     */
    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }

    /**
     * Creates a new post.
     *
     * @param post the post entity to create
     * @return the created post entity
     */
    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    /**
     * Retrieves all posts with their associated likes.
     *
     * @return a list of all posts with their likes
     */
    public List<Post> getAllPosts() {
        return postRepository.findAllWithLikes();
    }

    /**
     * Finds posts by the username of the associated user.
     *
     * @param username the username to find posts for
     * @return a list of posts for the specified username
     */
    public List<Post> findPostsByUsername(String username) {
        return postRepository.findByUserUsername(username);
    }

    /**
     * Gets the total number of likes for all posts by a given user.
     *
     * @param user the user to count likes for
     * @return the total number of likes for the user's posts
     */
    public int getTotalLikesByUser(User user) {
        List<Post> posts = postRepository.findByUser(user);
        return posts.stream().mapToInt(post -> post.getLikes().size()).sum(); // ✅ Counts the likes on the user's posts
    }

    /**
     * Retrieves a post by its ID.
     *
     * @param postId the ID of the post to retrieve
     * @return the post entity, or null if not found
     */
    public Post obtenerPostPorId(Long postId) {
        return postRepository.findById(postId).orElse(null);
    }

    public void updatePostDescription(Long postId, String newDescription) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            post.setDescription(newDescription);
            postRepository.save(post);  // Guarda la publicación con la nueva descripción
        }
    }
}