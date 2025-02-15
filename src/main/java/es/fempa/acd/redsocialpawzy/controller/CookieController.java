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
    // Muestra la página de inicio con el estado del consentimiento de las cookies
    @GetMapping("/")
    public String showLandingPage(Model model, HttpSession session) {
        Boolean cookieConsent = (Boolean) session.getAttribute("cookieConsent");
        if (cookieConsent == null) {
            model.addAttribute("cookieConsent", null);  // No se ha dado el consentimiento aún
        } else {
            model.addAttribute("cookieConsent", cookieConsent);  // El consentimiento ya fue tomado
        }
        return "index";  // Asegúrate de que este es el archivo correcto
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
        session.setAttribute("cookieConsent", true);  // Guardar en la sesión que el usuario aceptó

        // Crear la cookie para el consentimiento
        Cookie cookie = new Cookie("cookieConsent", "true");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(60 * 60 * 24);  // Duración de 1 día
        cookie.setPath("/");  // Disponible en todo el dominio
        response.addCookie(cookie);

        // Redirige al feed después de aceptar las cookies
        return "redirect:/posts";
    }

    /**
     * Handles the decline of cookies.
     * Sets the cookie consent attribute in the session and removes the cookie.
     *
     * @param session the HTTP session to set the cookie consent attribute
     * @param response the HTTP response to remove the cookie from
     * @return a redirect to the feed page
     */

    @PostMapping("/declineCookies")
    public String declineCookies(HttpSession session, HttpServletResponse response) {
        session.setAttribute("cookieConsent", false);  // Guardar en la sesión que el usuario rechazó

        // Eliminar la cookie de consentimiento
        Cookie cookie = new Cookie("cookieConsent", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(0);  // Expira la cookie inmediatamente
        cookie.setPath("/");  // Eliminar en todo el dominio
        response.addCookie(cookie);

        // Redirige al feed después de rechazar las cookies
        return "redirect:/posts";
    }

    /**
     * Handles the 'Politica de Cookies' page.
     * Returns the view for the cookie policy.
     *
     * @return the view name for the cookie policy page
     */
    @GetMapping("/politica-cookies")
    public String politicaCookies() {
        return "politica-cookies";  // Thymeleaf buscará 'src/main/resources/templates/politica-cookies.html'
    }
}
