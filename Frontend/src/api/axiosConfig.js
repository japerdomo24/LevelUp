import axios from 'axios';
import Swal from 'sweetalert2';

// Creamos una instancia de Axios con la URL base
const apiClient = axios.create({
  baseURL: 'http://localhost:8080',
});

// Variable para asegurar que el modal de error se muestre solo una vez
let isHandlingError = false;

// --- EL INTERCEPTOR ---
// Revisa CADA respuesta de la API
apiClient.interceptors.response.use(

  // (1) Si la respuesta es exitosa (2xx), simplemente la deja pasar
  (response) => {
    return response;
  },

  // (2) Si la respuesta es un error...
  async (error) => {

    // Si no es un error de API, lo deja pasar
    if (!error.response) {
      return Promise.reject(error);
    }

    // --- ¡AQUÍ ESTÁ TU LÓGICA! ---
    // Si el error es 401 (No Autorizado) o 403 (Prohibido)
    // Y NO estamos ya mostrando un error (para evitar el "glitch")
    // Y NO es la página de login (para no bloquear el 'clave incorrecta')
    if (
      (error.response.status === 401 || error.response.status === 403) &&
      !isHandlingError &&
      !error.config.url.endsWith('/usuarios/login')
    ) {
      isHandlingError = true; // Bloquea futuros errores

      // --- ¡TU ALERTA CLICKABLE (UNA SOLA VEZ)! ---
      await Swal.fire({
        icon: 'error',
        title: 'Sesión Expirada',
        text: 'Tu sesión ha expirado. Por favor, inicia sesión de nuevo.',
        confirmButtonColor: '#0071e3',
        confirmButtonText: 'Entendido',
        allowOutsideClick: false, // No permite cerrarla haciendo clic fuera
      });

      // Limpia el localStorage (como en App.vue/logout)
      localStorage.removeItem('userToken');
      localStorage.removeItem('userData');

      // Recarga la página para forzar al usuario a la pantalla de login
      // Usamos location.reload() para reiniciar el estado de Vue
      window.location.reload();

    }

    // Rechaza la promesa para que el .catch() local (si existe) se active
    // para otros errores (como 409, 404, etc.)
    return Promise.reject(error);
  }
);

export default apiClient;
