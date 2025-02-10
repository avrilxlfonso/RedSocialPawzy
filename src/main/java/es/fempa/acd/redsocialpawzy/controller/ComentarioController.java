package es.fempa.acd.redsocialpawzy.controller;

import es.fempa.acd.redsocialpawzy.model.Comentario;
import es.fempa.acd.redsocialpawzy.service.ComentarioService;
import es.fempa.acd.redsocialpawzy.model.Post;
import es.fempa.acd.redsocialpawzy.model.User;
import es.fempa.acd.redsocialpawzy.service.PostService;
import es.fempa.acd.redsocialpawzy.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/comentarios")
public class ComentarioController {

    private final ComentarioService comentarioService;
    private final PostService postService;
    private final UserService userService;

    public ComentarioController(ComentarioService comentarioService, PostService postService, UserService userService) {
        this.comentarioService = comentarioService;
        this.postService = postService;
        this.userService = userService;
    }

    // ✅ Obtener todos los comentarios y mostrarlos en el feed
    @GetMapping("/feed")
    public String getFeed(Model model, @AuthenticationPrincipal User user) {
        List<Post> posts = postService.getAllPosts();
        model.addAttribute("posts", posts);
        model.addAttribute("user", user);

        // ✅ Obtener comentarios organizados por postId
        Map<Long, List<Comentario>> comentariosPorPost = comentarioService.getAllComentarios()
                .stream()
                .collect(Collectors.groupingBy(Comentario::getPostId));
        model.addAttribute("comentariosPorPost", comentariosPorPost);

        return "feed"; // Renderiza la plantilla feed.html
    }

    // ✅ Crear un nuevo comentario
    @PostMapping("/crear")
    public String crearComentario(
            @RequestParam("postId") Long postId,
            @RequestParam("contenido") String contenido,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return "redirect:/auth/login"; // Redirigir si no está autenticado
        }

        Comentario comentario = new Comentario();
        comentario.setUserId(user.getId());
        comentario.setPostId(postId);
        comentario.setContenido(contenido);

        comentarioService.crearComentario(comentario);
        return "redirect:/comentarios/feed"; // Recargar la página del feed
    }
}
