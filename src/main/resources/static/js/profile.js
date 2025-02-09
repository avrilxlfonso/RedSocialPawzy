document.addEventListener("DOMContentLoaded", async function () {
    const userName = document.getElementById("userName");
    const userEmail = document.getElementById("userEmail");
    const userPosts = document.getElementById("userPosts");

    // ✅ Obtener datos del usuario autenticado
    async function fetchUserProfile() {
        try {
            const response = await fetch("http://localhost:8080/auth/user", {
                credentials: "include"
            });

            if (!response.ok) {
                console.log("Redirigiendo a login...");
                window.location.href = "login.html";
                return;
            }

            const user = await response.json();
            userName.textContent = user.username;
            userEmail.textContent = user.email;
        } catch (error) {
            console.error("Error al obtener el usuario:", error);
        }
    }

    fetchUserProfile();
});
