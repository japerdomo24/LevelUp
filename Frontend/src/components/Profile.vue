<template>
  <div class="profile-container">
    <div class="profile-card">
      <div class="profile-header">
        <h2>Mi Perfil</h2>
        <p>Gestiona tu información personal</p>
      </div>

      <div class="profile-content">
        <!-- Información del usuario -->
        <div class="user-info-section">
          <div class="avatar-section">
            <div class="avatar">
              {{ userInitials }}
            </div>
            <button class="btn-change-avatar" @click="changeAvatar">Cambiar foto</button>
          </div>

          <div class="user-details">
            <div class="detail-item">
              <label>Nombre completo:</label>
              <span>{{ user.name }}</span>
            </div>
            <div class="detail-item">
              <label>Correo electrónico:</label>
              <span>{{ user.email }}</span>
            </div>
            <div class="detail-item">
              <label>Miembro desde:</label>
              <span>{{ joinDate }}</span>
            </div>
          </div>
        </div>

        <!-- Formulario de edición -->
        <form @submit.prevent="updateProfile" class="profile-form">
          <h3>Editar Información</h3>

          <div class="form-row">
            <div class="form-group">
              <label for="firstName">Nombre</label>
              <input
                type="text"
                id="firstName"
                v-model="editData.firstName"
                placeholder="Tu nombre"
              />
            </div>

            <div class="form-group">
              <label for="lastName">Apellido</label>
              <input
                type="text"
                id="lastName"
                v-model="editData.lastName"
                placeholder="Tu apellido"
              />
            </div>
          </div>

          <div class="form-group">
            <label for="email">Correo Electrónico</label>
            <input type="email" id="email" v-model="editData.email" placeholder="tu@email.com" />
          </div>

          <div class="form-group">
            <label for="phone">Teléfono</label>
            <input type="tel" id="phone" v-model="editData.phone" placeholder="+1 234 567 8900" />
          </div>

          <div class="form-group">
            <label for="address">Dirección</label>
            <textarea
              id="address"
              v-model="editData.address"
              placeholder="Tu dirección completa"
              rows="3"
            ></textarea>
          </div>

          <div class="form-actions">
            <button type="button" class="btn-cancel" @click="cancelEdit">Cancelar</button>
            <button type="submit" class="btn-save" :disabled="loading">
              <span v-if="loading">Guardando...</span>
              <span v-else>Guardar Cambios</span>
            </button>
          </div>
        </form>

        <!-- Cambio de contraseña -->
        <div class="password-section">
          <h3>Cambiar Contraseña</h3>
          <form @submit.prevent="changePassword" class="password-form">
            <div class="form-group">
              <label for="currentPassword">Contraseña Actual</label>
              <input
                type="password"
                id="currentPassword"
                v-model="passwordData.currentPassword"
                placeholder="••••••••"
              />
            </div>

            <div class="form-group">
              <label for="newPassword">Nueva Contraseña</label>
              <input
                type="password"
                id="newPassword"
                v-model="passwordData.newPassword"
                placeholder="••••••••"
                minlength="6"
              />
              <div class="password-strength" :class="passwordStrength">
                Seguridad: {{ passwordStrengthText }}
              </div>
            </div>

            <div class="form-group">
              <label for="confirmPassword">Confirmar Nueva Contraseña</label>
              <input
                type="password"
                id="confirmPassword"
                v-model="passwordData.confirmPassword"
                placeholder="••••••••"
              />
              <div v-if="passwordMismatch" class="error-message">Las contraseñas no coinciden</div>
            </div>

            <button type="submit" class="btn-change-password" :disabled="loadingPassword">
              <span v-if="loadingPassword">Cambiando...</span>
              <span v-else>Cambiar Contraseña</span>
            </button>
          </form>
        </div>

        <!-- Preferencias -->
        <div class="preferences-section">
          <h3>Preferencias</h3>
          <div class="preferences">
            <label class="checkbox-label">
              <input type="checkbox" v-model="preferences.newsletter" />
              <span class="checkmark"></span>
              Recibir newsletter con ofertas
            </label>

            <label class="checkbox-label">
              <input type="checkbox" v-model="preferences.notifications" />
              <span class="checkmark"></span>
              Notificaciones por email
            </label>

            <label class="checkbox-label">
              <input type="checkbox" v-model="preferences.sms" />
              <span class="checkmark"></span>
              Notificaciones por SMS
            </label>
          </div>
          <button class="btn-save-preferences" @click="savePreferences">
            Guardar Preferencias
          </button>
        </div>

        <!-- Estadísticas -->
        <div class="stats-section">
          <h3>Mis Estadísticas</h3>
          <div class="stats-grid">
            <div class="stat-card">
              <div class="stat-number">{{ ordersCount }}</div>
              <div class="stat-label">Pedidos Realizados</div>
            </div>
            <div class="stat-card">
              <div class="stat-number">${{ totalSpent }}</div>
              <div class="stat-label">Total Gastado</div>
            </div>
            <div class="stat-card">
              <div class="stat-number">{{ wishlistCount }}</div>
              <div class="stat-label">En Lista de Deseos</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
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
      editData: {
        firstName: '',
        lastName: '',
        email: '',
        phone: '',
        address: '',
      },
      passwordData: {
        currentPassword: '',
        newPassword: '',
        confirmPassword: '',
      },
      preferences: {
        newsletter: true,
        notifications: true,
        sms: false,
      },
      loading: false,
      loadingPassword: false,
      ordersCount: 5,
      totalSpent: '1,250.00',
      wishlistCount: 3,
      joinDate: 'Enero 2024',
    }
  },
  computed: {
    userInitials() {
      if (this.user && this.user.name) {
        return this.user.name
          .split(' ')
          .map((word) => word[0])
          .join('')
          .toUpperCase()
          .substring(0, 2)
      }
      return 'US'
    },

    passwordMismatch() {
      return (
        this.passwordData.newPassword &&
        this.passwordData.confirmPassword &&
        this.passwordData.newPassword !== this.passwordData.confirmPassword
      )
    },

    passwordStrength() {
      const password = this.passwordData.newPassword
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
  mounted() {
    this.initializeData()
  },
  methods: {
    initializeData() {
      // Inicializar datos de edición con la información del usuario
      if (this.user.name) {
        const nameParts = this.user.name.split(' ')
        this.editData.firstName = nameParts[0] || ''
        this.editData.lastName = nameParts.slice(1).join(' ') || ''
      }
      this.editData.email = this.user.email || ''
    },

    async updateProfile() {
      this.loading = true

      try {
        // Simular actualización
        await new Promise((resolve) => setTimeout(resolve, 1500))

        const updatedUser = {
          ...this.user,
          name: `${this.editData.firstName} ${this.editData.lastName}`.trim(),
          email: this.editData.email,
          phone: this.editData.phone,
          address: this.editData.address,
        }

        // Emitir evento de actualización
        this.$emit('profile-updated', updatedUser)
        alert('Perfil actualizado exitosamente!')
      } catch (error) {
        console.error('Error actualizando perfil:', error)
        alert('Error al actualizar el perfil. Intenta nuevamente.')
      } finally {
        this.loading = false
      }
    },

    async changePassword() {
      if (this.passwordMismatch) {
        alert('Por favor, confirma tu nueva contraseña correctamente.')
        return
      }

      this.loadingPassword = true

      try {
        // Simular cambio de contraseña
        await new Promise((resolve) => setTimeout(resolve, 1500))

        // Limpiar formulario
        this.passwordData = {
          currentPassword: '',
          newPassword: '',
          confirmPassword: '',
        }

        alert('Contraseña cambiada exitosamente!')
      } catch (error) {
        console.error('Error cambiando contraseña:', error)
        alert('Error al cambiar la contraseña. Verifica tu contraseña actual.')
      } finally {
        this.loadingPassword = false
      }
    },

    async savePreferences() {
      try {
        // Simular guardado de preferencias
        await new Promise((resolve) => setTimeout(resolve, 500))
        alert('Preferencias guardadas exitosamente!')
      } catch (error) {
        console.error('Error guardando preferencias:', error)
        alert('Error al guardar las preferencias.')
      }
    },

    changeAvatar() {
      // Simular cambio de avatar
      alert('Funcionalidad de cambio de avatar en desarrollo')
    },

    cancelEdit() {
      this.initializeData()
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

.btn-change-avatar {
  background: transparent;
  color: #0071e3;
  border: 1px solid #0071e3;
  padding: 6px 12px;
  border-radius: 15px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-change-avatar:hover {
  background: #0071e3;
  color: white;
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
.preferences-section,
.stats-section {
  margin-bottom: 40px;
  padding-bottom: 30px;
  border-bottom: 1px solid #e5e5e7;
}

.profile-form h3,
.password-section h3,
.preferences-section h3,
.stats-section h3 {
  font-size: 20px;
  font-weight: 600;
  color: #1d1d1f;
  margin-bottom: 20px;
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
.btn-change-password,
.btn-save-preferences {
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
.btn-change-password:hover:not(:disabled),
.btn-save-preferences:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(0, 113, 227, 0.3);
}

.btn-save:disabled,
.btn-change-password:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.preferences {
  margin-bottom: 20px;
}

.checkbox-label {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  cursor: pointer;
  color: #1d1d1f;
  font-size: 14px;
}

.checkbox-label input {
  margin-right: 10px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.stat-card {
  background: linear-gradient(135deg, #f5f5f7, #ffffff);
  border: 1px solid #e5e5e7;
  border-radius: 12px;
  padding: 20px;
  text-align: center;
}

.stat-number {
  font-size: 24px;
  font-weight: 700;
  color: #0071e3;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #6e6e73;
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

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .form-actions {
    flex-direction: column;
  }
}
</style>
