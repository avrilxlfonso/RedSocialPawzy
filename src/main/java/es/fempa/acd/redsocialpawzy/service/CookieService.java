package es.fempa.acd.redsocialpawzy.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

/**
 * Service class for managing cookies.
 */
@Service
public class CookieService {

    /**
     * Creates a cookie and adds it to the HTTP response.
     *
     * @param response the HTTP response to add the cookie to
     * @param name the name of the cookie
     * @param value the value of the cookie
     * @param maxAge the maximum age of the cookie in seconds
     */
    public void createCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);  // For security
        cookie.setSecure(true);    // Only sent over HTTPS
        cookie.setMaxAge(maxAge);  // Duration of the cookie
        cookie.setPath("/");       // Available throughout the domain
        response.addCookie(cookie);
    }

    /**
     * Deletes a cookie by setting its maximum age to zero and adding it to the HTTP response.
     *
     * @param response the HTTP response to add the cookie to
     * @param name the name of the cookie to delete
     */
    public void deleteCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setHttpOnly(true);  // For security (not accessible by JavaScript)
        cookie.setSecure(true);    // Only sent over HTTPS
        cookie.setMaxAge(0);       // Expires immediately
        cookie.setPath("/");       // To ensure it is deleted throughout the domain
        response.addCookie(cookie);
    }
}