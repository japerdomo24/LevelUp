<template>
  <div class="profile-container">
    <div class="profile-card">
      <div class="profile-header">
        <h2>Mi Perfil</h2>
        <p>Gestiona tu información personal</p>
      </div>

      <div class="profile-content">
        <div class="user-info-section">
          <div class="avatar-section">
            <div class="avatar">
              {{ inicialesUsuario }}
            </div>
          </div>

          <div class="user-details">
            <div class="detail-item">
              <label>Nombre completo:</label>
              <span>{{ user.nombre }} {{ user.apellido }}</span>
            </div>
            <div class="detail-item">
              <label>Correo electrónico:</label>
              <span>{{ user.correo }}</span>
            </div>
          </div>
        </div>

        <form @submit.prevent="actualizarPerfil" class="profile-form">
          <h3>Editar Información</h3>

          <div class="form-row">
            <div class="form-group">
              <label for="firstName">Nombre</label>
              <input
                type="text"
                id="firstName"
                v-model="datosEdicion.nombre"
                placeholder="Tu nombre"
              />
            </div>

            <div class="form-group">
              <label for="lastName">Apellido</label>
              <input
                type="text"
                id="lastName"
                v-model="datosEdicion.apellido"
                placeholder="Tu apellido"
              />
            </div>
          </div>

          <div class="form-group">
            <label for="email">Correo Electrónico</label>
            <input
              type="email"
              id="email"
              v-model="datosEdicion.correo"
              placeholder="tu@email.com"
              disabled
            />
            <small class="form-hint">El correo no se puede cambiar.</small>
          </div>

          <div class="form-group">
            <label for="phone">Teléfono</label>
            <input type="tel" id="phone" v-model="datosEdicion.numero" placeholder="+58 412..." />
          </div>

          <div class="form-group">
            <label for="address">Dirección</label>
            <textarea
              id="address"
              v-model="datosEdicion.direccion"
              placeholder="Tu dirección completa"
              rows="3"
            ></textarea>
          </div>

          <div class="form-actions">
            <button type="button" class="btn-cancel" @click="cancelarEdicion">Cancelar</button>
            <button type="submit" class="btn-save" :disabled="loading">
              <span v-if="loading">Guardando...</span>
              <span v-else>Guardar Cambios</span>
            </button>
          </div>
        </form>

        <div class="password-section">
          <h3>Cambiar Contraseña</h3>
          <form @submit.prevent="cambiarClave" class="password-form">
            <div class="form-group">
              <label for="currentPassword">Contraseña Actual</label>
              <input
                type="password"
                id="currentPassword"
                v-model="datosClave.claveActual"
                placeholder="••••••••"
                required
              />
            </div>

            <div class="form-group">
              <label for="newPassword">Nueva Contraseña</label>
              <input
                type="password"
                id="newPassword"
                v-model="datosClave.claveNueva"
                placeholder="••••••••"
                minlength="6"
                required
              />
              <div class="password-strength" :class="fuerzaClave">
                Seguridad: {{ textoFuerzaClave }}
              </div>
            </div>

            <div class="form-group">
              <label for="confirmPassword">Confirmar Nueva Contraseña</label>
              <input
                type="password"
                id="confirmPassword"
                v-model="datosClave.confirmarClave"
                placeholder="••••••••"
                required
              />
              <div v-if="clavesNoCoinciden" class="error-message">Las contraseñas no coinciden</div>
            </div>

            <button type="submit" class="btn-change-password" :disabled="loadingPassword">
              <span v-if="loadingPassword">Cambiando...</span>
              <span v-else>Cambiar Contraseña</span>
            </button>
          </form>
        </div>

        <div class="danger-section">
          <h3>Zona de Peligro</h3>
          <p>Esta acción no se puede deshacer. Esto eliminará permanentemente tu cuenta.</p>
          <button class="btn-delete" @click="manejarEliminarCuenta">
            Eliminar mi cuenta
          </button>
        </div>

      </div>
    </div>
  </div>
</template>

<script>
import apiClient from '@/api/axiosConfig.js'
import Swal from 'sweetalert2'

export default {
  name: 'ProfilePage',
  props: {
    user: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      datosEdicion: {
        nombre: '',
        apellido: '',
        correo: '',
        numero: '',
        direccion: '',
      },
      datosClave: {
        claveActual: '',
        claveNueva: '',
        confirmarClave: '',
      },
      loading: false,
      loadingPassword: false,
    }
  },
  computed: {
    inicialesUsuario() {
      if (this.user && this.user.nombre && this.user.apellido) {
        return `${this.user.nombre[0]}${this.user.apellido[0]}`.toUpperCase()
      }
      if (this.user && this.user.nombre) {
        return this.user.nombre.substring(0, 2).toUpperCase()
      }
      return 'US'
    },
    clavesNoCoinciden() {
      return (
        this.datosClave.claveNueva &&
        this.datosClave.confirmarClave &&
        this.datosClave.claveNueva !== this.datosClave.confirmarClave
      )
    },
    fuerzaClave() {
      const password = this.datosClave.claveNueva
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
  },
  mounted() {
    this.inicializarDatos()
  },
  // --- MÉTODOS TRADUCIDOS ---
  methods: {
    inicializarDatos() {
      if (this.user) {
        this.datosEdicion.nombre = this.user.nombre || ''
        this.datosEdicion.apellido = this.user.apellido || ''
        this.datosEdicion.correo = this.user.correo || ''
        this.datosEdicion.numero = this.user.numero || ''
        this.datosEdicion.direccion = this.user.direccion || ''
      }
    },

    async actualizarPerfil() {
      const result = await Swal.fire({
        title: '¿Guardar cambios?',
        text: "Se actualizará tu información de perfil.",
        icon: 'question',
        showCancelButton: true,
        confirmButtonColor: '#0071e3',
        cancelButtonColor: '#6e6e73',
        confirmButtonText: 'Sí, guardar',
        cancelButtonText: 'Cancelar'
      })

      if (!result.isConfirmed) return;

      this.loading = true
      const token = localStorage.getItem('userToken')
      const apiPayload = {
        nombre: this.datosEdicion.nombre,
        apellido: this.datosEdicion.apellido,
        numero: this.datosEdicion.numero,
        direccion: this.datosEdicion.direccion,
      }

      try {
        const response = await apiClient.put(
          `/usuarios/${this.user.correo}`,
          apiPayload,
          { headers: { Authorization: `Bearer ${token}` } }
        )
        this.$emit('profile-updated', response.data) // App.vue muestra el modal de éxito

      } catch (error) {
        console.error('Error actualizando perfil:', error)
        Swal.fire({
          icon: 'error',
          title: 'Error al actualizar el perfil',
          confirmButtonColor: '#0071e3',
        })
      } finally {
        this.loading = false
      }
    },

    async cambiarClave() {
      if (this.clavesNoCoinciden) {
        Swal.fire({
          icon: 'warning',
          title: 'Las contraseñas no coinciden',
          confirmButtonColor: '#0071e3',
        })
        return
      }

      const result = await Swal.fire({
        title: '¿Cambiar contraseña?',
        icon: 'question',
        showCancelButton: true,
        confirmButtonColor: '#0071e3',
        cancelButtonColor: '#6e6e73',
        confirmButtonText: 'Sí, cambiar',
        cancelButtonText: 'Cancelar'
      })
      if (!result.isConfirmed) return;

      this.loadingPassword = true
      const token = localStorage.getItem('userToken')

      try {
        // 1. Verificar contraseña actual
        await apiClient.post('/usuarios/login', {
          correo: this.user.correo,
          clave: this.datosClave.claveActual
        })

        // 2. Cambiar la contraseña
        const apiPayload = { clavePlana: this.datosClave.claveNueva }
        await apiClient.put(
          `/usuarios/${this.user.correo}`,
          apiPayload,
          { headers: { Authorization: `Bearer ${token}` } }
        )

        this.datosClave = { claveActual: '', claveNueva: '', confirmarClave: '' }
        Swal.fire({
          icon: 'success',
          title: 'Contraseña cambiada',
          timer: 2000,
          showConfirmButton: false,
        })

      } catch (error) {
        if (error.response && error.response.status === 401) {
          Swal.fire({
            icon: 'error',
            title: 'Tu contraseña actual es incorrecta',
            confirmButtonColor: '#0071e3',
          })
        } else {
          console.error('Error cambiando contraseña:', error)
          Swal.fire({
            icon: 'error',
            title: 'Error al cambiar la contraseña',
            confirmButtonColor: '#0071e3',
          })
        }
      } finally {
        this.loadingPassword = false
      }
    },

    async manejarEliminarCuenta() {
      const token = localStorage.getItem('userToken');

      const { value: email } = await Swal.fire({
        title: '¿Estás absolutamente seguro?',
        text: `Esta acción es irreversible. Para confirmar, escribe tu correo: ${this.user.correo}`,
        icon: 'warning',
        input: 'email',
        inputPlaceholder: 'Escribe tu correo para confirmar',
        showCancelButton: true,
        confirmButtonColor: '#d33',
        cancelButtonColor: '#6e6e73',
        confirmButtonText: 'Sí, eliminar mi cuenta',
        cancelButtonText: 'Cancelar',
        inputValidator: (value) => {
          if (!value) {
            return '¡Necesitas escribir tu correo!'
          }
          if (value.toLowerCase() !== this.user.correo.toLowerCase()) {
            return '¡El correo no coincide!'
          }
        }
      })

      if (email) {
        try {
          await apiClient.delete(`/usuarios/${this.user.correo}`, {
            headers: { Authorization: `Bearer ${token}` }
          });

          this.$emit('logout', true); // Envía 'true' para saltar confirmación de logout

          await Swal.fire({
            title: 'Cuenta Eliminada',
            text: 'Tu cuenta ha sido eliminada permanentemente.',
            icon: 'success',
            confirmButtonColor: '#0071e3',
          });

        } catch (error) {
          console.error('Error eliminando cuenta:', error);
          Swal.fire({
            icon: 'error',
            title: 'No se pudo eliminar la cuenta',
            confirmButtonColor: '#0071e3',
          });
        }
      }
    },

    cancelarEdicion() {
      this.inicializarDatos()
    },
  },
}
</script>

<style scoped>
.profile-container {
  display: flex;
  justify-content: center;
  padding: 40px 20px;
  min-height: 80vh;
}

.profile-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 40px;
  width: 100%;
  max-width: 800px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.profile-header {
  text-align: center;
  margin-bottom: 40px;
}

.profile-header h2 {
  font-size: 32px;
  font-weight: 700;
  color: #1d1d1f;
  margin-bottom: 8px;
}

.profile-header p {
  color: #6e6e73;
  font-size: 16px;
}

.user-info-section {
  display: flex;
  gap: 40px;
  margin-bottom: 40px;
  padding-bottom: 30px;
  border-bottom: 1px solid #e5e5e7;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}

.avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #0071e3, #0077ed);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  font-weight: 700;
}

.user-details {
  flex: 1;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f7;
}

.detail-item label {
  font-weight: 600;
  color: #1d1d1f;
}

.detail-item span {
  color: #6e6e73;
}

.profile-form,
.password-section,
.danger-section {
  margin-bottom: 40px;
  padding-bottom: 30px;
  border-bottom: 1px solid #e5e5e7;
}

.profile-form:last-child,
.password-section:last-child,
.danger-section:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}


.profile-form h3,
.password-section h3,
.danger-section h3 {
  font-size: 20px;
  font-weight: 600;
  color: #1d1d1f;
  margin-bottom: 20px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
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

.form-group input:disabled {
  background: #f5f5f7;
  color: #8e8e93;
  cursor: not-allowed;
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

.form-actions {
  display: flex;
  gap: 15px;
  justify-content: flex-end;
}

.btn-cancel {
  background: transparent;
  color: #6e6e73;
  border: 2px solid #d2d2d7;
  padding: 12px 24px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-cancel:hover {
  background: #f5f5f7;
}

.btn-save,
.btn-change-password {
  background: linear-gradient(135deg, #0071e3, #0077ed);
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-save:hover:not(:disabled),
.btn-change-password:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(0, 113, 227, 0.3);
}

.btn-save:disabled,
.btn-change-password:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.danger-section h3 {
  color: #d33;
}
.danger-section p {
  font-size: 14px;
  color: #6e6e73;
  margin-bottom: 20px;
}
.btn-delete {
  background: #d33;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}
.btn-delete:hover {
  background: #c82333;
  box-shadow: 0 10px 20px rgba(211, 51, 51, 0.3);
}


/* Responsive */
@media (max-width: 768px) {
  .profile-card {
    padding: 30px 20px;
  }

  .user-info-section {
    flex-direction: column;
    gap: 20px;
  }

  .form-row {
    grid-template-columns: 1fr;
  }

  .form-actions {
    flex-direction: column;
  }
}
</style>
