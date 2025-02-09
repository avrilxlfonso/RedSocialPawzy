document.addEventListener("DOMContentLoaded", function () {
    const banner = document.getElementById("cookieBanner");

    // Si el usuario ya aceptó o rechazó las cookies, ocultamos el banner
    if (localStorage.getItem("cookiesAccepted") === "true" || localStorage.getItem("cookiesAccepted") === "false") {
        if (banner) banner.style.display = "none";
        return;
    }

    const acceptCookies = document.getElementById("acceptCookies");
    const declineCookies = document.getElementById("declineCookies");
    const moreInfoLink = document.querySelector(".cookie-banner a");

    function showNotification(message) {
        const notification = document.createElement("div");
        notification.classList.add("cookie-notification");
        notification.innerText = message;
        document.body.appendChild(notification);

        setTimeout(() => {
            notification.style.opacity = "0";
            setTimeout(() => notification.remove(), 500);
        }, 3000);
    }

    // Si el usuario no ha interactuado con las cookies, mostramos el banner
    if (!localStorage.getItem("cookiesAccepted")) {
        if (banner) banner.style.display = "block";
    }

    if (acceptCookies) {
        acceptCookies.addEventListener("click", function () {
            localStorage.setItem("cookiesAccepted", "true");
            if (banner) banner.style.display = "none";
            showNotification("✅ Has aceptado todas las cookies.");
        });
    }

    if (declineCookies) {
        declineCookies.addEventListener("click", function () {
            localStorage.setItem("cookiesAccepted", "false");
            if (banner) banner.style.display = "none";
            showNotification("❌ Has rechazado todas las cookies.");
        });
    }

    if (moreInfoLink) {
        moreInfoLink.addEventListener("click", function (event) {
            event.preventDefault(); // Evita la navegación inmediata
            window.location.href = "politica-cookies.html"; // Redirige manualmente
        });
    }
});