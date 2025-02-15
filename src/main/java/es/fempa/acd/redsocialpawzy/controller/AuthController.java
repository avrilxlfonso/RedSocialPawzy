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
@RequestMapping("/auth") // Define la ruta base para las solicitudes de autenticación
public class AuthController {

    @Autowired
    private UserService userService; // Servicio para manejar operaciones de usuario

    @Autowired
    private PostService postService; // Servicio para manejar operaciones de publicaciones

    /**
     * Displays the login page.
     *
     * @param model the model to add attributes to
     * @return the login page view
     */
    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("authRequest", new AuthRequest()); // Agrega un nuevo objeto AuthRequest al modelo
        return "login"; // Devuelve la vista de inicio de sesión
    }

    /**
     * Displays the registration page.
     *
     * @param model the model to add attributes to
     * @return the registration page view
     */
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("authRequest", new AuthRequest()); // Agrega un nuevo objeto AuthRequest al modelo
        return "register"; // Devuelve la vista de registro
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
        Optional<User> existingUser = userService.findByEmail(request.getEmail()); // Busca si el usuario ya existe

        if (existingUser.isPresent()) { // Si el usuario ya está registrado
            model.addAttribute("error", "⚠️ El correo ya está registrado."); // Agrega un mensaje de error al modelo
            return "register"; // Devuelve la vista de registro
        }

        // Registra al nuevo usuario
        userService.registerUser(request.getUsername(), request.getEmail(), request.getPassword());
        model.addAttribute("success", "✅ Registro exitoso. ¡Ahora inicia sesión!"); // Mensaje de éxito
        return "login"; // Devuelve la vista de inicio de sesión
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
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // Obtiene el usuario autenticado

        if (!(principal instanceof UserDetails)) { // Verifica si el usuario está autenticado
            return "redirect:/auth/login"; // Redirige a la página de inicio de sesión si no está autenticado
        }

        String email = ((UserDetails) principal).getUsername(); // Obtiene el correo del usuario autenticado
        User user = userService.findByEmail(email).orElse(null); // Busca el usuario por correo

        if (user == null) { // Si el usuario no existe
            return "redirect:/auth/login"; // Redirige a la página de inicio de sesión
        }

        Optional<Post> postOpt = Optional.ofNullable(postService.obtenerPostPorId(postId)); // Obtiene la publicación por ID

        if (postOpt.isPresent()) { // Si la publicación existe
            Post post = postOpt.get(); // Obtiene la publicación

            if (post.getUser().getId().equals(user.getId())) { // Verifica si el usuario es el propietario de la publicación
                postService.deletePost(postId); // Elimina la publicación
            } else {
                model.addAttribute("error", "❌ No puedes eliminar esta publicación."); // Mensaje de error si no es el propietario
            }
        }

        return "redirect:/auth/profile"; // Redirige a la página de perfil
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
        Optional<User> user = userService.findByEmail(request.getEmail()); // Busca el usuario por correo

        if (user.isPresent() && userService.verifyPassword(request.getPassword(), user.get().getPassword())) { // Verifica las credenciales
            session.setAttribute("user", user.get()); // Guarda el usuario en la sesión
            return "redirect:/auth/profile"; // Redirige a la página de perfil
        } else {
            model.addAttribute("error", "❌ Credenciales incorrectas"); // Mensaje de error si las credenciales son incorrectas
            return "login"; // Devuelve la vista de inicio de sesión
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
        session.invalidate(); // Invalida la sesión del usuario
        return "redirect:/auth/login"; // Redirige a la página de inicio de sesión
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
        User user = userService.findById(id).orElse(null); // Busca el usuario por ID

        if (user == null) { // Si el usuario no existe
            return "redirect:/posts"; // Redirige a la página de publicaciones
        }

        List<Post> posts = postService.getPostsByUser(user); // Obtiene las publicaciones del usuario
        int totalLikes = postService.getTotalLikesByUser(user); // Obtiene el total de likes del usuario

        model.addAttribute("user", user); // Agrega el usuario al modelo
        model.addAttribute("posts", posts); // Agrega las publicaciones al modelo
        model.addAttribute("totalLikes", totalLikes); // Agrega el total de likes al modelo

        return "user"; // Devuelve la vista de perfil del usuario
    }

    /**
     * Muestra la página de perfil del usuario autenticado.
     *
     * @param model the model to add attributes to
     * @return the profile page view
     */
    @GetMapping("/profile")
    public String getUserProfile(Model model) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // Obtiene el usuario autenticado

        if (!(principal instanceof UserDetails)) { // Verifica si el usuario está autenticado
            return "redirect:/auth/login"; // Redirige a la página de inicio de sesión si no está autenticado
        }

        String email = ((UserDetails) principal).getUsername(); // Obtiene el correo del usuario autenticado
        User user = userService.findByEmail(email).orElse(null); // Busca el usuario por correo
        List<Post> posts = postService.getPostsByUser(user); // Obtiene las publicaciones del usuario
        int totalLikes = postService.getTotalLikesByUser(user); // Obtiene el total de likes del usuario

        if (user == null) { // Si el usuario no existe
            return "redirect:/auth/login"; // Redirige a la página de inicio de sesión
        }

        model.addAttribute("posts", posts); // Agrega las publicaciones al modelo
        model.addAttribute("user", user); // Agrega el usuario al modelo
        model.addAttribute("totalLikes", totalLikes); // Agrega el total de likes al modelo
        return "profile"; // Devuelve la vista de perfil
    }
}