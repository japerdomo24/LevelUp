<template>
  <div class="profile-container">
    <div class="profile-card">
      <div class="profile-header">
        <h2>Mi Perfil</h2>
        <p>Esta es tu información personal registrada.</p>
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
            <div class="detail-item">
              <label>Teléfono:</label>
              <span>{{ user.numero || 'No registrado' }}</span>
            </div>
            <div class="detail-item">
              <label>Dirección:</label>
              <span>{{ user.direccion || 'No registrada' }}</span>
            </div>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<script>
// No se necesita apiClient ni Swal si solo es de lectura
// import apiClient from '@/api/axiosConfig'
// import Swal from 'sweetalert2'

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
      // Todos los data relacionados con formularios han sido eliminados
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
    // Se eliminaron los computed de contraseña
  },
  mounted() {
    // Ya no necesita inicializar datos del formulario
    // this.inicializarDatos()
  },
  methods: {
    // Todos los métodos de API (actualizar, cambiar clave, eliminar) han sido eliminados
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
  max-width: 800px; /* Puedes reducir esto si quieres */
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
  /* Se eliminaron los bordes inferiores ya que es la única sección */
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
.detail-item:last-child {
  border-bottom: none;
}

.detail-item label {
  font-weight: 600;
  color: #1d1d1f;
}

.detail-item span {
  color: #6e6e73;
  text-align: right;
}

/* --- Todos los estilos de formularios y zona de peligro eliminados --- */

/* Responsive */
@media (max-width: 768px) {
  .profile-card {
    padding: 30px 20px;
  }

  .user-info-section {
    flex-direction: column;
    gap: 20px;
  }
}
</style>
