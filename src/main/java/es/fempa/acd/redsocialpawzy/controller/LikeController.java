package es.fempa.acd.redsocialpawzy.controller;

import es.fempa.acd.redsocialpawzy.model.Post;
import es.fempa.acd.redsocialpawzy.model.User;
import es.fempa.acd.redsocialpawzy.service.LikeService;
import es.fempa.acd.redsocialpawzy.service.PostService;
import es.fempa.acd.redsocialpawzy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador para gestionar las interacciones de "me gusta" en las publicaciones.
 */
@Controller
@RequestMapping("/likes") // Define la ruta base para las solicitudes relacionadas con likes.
public class LikeController {

    @Autowired
    private LikeService likeService; // Servicio para gestionar los "me gusta".

    @Autowired
    private PostService postService; // Servicio para gestionar publicaciones.

    @Autowired
    private UserService userService; // Servicio para gestionar usuarios.

    /**
     * Alterna el estado de "me gusta" en una publicación específica.
     * Si el usuario ya dio "me gusta", lo elimina. Si no, lo añade.
     *
     * @param postId ID de la publicación a la que se quiere dar o quitar "me gusta".
     * @return Redirige a la página de publicaciones después de la acción.
     */
    @GetMapping("/toggle/{postId}")
    public String toggleLike(@PathVariable Long postId) {
        // Obtener el usuario autenticado desde el contexto de seguridad de Spring Security.
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Si el usuario no está autenticado, redirige al login.
        if (!(principal instanceof UserDetails)) {
            return "redirect:/auth/login";
        }

        // Obtener el correo del usuario autenticado.
        String email = ((UserDetails) principal).getUsername();
        User user = userService.findByEmail(email).orElse(null);

        // Si no se encuentra el usuario en la base de datos, redirige al login.
        if (user == null) {
            return "redirect:/auth/login";
        }

        // Obtener la publicación por ID.
        Post post = postService.obtenerPostPorId(postId);

        // Si la publicación no existe, redirige a la página de publicaciones.
        if (post == null) {
            return "redirect:/posts";
        }

        // Alternar el estado de "me gusta" en la publicación.
        likeService.toggleLike(user, post);

        // Redirigir nuevamente a la página de publicaciones después de la acción.
        return "redirect:/posts";
    }
}
