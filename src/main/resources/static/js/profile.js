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
                console.log("🔴 Usuario no autenticado. Redirigiendo a login...");
                window.location.href = "login.html";
                return;
            }

            const user = await response.json();
            userName.textContent = user.username;
            userEmail.textContent = user.email;

            console.log("✅ Usuario cargado:", user);

            // 🔄 Cargar publicaciones después de obtener el usuario
            fetchUserPosts();
        } catch (error) {
            console.error("❌ Error al obtener el usuario:", error);
        }
    }

    // ✅ Obtener publicaciones del usuario autenticado
    async function fetchUserPosts() {
        try {
            const response = await fetch("http://localhost:8080/posts/user", {
                credentials: "include"
            });

            if (!response.ok) throw new Error("❌ No se pudieron cargar las publicaciones.");

            const posts = await response.json();
            userPosts.innerHTML = ""; // 🔄 Limpiar publicaciones previas

            if (posts.length === 0) {
                userPosts.innerHTML = "<p>No tienes publicaciones aún.</p>";
            }

            posts.forEach(post => {
                const postElement = document.createElement("div");
                postElement.classList.add("post-item");
                postElement.innerHTML = `
                    <img src="${post.imageUrl}" alt="Publicación">
                    <p>${post.description}</p>
                `;
                userPosts.appendChild(postElement);
            });

            console.log("✅ Publicaciones cargadas:", posts);
        } catch (error) {
            console.error("❌ Error al cargar publicaciones:", error);
        }
    }

    // ✅ Subir nueva publicación
    document.getElementById("uploadButton").addEventListener("click", async function () {
        const fileInput = document.getElementById("fileInput").files[0];
        const description = document.getElementById("descriptionInput").value.trim();

        if (!fileInput || !description) {
            alert("⚠️ Sube una imagen y agrega una descripción.");
            return;
        }

        const reader = new FileReader();
        reader.readAsDataURL(fileInput);
        reader.onloadend = async function () {
            const postData = {
                imageUrl: reader.result, // 📌 Guardar imagen como Base64
                description: description
            };

            try {
                const response = await fetch("http://localhost:8080/posts/create", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(postData),
                    credentials: "include" // ✅ Asegurar sesión
                });

                if (!response.ok) {
                    throw new Error("❌ Error al subir la publicación.");
                }

                alert("✅ Publicación subida con éxito");
                fetchUserPosts(); // 🔄 Cargar las publicaciones nuevamente
            } catch (error) {
                console.error("❌ Error al subir la publicación:", error);
            }
        };
    });

    // ✅ Cerrar sesión
    document.getElementById("logoutButton").addEventListener("click", async function () {
        await fetch("http://localhost:8080/auth/logout", { credentials: "include" });
        window.location.href = "login.html";
    });

    // 🔄 Cargar usuario al inicio
    fetchUserProfile();
});
