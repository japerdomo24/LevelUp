<template>
  <div class="register-container">
    <div class="register-card">
      <div class="register-header">
        <h2>Crear Cuenta</h2>
        <p>Únete a Level Up</p>
      </div>

      <form @submit.prevent="handleRegister" class="register-form">
        <div class="form-row">
          <div class="form-group">
            <label for="firstName">Nombre *</label>
            <input
              type="text"
              id="firstName"
              v-model="registerData.firstName"
              placeholder="Tu nombre"
              required
            />
          </div>

          <div class="form-group">
            <label for="lastName">Apellido *</label>
            <input
              type="text"
              id="lastName"
              v-model="registerData.lastName"
              placeholder="Tu apellido"
              required
            />
          </div>
        </div>

        <div class="form-group">
          <label for="email">Correo Electrónico *</label>
          <input
            type="email"
            id="email"
            v-model="registerData.email"
            placeholder="tu@email.com"
            required
          />
          <small class="form-hint">
            💡 Usa un email sin "admin" para cuenta de usuario normal
          </small>
        </div>

        <div class="form-group">
          <label for="password">Contraseña *</label>
          <input
            type="password"
            id="password"
            v-model="registerData.password"
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
          <input type="tel" id="phone" v-model="registerData.phone" placeholder="+1 234 567 8900" />
        </div>

        <div class="form-group">
          <label for="address">Dirección</label>
          <textarea
            id="address"
            v-model="registerData.address"
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

          <label class="checkbox-label">
            <input type="checkbox" v-model="registerData.newsletter" />
            <span class="checkmark"></span>
            Quiero recibir ofertas y novedades
          </label>
        </div>

        <!-- Demo Info -->
        <div class="demo-info">
          <h4>💡 Información Importante:</h4>
          <p>
            Las cuentas creadas aquí son de demostración. Todos los nuevos usuarios tendrán rol de
            "Usuario Normal".
          </p>
          <p>Para acceso de administrador, usa el login con: <strong>admin@levelup.com</strong></p>
        </div>

        <button type="submit" class="btn-register" :disabled="loading || passwordMismatch">
          <span v-if="loading">Creando cuenta...</span>
          <span v-else>Crear Cuenta</span>
        </button>
      </form>

      <div class="register-footer">
        <p>
          ¿Ya tienes una cuenta?
          <a href="#" @click.prevent="goToLogin" class="link">Inicia sesión aquí</a>
        </p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'RegisterPage',
  data() {
    return {
      registerData: {
        firstName: '',
        lastName: '',
        email: '',
        password: '',
        confirmPassword: '',
        phone: '',
        address: '',
        terms: false,
        newsletter: false,
      },
      loading: false,
    }
  },
  computed: {
    passwordMismatch() {
      return (
        this.registerData.password &&
        this.registerData.confirmPassword &&
        this.registerData.password !== this.registerData.confirmPassword
      )
    },

    passwordStrength() {
      const password = this.registerData.password
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
  methods: {
    async handleRegister() {
      if (this.passwordMismatch) {
        alert('Por favor, confirma tu contraseña correctamente.')
        return
      }

      if (!this.registerData.terms) {
        alert('Debes aceptar los términos y condiciones para continuar.')
        return
      }

      this.loading = true

      try {
        // Simular registro
        await new Promise((resolve) => setTimeout(resolve, 2000))

        // Todos los nuevos registros son usuarios normales
        const userData = {
          email: this.registerData.email,
          name: `${this.registerData.firstName} ${this.registerData.lastName}`,
          role: 'user', // Siempre usuario normal
          phone: this.registerData.phone,
          address: this.registerData.address,
        }

        // Emitir evento de registro exitoso
        this.$emit('register-success', userData)
      } catch (error) {
        console.error('Error en registro:', error)
        alert('Error al crear la cuenta. Intenta nuevamente.')
      } finally {
        this.loading = false
      }
    },

    goToLogin() {
      this.$emit('change-auth-mode', 'login')
    },
  },
}
</script>

<style scoped>
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
