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
              @blur="validarNombre"
            />
            <div v-if="errores.nombre" class="error-message">
              {{ errores.nombre }}
            </div>
          </div>

          <div class="form-group">
            <label for="lastName">Apellido *</label>
            <input
              type="text"
              id="lastName"
              v-model="registerData.apellido"
              placeholder="Tu apellido"
              required
              @blur="validarApellido"
            />
            <div v-if="errores.apellido" class="error-message">
              {{ errores.apellido }}
            </div>
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
          <div v-if="errores.correo" class="error-message">
            {{ errores.correo }}
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
          <div class="password-strength" :class="fuerzaClave">
            Seguridad: {{ textoFuerzaClave }}
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
          <div v-if="clavesNoCoinciden" class="error-message">Las contraseñas no coinciden</div>
        </div>

        <div class="form-group">
          <label for="phone">Teléfono *</label>
          <input
            type="tel"
            id="phone"
            v-model="registerData.numero"
            placeholder="+58 412 123 4567"
            @blur="validarNumero"
            required
          />
          <div v-if="errores.numero" class="error-message">
            {{ errores.numero }}
          </div>
        </div>

        <div class="form-group">
          <label for="address">Dirección *</label>
          <textarea
            id="address"
            v-model="registerData.direccion"
            placeholder="Tu dirección completa"
            rows="2"
            @blur="validarDireccion"
            required
          ></textarea>
          <div v-if="errores.direccion" class="error-message">
            {{ errores.direccion }}
          </div>
        </div>

        <button
          type="submit"
          class="btn-register"
          :disabled="loading || clavesNoCoinciden || !esFormularioValido"
        >
          <span v-if="loading">Creando cuenta...</span>
          <span v-else>Crear Cuenta</span>
        </button>
      </form>

      <div class="register-footer">
        <p>
          ¿Ya tienes una cuenta?
          <a href="" @click.prevent="irALogin" class="link">Inicia sesión aquí</a>
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
      errores: {
        nombre: null,
        apellido: null,
        correo: null,
        numero: null,
        direccion: null,
      },
      loading: false,
    }
  },
  computed: {
    clavesNoCoinciden() {
      return (
        this.registerData.clavePlana &&
        this.registerData.confirmPassword &&
        this.registerData.clavePlana !== this.registerData.confirmPassword
      )
    },

    fuerzaClave() {
      const password = this.registerData.clavePlana
      if (!password) return 'weak'
      if (password.length < 6) return 'weak'
      if (password.length < 8) return 'medium'
      if (/[A-Z]/.test(password) && /[0-9]/.test(password) && /[^A-Za-z0-9]/.test(password)) {
        return 'strong'
      }
      return 'medium'
    },

    textoFuerzaClave() {
      const strengths = {
        weak: 'Débil',
        medium: 'Media',
        strong: 'Fuerte',
      }
      return strengths[this.fuerzaClave]
    },

    esFormularioValido() {
      // Revisa que no haya ningún mensaje de error activo
      return !this.errores.nombre &&
        !this.errores.apellido &&
        !this.errores.correo &&
        !this.errores.numero &&
        !this.errores.direccion;
    }
  },
  methods: {
    validarDominioEmail() {
      if (!this.registerData.correo) {
        this.errores.correo = 'El correo es obligatorio.';
        return;
      }
      const isValid = this.registerData.correo.endsWith('@ucab.edu.ve') ||
        this.registerData.correo.endsWith('@est.ucab.edu.ve');

      this.errores.correo = isValid ? null : 'Debe ser un correo @ucab.edu.ve o @est.ucab.edu.ve';
    },

    validarNombre() {
      if (!this.registerData.nombre) {
        this.errores.nombre = 'El nombre es obligatorio.';
        return;
      }
      const regex = /^[a-zA-ZáéíóúüñÁÉÍÓÚÜÑ ]+$/;
      this.errores.nombre = regex.test(this.registerData.nombre) ? null : 'El nombre solo debe contener letras.';
    },

    validarApellido() {
      if (!this.registerData.apellido) {
        this.errores.apellido = 'El apellido es obligatorio.';
        return;
      }
      const regex = /^[a-zA-ZáéíóúüñÁÉÍÓÚÜÑ ]+$/;
      this.errores.apellido = regex.test(this.registerData.apellido) ? null : 'El apellido solo debe contener letras.';
    },

    // --- ¡MÉTODO ACTUALIZADO! ---
    validarNumero() {
      // CAMBIO: Ahora es obligatorio
      if (!this.registerData.numero) {
        this.errores.numero = 'El teléfono es obligatorio.';
        return;
      }
      const regex = /^[0-9+\-() ]+$/;
      this.errores.numero = regex.test(this.registerData.numero) ? null : 'Formato de teléfono inválido.';
    },

    // --- ¡MÉTODO ACTUALIZADO! ---
    validarDireccion() {
      // CAMBIO: Ahora es obligatorio
      if (!this.registerData.direccion) {
        this.errores.direccion = 'La dirección es obligatoria.';
        return;
      }
      this.errores.direccion = this.registerData.direccion.length < 10 ? 'La dirección parece demasiado corta.' : null;
    },

    async manejarRegistro() {
      // 1. Ejecutar todas las validaciones
      this.validarNombre();
      this.validarApellido();
      this.validarDominioEmail();
      this.validarNumero();
      this.validarDireccion();

      // 2. Comprobar si hay algún error
      if (!this.esFormularioValido) {
        Swal.fire({
          icon: 'warning',
          title: 'Formulario Incompleto',
          text: 'Por favor, revisa los campos marcados en rojo.',
          confirmButtonColor: '#0071e3',
        });
        return;
      }

      if (this.clavesNoCoinciden) {
        Swal.fire({
          icon: 'warning',
          title: 'Contraseñas no coinciden',
          confirmButtonColor: '#0071e3',
        });
        return
      }

      // 3. Si todo está bien, enviar a la API
      this.loading = true
      const apiPayload = {
        correo: this.registerData.correo,
        clavePlana: this.registerData.clavePlana,
        rol: this.registerData.rol,
        nombre: this.registerData.nombre,
        apellido: this.registerData.apellido,
        numero: this.registerData.numero, // Ya está validado
        direccion: this.registerData.direccion, // Ya está validado
      }

      try {
        await apiClient.post(
          '/usuarios',
          apiPayload
        )

        await Swal.fire({
          icon: 'success',
          title: '¡Registro exitoso!',
          text: 'Por favor, inicia sesión para continuar.',
          confirmButtonColor: '#0071e3',
        })
        this.irALogin();

      } catch (error) {
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
      this.$emit('change-auth-mode', 'login')
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
