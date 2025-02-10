package es.fempa.acd.redsocialpawzy.controller;

import es.fempa.acd.redsocialpawzy.model.Post;
import es.fempa.acd.redsocialpawzy.model.User;
import es.fempa.acd.redsocialpawzy.service.PostService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    // 🔹 Obtener publicaciones del usuario autenticado y mostrar en la vista de perfil
    @GetMapping("/user")
    public String getUserPosts(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth/login"; // Redirigir si no está autenticado
        }

        List<Post> posts = postService.getPostsByUser(user);
        model.addAttribute("user", user);
        model.addAttribute("userPosts", posts);

        return "profile"; // Carga profile.html con las publicaciones del usuario
    }

    // 🔹 Subir una nueva publicación
    @PostMapping("/create")
    public String createPost(
            @RequestParam("image") MultipartFile image,
            @RequestParam("description") String description,
            HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth/login"; // Redirigir si no está autenticado
        }

        // Guardar imagen en un directorio (esto se puede mejorar usando un servicio de almacenamiento en la nube)
        String uploadDir = "src/main/resources/static/uploads/";
        File uploadFolder = new File(uploadDir);
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs();
        }

        String imageUrl = "/uploads/" + image.getOriginalFilename();
        try {
            image.transferTo(new File(uploadDir + image.getOriginalFilename()));
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/auth/profile?error=upload_failed"; // Manejar error
        }

        // Guardar el post en la base de datos
        Post post = new Post();
        post.setImageUrl(imageUrl);
        post.setDescription(description);
        post.setUser(user);
        postService.createPost(post);

        return "redirect:/auth/profile"; // Redirigir al perfil después de subir la publicación
    }
}
