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

/**
 * Controller class for handling post-related requests.
 */
@Controller
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    /**
     * Retrieves all posts and displays them in the feed view.
     *
     * @param model the model to add attributes to
     * @return the view with the posts
     */
    @GetMapping
    public String getPosts(Model model) {
        List<Post> posts = postService.getAllPosts();
        model.addAttribute("posts", posts);
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            User user = userService.findByEmail(email).orElse(null);

            if (user == null) {
                return "redirect:/auth/login";
            }

            model.addAttribute("user", user);
            return "feed";
        }

        return "redirect:/auth/login";
    }

    /**
     * Retrieves posts of the authenticated user and displays them in the profile view.
     *
     * @param model the model to add attributes to
     * @return the view with the user's posts
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
            model.addAttribute("posts", posts);  // Cambié 'userPosts' por 'posts'

            return "profile";
        }

        return "redirect:/auth/login";
    }


    /**
     * Searches for posts by username and displays them in the feed view.
     *
     * @param username the username to search for
     * @param model the model to add attributes to
     * @return the view with the search results
     */
    @GetMapping("/search")
    public String searchPostsByUser(@RequestParam("username") String username, Model model) {
        List<Post> posts = postService.findPostsByUsername(username);
        model.addAttribute("posts", posts);

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            User user = userService.findByEmail(email).orElse(null);
            if (user != null) {
                model.addAttribute("user", user);
            }
        }

        return "feed";
    }



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

        return "redirect:/posts/user"; // Redirige a la vista de perfil correctamente
    }





    @PostMapping("/create")
    public String createPost(
            @RequestParam("image") MultipartFile image,
            @RequestParam("description") String description) {

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
        postService.createPost(post);

        return "redirect:/auth/profile";
    }
}