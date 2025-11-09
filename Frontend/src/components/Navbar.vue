<template>
  <nav class="navbar">
    <div class="navbar-content container">
      <div class="navbar-logo">Level Up</div>
      <div class="navbar-links">
        <a href="#" class="navbar-link" @click.prevent="changePage('home')">Inicio</a>

        <a href="#" class="navbar-link" @click.prevent="changePage('products', 'iPhone')">iPhone</a>
        <a href="#" class="navbar-link" @click.prevent="changePage('products', 'Mac')">Mac</a>
        <a href="#" class="navbar-link" @click.prevent="changePage('products', 'iPad')">iPad</a>
        <a href="#" class="navbar-link" @click.prevent="changePage('products', 'Accesorios')"
          >Accesorios</a
        >

        <a href="#" class="navbar-link" @click.prevent="changePage('cart')">Carrito</a>

        <!-- Secciones de administrador -->
        <template v-if="user?.role === 'admin'">
          <a href="#" class="navbar-link admin-link" @click.prevent="changePage('suppliers')"
            >Proveedores</a
          >
          <a href="#" class="navbar-link admin-link" @click.prevent="changePage('sales')">Ventas</a>
        </template>

        <!-- Mostrar perfil/logout o login -->
        <div v-if="user" class="user-menu">
          <a href="#" class="navbar-link" @click.prevent="openProfile">Mi Perfil</a>
          <span class="user-greeting">Hola, {{ user.name }}</span>
          <span class="user-role" :class="user.role">{{
            user.role === 'admin' ? '👑 Admin' : '👤 User'
          }}</span>
          <button class="btn-logout" @click="logout">Cerrar Sesión</button>
        </div>
        <a v-else href="#" class="navbar-link" @click.prevent="openLogin">Iniciar Sesión</a>
      </div>
    </div>
  </nav>
</template>

<script>
export default {
  name: 'AppNavbar',
  props: {
    currentPage: String,
    user: Object,
  },
  methods: {
    changePage(page, category = null) {
      this.$emit('change-page', page, category)
    },
    openLogin() {
      this.$emit('open-login')
    },
    openProfile() {
      this.$emit('open-profile')
    },
    logout() {
      this.$emit('logout')
    },
  },
}
</script>

<style scoped>
.navbar {
  background: rgba(63, 61, 61, 0.5);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  color: rgb(255, 255, 255);
  padding: 0 20px;
  position: sticky;
  top: 0;
  z-index: 1000;
}

.navbar-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 70px;
}

.navbar-logo {
  font-size: 24px;
  font-weight: 700;
}

.navbar-links {
  display: flex;
  gap: 25px;
  align-items: center;
}

.navbar-link {
  font-size: 16px;
  color: rgb(255, 255, 255);
  text-decoration: none;
  font-weight: 500;
  padding: 8px 16px;
  border-radius: 20px;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.navbar-link:hover {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
}

.admin-link {
  background: rgba(255, 215, 0, 0.2);
  border: 1px solid rgba(255, 215, 0, 0.3);
}

.admin-link:hover {
  background: rgba(255, 215, 0, 0.3);
}

.user-menu {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-greeting {
  color: white;
  font-weight: 500;
  font-size: 14px;
}

.user-role {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 10px;
  font-weight: 600;
}

.user-role.admin {
  background: rgba(255, 215, 0, 0.3);
  color: #ffd700;
}

.user-role.user {
  background: rgba(0, 113, 227, 0.3);
  color: #404850;
}

.btn-logout {
  background: rgba(255, 255, 255, 0.1);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.3);
  padding: 6px 12px;
  border-radius: 15px;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.btn-logout:hover {
  background: rgba(255, 255, 255, 0.2);
}

/* Responsive */
@media (max-width: 768px) {
  .navbar-links {
    gap: 15px;
  }

  .navbar-link {
    font-size: 14px;
    padding: 6px 12px;
  }

  .user-menu {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }

  .user-greeting {
    display: none;
  }

  .user-role {
    display: none;
  }

  .btn-logout {
    font-size: 11px;
    padding: 4px 8px;
  }
}
</style>
