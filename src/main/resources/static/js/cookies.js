document.addEventListener("DOMContentLoaded", function () {
    const banner = document.getElementById("cookieBanner"); // Banner principal
    const modal = document.getElementById("cookieModal");
    const closeModal = document.querySelector(".close");
    const acceptCookies = document.getElementById("acceptCookies");
    const declineCookies = document.getElementById("rejectCookies");
    const saveCookies = document.getElementById("saveCookies");
    const tabButtons = document.querySelectorAll(".cookie-tab");
    const tabContents = document.querySelectorAll(".cookie-section");
    const analyticsCheckbox = document.getElementById("analyticsCookies");
    const adsCheckbox = document.getElementById("adsCookies");

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

    // Mostrar banner de cookies si no se ha aceptado
    if (banner && !localStorage.getItem("cookiesAccepted")) {
        banner.style.display = "block";
    }

    // Función para redirigir solo si estamos en privacidad-cookies.html
    function redirectIfOnCookiesPage() {
        if (window.location.pathname.includes("politica-cookies.html")) {
            setTimeout(() => {
                window.location.href = "index.html";
            }, 1000);
        }
    }

    // 📌 Manejo de botones en la PÁGINA DE POLÍTICA DE COOKIES
    if (acceptCookies) {
        acceptCookies.addEventListener("click", function () {
            localStorage.setItem("cookiesAccepted", "true");
            localStorage.setItem("analyticsCookies", "true");
            localStorage.setItem("adsCookies", "true");
            showNotification("✅ Se han aceptado todas las cookies.");
            redirectToIndex();
        });
    }

    if (declineCookies) {
        declineCookies.addEventListener("click", function () {
            localStorage.setItem("cookiesAccepted", "false");
            localStorage.setItem("analyticsCookies", "false");
            localStorage.setItem("adsCookies", "false");
            showNotification("❌ Se han rechazado todas las cookies.");
            redirectToIndex();
        });
    }

    if (saveCookies) {
        saveCookies.addEventListener("click", function () {
            let preferences = {
                analytics: analyticsCheckbox ? analyticsCheckbox.checked : false,
                ads: adsCheckbox ? adsCheckbox.checked : false
            };
            localStorage.setItem("cookiePreferences", JSON.stringify(preferences));
            localStorage.setItem("cookiesAccepted", "true"); // ⚠️ Para evitar que el banner aparezca
            showNotification("⚙️ Se han guardado tus preferencias.");
            redirectToIndex();
        });
    }

    // 📌 Redirigir de vuelta a index.html
    function redirectToIndex() {
        setTimeout(() => {
            window.location.href = "index.html";
        }, 1000);
    }

    // 📌 Manejo de navegación en pestañas de sidebar
    tabButtons.forEach(button => {
        button.addEventListener("click", function () {
            tabButtons.forEach(btn => btn.classList.remove("active"));
            this.classList.add("active");

            tabContents.forEach(content => content.classList.remove("active"));
            document.getElementById(this.dataset.tab).classList.add("active");
        });
    });
});
