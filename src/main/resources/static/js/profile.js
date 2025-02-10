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

    const fileInput = document.getElementById("fileInput");
    const uploadBox = document.querySelector(".upload-box");

    fileInput.addEventListener("change", function () {
        const file = fileInput.files[0];

        if (file) {
            const reader = new FileReader();

            reader.onload = function (e) {
                uploadBox.style.backgroundImage = `url(${e.target.result})`;
                uploadBox.style.backgroundSize = "cover";
                uploadBox.style.backgroundPosition = "center";
                uploadBox.style.border = "2px solid #8b5a2b"; // Cambio de borde al seleccionar imagen
                uploadBox.innerHTML = ""; // Elimina el texto para solo mostrar la imagen
            };

            reader.readAsDataURL(file);
        }
    });

    const userAvatar = document.getElementById("userAvatar");
    const fileInputProfile = document.createElement("input");
    fileInputProfile.type = "file";
    fileInputProfile.accept = "image/*";
    fileInputProfile.style.display = "none";

    const changeProfileButton = document.createElement("button");
    changeProfileButton.innerText = "Modificar";
    changeProfileButton.id = "changeProfileButton";

    // Insertar el botón debajo de la imagen de perfil
    userAvatar.insertAdjacentElement("afterend", changeProfileButton);

    // Abrir el selector de archivos al hacer clic en el botón
    changeProfileButton.addEventListener("click", function () {
        fileInputProfile.click();
    });

    fileInputProfile.addEventListener("change", async function () {
        const file = fileInputProfile.files[0];
        if (!file) return;

        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onloadend = async function () {
            const imageBase64 = reader.result; // Convertir imagen a Base64

            // 📌 Enviar imagen al backend para actualizar perfil
            try {
                const response = await fetch("http://localhost:8080/auth/updateProfileImage", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ profileImage: imageBase64 }),
                    credentials: "include"
                });

                if (!response.ok) throw new Error("❌ Error al actualizar imagen de perfil.");

                userAvatar.src = imageBase64; // Mostrar nueva imagen de perfil
                localStorage.setItem("userProfileImage", imageBase64); // Guardar en localStorage

                alert("✅ Imagen de perfil actualizada con éxito");
            } catch (error) {
                console.error("❌ Error al actualizar imagen de perfil:", error);
            }
        };
    });

    // ✅ Mostrar imagen de perfil guardada en localStorage si está disponible
    const savedProfileImage = localStorage.getItem("userProfileImage");
    if (savedProfileImage) {
        userAvatar.src = savedProfileImage;
    }

    // Insertar input en el documento (pero oculto)
    document.body.appendChild(fileInputProfile);
});
