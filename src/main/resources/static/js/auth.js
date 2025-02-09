document.getElementById("loginForm").addEventListener("submit", async function(event) {
    event.preventDefault();

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    try {
        const response = await fetch("http://localhost:8080/auth/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, password }),
            credentials: "include" // Asegura que se envían las cookies de sesión
        });

        const data = await response.json();
        if (!response.ok) {
            throw new Error(data.error || "Error en el login");
        }

        // 🔹 Guardar el usuario en localStorage
        localStorage.setItem("userProfile", JSON.stringify({ username: data.username, email: data.email }));

        // ✅ Redirigir al feed después del login exitoso
        window.location.href = "feed.html";
    } catch (error) {
        console.error("Error en el login:", error);
        document.getElementById("message").textContent = error.message;
    }
});

// 🔹 Función para alternar el menú (si tienes un menú desplegable)
function toggleMenu() {
    const menu = document.querySelector('.menu');
    menu.classList.toggle('show');
}
