package es.fempa.acd.redsocialpawzy.controller;

import es.fempa.acd.redsocialpawzy.dto.AuthRequest;
import es.fempa.acd.redsocialpawzy.model.User;
import es.fempa.acd.redsocialpawzy.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody AuthRequest request) {
        Optional<User> existingUser = userService.findByEmail(request.getEmail());
        Map<String, String> response = new HashMap<>();

        if (existingUser.isPresent()) {
            response.put("error", "El correo ya está registrado.");
            return ResponseEntity.status(400).body(response);
        }

        userService.registerUser(request.getUsername(), request.getEmail(), request.getPassword());
        response.put("message", "Registro exitoso");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody AuthRequest request, HttpSession session) {
        Optional<User> user = userService.findByEmail(request.getEmail());

        Map<String, String> response = new HashMap<>();
        if (user.isPresent() && userService.verifyPassword(request.getPassword(), user.get().getPassword())) {
            session.setAttribute("user", user.get()); // Guardamos el usuario en la sesión
            System.out.println("✅ Usuario autenticado: " + user.get().getUsername()); // LOG para depuración
            response.put("message", "Login exitoso");
            return ResponseEntity.ok(response);
        } else {
            System.out.println("❌ Fallo en el login: Credenciales incorrectas"); // LOG para depuración
            response.put("error", "Credenciales incorrectas");
            return ResponseEntity.status(401).body(response);
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpSession session) {
        System.out.println("🔴 Cerrando sesión...");
        session.invalidate(); // Cierra la sesión
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logout exitoso");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserSession(HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (user == null) {
            System.out.println("⚠️ No hay usuario autenticado en la sesión.");
            return ResponseEntity.status(401).body("No hay usuario autenticado");
        }

        System.out.println("✅ Usuario autenticado: " + user.getUsername());
        return ResponseEntity.ok(user);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            System.out.println("⚠️ Intento de acceso al perfil sin autenticación");
            return ResponseEntity.status(401).body("No hay usuario autenticado");
        }

        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("id", user.getId());
        userProfile.put("username", user.getUsername());
        userProfile.put("email", user.getEmail());
        userProfile.put("profileImage", "/img/user.webp");

        System.out.println("✅ Perfil cargado para: " + user.getUsername());
        return ResponseEntity.ok(userProfile);
    }
}
