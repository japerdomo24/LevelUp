<template>
  <nav class="navbar">
    <div class="navbar-content container">
      <div class="navbar-logo">Level Up</div>

      <button class="navbar-toggle" @click="toggleMenuMovil" aria-label="Abrir menú">
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>

      <div class="navbar-links" :class="{ 'is-open': menuMovilAbierto }">
        <a href="#" class="navbar-link" @click.prevent="cambiarPagina('home')">Inicio</a>

        <a href="#" class="navbar-link" @click.prevent="cambiarPagina('products', 'iPhone')">iPhone</a>
        <a href="#" class="navbar-link" @click.prevent="cambiarPagina('products', 'Mac')">Mac</a>
        <a href="#" class="navbar-link" @click.prevent="cambiarPagina('products', 'iPad')">iPad</a>
        <a href="#" class="navbar-link" @click.prevent="cambiarPagina('products', 'Accesorios')"
        >Accesorios</a
        >

        <a
          v-if="user && user.rol === 'user'"
          href="#"
          class="navbar-link"
          @click.prevent="cambiarPagina('cart')"
        >Carrito</a
        >

        <template v-if="user?.rol === 'admin'">
          <a href="#" class="navbar-link admin-link" @click.prevent="cambiarPagina('create-product')"
          >Crear Producto</a
          >
          <a href="#" class="navbar-link admin-link" @click.prevent="cambiarPagina('suppliers')"
          >Proveedores</a
          >
          <a href="#" class="navbar-link admin-link" @click.prevent="cambiarPagina('sales')">Ventas</a>
        </template>

        <div class="mobile-divider"></div>

        <div v-if="user" class="user-menu">
          <a href="#" class="navbar-link" @click.prevent="abrirPerfil">Mi Perfil</a>
          <span class="user-greeting">Hola, {{ user.nombre }}</span>
          <span class="user-role" :class="user.rol">{{
              user.rol === 'admin' ? '👑 Admin' : '👤 User'
            }}</span>
          <button class="btn-logout" @click="cerrarSesion">Cerrar Sesión</button>
        </div>
        <a v-else href="#" class="navbar-link" @click.prevent="abrirLogin">Iniciar Sesión</a>
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
  data() {
    return {
      menuMovilAbierto: false, // Estado para el menú hamburguesa
    }
  },
  // --- MÉTODOS TRADUCIDOS ---
  methods: {
    cambiarPagina(page, category = null) {
      this.$emit('change-page', page, category)
      this.menuMovilAbierto = false; // Cierra el menú al navegar
    },
    abrirLogin() {
      this.$emit('open-login')
      this.menuMovilAbierto = false; // Cierra el menú
    },
    abrirPerfil() {
      this.$emit('open-profile')
      this.menuMovilAbierto = false; // Cierra el menú
    },
    cerrarSesion() {
      this.$emit('logout')
      this.menuMovilAbierto = false; // Cierra el menú
    },
    toggleMenuMovil() {
      this.menuMovilAbierto = !this.menuMovilAbierto;
    }
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
  color: #cce4ff; /* Color ajustado para mejor contraste */
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
  background: rgba(255, 50, 50, 0.2); /* Color rojo al pasar el mouse */
  border-color: rgba(255, 50, 50, 0.4);
}

/* --- ESTILOS RESPONSIVE AÑADIDOS --- */

.navbar-toggle {
  display: none; /* Oculto en desktop */
  background: transparent;
  border: none;
  cursor: pointer;
  padding: 10px;
  z-index: 1001; /* Encima del menú */
}

.icon-bar {
  display: block;
  width: 22px;
  height: 2px;
  background-color: white;
  border-radius: 1px;
  margin: 4px 0;
  transition: all 0.3s ease;
}

/* Oculta el separador en desktop */
.mobile-divider {
  display: none;
}


/* --- Media Query para pantallas medianas y pequeñas --- */
@media (max-width: 1024px) {

  /* Muestra el botón hamburguesa */
  .navbar-toggle {
    display: block;
  }

  /* Oculta los links por defecto */
  .navbar-links {
    display: none; /* Oculto */
    position: absolute;
    top: 70px; /* Debajo del navbar */
    left: 0;
    right: 0;
    width: 100%;
    background: rgba(40, 40, 40, 0.9); /* Fondo más oscuro para el menú */
    backdrop-filter: blur(15px);
    flex-direction: column;
    align-items: center;
    padding: 20px 0 30px 0; /* Más padding abajo */
    gap: 10px;
    border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  }

  /* Muestra el menú cuando está abierto */
  .navbar-links.is-open {
    display: flex;
  }

  /* Estilos de los links en el menú móvil */
  .navbar-link {
    font-size: 18px;
    padding: 10px 20px;
    width: 90%;
    text-align: center;
  }

  /* Muestra el separador en móvil */
  .mobile-divider {
    display: block;
    height: 1px;
    width: 90%;
    background-color: rgba(255, 255, 255, 0.2);
    margin: 10px 0;
  }

  /* Estilos del menú de usuario en móvil */
  .user-menu {
    flex-direction: column;
    align-items: center;
    gap: 15px;
    width: 100%;
  }

  /* Muestra saludo y rol en el menú móvil */
  .user-greeting {
    display: block;
    font-size: 16px;
  }
  .user-role {
    display: block;
    font-size: 12px;
  }

  /* Botón de logout más grande en móvil */
  .btn-logout {
    width: 90%;
    text-align: center;
    padding: 12px;
    font-size: 16px;
    background: rgba(255, 50, 50, 0.2);
    border-color: rgba(255, 50, 50, 0.4);
  }
  .btn-logout:hover {
    background: rgba(255, 50, 50, 0.4);
  }
}
</style>
