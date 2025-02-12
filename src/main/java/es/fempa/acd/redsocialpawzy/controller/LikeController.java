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

@Controller
@RequestMapping("/likes")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @GetMapping("/toggle/{postId}")
    public String toggleLike(@PathVariable Long postId) {
        // ✅ Obtener usuario autenticado
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof UserDetails)) {
            return "redirect:/auth/login";
        }

        String email = ((UserDetails) principal).getUsername();
        User user = userService.findByEmail(email).orElse(null);
        if (user == null) {
            return "redirect:/auth/login";
        }

        // ✅ Buscar el post en la BD
        Post post = postService.obtenerPostPorId(postId);
        if (post == null) {
            return "redirect:/posts";
        }

        // ✅ Alternar el like (si ya existe, se elimina; si no existe, se agrega)
        likeService.toggleLike(user, post);

        return "redirect:/posts"; // 🔹 Redirigir al feed
    }
}
