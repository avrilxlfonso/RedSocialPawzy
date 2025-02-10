package es.fempa.acd.redsocialpawzy.controller;

import es.fempa.acd.redsocialpawzy.dto.AuthRequest;
import es.fempa.acd.redsocialpawzy.model.User;
import es.fempa.acd.redsocialpawzy.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

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

    @GetMapping("/profile")
    public String getUserProfile(Model model) {
        // 🔹 Obtener el usuario autenticado desde Spring Security
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof UserDetails)) {
            return "redirect:/auth/login"; // 🔹 Si no está autenticado, redirigir
        }

        String email = ((UserDetails) principal).getUsername();
        User user = userService.findByEmail(email).orElse(null);

        if (user == null) {
            return "redirect:/auth/login"; // 🔹 Si no existe en la BD, redirigir
        }

        // 🔹 Pasar el usuario a la vista
        model.addAttribute("user", user);
        return "profile";
    }

}
