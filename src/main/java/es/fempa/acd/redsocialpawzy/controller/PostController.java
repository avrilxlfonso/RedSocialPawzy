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
import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Controlador para manejar las solicitudes relacionadas con las publicaciones.
 *
 * Este controlador gestiona la creación, edición, visualización y búsqueda de publicaciones.
 */
@Controller
@RequestMapping("/posts") // Define la ruta base para todas las solicitudes de publicaciones.
public class PostController {

    @Autowired
    private PostService postService; // Servicio para manejar operaciones de publicaciones.

    @Autowired
    private UserService userService; // Servicio para manejar operaciones de usuario.

    /**
     * Recupera todas las publicaciones y las muestra en la vista de feed.
     *
     * @param model Modelo de datos para la vista.
     * @return La vista con las publicaciones.
     */
    @GetMapping
    public String getPosts(Model model) {
        List<Post> posts = postService.getAllPublicPosts(); // Obtiene todas las publicaciones.
        model.addAttribute("posts", posts);

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            User user = userService.findByEmail(email).orElse(null);

            if (user == null) {
                return "redirect:/auth/login"; // Si el usuario no está autenticado, redirige a login.
            }

            model.addAttribute("user", user);
            return "feed"; // Retorna la vista del feed de publicaciones.
        }

        return "redirect:/auth/login";
    }

    /**
     * Recupera las publicaciones del usuario autenticado y las muestra en la vista de perfil.
     *
     * @param model Modelo de datos para la vista.
     * @return La vista con las publicaciones del usuario.
     */
    @GetMapping("/user")
    public String getUserPosts(Model model) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            User user = userService.findByEmail(email).orElse(null);

            if (user == null) {
                return "redirect:/auth/login";
            }

            List<Post> posts = postService.getPostsByUser(user);
            model.addAttribute("user", user);
            model.addAttribute("posts", posts);

            return "profile"; // Retorna la vista del perfil del usuario con sus publicaciones.
        }

        return "redirect:/auth/login";
    }

    /**
     * Busca publicaciones por nombre de usuario y las muestra en la vista de feed.
     *
     * @param username Nombre de usuario para buscar.
     * @param model Modelo de datos para la vista.
     * @return La vista con los resultados de la búsqueda.
     */
    @GetMapping("/search")
    public String searchPostsByUser(@RequestParam("username") String username, Model model) {
        List<Post> posts = postService.findPublicPostsByUsername(username);
        model.addAttribute("posts", posts);

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            User user = userService.findByEmail(email).orElse(null);
            if (user != null) {
                model.addAttribute("user", user);
            }
        }

        return "feed"; // Retorna la vista del feed con los resultados de la búsqueda.
    }

    /**
     * Edita la descripción de una publicación existente.
     *
     * @param postId ID de la publicación a editar.
     * @param description Nueva descripción de la publicación.
     * @param session Sesión HTTP para almacenar los posts actualizados.
     * @return Redirige a la vista de perfil con los cambios reflejados.
     */
    @PostMapping("/edit/{postId}")
    public String editPostDescription(@PathVariable Long postId, @RequestParam String description, HttpSession session) {
        postService.updatePostDescription(postId, description);

        // Recargar la sesión con los posts actualizados
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            User user = userService.findByEmail(email).orElse(null);
            if (user != null) {
                List<Post> posts = postService.getPostsByUser(user);
                session.setAttribute("userPosts", posts); // Guarda los posts en la sesión
            }
        }

        return "redirect:/posts/user"; // Redirige a la vista de perfil con la descripción editada.
    }

    /**
     * Crea una nueva publicación con una imagen y una descripción.
     *
     * @param image Imagen de la publicación.
     * @param description Descripción de la publicación.
     * @return Redirige a la vista de perfil tras crear la publicación.
     */
    @PostMapping("/create")
    public String createPost(
            @RequestParam("image") MultipartFile image,
            @RequestParam("description") String description,
            @RequestParam(name = "privatePost", defaultValue = "false") boolean privatePost) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof UserDetails)) {
            return "redirect:/auth/login";
        }

        String email = ((UserDetails) principal).getUsername();
        User user = userService.findByEmail(email).orElse(null);

        if (user == null) {
            return "redirect:/auth/login";
        }

        String projectRoot = System.getProperty("user.dir");
        String uploadDir = projectRoot + File.separator + "uploads";

        File uploadFolder = new File(uploadDir);
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs();
        }

        String fileExtension = image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf("."));
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

        Path destinationPath = Paths.get(uploadDir, uniqueFilename);
        File destinationFile = destinationPath.toFile();

        try {
            image.transferTo(destinationFile);
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/auth/profile?error=upload_failed";
        }

        String imageUrl = "/uploads/" + uniqueFilename;

        Post post = new Post();
        post.setImageUrl(imageUrl);
        post.setDescription(description);
        post.setUser(user);
        post.setPrivatePost(privatePost);
        postService.createPost(post);

        return "redirect:/auth/profile";
    }

    @PostMapping("/updateVisibility/{postId}")
    public String updatePostVisibility(@PathVariable Long postId, @RequestParam("privatePost") boolean privatePost) {
        postService.updatePostVisibility(postId, privatePost);
        return "redirect:/auth/profile"; // Redirige al perfil del usuario
    }


}
