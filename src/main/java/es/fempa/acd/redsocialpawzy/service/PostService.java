package es.fempa.acd.redsocialpawzy.service;

import es.fempa.acd.redsocialpawzy.model.Post;
import es.fempa.acd.redsocialpawzy.model.User;
import es.fempa.acd.redsocialpawzy.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<Post> getAllPosts() {
        return postRepository.findAllWithLikes();
    }

    public List<Post> findPostsByUsername(String username) {
        return postRepository.findByUserUsername(username);
    }

    public int getTotalLikesByUser(User user) {
        List<Post> posts = postRepository.findByUser(user);
        return posts.stream().mapToInt(post -> post.getLikes().size()).sum(); // ✅ Cuenta los likes en sus posts
    }

    public Post obtenerPostPorId(Long postId) { return postRepository.findById(postId).orElse(null);}
}
