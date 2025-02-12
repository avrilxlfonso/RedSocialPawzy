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

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private PostService postService;

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("authRequest", new AuthRequest());
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("authRequest", new AuthRequest());
        return "register";
    }

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

    @PostMapping("/deletePost/{postId}")
    public String deletePost(@PathVariable Long postId, Model model) {
        // 🔹 Obtener usuario autenticado desde Spring Security
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof UserDetails)) {
            return "redirect:/auth/login"; // Si no está autenticado, redirigir al login
        }

        String email = ((UserDetails) principal).getUsername();
        User user = userService.findByEmail(email).orElse(null);

        if (user == null) {
            return "redirect:/auth/login"; // Si el usuario no está en la BD, redirigir
        }

        // 🔹 Buscar el post
        Optional<Post> postOpt = Optional.ofNullable(postService.obtenerPostPorId(postId));

        if (postOpt.isPresent()) {
            Post post = postOpt.get();

            // ✅ Verificar que el usuario es el propietario
            if (post.getUser().getId().equals(user.getId())) {
                postService.deletePost(postId); // Eliminar la publicación
            } else {
                model.addAttribute("error", "❌ No puedes eliminar esta publicación.");
            }
        }

        return "redirect:/auth/profile"; // Volver al perfil tras la eliminación
    }



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

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/auth/login";
    }

    @GetMapping("/profile/{id}")
    public String getUserProfileById(@PathVariable Long id, Model model) {
        // 🔹 Buscar el usuario por ID
        User user = userService.findById(id).orElse(null);

        if (user == null) {
            return "redirect:/posts"; // 🔹 Si no existe, redirigir al feed
        }

        // 🔹 Obtener publicaciones del usuario
        List<Post> posts = postService.getPostsByUser(user);

        // 🔹 Pasar el usuario y publicaciones a la vista
        model.addAttribute("user", user);
        model.addAttribute("posts", posts);

        return "user"; // 🔹 Cargar la vista del perfil
    }


    @GetMapping("/profile")
    public String getUserProfile(Model model) {
        // 🔹 Obtener el usuario autenticado desde Spring Security
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof UserDetails)) {
            return "redirect:/auth/login"; // 🔹 Si no está autenticado, redirigir
        }


        String email = ((UserDetails) principal).getUsername();
        User user = userService.findByEmail(email).orElse(null);
        List<Post> posts = postService.getPostsByUser(user);

        if (user == null) {
            return "redirect:/auth/login"; // 🔹 Si no existe en la BD, redirigir
        }

        // 🔹 Pasar el usuario a la vista
        model.addAttribute("posts", posts);
        model.addAttribute("user", user);
        return "profile";
    }

}
