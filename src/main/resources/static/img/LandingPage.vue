<template>
  <!-- Contenedor principal -->
  <div class="landing-container">
    <!-- Cabecera con logo y botones -->
    <header class="header">
      <div class="logo">
        <img src="../../content/images/animals.png" alt="Logo" class="logo-img" />
        <img src="../../content/images/letraLogo.png" alt="Logo" class="logo-img" />
      </div>
      <nav class="nav">
        <button @click="showLoginModal = true" class="nav-button">Iniciar sesión</button>
        <button @click="showRegisterModal = true" class="nav-button nav-button--outline">Registrarse</button>
      </nav>
    </header>

    <!-- 🏠 Sección de información adicional -->
    <section class="info-section">
      <h1>EXPLORA LAS  MEJORES FOTOS DE MASCOTAS</h1>
      <p>Únete a nuestra comunidad y comparte tus momentos favoritos con tus amigos y familiares.</p>
    </section>

    <!-- 🔥 Sección Estilo Pinterest -->
    <section class="hero-section">
      <div class="pinterest-grid">
        <div class="column">
          <img src="../../content/images/cat-1.jpg" height="353" width="735" alt="Mascota 1" class="pinterest-item">
          <img src="../../content/images/Bird.jpg" height="313" width="456" alt="Mascota 2" class="pinterest-item">
        </div>
        <div class="column">
          <img src="../../content/images/cat2.jpg" height="443" alt="Mascota 3" class="pinterest-item">
          <img src="../../content/images/erizo.jpg" height="323" alt="Mascota 4" class="pinterest-item">
        </div>
        <div class="column">
          <img src="../../content/images/nutria.jpg" height="293"alt="Mascota 5" class="pinterest-item">
          <img src="../../content/images/penguin.jpg" height="329" width="456" alt="Mascota 6" class="pinterest-item">
        </div>
      </div>
      <!-- 🔽 Indicador de Scroll Down -->
      <div class="scroll-down-indicator">
        <span class="arrow">↓</span>
      </div>
    </section>


    <!-- Modal de Inicio de Sesión -->
    <div v-if="showLoginModal" class="modal-overlay" @click.self="showLoginModal = false">
      <div class="modal-content">
        <button class="close-button" @click="showLoginModal = false">X</button>
        <h2>Te damos la bienvenida a Pawzy</h2>
        <p>Inicia sesión para seguir descubriendo</p>
        <form @submit.prevent="login">
          <label>Correo electrónico</label>
          <input v-model="loginEmail" type="email" required />
          <label>Contraseña</label>
          <input v-model="loginPassword" type="password" required />
          <button type="submit" class="btn-submit">Iniciar sesión</button>
        </form>
      </div>
    </div>

    <!-- Modal de Registro -->
    <div v-if="showRegisterModal" class="modal-overlay" @click.self="showRegisterModal = false">
      <div class="modal-content">
        <button class="close-button" @click="showRegisterModal = false">X</button>
        <h2>Te damos la bienvenida a Pawzy</h2>
        <p>¡Crea una cuenta y empieza a compartir!</p>
        <form @submit.prevent="registerUser">
          <label>Correo electrónico</label>
          <input v-model="registerEmail" type="email" required />
          <label>Contraseña</label>
          <input v-model="registerPassword" type="password" required />
          <label>Fecha de nacimiento</label>
          <input v-model="registerBirthdate" type="date" required />
          <button type="submit" class="btn-submit">Continuar</button>
        </form>
      </div>
    </div>

    <!-- 🌟 Sección de Colecciones de Mascotas -->
    <section class="collections-section">
      <div class="collections-container">
        <div class="collections-text">
          <h2 class="title2">Guarda las mejores fotos de mascotas</h2>
          <p class="title2">Recopila tus favoritos y vuelve a verlos después.</p>
          <button class="explore-button">Explorar</button>
        </div>
        <div class="collections-grid">
          <div class="collection-item large">
            <img src="https://placedog.net/500/400" alt="Perros adorables">
            <span class="collection-title">Mis perros favoritos</span>
          </div>
          <div class="collection-item">
            <img src="../../content/images/cat-1.jpg" alt="Gatos adorables">
            <span class="collection-title">Gatos curiosos</span>
          </div>
          <div class="collection-item">
            <img src="https://placebear.com/300/300" alt="Animales salvajes">
            <span class="collection-title">Animales salvajes</span>
          </div>
          <div class="collection-item">
            <img src="https://placebear.com/300/300" alt="Animales salvajes">
            <span class="collection-title">Animales salvajes</span>
          </div>
          <div class="collection-item">
            <img src="../../content/images/funnydogs.jpg" alt="Perritos">
            <span class="collection-title">Cachorros tiernos</span>
          </div>
          <div class="collection-item">
            <img src="../../content/images/dog.jpg" alt="Divertidos">
            <span class="collection-title">Fotos divertidas</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 🔻 Pie de Página -->
    <footer class="footer">
      <div class="footer-container">
        <div class="footer-links">
          <a href="#">Condiciones de servicio</a>
          <a href="#">Política de privacidad</a>
          <a href="#">Ayuda</a>
          <a href="#">Aplicación para iPhone</a>
          <a href="#">Aplicación para Android</a>
          <a href="#">Usuarios</a>
          <a href="#">Colecciones</a>
          <a href="#">Compras</a>
          <a href="#">Explorar</a>
        </div>
        <p class="footer-text">© 2024 Pawzy. Inspirado en Pinterest.</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref , onMounted} from 'vue';

// Manejo de modales
const showLoginModal = ref(false);
const showRegisterModal = ref(false);

// Datos de login
const loginEmail = ref('');
const loginPassword = ref('');

// Datos de registro
const registerEmail = ref('');
const registerPassword = ref('');
const registerBirthdate = ref('');

// Funciones (simuladas)
function login() {
  alert(`Iniciando sesión con: ${loginEmail.value}`);
  showLoginModal.value = false;
}

function registerUser() {
  alert(`Registrando usuario: ${registerEmail.value}`);
  showRegisterModal.value = false;
}


onMounted(() => {
  const observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          entry.target.classList.add("fade-in");
        }
      });
    },
    { threshold: 0.3 }
  );

  document.querySelectorAll(".collection-item").forEach((item) => {
    observer.observe(item);
  });
});

</script>

<style scoped>
*{
  box-sizing: border-box;
}
/* Contenedor principal */
.landing-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #ffffff;
}
h1{
  font-size: 55px;
  color: #6b4f4f;
  font-family: 'Roboto';
}

/* Cabecera */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  background-color: #6e3c3c;
}

.logo {
  display: flex;
  align-items: center;
}

.logo-img {
  width: 100px;
  height: auto;
  margin-right: 0.5rem;
}

.nav {
  display: flex;
  gap: 0.5rem;
}

.nav-button {
  padding: 0.5rem 1rem;
  cursor: pointer;
  border: none;
  border-radius: 4px;
  background-color: #a37c7c;
  color: #fff;
  font-weight: bold;
}

.nav-button--outline {
  background-color: transparent;
  border: 2px solid #a37c7c;
  color: #ffffff;
}

/* 🔥 Estilo Pinterest */
.hero-section {
  flex: 1;
  padding: 2rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  background-color: #ba4242;
}

.pinterest-grid {
  display: flex;
  gap: 15px;
  max-width: 1200px;
  margin: auto;
  justify-content: center;
}

.column {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.pinterest-item {
  width: 100%;
  border-radius: 10px;
  object-fit: cover;
  transition: transform 0.3s ease-in-out;
}

.pinterest-item:hover {
  transform: scale(1.05);
}

/* 🔽 Indicador Scroll Down */
.scroll-down-indicator {
  margin-top: 20px;
  font-size: 54px;
  font-weight: bold;
  color: #a37c7c;
  animation: bounce 1.5s infinite;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(10px); }
}

/* 🔥 Sección de información adicional */
.info-section {
  text-align: center;
  padding: 2rem;
  color: #4c0f0f;
}

.info-section h2 {
  font-size: 2rem;
}

.info-section p {
  font-size: 1.2rem;
}

/* Modal */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-content {
  background-color: #fff;
  width: 90%;
  max-width: 400px;
  padding: 2rem;
  position: relative;
  border-radius: 8px;
}

.close-button {
  position: absolute;
  top: 0.5rem;
  right: 0.5rem;
  background: transparent;
  border: none;
  font-size: 1.2rem;
  cursor: pointer;
}



.logo {
  display: flex;
  align-items: center;
}

.logo-img {
  width: 100px;
  height: auto;
  margin-right: 0.5rem;
}

/* 🔥 Estilo Pinterest */
.hero-section {
  flex: 1;
  padding: 2rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  background-color: #fff;
}

.pinterest-grid {
  display: flex;
  gap: 15px;
  max-width: 1200px;
  margin: auto;
  justify-content: center;
}

.column {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.pinterest-item {
  width: 100%;
  border-radius: 10px;
  object-fit: cover;
  transition: transform 0.3s ease-in-out;
}

.pinterest-item:hover {
  transform: scale(1.05);
}

/* 🔽 Indicador Scroll Down */
.scroll-down-indicator {
  margin-top: 20px;
  font-size: 24px;
  font-weight: bold;
  color: #a37c7c;
  animation: bounce 1.5s infinite;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(10px); }
}

/* 🔥 Estilos de los Modales */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-content {
  background-color: #fff;
  width: 90%;
  max-width: 400px;
  padding: 2rem;
  position: relative;
  border-radius: 8px;
}

.close-button {
  position: absolute;
  top: 0.5rem;
  right: 0.5rem;
  background: transparent;
  border: none;
  font-size: 1.2rem;
  cursor: pointer;
}

.modal-content form {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.modal-content label {
  font-weight: bold;
  color: #6b4f4f;
}

.modal-content input {
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.btn-submit {
  margin-top: 1rem;
  background-color: #a37c7c;
  color: #fff;
  border: none;
  border-radius: 4px;
  padding: 0.75rem;
  cursor: pointer;
}

/* 🔻 Estilos del Pie de Página */
.footer {
  background-color: #390d0d;
  padding: 1.5rem 0;
  border-top: 1px solid #ddd;
  text-align: center;
  margin-top: 20px;
}

.footer-container {
  max-width: 1200px;
  margin: auto;
}

.footer-links {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 15px;
  margin-bottom: 10px;
}

.footer-links a {
  text-decoration: none;
  color: #ffffff;
  font-size: 14px;
  transition: color 0.3s ease;
}

.footer-links a:hover {
  color: #c50228; /* Color rojo similar al de Pinterest */
}

.footer-text {
  font-size: 12px;
  color: #b53939;
}


/* 🌟 Sección de Colecciones */
.collections-section {
  background-color: #701921; /* Verde claro similar a la imagen */
  padding: 4rem 2rem;
  display: flex;
  justify-content: center;
}

.collections-container {
  max-width: 1200px;
  display: flex;
  align-items: center;
  gap: 3rem;
}

/* 📜 Texto de la sección */
.collections-text {
  max-width: 40%;
}

.collections-text h2 {
  font-size: 2rem;
  color: #ffffff; /* Verde oscuro */
}

.collections-text p {
  font-size: 1.2rem;
  color: #c55965;
  margin-bottom: 1rem;
}

.explore-button {
  background-color: #2e0b10; /* Rojo Pinterest */
  color: #fff;
  border: none;
  padding: 10px 20px;
  font-size: 1rem;
  border-radius: 20px;
  cursor: pointer;
}

.explore-button:hover {
  background-color: #cd4f55;
}

/* 🖼️ Diseño de las colecciones */
.collections-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 15px;
}

.collection-item {
  position: relative;
  border-radius: 15px;
  overflow: hidden;
  width: 150px;
  height: 150px;
}

.collection-item.large {
  width: 300px;
  height: 300px;
}

.collection-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease-in-out;
}

.collection-item:hover img {
  transform: scale(1.05);
}

.collection-title {
  position: absolute;
  bottom: 10px;
  left: 10px;
  background-color: rgba(0, 0, 0, 0.6);
  color: white;
  padding: 5px 10px;
  font-size: 0.9rem;
  border-radius: 5px;
}

/* 🖱️ Efecto de desplazamiento suave */
html {
  scroll-behavior: smooth;
}

/* 🌟 Efecto de aparición suave */
.collection-item {
  opacity: 0;
  transform: translateY(20px);
  transition: opacity 0.6s ease-out, transform 0.6s ease-out;
}

.collection-item.fade-in {
  opacity: 1;
  transform: translateY(0);
}



</style>
