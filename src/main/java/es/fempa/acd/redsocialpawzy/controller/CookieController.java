package es.fempa.acd.redsocialpawzy.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador para gestionar el consentimiento de cookies en la aplicación.
 */
@Controller
@RequestMapping("/cookies") // Define la ruta base para todas las peticiones relacionadas con cookies.
public class CookieController {

    /**
     * Muestra la página de inicio con el estado del consentimiento de las cookies.
     *
     * @param model   Modelo para pasar atributos a la vista.
     * @param session La sesión HTTP para recuperar el estado del consentimiento de cookies.
     * @return La vista de la página principal (index).
     */
    @GetMapping("/")
    public String showLandingPage(Model model, HttpSession session) {
        Boolean cookieConsent = (Boolean) session.getAttribute("cookieConsent");

        // Si el usuario aún no ha tomado una decisión sobre las cookies, se mantiene como null.
        if (cookieConsent == null) {
            model.addAttribute("cookieConsent", null);
        } else {
            model.addAttribute("cookieConsent", cookieConsent);
        }

        return "index";  // Asegúrate de que 'index.html' es la vista correcta.
    }

    /**
     * Maneja la aceptación de cookies por parte del usuario.
     * - Guarda la preferencia en la sesión.
     * - Crea una cookie para recordar la decisión del usuario.
     *
     * @param session  La sesión HTTP para almacenar la preferencia de cookies.
     * @param response La respuesta HTTP para enviar la cookie al cliente.
     * @return Redirige a la página de publicaciones tras aceptar las cookies.
     */
    @PostMapping("/acceptCookies")
    public String acceptCookies(HttpSession session, HttpServletResponse response) {
        session.setAttribute("cookieConsent", true);  // Guarda en la sesión que el usuario aceptó las cookies.

        // Creación de la cookie para el consentimiento
        Cookie cookie = new Cookie("cookieConsent", "true");
        cookie.setHttpOnly(true);  // Previene el acceso a la cookie desde JavaScript por seguridad.
        cookie.setSecure(true);    // Asegura que la cookie solo se envíe en conexiones HTTPS.
        cookie.setMaxAge(60 * 60 * 24); // La cookie dura 1 día.
        cookie.setPath("/");  // Disponible en todo el dominio de la aplicación.
        response.addCookie(cookie); // Agrega la cookie a la respuesta.

        return "redirect:/posts";  // Redirige al feed de publicaciones después de aceptar cookies.
    }

    /**
     * Maneja el rechazo de cookies por parte del usuario.
     * - Guarda la preferencia en la sesión.
     * - Elimina cualquier cookie previamente establecida.
     *
     * @param session  La sesión HTTP para almacenar la preferencia de cookies.
     * @param response La respuesta HTTP para eliminar la cookie del navegador.
     * @return Redirige al feed de publicaciones tras rechazar las cookies.
     */
    @PostMapping("/declineCookies")
    public String declineCookies(HttpSession session, HttpServletResponse response) {
        session.setAttribute("cookieConsent", false);  // Guarda en la sesión que el usuario rechazó las cookies.

        // Creación de una cookie con tiempo de vida 0 para eliminar cualquier cookie existente.
        Cookie cookie = new Cookie("cookieConsent", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(0);  // Expira inmediatamente la cookie.
        cookie.setPath("/");
        response.addCookie(cookie); // Se envía la cookie eliminada al cliente.

        return "redirect:/posts"; // Redirige al feed después de rechazar cookies.
    }

    /**
     * Muestra la página de Política de Cookies.
     *
     * @return La vista que contiene la política de cookies.
     */
    @GetMapping("/politica-cookies")
    public String politicaCookies() {
        return "politica-cookies";  // Thymeleaf buscará 'src/main/resources/templates/politica-cookies.html'
    }
}
