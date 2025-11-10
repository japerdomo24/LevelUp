<template>
  <div class="register-container">
    <div class="register-card">
      <div class="register-header">
        <h2>Crear Cuenta</h2>
        <p>Únete a Level Up</p>
      </div>

      <form @submit.prevent="manejarRegistro" class="register-form">
        <div class="form-row">
          <div class="form-group">
            <label for="firstName">Nombre *</label>
            <input
              type="text"
              id="firstName"
              v-model="registerData.nombre"
              placeholder="Tu nombre"
              required
            />
          </div>

          <div class="form-group">
            <label for="lastName">Apellido *</label>
            <input
              type="text"
              id="lastName"
              v-model="registerData.apellido"
              placeholder="Tu apellido"
              required
            />
          </div>
        </div>

        <div class="form-group">
          <label for="email">Correo Electrónico (UCAB) *</label>
          <input
            type="email"
            id="email"
            v-model="registerData.correo"
            placeholder="tu.correo@ucab.edu.ve"
            required
            @blur="validarDominioEmail"
          />
          <div v-if="isEmailInvalid" class="error-message">
            El correo debe ser @ucab.edu.ve o @est.ucab.edu.ve
          </div>
        </div>

        <div class="form-group">
          <label for="password">Contraseña *</label>
          <input
            type="password"
            id="password"
            v-model="registerData.clavePlana"
            placeholder="••••••••"
            required
            minlength="6"
          />
          <div class="password-strength" :class="passwordStrength">
            Seguridad: {{ passwordStrengthText }}
          </div>
        </div>

        <div class="form-group">
          <label for="confirmPassword">Confirmar Contraseña *</label>
          <input
            type="password"
            id="confirmPassword"
            v-model="registerData.confirmPassword"
            placeholder="••••••••"
            required
          />
          <div v-if="passwordMismatch" class="error-message">Las contraseñas no coinciden</div>
        </div>

        <div class="form-group">
          <label for="phone">Teléfono</label>
          <input type="tel" id="phone" v-model="registerData.numero" placeholder="+58 412 123 4567" />
        </div>

        <div class="form-group">
          <label for="address">Dirección</label>
          <textarea
            id="address"
            v-model="registerData.direccion"
            placeholder="Tu dirección completa"
            rows="2"
          ></textarea>
        </div>

        <div class="form-options">
          <label class="checkbox-label required">
            <input type="checkbox" v-model="registerData.terms" required />
            <span class="checkmark"></span>
            Acepto los <a href="#" class="link">términos y condiciones</a> *
          </label>
        </div>

        <button
          type="submit"
          class="btn-register"
          :disabled="loading || passwordMismatch || isEmailInvalid"
        >
          <span v-if="loading">Creando cuenta...</span>
          <span v-else>Crear Cuenta</span>
        </button>
      </form>

      <div class="register-footer">
        <p>
          ¿Ya tienes una cuenta?
          <a href="#" @click.prevent="irALogin" class="link">Inicia sesión aquí</a>
        </p>
      </div>
    </div>
  </div>
</template>

<script>
import apiClient from '@/api/axiosConfig.js'
import Swal from 'sweetalert2'

export default {
  name: 'RegisterPage',
  data() {
    return {
      registerData: {
        nombre: '',
        apellido: '',
        correo: '',
        clavePlana: '',
        confirmPassword: '',
        numero: '',
        direccion: '',
        rol: 'user',
        terms: false,
      },
      loading: false,
      isEmailInvalid: false,
    }
  },
  computed: {
    passwordMismatch() {
      return (
        this.registerData.clavePlana &&
        this.registerData.confirmPassword &&
        this.registerData.clavePlana !== this.registerData.confirmPassword
      )
    },

    passwordStrength() {
      const password = this.registerData.clavePlana
      if (!password) return 'weak'
      if (password.length < 6) return 'weak'
      if (password.length < 8) return 'medium'
      if (/[A-Z]/.test(password) && /[0-9]/.test(password) && /[^A-Za-z0-9]/.test(password)) {
        return 'strong'
      }
      return 'medium'
    },

    passwordStrengthText() {
      const strengths = {
        weak: 'Débil',
        medium: 'Media',
        strong: 'Fuerte',
      }
      return strengths[this.passwordStrength]
    },
  },
  // --- MÉTODOS TRADUCIDOS ---
  methods: {
    validarDominioEmail() {
      if (!this.registerData.correo) {
        this.isEmailInvalid = false;
        return;
      }
      const isValid = this.registerData.correo.endsWith('@ucab.edu.ve') ||
        this.registerData.correo.endsWith('@est.ucab.edu.ve');
      this.isEmailInvalid = !isValid;
    },

    async manejarRegistro() {
      // Validaciones con Alertas Clickables
      this.validarDominioEmail();
      if (this.isEmailInvalid) {
        Swal.fire({
          icon: 'warning',
          title: 'Correo Inválido',
          text: 'Debe ser un correo con dominio @ucab.edu.ve o @est.ucab.edu.ve',
          confirmButtonColor: '#0071e3',
        });
        return;
      }
      if (this.passwordMismatch) {
        Swal.fire({
          icon: 'warning',
          title: 'Contraseñas no coinciden',
          confirmButtonColor: '#0071e3',
        });
        return
      }
      if (!this.registerData.terms) {
        Swal.fire({
          icon: 'warning',
          title: 'Términos y Condiciones',
          text: 'Debes aceptar los términos para continuar.',
          confirmButtonColor: '#0071e3',
        });
        return
      }

      this.loading = true
      const apiPayload = {
        correo: this.registerData.correo,
        clavePlana: this.registerData.clavePlana,
        rol: this.registerData.rol,
        nombre: this.registerData.nombre,
        apellido: this.registerData.apellido,
        numero: this.registerData.numero,
        direccion: this.registerData.direccion,
      }

      try {
        await apiClient.post(
          '/usuarios',
          apiPayload
        )

        // ¡Alerta CLICKABLE de éxito!
        await Swal.fire({
          icon: 'success',
          title: '¡Registro exitoso!',
          text: 'Por favor, inicia sesión para continuar.',
          confirmButtonColor: '#0071e3',
        })
        this.irALogin(); // Llama al método traducido

      } catch (error) {
        // Alertas CLICKABLE de error
        if (error.response && error.response.status === 409) {
          Swal.fire({
            icon: 'error',
            title: 'El correo ya está registrado',
            confirmButtonColor: '#0071e3',
          })
        } else {
          Swal.fire({
            icon: 'error',
            title: 'Error al crear la cuenta',
            confirmButtonColor: '#0071e3',
          })
        }
      } finally {
        this.loading = false
      }
    },

    irALogin() {
      this.$emit('change-auth-mode', 'register') // El evento 'register' le dice a App.vue que cambie a 'login'
    },
  },
}
</script>

<style scoped>
/* ... (Todos tus estilos <style scoped> permanecen exactamente igual) ... */
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 70vh;
  padding: 40px 20px;
}

.register-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 40px;
  width: 100%;
  max-width: 500px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.register-header h2 {
  font-size: 28px;
  font-weight: 700;
  color: #1d1d1f;
  margin-bottom: 8px;
}

.register-header p {
  color: #6e6e73;
  font-size: 16px;
}

.register-form {
  margin-bottom: 25px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
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

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #d2d2d7;
  border-radius: 12px;
  font-size: 16px;
  transition: all 0.3s ease;
  background: white;
  font-family: inherit;
}

.form-group input:focus,
.form-group textarea:focus {
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

.password-strength {
  font-size: 12px;
  margin-top: 5px;
  font-weight: 500;
}

.password-strength.weak {
  color: #ff3b30;
}

.password-strength.medium {
  color: #ff9500;
}

.password-strength.strong {
  color: #34c759;
}

.error-message {
  color: #ff3b30;
  font-size: 12px;
  margin-top: 5px;
  font-weight: 500;
}

.form-options {
  margin-bottom: 25px;
}

.checkbox-label {
  display: flex;
  align-items: flex-start;
  margin-bottom: 15px;
  cursor: pointer;
  color: #1d1d1f;
  font-size: 14px;
  line-height: 1.4;
}

.checkbox-label.required {
  font-weight: 600;
}

.checkbox-label input {
  margin-right: 10px;
  margin-top: 2px;
}

.demo-info {
  background: #fff3cd;
  border: 1px solid #ffeaa7;
  border-radius: 12px;
  padding: 15px;
  margin: 20px 0;
}

.demo-info h4 {
  font-size: 14px;
  font-weight: 600;
  color: #856404;
  margin-bottom: 8px;
}

.demo-info p {
  font-size: 12px;
  color: #856404;
  line-height: 1.4;
  margin-bottom: 5px;
}

.demo-info strong {
  color: #5a4a00;
}

.btn-register {
  width: 100%;
  background: linear-gradient(135deg, #34c759, #30d158);
  color: white;
  border: none;
  border-radius: 12px;
  padding: 14px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-register:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(52, 199, 89, 0.3);
}

.btn-register:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.register-footer {
  text-align: center;
  border-top: 1px solid #d2d2d7;
  padding-top: 20px;
}

.register-footer p {
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
  .register-card {
    padding: 30px 20px;
  }

  .form-row {
    grid-template-columns: 1fr;
  }

  .demo-info {
    padding: 12px;
  }

  .demo-info p {
    font-size: 11px;
  }
}
</style>
