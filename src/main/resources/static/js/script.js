document.addEventListener("DOMContentLoaded", function () {
    const feedGrid = document.getElementById("feed-grid");

    const numColumns = window.innerWidth > 768 ? 4 : 2;
    const columns = Array.from({ length: numColumns }, () => document.createElement("div"));

    columns.forEach(col => {
        col.classList.add("feed-column");
        feedGrid.appendChild(col);
    });

    // 📸 Imágenes locales
    const localImages = [
        "img/Bird.jpg", "img/bird1.jpg", "img/5.jpg", "img/bunny2.jpg",
        "img/cat-1.jpg", "img/3.jpg", "img/cat2.jpg", "img/dog.jpg",
        "img/dog2.jpg", "img/dog3.jpg", "img/erizo.jpg", "img/fox.jpg",
        "img/2.jpg", "img/1.jpg", "img/hamsterpose2.jpg",
        "img/hamsterpose3.jpg", "img/nutria.jpg", "img/4.jpg"
    ];

    // 📥 APIs de imágenes (con API Key protegida)
    const catApiKey = "live_Nm6bVfMqfTDbrQJzldTBUnqCndZbJXa4ti2raXU38uruWk0dvtOdzDpOtD4vYEsI"; // 🔒 API Key de TheCatAPI
    const apis = [
        { 
            url: "https://dog.ceo/api/breeds/image/random/60", 
            key: "message" 
        }, // 60 imágenes de perros
        { 
            url: `https://api.thecatapi.com/v1/images/search?limit=60&api_key=${catApiKey}`, 
            key: "" 
        }, // 60 imágenes de gatos (usando API Key)
        { 
            url: "https://cataas.com/api/cats?limit=60", 
            key: "_id", 
            prefix: "https://cataas.com/cat/" 
        } // 60 imágenes de Cataas
    ];

    // 📌 Función para añadir imágenes
    function addImageToGrid(imgSrc) {
        const img = document.createElement("img");
        img.src = imgSrc;
        img.classList.add("feed-item");

        // Si la imagen no se carga, usar fallback
        img.onerror = function() {
            console.warn(`No se pudo cargar: ${imgSrc}, usando imagen local.`);
            img.src = "img/fallback.jpg"; // Imagen de respaldo
        };

        // Añadir a la columna con menos imágenes
        const smallestColumn = columns.reduce((minCol, currentCol) => {
            return currentCol.children.length < minCol.children.length ? currentCol : minCol;
        });

        smallestColumn.appendChild(img);
    }

    // 📷 Cargar imágenes locales
    localImages.forEach(imgSrc => addImageToGrid(imgSrc));

    // 🌐 Cargar imágenes de APIs con validación
    apis.forEach(api => {
        fetch(api.url)
            .then(response => response.json())
            .then(data => {
                let images = api.key ? data[api.key] : data; // Obtener imágenes
                images.forEach(item => {
                    let imgSrc = api.prefix ? `${api.prefix}${item[api.key]}` : item.url || item;
                    addImageToGrid(imgSrc);
                });
            })
            .catch(error => console.error(`Error cargando imágenes de ${api.url}:`, error));
    });
});
