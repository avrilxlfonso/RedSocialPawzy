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
 * Controller class for managing cookie consent.
 */
@Controller
@RequestMapping("/cookies")
public class CookieController {

    /**
     * Displays the landing page with the cookie consent status.
     *
     * @param model the model to add attributes to
     * @param session the HTTP session to retrieve the cookie consent status
     * @return the name of the view to render
     */
    @GetMapping("/")
    public String showLandingPage(Model model, HttpSession session) {
        // Verificar si ya hay un valor de consentimiento de cookies
        Boolean cookieConsent = (Boolean) session.getAttribute("cookieConsent");
        if (cookieConsent == null) {
            cookieConsent = false; // Por defecto, no se ha dado consentimiento
        }
        model.addAttribute("cookieConsent", cookieConsent);
        return "index";  // Asegúrate de que el archivo se llame "index.html"
    }

    /**
     * Handles the acceptance of cookies.
     * Sets the cookie consent attribute in the session and creates a cookie.
     *
     * @param session the HTTP session to set the cookie consent attribute
     * @param response the HTTP response to add the cookie to
     * @return a redirect to the main page
     */
    @PostMapping("/acceptCookies")
    public String acceptCookies(HttpSession session, HttpServletResponse response) {
        // Marcar que el usuario aceptó las cookies
        session.setAttribute("cookieConsent", true);

        // Crear la cookie para almacenar el consentimiento
        Cookie cookie = new Cookie("cookieConsent", "true");  // Guardamos el consentimiento en una cookie
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(60 * 60 * 24);  // Duración de la cookie de 1 día
        cookie.setPath("/");  // Para que la cookie esté disponible en todo el dominio
        response.addCookie(cookie);

        // Redirigir a la página principal después de aceptar las cookies
        return "redirect:/";  // Redirige a la página principal en lugar de al login
    }

    /**
     * Handles the decline of cookies.
     * Sets the cookie consent attribute in the session and removes the cookie.
     *
     * @param session the HTTP session to set the cookie consent attribute
     * @param response the HTTP response to remove the cookie from
     * @return a redirect to the main page
     */
    @PostMapping("/declineCookies")
    public String declineCookies(HttpSession session, HttpServletResponse response) {
        // Marcar que el usuario rechazó las cookies
        session.setAttribute("cookieConsent", false);

        // Eliminar la cookie de consentimiento
        Cookie cookie = new Cookie("cookieConsent", null);  // Eliminar la cookie
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(0);  // Expira la cookie inmediatamente
        cookie.setPath("/");  // Asegurar que se elimine en todo el dominio
        response.addCookie(cookie);

        // Redirigir al usuario a la página principal después de rechazar las cookies
        return "redirect:/";  // Redirige a la página principal en lugar de al login
    }
}