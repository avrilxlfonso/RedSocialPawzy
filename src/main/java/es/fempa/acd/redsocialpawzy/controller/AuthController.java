package es.fempa.acd.redsocialpawzy.controller;

import es.fempa.acd.redsocialpawzy.dto.AuthRequest;
import es.fempa.acd.redsocialpawzy.model.Post;
import es.fempa.acd.redsocialpawzy.model.User;
import es.fempa.acd.redsocialpawzy.service.PostService;
import es.fempa.acd.redsocialpawzy.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controller class for handling authentication-related requests.
 */
@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private PostService postService;

    /**
     * Displays the login page.
     *
     * @param model the model to add attributes to
     * @return the login page view
     */
    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("authRequest", new AuthRequest());
        return "login";
    }

    /**
     * Displays the registration page.
     *
     * @param model the model to add attributes to
     * @return the registration page view
     */
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("authRequest", new AuthRequest());
        return "register";
    }

    /**
     * Handles user registration.
     *
     * @param request the authentication request containing user details
     * @param model the model to add attributes to
     * @return the login page view if registration is successful, otherwise the registration page view
     */
    @PostMapping("/register")
    public String register(@ModelAttribute AuthRequest request, Model model) {
        Optional<User> existingUser = userService.findByEmail(request.getEmail());

        if (existingUser.isPresent()) {
            model.addAttribute("error", "⚠️ El correo ya está registrado.");
            return "register";
        }

        userService.registerUser(request.getUsername(), request.getEmail(), request.getPassword());
        model.addAttribute("success", "✅ Registro exitoso. ¡Ahora inicia sesión!");
        return "login";
    }

    /**
     * Handles post deletion.
     *
     * @param postId the ID of the post to delete
     * @param model the model to add attributes to
     * @return the profile page view
     */
    @PostMapping("/deletePost/{postId}")
    public String deletePost(@PathVariable Long postId, Model model) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof UserDetails)) {
            return "redirect:/auth/login";
        }

        String email = ((UserDetails) principal).getUsername();
        User user = userService.findByEmail(email).orElse(null);

        if (user == null) {
            return "redirect:/auth/login";
        }

        Optional<Post> postOpt = Optional.ofNullable(postService.obtenerPostPorId(postId));

        if (postOpt.isPresent()) {
            Post post = postOpt.get();

            if (post.getUser().getId().equals(user.getId())) {
                postService.deletePost(postId);
            } else {
                model.addAttribute("error", "❌ No puedes eliminar esta publicación.");
            }
        }

        return "redirect:/auth/profile";
    }

    /**
     * Handles user login.
     *
     * @param request the authentication request containing user details
     * @param session the HTTP session
     * @param model the model to add attributes to
     * @return the profile page view if login is successful, otherwise the login page view
     */
    @PostMapping("/login")
    public String login(@ModelAttribute AuthRequest request, HttpSession session, Model model) {
        Optional<User> user = userService.findByEmail(request.getEmail());

        if (user.isPresent() && userService.verifyPassword(request.getPassword(), user.get().getPassword())) {
            session.setAttribute("user", user.get());
            return "redirect:/auth/profile";
        } else {
            model.addAttribute("error", "❌ Credenciales incorrectas");
            return "login";
        }
    }

    /**
     * Handles user logout.
     *
     * @param session the HTTP session
     * @return the login page view
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/auth/login";
    }

    /**
     * Displays the profile page of a user by ID.
     *
     * @param id the ID of the user
     * @param model the model to add attributes to
     * @return the user profile page view
     */
    @GetMapping("/profile/{id}")
    public String getUserProfileById(@PathVariable Long id, Model model) {
        User user = userService.findById(id).orElse(null);

        if (user == null) {
            return "redirect:/posts";
        }

        List<Post> posts = postService.getPostsByUser(user);
        int totalLikes = postService.getTotalLikesByUser(user);

        model.addAttribute("user", user);
        model.addAttribute("posts", posts);
        model.addAttribute("totalLikes", totalLikes);

        return "user";
    }

    /**
     * Displays the profile page of the authenticated user.
     *
     * @param model the model to add attributes to
     * @return the profile page view
     */
    @GetMapping("/profile")
    public String getUserProfile(Model model) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof UserDetails)) {
            return "redirect:/auth/login";
        }

        String email = ((UserDetails) principal).getUsername();
        User user = userService.findByEmail(email).orElse(null);
        List<Post> posts = postService.getPostsByUser(user);
        int totalLikes = postService.getTotalLikesByUser(user);

        if (user == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("posts", posts);
        model.addAttribute("user", user);
        model.addAttribute("totalLikes", totalLikes);
        return "profile";
    }
}