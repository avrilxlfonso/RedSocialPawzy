package es.fempa.acd.redsocialpawzy.controller;

import es.fempa.acd.redsocialpawzy.model.Post;
import es.fempa.acd.redsocialpawzy.model.User;
import es.fempa.acd.redsocialpawzy.service.PostService;
import es.fempa.acd.redsocialpawzy.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;


@Controller
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    // 🔹 Obtener publicaciones del usuario autenticado y mostrar en la vista de perfil

    @GetMapping
    public String getPosts(Model model) {
        List<Post> posts = postService.getAllPosts();
        model.addAttribute("posts", posts);
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername(); // Spring Security usa email como username
            User user = userService.findByEmail(email).orElse(null);

            if (user == null) {
                return "redirect:/auth/login"; // Redirigir si no existe en la BD
            }

            model.addAttribute("user", user);
            return "feed";
        }

        return "redirect:/auth/login"; // Si no está autenticado, redirigir al login
    }

    @GetMapping("/user")
    public String getUserPosts(Model model) {
        // 🔹 Obtiene el usuario autenticado desde Spring Security
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername(); // Spring Security usa email como username
            User user = userService.findByEmail(email).orElse(null);

            if (user == null) {
                return "redirect:/auth/login"; // Redirigir si no existe en la BD
            }

            List<Post> posts = postService.getPostsByUser(user);
            model.addAttribute("user", user);
            model.addAttribute("userPosts", posts);

            return "profile"; // Cargar la vista con los datos correctos
        }

        return "redirect:/auth/login"; // Si no está autenticado, redirigir al login
    }

    @GetMapping("/search")
    public String searchPostsByUser(@RequestParam("username") String username, Model model) {
        List<Post> posts = postService.findPostsByUsername(username);
        model.addAttribute("posts", posts);

        // Obtener usuario autenticado
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            User user = userService.findByEmail(email).orElse(null);
            if (user != null) {
                model.addAttribute("user", user);
            }
        }

        return "feed"; // Redirigir a la vista con los resultados
    }


    @PostMapping("/create")
    public String createPost(
            @RequestParam("image") MultipartFile image,
            @RequestParam("description") String description) {

        // 🔹 Obtener el usuario autenticado desde Spring Security
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof UserDetails)) {
            return "redirect:/auth/login"; // 🔹 Si no está autenticado, redirigir al login
        }

        String email = ((UserDetails) principal).getUsername();
        User user = userService.findByEmail(email).orElse(null);

        if (user == null) {
            return "redirect:/auth/login"; // 🔹 Si no existe en la BD, redirigir
        }

        // 🔹 Obtener la ruta real del proyecto para guardar imágenes
        String projectRoot = System.getProperty("user.dir"); // Ruta base del proyecto
        String uploadDir = projectRoot + File.separator + "uploads"; // 📂 Carpeta donde se guardarán las imágenes

        File uploadFolder = new File(uploadDir);
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs(); // 🔹 Crear la carpeta si no existe
        }

        // 🔹 Generar un nombre único para la imagen
        String fileExtension = image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf("."));
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

        // 🔹 Ruta completa donde se guardará el archivo
        Path destinationPath = Paths.get(uploadDir, uniqueFilename);
        File destinationFile = destinationPath.toFile();

        try {
            image.transferTo(destinationFile); // 🔹 Guardar la imagen en el servidor
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/auth/profile?error=upload_failed"; // 🔹 Manejar error
        }

        // 🔹 Guardar la URL en la base de datos (ruta relativa para el frontend)
        String imageUrl = "/uploads/" + uniqueFilename;

        Post post = new Post();
        post.setImageUrl(imageUrl);
        post.setDescription(description);
        post.setUser(user);
        postService.createPost(post);

        return "redirect:/auth/profile"; // 🔹 Redirigir al perfil después de subir la publicación
    }

}
