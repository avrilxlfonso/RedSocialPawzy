document.getElementById("registerForm").addEventListener("submit", async function(event) {
    event.preventDefault();

    const username = document.getElementById("username").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    try {
        const response = await fetch("http://localhost:8080/auth/register", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, email, password })
        });

        const data = await response.json();
        if (!response.ok) {
            throw new Error(data.error || "Error en el registro");
        }

        document.getElementById("message").textContent = data.message;
        window.location.href = "login.html"; // Redirigir al login después del registro
    } catch (error) {
        console.error("Error en el registro:", error);
        document.getElementById("message").textContent = error.message;
    }
});
