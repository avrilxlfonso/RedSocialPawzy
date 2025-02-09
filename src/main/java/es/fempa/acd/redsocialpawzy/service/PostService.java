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

    public Post createPost(Post post) {
        return postRepository.save(post);
    }
}
