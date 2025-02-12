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
 * Controller class for handling comment-related requests.
 */
@Controller
@RequestMapping("/comentarios")
public class ComentarioController {

    @Autowired
    private ComentarioService comentarioService;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    /**
     * Retrieves comments for a specific post.
     *
     * @param postId the ID of the post
     * @param model the model to add attributes to
     * @return the view with the comments
     */
    @GetMapping("/{postId}")
    public String obtenerComentarios(@PathVariable Long postId, Model model) {
        Post post = postService.obtenerPostPorId(postId);
        List<Comentario> comentarios = comentarioService.obtenerComentariosPorPost(post);
        model.addAttribute("post", post);
        model.addAttribute("comentarios", comentarios);
        return "feed";
    }

    /**
     * Creates a new comment for a specific post.
     *
     * @param postId the ID of the post
     * @param contenido the content of the comment
     * @return the redirect URL after creating the comment
     */
    @PostMapping("/crear/{postId}")
    public String crearComentario(@PathVariable Long postId, @RequestParam("contenido") String contenido) {
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

        Comentario nuevoComentario = new Comentario();
        nuevoComentario.setContenido(contenido);
        nuevoComentario.setUser(user);
        nuevoComentario.setPost(post);

        comentarioService.guardarComentario(nuevoComentario);

        return "redirect:/posts";
    }

    /**
     * Creates a new comment for a specific post and redirects to the profile page.
     *
     * @param postId the ID of the post
     * @param contenido the content of the comment
     * @return the redirect URL after creating the comment
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

        Comentario nuevoComentario = new Comentario();
        nuevoComentario.setContenido(contenido);
        nuevoComentario.setUser(user);
        nuevoComentario.setPost(post);

        User autor = userService.findByPostId(postId);

        comentarioService.guardarComentario(nuevoComentario);

        return "redirect:/auth/profile/" + autor.getId();
    }
}