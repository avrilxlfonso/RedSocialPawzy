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
            response.put("message", "Login exitoso");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Credenciales incorrectas");
            return ResponseEntity.status(401).body(response);
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpSession session) {
        session.invalidate(); // Cierra la sesión
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logout exitoso");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserSession(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).body("No hay usuario autenticado");
        }
        return ResponseEntity.ok(user);
    }
}
