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
 * Controller for managing cookie preferences.
 */
    @Controller
    @RequestMapping("/cookies")
    public class CookiePreferencesController {

        @GetMapping("/preferences")
        public String showCookiePreferences(Model model, HttpSession session) {
            // Retrieve the current cookie preferences
            Boolean analyticsConsent = (Boolean) session.getAttribute("analyticsConsent");
            Boolean adsConsent = (Boolean) session.getAttribute("adsConsent");

            // Default to false if no preference is set
            if (analyticsConsent == null) {
                analyticsConsent = false;
            }
            if (adsConsent == null) {
                adsConsent = false;
            }

            model.addAttribute("analyticsConsent", analyticsConsent);
            model.addAttribute("adsConsent", adsConsent);
            return "cookies/preferences";
        }

        @PostMapping("/savePreferences")
        public String savePreferences(HttpSession session, HttpServletResponse response,
                                      @RequestParam(required = false) Boolean analyticsConsent,
                                      @RequestParam(required = false) Boolean adsConsent) {
            if (analyticsConsent == null) {
                analyticsConsent = false; // Default to false if not provided
            }
            if (adsConsent == null) {
                adsConsent = false; // Default to false if not provided
            }

            // Store preferences in session
            session.setAttribute("analyticsConsent", analyticsConsent);
            session.setAttribute("adsConsent", adsConsent);

            // Create cookies
            response.addCookie(new Cookie("analyticsConsent", analyticsConsent.toString()));
            response.addCookie(new Cookie("adsConsent", adsConsent.toString()));

            return "redirect:/posts"; // Redirect after saving preferences
        }

    @PostMapping("/rejectAllCookies")
    public String rejectAllCookies(HttpSession session, HttpServletResponse response) {
        session.removeAttribute("analyticsConsent");
        session.removeAttribute("adsConsent");
        session.setAttribute("cookieConsent", false); // Se considera como rechazo

        Cookie analyticsCookie = new Cookie("analyticsConsent", null);
        Cookie adsCookie = new Cookie("adsConsent", null);
        Cookie cookieConsent = new Cookie("cookieConsent", "false");

        analyticsCookie.setMaxAge(0);
        adsCookie.setMaxAge(0);
        cookieConsent.setMaxAge(60 * 60 * 24); // Guardar la preferencia de rechazo por 1 día

        response.addCookie(analyticsCookie);
        response.addCookie(adsCookie);
        response.addCookie(cookieConsent);

        return "redirect:/posts";
    }

        @PostMapping("/acceptAllCookies")
        public String acceptAllCookies(HttpSession session, HttpServletResponse response) {
            session.setAttribute("analyticsConsent", true);
            session.setAttribute("adsConsent", true);

            response.addCookie(new Cookie("analyticsConsent", "true"));
            response.addCookie(new Cookie("adsConsent", "true"));

            return "redirect:/posts";
        }
    }
