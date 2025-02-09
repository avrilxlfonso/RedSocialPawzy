package es.fempa.acd.redsocialpawzy.controller;

import es.fempa.acd.redsocialpawzy.model.Post;
import es.fempa.acd.redsocialpawzy.model.User;
import es.fempa.acd.redsocialpawzy.service.PostService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    // 📌 Obtener publicaciones del usuario autenticado
    @GetMapping("/user")
    public ResponseEntity<List<Post>> getUserPosts(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        List<Post> posts = postService.getPostsByUser(user);
        return ResponseEntity.ok(posts);
    }

    // 📌 Crear una nueva publicación
    @PostMapping("/create")
    public ResponseEntity<Post> createPost(@RequestBody Post post, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        post.setUser(user);
        Post savedPost = postService.createPost(post);
        return ResponseEntity.ok(savedPost);
    }
}
