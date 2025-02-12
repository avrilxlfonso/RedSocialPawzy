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

@Controller
@RequestMapping("/comentarios")
public class ComentarioController {

    @Autowired
    private ComentarioService comentarioService;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    // 🔹 Obtener comentarios de un post
    @GetMapping("/{postId}")
    public String obtenerComentarios(@PathVariable Long postId, Model model) {
        Post post = postService.obtenerPostPorId(postId);
        List<Comentario> comentarios = comentarioService.obtenerComentariosPorPost(post);
        model.addAttribute("post", post);
        model.addAttribute("comentarios", comentarios);
        return "feed"; // 🔹 Retorna la vista con los comentarios
    }

    // 🔹 Crear un nuevo comentario
    @PostMapping("/crear/{postId}")
    public String crearComentario(@PathVariable Long postId, @RequestParam("contenido") String contenido) {
        // ✅ Obtener usuario autenticado desde Spring Security
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof UserDetails)) {
            return "redirect:/auth/login"; // 🔹 Si no está autenticado, redirigir al login
        }

        String email = ((UserDetails) principal).getUsername();
        User user = userService.findByEmail(email).orElse(null);

        if (user == null) {
            return "redirect:/auth/login"; // 🔹 Si no existe en la BD, redirigir
        }

        // ✅ Buscar el post en la BD
        Post post = postService.obtenerPostPorId(postId);
        if (post == null) {
            return "redirect:/posts"; // 🔹 Redirigir si el post no existe
        }

        // ✅ Crear comentario asegurando que el usuario se asigne correctamente
        Comentario nuevoComentario = new Comentario();
        nuevoComentario.setContenido(contenido);
        nuevoComentario.setUser(user);  // ✅ Establecer el usuario
        nuevoComentario.setPost(post);

        System.out.println(user.toString());

        comentarioService.guardarComentario(nuevoComentario); // ✅ Guardar en BD

        return "redirect:/posts"; // 🔹 Volver al feed después de comentar
    }

    @PostMapping("/crearPerfil/{postId}")
    public String crearComentarioPerfil(@PathVariable Long postId, @RequestParam("contenido") String contenido) {
        // ✅ Obtener usuario autenticado desde Spring Security
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof UserDetails)) {
            return "redirect:/auth/login"; // 🔹 Si no está autenticado, redirigir al login
        }

        String email = ((UserDetails) principal).getUsername();
        User user = userService.findByEmail(email).orElse(null);

        if (user == null) {
            return "redirect:/auth/login"; // 🔹 Si no existe en la BD, redirigir
        }

        // ✅ Buscar el post en la BD
        Post post = postService.obtenerPostPorId(postId);
        if (post == null) {
            return "redirect:/posts"; // 🔹 Redirigir si el post no existe
        }

        // ✅ Crear comentario asegurando que el usuario se asigne correctamente
        Comentario nuevoComentario = new Comentario();
        nuevoComentario.setContenido(contenido);
        nuevoComentario.setUser(user);  // ✅ Establecer el usuario
        nuevoComentario.setPost(post);

        System.out.println(user.toString());

        User autor = userService.findByPostId(postId);

        comentarioService.guardarComentario(nuevoComentario); // ✅ Guardar en BD

        // ✅ Redirigir correctamente al perfil con la publicación específica
        return "redirect:/auth/profile/" + autor.getId();
    }

}
