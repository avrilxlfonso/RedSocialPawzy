document.addEventListener("DOMContentLoaded", async function () {
    const feedGrid = document.getElementById("feed-grid");

    // 📌 Definir columnas dinámicas según el tamaño de pantalla
    const numColumns = window.innerWidth > 768 ? 4 : 2;
    const columns = Array.from({ length: numColumns }, () => document.createElement("div"));

    columns.forEach(col => {
        col.classList.add("feed-column");
        feedGrid.appendChild(col);
    });

    // 📌 Lista de imágenes locales
    const localImages = [
        "img/Bird.jpg", "img/bird1.jpg", "img/bunny.jpg", "img/bunny2.jpg",
        "img/cat-1.jpg", "img/cat.jpg", "img/cat2.jpg", "img/dog.jpg",
        "img/dog2.jpg", "img/dog3.jpg", "img/erizo.jpg", "img/fox.jpg",
        "img/funnydogs.jpg", "img/hamsterpose.jpg", "img/hamsterpose2.jpg",
        "img/hamsterpose3.jpg", "img/nutria.jpg", "img/penguin.jpg"
    ];

    // 📌 APIs para imágenes y GIFs
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
            console.warn(`No se pudo cargar: ${imgSrc}, usando imagen local.`);
            img.src = "img/fallback.jpg"; // Imagen de respaldo
        };

        let minColIndex = 0;
        let minHeight = columns[0].offsetHeight;

        for (let i = 1; i < columns.length; i++) {
            if (columns[i].offsetHeight < minHeight) {
                minColIndex = i;
                minHeight = columns[i].offsetHeight;
            }
        }

        columns[minColIndex].appendChild(img);
    }

    // 📷 **Carga inicial de imágenes locales (mínimo 50 imágenes desde el inicio)**
    localImages.slice(0, 50).forEach(imgSrc => addImageToGrid(imgSrc));

    // 🌐 **Carga imágenes de APIs sin afectar la estructura**
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

    // 🖼️ **Cargar más imágenes cuando el usuario hace scroll hacia abajo**
    let loading = false;
    const observer = new IntersectionObserver(
        async (entries) => {
            const lastImage = entries[0];

            if (lastImage.isIntersecting && !loading) {
                loading = true;

                // 🚀 Cargar más imágenes sin que las anteriores salten
                try {
                    const moreImages = await fetch("https://dog.ceo/api/breeds/image/random/10")
                        .then(res => res.json())
                        .then(data => data.message);

                    moreImages.forEach(imgSrc => addImageToGrid(imgSrc));

                } catch (error) {
                    console.error("Error cargando más imágenes:", error);
                }

                setTimeout(() => { loading = false; }, 1500);
            }
        },
        { threshold: 0.5 }
    );

    observer.observe(feedGrid.lastElementChild);
});
