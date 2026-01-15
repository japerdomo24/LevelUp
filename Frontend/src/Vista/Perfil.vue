<template>
  <div class="profile-container">
    <div class="profile-card">

      <div class="profile-header">
        <h2>{{ isEditing ? 'Editar Perfil' : 'Mi Perfil' }}</h2>
        <p v-if="!isEditing">Gestiona tu información personal y seguridad.</p>
        <p v-else>Actualiza tus datos y guarda los cambios.</p>
      </div>

      <div class="profile-content">

        <div class="user-info-section">
          <div class="avatar-section">
            <div class="avatar">
              {{ inicialesUsuario }}
            </div>

            <span v-if="user.rol === 'admin'" class="badge-admin">Administrador</span>

            <div class="action-buttons" v-if="!isEditing">
              <button class="btn btn-primary" @click="activarEdicion">
                Editar Información
              </button>
              <button class="btn btn-secondary" @click="cambiarContrasena">
                Cambiar Contraseña
              </button>
            </div>
          </div>

          <div class="user-details">

            <div v-if="!isEditing" class="info-view">
              <div class="detail-item">
                <label>Nombre completo:</label>
                <span>{{ user.nombre }} {{ user.apellido }}</span>
              </div>
              <div class="detail-item">
                <label>Correo electrónico:</label>
                <span>{{ user.correo }}</span>
              </div>
              <div class="detail-item">
                <label>Teléfono:</label>
                <span>{{ user.numero || 'No registrado' }}</span>
              </div>
              <div class="detail-item">
                <label>Dirección:</label>
                <span>{{ user.direccion || 'No registrada' }}</span>
              </div>
            </div>

            <form v-else @submit.prevent="guardarCambios" class="edit-form">
              <div class="form-row">
                <div class="form-group">
                  <label>Nombre</label>
                  <input v-model="form.nombre" type="text" required />
                </div>
                <div class="form-group">
                  <label>Apellido</label>
                  <input v-model="form.apellido" type="text" required />
                </div>
              </div>

              <div class="form-group">
                <label>Teléfono</label>
                <div class="phone-input-group">
                  <div class="fixed-prefix">🇻🇪 +58</div>

                  <input
                  v-model="form.numero"
                  type="tel"
                  placeholder="4121234567"
                  class="phone-body-input"
                  maxlength="10"
                />
                </div>
              </div>

              <div class="form-group">
                <label>Dirección</label>
                <input v-model="form.direccion" type="text" placeholder="Tu dirección de envío" />
              </div>

              <div class="edit-actions">
                <button type="button" class="btn btn-secondary" @click="cancelarEdicion">Cancelar</button>
                <button type="submit" class="btn btn-primary">Guardar Cambios</button>
              </div>
            </form>

          </div>
        </div>

        <hr class="divider">

        <div class="danger-zone">
          <h3>Zona de Peligro</h3>
          <p>Una vez que elimines tu cuenta, no hay vuelta atrás. Por favor, asegúrate.</p>
          <button class="btn-danger" @click="eliminarCuenta">
            Eliminar mi cuenta
          </button>
        </div>

      </div>
    </div>
  </div>
</template>

<script>
import apiClient from '@/api/axiosConfig'
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
      isEditing: false,
      form: {
        nombre: '',
        apellido: '',
        numero: '',
        direccion: ''
      }
    }
  },
  computed: {
    inicialesUsuario() {
      if (this.user && this.user.nombre && this.user.apellido) {
        return `${this.user.nombre[0]}${this.user.apellido[0]}`.toUpperCase()
      }
      return 'US'
    }
  },
  methods: {
    // 1. Activar modo edición
    activarEdicion() {
      this.form.nombre = this.user.nombre;
      this.form.apellido = this.user.apellido;
      this.form.direccion = this.user.direccion;

      // Si existe un número y empieza por +58, lo quitamos para editar
      if (this.user.numero && this.user.numero.startsWith('+58')) {
        this.form.numero = this.user.numero.replace('+58', '');
      } else {
        // Si es un número viejo sin formato o vacío, lo dejamos tal cual o vacío
        this.form.numero = this.user.numero || '';
      }

      this.isEditing = true;
    },

    cancelarEdicion() {
      this.isEditing = false;
    },


// 2. Guardar cambios (PUT)
    async guardarCambios() {
      // Limpiamos espacios por si acaso
      const cuerpoTelefono = this.form.numero.trim();

      // Validación: Exactamente 10 dígitos numéricos
      const regex10Digitos = /^\d{10}$/;

      if (!regex10Digitos.test(cuerpoTelefono)) {
        Swal.fire({
          icon: 'warning',
          title: 'Teléfono inválido',
          text: 'Ingresa los 10 dígitos de tu número (Ej: 4141234567).',
          confirmButtonColor: '#0071e3',
        });
        return;
      }

      // Concatenamos el prefijo fijo
      const telefonoFinal = '+58' + cuerpoTelefono;

      try {
        const token = localStorage.getItem('userToken');

        const payload = {
          nombre: this.form.nombre,
          apellido: this.form.apellido,
          numero: telefonoFinal, // Enviamos +58XXXXXXXXXX
          direccion: this.form.direccion
        };

        const response = await apiClient.put(`/usuarios/${this.user.correo}`, payload, {
          headers: { Authorization: `Bearer ${token}` }
        });

        this.$emit('profile-updated', response.data);
        this.isEditing = false;

        Swal.fire({
          icon: 'success',
          title: 'Perfil actualizado',
          timer: 1500,
          showConfirmButton: false
        });

      } catch (error) {
        console.error(error);
        Swal.fire('Error', 'No se pudieron guardar los cambios.', 'error');
      }
    },

    // 3. Cambiar Contraseña (PUT)
    async cambiarContrasena() {
      const { value: password } = await Swal.fire({
        title: 'Cambiar Contraseña',
        input: 'password',
        inputLabel: 'Ingresa tu nueva contraseña',
        inputPlaceholder: 'Nueva contraseña',
        showCancelButton: true,
        confirmButtonColor: '#0071e3',
        cancelButtonColor: '#6e6e73',
        inputValidator: (value) => {
          if (!value) {
            return '¡Debes escribir una contraseña!';
          }
          if (value.length < 4) {
            return 'La contraseña debe tener al menos 4 caracteres';
          }
        }
      });

      if (password) {
        try {
          const token = localStorage.getItem('userToken');

          // Solo enviamos la clave
          const payload = { clavePlana: password };

          await apiClient.put(`/usuarios/${this.user.correo}`, payload, {
            headers: { Authorization: `Bearer ${token}` }
          });

          Swal.fire('¡Listo!', 'Tu contraseña ha sido actualizada.', 'success');

        } catch (error) {
          console.error(error);
          Swal.fire('Error', 'No se pudo cambiar la contraseña.', 'error');
        }
      }
    },

    // 4. Eliminar Cuenta (DELETE)
    async eliminarCuenta() {
      const result = await Swal.fire({
        title: '¿Estás seguro?',
        text: "Perderás acceso a tus compras y carrito. Esta acción es permanente.",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#ff3b30',
        cancelButtonColor: '#6e6e73',
        confirmButtonText: 'Sí, eliminar mi cuenta'
      });

      if (result.isConfirmed) {
        try {
          const token = localStorage.getItem('userToken');

          await apiClient.delete(`/usuarios/${this.user.correo}`, {
             headers: { Authorization: `Bearer ${token}` }
          });

          // Avisamos a App.vue para que cierre sesión forzosamente
          this.$emit('logout', true);

          Swal.fire('Cuenta eliminada', 'Lamentamos verte partir.', 'success');

        } catch (error) {
          console.error(error);
          Swal.fire('Error', 'No se pudo eliminar la cuenta.', 'error');
        }
      }
    }
  }
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
  max-width: 850px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.5);
}

.profile-header {
  text-align: center;
  margin-bottom: 40px;
}
.profile-header h2 { font-size: 28px; color: #1d1d1f; margin-bottom: 8px;}
.profile-header p { color: #86868b; }

.user-info-section {
  display: flex;
  gap: 50px;
  margin-bottom: 40px;
}

/* Sección Avatar y Botones */
.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 200px;
  flex-shrink: 0;
}

.avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: linear-gradient(135deg, #0071e3, #0077ed);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 40px;
  font-weight: 700;
  box-shadow: 0 10px 20px rgba(0, 113, 227, 0.3);
  margin-bottom: 15px;
}

.badge-admin {
  background: #000;
  color: #fff;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  margin-bottom: 20px;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: 100%;
}

/* Detalles y Formulario */
.user-details {
  flex: 1;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  padding: 15px 0;
  border-bottom: 1px solid #f5f5f7;
}
.detail-item label { font-weight: 600; color: #1d1d1f; }
.detail-item span { color: #86868b; }

/* Estilos Formulario Edición */
.edit-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.form-row {
  display: flex;
  gap: 20px;
}
.form-group { flex: 1; display: flex; flex-direction: column; gap: 8px; }
.form-group label { font-size: 14px; font-weight: 600; color: #1d1d1f; }
.form-group input {
  padding: 12px;
  border-radius: 12px;
  border: 1px solid #d2d2d7;
  font-size: 16px;
  transition: border-color 0.2s;
}
.form-group input:focus {
  outline: none;
  border-color: #0071e3;
  box-shadow: 0 0 0 4px rgba(0,113,227, 0.1);
}

.edit-actions {
  display: flex;
  gap: 15px;
  justify-content: flex-end;
  margin-top: 10px;
}

.btn {
  padding: 10px 20px;
  border-radius: 12px;
  border: none;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: transform 0.1s;
}
.btn:active { transform: scale(0.98); }
.btn-primary { background: #0071e3; color: white; }
.btn-secondary { background: #f5f5f7; color: #0071e3; }

/* Zona de Peligro */
.divider { border: 0; height: 1px; background: #e5e5e7; margin: 0 0 30px 0; }
.danger-zone {
  background: #fff5f5;
  border: 1px solid #ffcfcf;
  border-radius: 16px;
  padding: 25px;
}
.danger-zone h3 { color: #ff3b30; margin-bottom: 5px; font-size: 18px; }
.danger-zone p { color: #6e6e73; font-size: 14px; margin-bottom: 20px; }
.btn-danger {
  background: transparent;
  border: 1px solid #ff3b30;
  color: #ff3b30;
  padding: 8px 16px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.2s;
}
.btn-danger:hover { background: #ff3b30; color: white; }

@media (max-width: 768px) {
  .user-info-section { flex-direction: column; align-items: center; }
  .avatar-section { width: 100%; margin-bottom: 20px; }
  .form-row { flex-direction: column; gap: 20px; }
}

.phone-input-group {
  display: flex;
  align-items: center;
  gap: 10px;
}

/* Estilo del cuadro gris fijo */
.fixed-prefix {
  background-color: #f5f5f7;
  color: #6e6e73;
  padding: 12px 16px;
  border-radius: 12px;
  border: 1px solid #d2d2d7;
  font-size: 16px;
  font-weight: 600;
  white-space: nowrap; /* Evita que se rompa la línea */
  user-select: none;   /* Evita que se seleccione el texto */
}

/* El input ocupa el resto del espacio */
.phone-body-input {
  flex: 1;
  padding: 12px;
  border-radius: 12px;
  border: 1px solid #d2d2d7;
  font-size: 16px;
  transition: all 0.2s;
}

.phone-body-input:focus {
  outline: none;
  border-color: #0071e3;
  box-shadow: 0 0 0 4px rgba(0, 113, 227, 0.1);
}
</style>
