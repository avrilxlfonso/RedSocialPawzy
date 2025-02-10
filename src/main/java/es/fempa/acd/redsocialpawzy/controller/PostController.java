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

@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    // Obtener publicaciones del usuario autenticado
    @GetMapping("/user")
    public ResponseEntity<List<Post>> getUserPosts(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        List<Post> posts = postService.getPostsByUser(user);
        return ResponseEntity.ok(posts);
    }

    @PostMapping("/create")
    public ResponseEntity<Post> createPost(@RequestBody Post post, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            System.out.println("⚠️ Usuario no autenticado al intentar crear un post");
            return ResponseEntity.status(403).build(); // 403 Forbidden
        }

        post.setUser(user);
        Post savedPost = postService.createPost(post);
        System.out.println("✅ Post guardado: " + savedPost.getDescription());

        return ResponseEntity.ok(savedPost);
    }

}
