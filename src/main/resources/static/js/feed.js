document.addEventListener("DOMContentLoaded", async function () {
    const feedGrid = document.getElementById("feed-grid");

    // 📌 APIs para imágenes
    const catApiKey = "live_Nm6bVfMqfTDbrQJzldTBUnqCndZbJXa4ti2raXU38uruWk0dvtOdzDpOtD4vYEsI";
    const dogApiKey = "live_fmdtm2fpk3rOHjuoS5MRZ3LA5LTqzaeGySnBkVtm2Vi9Fp4819Ppo8tVoeagwkl1";
    const giphyApiKey = "qKsm6AK9ZX5iP02bXik5nvoxFh8dqrtL";

    const apis = [
        { url: `https://api.thedogapi.com/v1/images/search?limit=50&api_key=${dogApiKey}`, key: "url" },
        { url: `https://api.thecatapi.com/v1/images/search?limit=50&api_key=${catApiKey}`, key: "url" },
        { url: "https://cataas.com/api/cats?limit=50", key: "_id", prefix: "https://cataas.com/cat/" },
        { url: `https://api.giphy.com/v1/gifs/search?api_key=${giphyApiKey}&q=cute+pets&limit=30&rating=g`, key: "images.original.url" },
        { url: `https://api.giphy.com/v1/gifs/search?api_key=${giphyApiKey}&q=funny+animals&limit=30&rating=g`, key: "images.original.url" }
    ];

    // 📌 Evitar imágenes repetidas
    const imageSet = new Set();

    function addImageToGrid(imgSrc) {
        if (imageSet.has(imgSrc)) return;
        imageSet.add(imgSrc);

        const img = document.createElement("img");
        img.src = imgSrc;
        img.classList.add("feed-item");
        img.style.opacity = "0"; // Inicialmente invisible
        img.loading = "lazy";

        img.onload = () => {
            requestAnimationFrame(() => {
                img.style.transition = "opacity 0.8s ease-in-out, transform 0.5s ease-in-out";
                img.style.opacity = "1"; // Aparece suavemente
                img.style.transform = "translateY(0)"; // Evita el salto de contenido
            });
        };

        img.onerror = function () {
            console.warn(`No se pudo cargar: ${imgSrc}`);
        };

        img.addEventListener("click", () => openModal(imgSrc));

        feedGrid.appendChild(img);
    }

    // 🌐 **Carga imágenes de APIs**
    try {
        const results = await Promise.allSettled(apis.map(api => fetch(api.url).then(res => res.json())));

        results.forEach((result, index) => {
            if (result.status === "fulfilled") {
                const api = apis[index];

                if (Array.isArray(result.value)) {
                    result.value.forEach(item => {
                        let imgSrc = api.prefix ? `${api.prefix}${item[api.key]}` : item[api.key];
                        addImageToGrid(imgSrc);
                    });
                } else if (result.value.data) {
                    result.value.data.forEach(item => {
                        let imgSrc = api.key.split('.').reduce((o, i) => o[i], item);
                        addImageToGrid(imgSrc);
                    });
                }
            } else {
                console.error(`Error en API ${apis[index].url}:`, result.reason);
            }
        });

    } catch (error) {
        console.error("Error general al cargar imágenes:", error);
    }

    // 🖼️ **Funcionalidad de Modal**
    function openModal(imgSrc) {
        const modal = document.getElementById("imageModal");
        document.getElementById("modalImage").src = imgSrc;
        document.getElementById("likeCount").textContent = "0"; // Reiniciar likes
        document.getElementById("commentSection").innerHTML = ""; // Reiniciar comentarios
        modal.style.display = "flex";
    }

    document.querySelector(".close-button").addEventListener("click", () => {
        document.getElementById("imageModal").style.display = "none";
    });

    // ❤️ **Función para "Me Gusta"**
    let likes = 0;
    document.getElementById("likeButton").addEventListener("click", () => {
        likes++;
        document.getElementById("likeCount").textContent = likes;
    });

    // 💬 **Agregar comentarios**
    document.getElementById("addCommentButton").addEventListener("click", () => {
        const commentInput = document.getElementById("commentInput");
        const commentText = commentInput.value.trim();

        if (commentText !== "") {
            const commentSection = document.getElementById("commentSection");
            const comment = document.createElement("p");
            comment.textContent = commentText;
            commentSection.appendChild(comment);
            commentInput.value = "";
        }
    });

});

function goToProfile() {
    const userProfile = localStorage.getItem("userProfile");

    if (userProfile) {
        window.location.href = "profile.html";
    } else {
        alert("Debes iniciar sesión primero.");
        window.location.href = "login.html";
    }
}

// 🚀 **Redirigir a `profile.html` si el usuario está autenticado**
document.querySelector(".feed-user").addEventListener("click", async function () {
    try {
        const response = await fetch("http://localhost:8080/auth/user", {
            credentials: "include" // Importante para enviar la cookie de sesión
        });

        if (!response.ok) {
            console.log("⚠️ Usuario no autenticado. Redirigiendo a login...");
            window.location.href = "login.html";
            return;
        }

        console.log("✅ Usuario autenticado. Redirigiendo a perfil...");
        window.location.href = "profile.html";
    } catch (error) {
        console.error("❌ Error al obtener usuario:", error);
    }
});

