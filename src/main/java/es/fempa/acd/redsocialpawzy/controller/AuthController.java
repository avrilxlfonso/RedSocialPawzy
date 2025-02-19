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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Controlador encargado de gestionar la autenticación y el perfil de usuario.
 */
@Controller
@RequestMapping("/auth") // Define la ruta base para todas las solicitudes de autenticación
public class AuthController {

    @Autowired
    private UserService userService; // Servicio para manejar operaciones relacionadas con usuarios

    @Autowired
    private PostService postService; // Servicio para manejar operaciones relacionadas con publicaciones

    /**
     * Muestra la página de inicio de sesión.
     *
     * @param model Modelo para agregar atributos a la vista.
     * @return Vista de inicio de sesión.
     */
    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("authRequest", new AuthRequest()); // Agrega un objeto vacío para capturar datos de usuario
        return "login"; // Devuelve la vista de inicio de sesión
    }

    /**
     * Muestra la página de registro.
     *
     * @param model Modelo para agregar atributos a la vista.
     * @return Vista de registro.
     */
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("authRequest", new AuthRequest()); // Agrega un objeto vacío para capturar datos de usuario
        return "register"; // Devuelve la vista de registro
    }

    /**
     * Maneja el registro de nuevos usuarios.
     *
     * @param request Datos del usuario ingresados en el formulario.
     *Se mapean automáticamente con el objeto AuthRequest.
     * @param redirectAttributes Atributos para redirigir a la vista de registro o inicio de sesión.
     * @return Vista de inicio de sesión si el registro es exitoso, o vista de registro si hay un error.
     */
    @PostMapping("/register")
    public String register(@ModelAttribute AuthRequest request, RedirectAttributes redirectAttributes) {
        Optional<User> existingUser = userService.findByEmail(request.getEmail()); // Verifica si el usuario ya está registrado

        if (existingUser.isPresent()) { // Si el usuario ya existe, muestra un error
            redirectAttributes.addFlashAttribute("error", "⚠️ El correo ya está registrado.");
            return "redirect:/auth/register"; // Redirige a la vista de registro
        }

        userService.registerUser(request.getUsername(), request.getEmail(), request.getPassword()); // Registra el usuario en la base de datos
        redirectAttributes.addFlashAttribute("success", "✅ Registro exitoso. ¡Ahora inicia sesión!"); // Mensaje de éxito
        return "redirect:/auth/login"; // Redirige a la vista de inicio de sesión
    }

    /**
     * Maneja la eliminación de publicaciones por parte del usuario.
     *
     * @param postId ID de la publicación a eliminar.
     * @param model Modelo para agregar atributos a la vista.
     * @return Redirige a la página de perfil tras la eliminación.
     */
    @PostMapping("/deletePost/{postId}")
    public String deletePost(@PathVariable Long postId, Model model) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // Obtiene el usuario autenticado

        if (!(principal instanceof UserDetails)) { // Verifica si el usuario está autenticado
            return "redirect:/auth/login"; // Redirige a la página de inicio de sesión si no está autenticado
        }

        String email = ((UserDetails) principal).getUsername(); // Obtiene el correo del usuario autenticado
        User user = userService.findByEmail(email).orElse(null); // Busca al usuario en la base de datos

        if (user == null) { // Si el usuario no existe, redirige al login
            return "redirect:/auth/login";
        }

        Optional<Post> postOpt = Optional.ofNullable(postService.obtenerPostPorId(postId)); // Obtiene la publicación por su ID

        if (postOpt.isPresent()) { // Verifica si la publicación existe
            Post post = postOpt.get();

            if (post.getUser().getId().equals(user.getId())) { // Solo permite eliminar publicaciones propias
                postService.deletePost(postId);
            } else {
                model.addAttribute("error", "❌ No puedes eliminar esta publicación.");
            }
        }

        return "redirect:/auth/profile"; // Redirige al perfil del usuario
    }

    /**
     * Maneja el proceso de inicio de sesión.
     *
     * @param request Datos ingresados en el formulario de inicio de sesión.
     * @param session Sesión del usuario autenticado.
     * @param model Modelo para agregar atributos a la vista.
     * @return Redirige a la vista de perfil si las credenciales son correctas, de lo contrario, vuelve al login.
     */
    @PostMapping("/login")
    public String login(@ModelAttribute AuthRequest request, HttpSession session, Model model) {
        Optional<User> user = userService.findByEmail(request.getEmail()); // Busca el usuario en la base de datos

        if (user.isPresent() && userService.verifyPassword(request.getPassword(), user.get().getPassword())) {
            session.setAttribute("user", user.get()); // Guarda el usuario en la sesión
            return "redirect:/auth/profile"; // Redirige a la página de perfil
        } else {
            model.addAttribute("error", "❌ Credenciales incorrectas"); // Muestra mensaje de error
            return "login"; // Redirige a la página de inicio de sesión
        }
    }

    /**
     * Maneja el cierre de sesión del usuario.
     *
     * @param session Sesión actual del usuario.
     * @return Redirige a la página de inicio de sesión.
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Invalida la sesión del usuario
        return "redirect:/auth/login"; // Redirige a la página de inicio de sesión
    }

    /**
     * Muestra el perfil de un usuario específico por ID.
     *
     * @param id ID del usuario.
     * @param model Modelo para agregar atributos a la vista.
     * @return Vista del perfil del usuario.
     */
    @GetMapping("/profile/{id}")
    public String getUserProfileById(@PathVariable Long id, Model model) {
        User user = userService.findById(id).orElse(null); // Busca el usuario en la base de datos

        if (user == null) { // Si el usuario no existe, redirige al feed
            return "redirect:/posts";
        }

        List<Post> posts = postService.getPostsByUser(user); // Obtiene las publicaciones del usuario
        int totalLikes = postService.getTotalLikesByUser(user); // Obtiene el total de likes en sus publicaciones

        model.addAttribute("user", user);
        model.addAttribute("posts", posts);
        model.addAttribute("totalLikes", totalLikes);

        return "user"; // Retorna la vista del perfil del usuario
    }

    /**
     * Muestra el perfil del usuario autenticado.
     *
     * @param model Modelo para agregar atributos a la vista.
     * @return Vista del perfil del usuario autenticado.
     */
    @GetMapping("/profile")
    public String getUserProfile(Model model) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // Obtiene el usuario autenticado

        if (!(principal instanceof UserDetails)) { // Si el usuario no está autenticado, redirige al login
            return "redirect:/auth/login";
        }

        String email = ((UserDetails) principal).getUsername();
        User user = userService.findByEmail(email).orElse(null);
        List<Post> posts = postService.getPostsByUser(user);
        int totalLikes = postService.getTotalLikesByUser(user);

        if (user == null) { // Si el usuario no existe, redirige al login
            return "redirect:/auth/login";
        }

        model.addAttribute("posts", posts);
        model.addAttribute("user", user);
        model.addAttribute("totalLikes", totalLikes);

        return "profile"; // Retorna la vista de perfil
    }

    @PostMapping("/updateProfileImage")
    public String updateProfileImage(@RequestParam("image") MultipartFile image) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof UserDetails)) {
            return "redirect:/auth/login";
        }

        String email = ((UserDetails) principal).getUsername();
        User user = userService.findByEmail(email).orElse(null);

        if (user == null) {
            return "redirect:/auth/login";
        }

        String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";
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
        userService.updateProfileImage(user.getId(), imageUrl);

        return "redirect:/auth/profile";
    }
}
