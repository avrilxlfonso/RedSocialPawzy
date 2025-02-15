package es.fempa.acd.redsocialpawzy.controller;

import es.fempa.acd.redsocialpawzy.model.Comentario;
import es.fempa.acd.redsocialpawzy.model.Post;
import es.fempa.acd.redsocialpawzy.model.User;
import es.fempa.acd.redsocialpawzy.service.ComentarioService;
import es.fempa.acd.redsocialpawzy.service.PostService;
import es.fempa.acd.redsocialpawzy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador encargado de gestionar los comentarios en las publicaciones.
 */
@Controller
@RequestMapping("/comentarios")
public class ComentarioController {

    @Autowired
    private ComentarioService comentarioService; // Servicio para manejar los comentarios

    @Autowired
    private PostService postService; // Servicio para manejar las publicaciones

    @Autowired
    private UserService userService; // Servicio para manejar los usuarios

    /**
     * Obtiene los comentarios de una publicación específica.
     *
     * @param postId ID de la publicación
     * @param model  Modelo para agregar atributos a la vista
     * @return Vista con los comentarios asociados a la publicación
     */
    @GetMapping("/{postId}")
    public String obtenerComentarios(@PathVariable Long postId, Model model) {
        Post post = postService.obtenerPostPorId(postId); // Obtiene la publicación
        List<Comentario> comentarios = comentarioService.obtenerComentariosPorPost(post); // Obtiene los comentarios
        model.addAttribute("post", post);
        model.addAttribute("comentarios", comentarios);
        return "feed"; // Retorna la vista del feed
    }

    /**
     * Crea un nuevo comentario en una publicación.
     *
     * @param postId    ID de la publicación
     * @param contenido Contenido del comentario
     * @return Redirige al feed después de comentar
     */
    @PostMapping("/crear/{postId}")
    public String crearComentario(@PathVariable Long postId, @RequestParam("contenido") String contenido) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof UserDetails)) {
            return "redirect:/auth/login"; // Redirige si el usuario no está autenticado
        }

        String email = ((UserDetails) principal).getUsername();
        User user = userService.findByEmail(email).orElse(null);

        if (user == null) {
            return "redirect:/auth/login";
        }

        Post post = postService.obtenerPostPorId(postId);
        if (post == null) {
            return "redirect:/posts";
        }

        // Crea un nuevo comentario
        Comentario nuevoComentario = new Comentario();
        nuevoComentario.setContenido(contenido);
        nuevoComentario.setUser(user);
        nuevoComentario.setPost(post);

        comentarioService.guardarComentario(nuevoComentario); // Guarda el comentario en la BD

        return "redirect:/posts";
    }

    /**
     * Crea un comentario en una publicación y redirige al perfil del usuario.
     *
     * @param postId    ID de la publicación
     * @param contenido Contenido del comentario
     * @return Redirige al perfil del usuario propietario de la publicación
     */
    @PostMapping("/crearPerfil/{postId}")
    public String crearComentarioPerfil(@PathVariable Long postId, @RequestParam("contenido") String contenido) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof UserDetails)) {
            return "redirect:/auth/login";
        }

        String email = ((UserDetails) principal).getUsername();
        User user = userService.findByEmail(email).orElse(null);

        if (user == null) {
            return "redirect:/auth/login";
        }

        Post post = postService.obtenerPostPorId(postId);
        if (post == null) {
            return "redirect:/posts";
        }

        // Crea un nuevo comentario
        Comentario nuevoComentario = new Comentario();
        nuevoComentario.setContenido(contenido);
        nuevoComentario.setUser(user);
        nuevoComentario.setPost(post);

        User autor = userService.findByPostId(postId); // Obtiene el usuario propietario de la publicación

        comentarioService.guardarComentario(nuevoComentario); // Guarda el comentario en la BD

        return "redirect:/auth/profile/" + autor.getId(); // Redirige al perfil del usuario
    }

    /**
     * Muestra el formulario para editar un comentario.
     *
     * @param comentarioId ID del comentario a editar
     * @param model        Modelo para pasar datos a la vista
     * @return Vista del formulario de edición de comentario
     */
    @GetMapping("/editar/{comentarioId}")
    public String mostrarFormularioEdicion(@PathVariable Long comentarioId, Model model) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof UserDetails)) {
            return "redirect:/auth/login";
        }

        String email = ((UserDetails) principal).getUsername();
        User user = userService.findByEmail(email).orElse(null);

        Comentario comentario = comentarioService.obtenerComentarioPorId(comentarioId);

        // Si el comentario no existe o no pertenece al usuario autenticado, redirigir
        if (comentario == null || !comentario.getUser().equals(user)) {
            return "redirect:/posts";
        }

        model.addAttribute("comentario", comentario);
        return "editar-comentario"; // Vista donde se edita el comentario
    }

    /**
     * Procesa la edición de un comentario.
     *
     * @param comentarioId ID del comentario a editar
     * @param contenido    Nuevo contenido del comentario
     * @return Redirige al feed después de la edición
     */
    @PostMapping("/editar/{comentarioId}")
    public String editarComentario(@PathVariable Long comentarioId, @RequestParam("contenido") String contenido) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof UserDetails)) {
            return "redirect:/auth/login";
        }

        String email = ((UserDetails) principal).getUsername();
        User user = userService.findByEmail(email).orElse(null);

        Comentario comentario = comentarioService.obtenerComentarioPorId(comentarioId);

        // Verifica si el comentario pertenece al usuario autenticado
        if (comentario != null && comentario.getUser().equals(user)) {
            comentario.setContenido(contenido);
            comentarioService.guardarComentario(comentario);
        }

        return "redirect:/posts";
    }

    /**
     * Elimina un comentario si el usuario es el propietario.
     *
     * @param comentarioId ID del comentario a eliminar
     * @return Redirige al feed después de eliminar
     */
    @PostMapping("/eliminar/{comentarioId}")
    public String eliminarComentario(@PathVariable Long comentarioId) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof UserDetails)) {
            return "redirect:/auth/login";
        }

        String email = ((UserDetails) principal).getUsername();
        User user = userService.findByEmail(email).orElse(null);

        if (user == null) {
            return "redirect:/auth/login";
        }

        Comentario comentario = comentarioService.obtenerComentarioPorId(comentarioId);

        // Si el comentario no existe o no pertenece al usuario autenticado, no lo elimina
        if (comentario == null || !comentario.getUser().equals(user)) {
            return "redirect:/posts";
        }

        comentarioService.eliminarComentario(comentarioId); // Elimina el comentario de la BD

        return "redirect:/posts";
    }
}
