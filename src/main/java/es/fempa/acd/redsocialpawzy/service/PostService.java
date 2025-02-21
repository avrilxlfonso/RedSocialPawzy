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

    public List<Post> getPostsByUser(User user) {
        return postRepository.findByUser(user);
    }

    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }

    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    public List<Post> getAllPublicPosts() {
        return postRepository.findAllPublicPostsWithLikes();
    }

    public List<Post> findPublicPostsByUsername(String username) {
        return postRepository.findByUserUsernameAndPrivatePostFalse(username);
    }

    public int getTotalLikesByUser(User user) {
        List<Post> posts = postRepository.findByUser(user);
        return posts.stream().mapToInt(post -> post.getLikes().size()).sum();
    }

    public Post obtenerPostPorId(Long postId) {
        return postRepository.findById(postId).orElse(null);
    }

    public void updatePostDescription(Long postId, String newDescription) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            post.setDescription(newDescription);
            postRepository.save(post);
        }
    }

    public List<Post> getPublicPostsByUser(User user) {
        return postRepository.findPublicPostsByUser(user);
    }

}
