package es.fempa.acd.redsocialpawzy.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controlador para la gestión de las preferencias de cookies de los usuarios.
 * Permite aceptar, rechazar o configurar el uso de cookies de análisis y publicidad.
 */
@Controller
@RequestMapping("/cookies") // Ruta base para la gestión de cookies.
public class CookiePreferencesController {

    /**
     * Muestra la página de preferencias de cookies.
     *
     * @param model   Modelo para pasar atributos a la vista.
     * @param session La sesión HTTP para recuperar las preferencias almacenadas.
     * @return La vista con las opciones de configuración de cookies.
     */
    @GetMapping("/preferences")
    public String showCookiePreferences(Model model, HttpSession session) {
        // Obtener preferencias almacenadas en la sesión
        Boolean analyticsConsent = (Boolean) session.getAttribute("analyticsConsent");
        Boolean adsConsent = (Boolean) session.getAttribute("adsConsent");

        // Si las preferencias no están definidas, se establecen en false por defecto.
        if (analyticsConsent == null) {
            analyticsConsent = false;
        }
        if (adsConsent == null) {
            adsConsent = false;
        }

        // Agregar las preferencias al modelo para mostrarlas en la vista
        model.addAttribute("analyticsConsent", analyticsConsent);
        model.addAttribute("adsConsent", adsConsent);
        return "cookies/preferences"; // Renderiza la vista con las preferencias de cookies
    }

    /**
     * Guarda las preferencias de cookies del usuario.
     *
     * @param session         La sesión HTTP donde se almacenarán las preferencias.
     * @param response        La respuesta HTTP para agregar las cookies al navegador.
     * @param analyticsConsent Consentimiento para cookies de análisis (opcional).
     * @param adsConsent      Consentimiento para cookies de publicidad (opcional).
     * @return Redirige al feed de publicaciones tras guardar las preferencias.
     */
    @PostMapping("/savePreferences")
    public String savePreferences(HttpSession session, HttpServletResponse response,
                                  @RequestParam(required = false) Boolean analyticsConsent,
                                  @RequestParam(required = false) Boolean adsConsent) {
        // Si no se selecciona ninguna opción, por defecto se establece en false.
        if (analyticsConsent == null) {
            analyticsConsent = false;
        }
        if (adsConsent == null) {
            adsConsent = false;
        }

        // Almacenar preferencias en la sesión del usuario
        session.setAttribute("analyticsConsent", analyticsConsent);
        session.setAttribute("adsConsent", adsConsent);

        // Crear cookies con las preferencias seleccionadas
        Cookie analyticsCookie = new Cookie("analyticsConsent", analyticsConsent.toString());
        Cookie adsCookie = new Cookie("adsConsent", adsConsent.toString());

        // Configurar las cookies para que persistan durante 1 día
        analyticsCookie.setMaxAge(60 * 60 * 24);
        adsCookie.setMaxAge(60 * 60 * 24);

        response.addCookie(analyticsCookie);
        response.addCookie(adsCookie);

        return "redirect:/posts"; // Redirige al feed de publicaciones después de guardar las preferencias.
    }

    /**
     * Permite al usuario rechazar todas las cookies, eliminándolas y almacenando la preferencia.
     *
     * @param session  La sesión HTTP donde se actualizarán las preferencias.
     * @param response La respuesta HTTP para eliminar las cookies del navegador.
     * @return Redirige al feed de publicaciones tras rechazar todas las cookies.
     */
    @PostMapping("/rejectAllCookies")
    public String rejectAllCookies(HttpSession session, HttpServletResponse response) {
        // Eliminar preferencias de la sesión
        session.removeAttribute("analyticsConsent");
        session.removeAttribute("adsConsent");
        session.setAttribute("cookieConsent", false); // Se almacena el rechazo explícito

        // Crear cookies vacías con expiración inmediata para eliminar cualquier rastro
        Cookie analyticsCookie = new Cookie("analyticsConsent", null);
        Cookie adsCookie = new Cookie("adsConsent", null);
        Cookie cookieConsent = new Cookie("cookieConsent", "false");

        analyticsCookie.setMaxAge(0); // Expira inmediatamente
        adsCookie.setMaxAge(0); // Expira inmediatamente
        cookieConsent.setMaxAge(60 * 60 * 24); // Se mantiene la preferencia de rechazo por 1 día

        response.addCookie(analyticsCookie);
        response.addCookie(adsCookie);
        response.addCookie(cookieConsent);

        return "redirect:/posts"; // Redirige al feed de publicaciones
    }

    /**
     * Permite al usuario aceptar todas las cookies disponibles.
     *
     * @param session  La sesión HTTP donde se actualizarán las preferencias.
     * @param response La respuesta HTTP para agregar las cookies del consentimiento.
     * @return Redirige al feed de publicaciones tras aceptar todas las cookies.
     */
    @PostMapping("/acceptAllCookies")
    public String acceptAllCookies(HttpSession session, HttpServletResponse response) {
        // Guardar la preferencia en la sesión
        session.setAttribute("analyticsConsent", true);
        session.setAttribute("adsConsent", true);

        // Crear cookies de consentimiento
        Cookie analyticsCookie = new Cookie("analyticsConsent", "true");
        Cookie adsCookie = new Cookie("adsConsent", "true");

        // Configurar las cookies para que persistan durante 1 día
        analyticsCookie.setMaxAge(60 * 60 * 24);
        adsCookie.setMaxAge(60 * 60 * 24);

        response.addCookie(analyticsCookie);
        response.addCookie(adsCookie);

        return "redirect:/posts"; // Redirige al feed de publicaciones
    }
}
