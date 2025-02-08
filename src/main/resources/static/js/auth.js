document.getElementById("loginForm").addEventListener("submit", async function(event) {
    event.preventDefault();

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    try {
        const response = await fetch("http://localhost:8080/auth/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, password }),
            credentials: "include" // Importante para manejar sesiones
        });

        const data = await response.json();
        if (!response.ok) {
            throw new Error(data.error || "Error en el login");
        }

        document.getElementById("message").textContent = data.message;
        window.location.href = "feed.html"; // Redirigir a la página del feed
    } catch (error) {
        console.error("Error en el login:", error);
        document.getElementById("message").textContent = error.message;
    }

    function toggleMenu() {
        const menu = document.querySelector('.menu');
        menu.classList.toggle('show');
    }
});
