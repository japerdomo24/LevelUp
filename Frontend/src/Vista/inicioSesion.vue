<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <h2>Iniciar Sesión</h2>
        <p>Bienvenido de vuelta a Level Up</p>
      </div>

      <form @submit.prevent="manejarLogin" class="login-form">
        <div class="form-group">
          <label for="email">Correo Electrónico</label>
          <input
            type="email"
            id="email"
            v-model="datosLogin.correo"
            placeholder="tu@email.com"
            required
          />
        </div>

        <div class="form-group">
          <label for="password">Contraseña</label>
          <input
            type="password"
            id="password"
            v-model="datosLogin.clave"
            placeholder="••••••••"
            required
          />
        </div>

        <div class="form-options">
          <label class="checkbox-label">
            <input type="checkbox" v-model="datosLogin.recordar" />
            <span class="checkmark"></span>
            Recordarme
          </label>
          <a href="#" class="forgot-password">¿Olvidaste tu contraseña?</a>
        </div>

        <button type="submit" class="btn-login" :disabled="loading">
          <span v-if="loading">Iniciando sesión...</span>
          <span v-else>Iniciar Sesión</span>
        </button>
      </form>

      <div class="login-footer">
        <p>
          ¿No tienes una cuenta?
          <a href="#" @click.prevent="irARegistro" class="link">Regístrate aquí</a>
        </p>
      </div>
    </div>
  </div>
</template>

<script>
import apiClient from '@/api/axiosConfig.js'
import Swal from 'sweetalert2'

export default {
  name: 'LoginPage',
  data() {
    return {
      datosLogin: { // <-- Traducido
        correo: '',
        clave: '',
        recordar: false, // <-- Traducido
      },
      loading: false,
    }
  },
  // --- MÉTODOS TRADUCIDOS ---
  methods: {
    async manejarLogin() { // <-- Traducido
      this.loading = true
      const apiPayload = {
        correo: this.datosLogin.correo,
        clave: this.datosLogin.clave,
      }

      try {
        const response = await apiClient.post(
          '/usuarios/login',
          apiPayload
        )
        // El éxito (modal) se maneja en App.vue
        this.$emit('login-success', response.data)

      } catch (error) {
        // Alerta CLICKABLE
        if (error.response && error.response.status === 401) {
          Swal.fire({
            icon: 'error',
            title: 'Credenciales inválidas',
            text: 'Verifica tu correo y contraseña.',
            confirmButtonColor: '#0071e3',
          })
        } else {
          Swal.fire({
            icon: 'error',
            title: 'Error al iniciar sesión',
            text: 'Por favor, intenta de nuevo más tarde.',
            confirmButtonColor: '#0071e3',
          })
        }

      } finally {
        this.loading = false
      }
    },

    irARegistro() { // <-- Traducido
      this.$emit('change-auth-mode', 'register')
    },
  },
}
</script>

<style scoped>
/* ... (Todos tus estilos <style scoped> permanecen exactamente igual) ... */
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 70vh;
  padding: 40px 20px;
}

.login-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 40px;
  width: 100%;
  max-width: 450px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h2 {
  font-size: 28px;
  font-weight: 700;
  color: #1d1d1f;
  margin-bottom: 8px;
}

.login-header p {
  color: #6e6e73;
  font-size: 16px;
}

.login-form {
  margin-bottom: 25px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #1d1d1f;
  font-size: 14px;
}

.form-group input {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #d2d2d7;
  border-radius: 12px;
  font-size: 16px;
  transition: all 0.3s ease;
  background: white;
}

.form-group input:focus {
  outline: none;
  border-color: #0071e3;
  box-shadow: 0 0 0 4px rgba(0, 113, 227, 0.1);
}

.form-hint {
  display: block;
  margin-top: 5px;
  font-size: 12px;
  color: #8e8e93;
  font-style: italic;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  font-size: 14px;
}

.checkbox-label {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #1d1d1f;
}

.checkbox-label input {
  margin-right: 8px;
}

.forgot-password {
  color: #0071e3;
  text-decoration: none;
  font-weight: 500;
}

.forgot-password:hover {
  text-decoration: underline;
}

.btn-login {
  width: 100%;
  background: linear-gradient(135deg, #0071e3, #0077ed);
  color: white;
  border: none;
  border-radius: 12px;
  padding: 14px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-bottom: 20px;
}

.btn-login:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(0, 113, 227, 0.3);
}

.btn-login:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.demo-info {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 15px;
  margin-top: 20px;
  border-left: 4px solid #0071e3;
}

.demo-info h4 {
  font-size: 14px;
  font-weight: 600;
  color: #1d1d1f;
  margin-bottom: 10px;
}

.demo-accounts {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.demo-account {
  font-size: 12px;
  color: #6e6e73;
  line-height: 1.4;
}

.demo-account strong {
  color: #1d1d1f;
}

.login-footer {
  text-align: center;
  border-top: 1px solid #d2d2d7;
  padding-top: 20px;
}

.login-footer p {
  color: #6e6e73;
  margin: 0;
}

.link {
  color: #0071e3;
  text-decoration: none;
  font-weight: 600;
}

.link:hover {
  text-decoration: underline;
}

/* Responsive */
@media (max-width: 480px) {
  .login-card {
    padding: 30px 20px;
  }

  .form-options {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }

  .demo-info {
    padding: 12px;
  }

  .demo-accounts {
    font-size: 11px;
  }
}
</style>
