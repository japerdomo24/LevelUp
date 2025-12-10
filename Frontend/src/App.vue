<template>
  <div id="app">
    <Navbar
      :currentPage="currentPage"
      :user="user"
      @change-page="cambiarPagina"
      @open-login="cambiarModoAutenticacion('login')"
      @open-profile="abrirPerfil"
      @logout="cerrarSesion"
    />

    <main class="main-content">
      <div class="container">
        <Home
          v-if="currentPage === 'home'"
          @change-page="cambiarPagina"
          :user="user"
          @edit-home="cambiarPagina('edit-home')"
        />

        <Products
          v-else-if="currentPage === 'products'"
          :category="currentCategory"
          :user="user"
          @view-product="verProducto"
          @change-page="cambiarPagina"
          @create-product="cambiarPagina('create-product')"
        />

        <ProductDetail
          v-else-if="currentPage === 'product-detail'"
          :product="selectedProduct"
          :user="user"
          @add-to-cart="agregarAlCarrito"
          @change-page="cambiarPagina"
          @edit-product="manejarEditarProducto"
        />

        <Cart
          v-else-if="currentPage === 'cart' && user?.rol === 'user'"
          @change-page="cambiarPagina"
        />

        <Suppliers
          v-else-if="currentPage === 'suppliers' && user?.rol === 'admin'"
          @change-page="cambiarPagina"
        />

        <Sales
          v-else-if="currentPage === 'sales' && user?.rol === 'admin'"
          @change-page="cambiarPagina"
        />

        <CreateProduct
          v-else-if="currentPage === 'create-product' && user?.rol === 'admin'"
          :category="currentCategory"
          @change-page="cambiarPagina"
          @product-created="manejarProductoCreado"
        />

        <div v-else-if="currentPage === 'auth'">
          <Login
            v-if="authMode === 'login'"
            @login-success="manejarLoginExitoso"
            @change-auth-mode="cambiarModoAutenticacion"
          />
          <Register
            v-else
            @register-success="manejarRegistroExitoso"
            @change-auth-mode="cambiarModoAutenticacion"
          />
        </div>

        <Profile
          v-else-if="currentPage === 'profile' && user"
          :user="user"
          @profile-updated="manejarActualizacionPerfil"
          @change-page="cambiarPagina"
          @logout="cerrarSesion" />

        <div v-else-if="requiresAdmin || (currentPage === 'cart' && (!user || user.rol !== 'user'))" class="admin-required">
          <div class="admin-message">
            <h2>Acceso Denegado</h2>
            <p>No tienes permisos para acceder a esta sección.</p>
            <button class="btn-home" @click="cambiarPagina('home')">Volver al Inicio</button>
          </div>
        </div>
      </div>
    </main>

    <MyFooter />
  </div>
</template>

<script>
// --- Importaciones de Componentes ---
import Navbar from '@/Componentes/Navbar.vue'
import MyFooter from '@/Componentes/MyFooter.vue'
import Home from './Vista/Home.vue'
import Productos from './Vista/Productos.vue'
import DetalleProducto from './Vista/DetalleProducto.vue'
import Carrito from './Vista/Carrito.vue'
import InicioSesion from './Vista/inicioSesion.vue'
import Registro from './Vista/Registro.vue'
import Perfil from './Vista/Perfil.vue'
import Proveedores from './Vista/Proveedores.vue'
import Ventas from './Vista/Ventas.vue'
import CrearProducto from './Vista/CrearProducto.vue'

// --- Importaciones de API ---
import apiClient from '@/api/axiosConfig'
import Swal from 'sweetalert2'

// --- Configuración de Toast (SOLO PARA 'ADD-TO-CART') ---
const Toast = Swal.mixin({
  toast: true,
  position: 'middle',
  showConfirmButton: true,
  confirmButtonColor: '#0071e3',
  timer: 3000,
  timerProgressBar: true,
})

export default {
  name: 'App',
  components: {
    Navbar,
    MyFooter,
    Home,
    Products: Productos,
    ProductDetail: DetalleProducto,
    Cart: Carrito,
    Login: InicioSesion,
    Register: Registro,
    Profile: Perfil,
    Suppliers: Proveedores,
    Sales: Ventas,
    CreateProduct: CrearProducto,
  },
  data() {
    return {
      currentPage: 'home',
      currentCategory: '',
      selectedProduct: null,
      authMode: 'login',
      user: null,
      token: null,
    }
  },
  computed: {
    requiresAdmin() {
      const adminPages = ['suppliers', 'sales', 'create-product', 'edit-home']
      return adminPages.includes(this.currentPage) && (!this.user || this.user.rol !== 'admin')
    },
  },
  // --- MÉTODOS TRADUCIDOS ---
  methods: {
    cambiarPagina(page, category = '') {
      this.currentPage = page
      this.currentCategory = category
      if (page === 'products') {
        this.selectedProduct = null
      }
    },

    verProducto(product) {
      this.selectedProduct = product
      this.currentPage = 'product-detail'
    },

    async agregarAlCarrito(cartItem) {
      if (!this.token) {
        Swal.fire({
          icon: 'warning',
          title: 'Acción Requerida',
          text: 'Debes iniciar sesión primero.',
          confirmButtonColor: '#0071e3',
        });
        this.cambiarModoAutenticacion('login');
        return;
      }

      try {
        const payload = {
          productoId: cartItem.id,
          cantidad: cartItem.quantity
        };

        await apiClient.post('/carrito', payload, {
          headers: { Authorization: `Bearer ${this.token}` }
        });

        //Notificaciones de agregar un producto al carrito
        Toast.fire({
          icon: 'success',
          title: `¡${cartItem.serie} añadido al carrito!`
        }).then(()=> {
          Swal.fire({
            title: "¿Deseas agregar otro producto al carrito?",
            showDenyButton: true,
            denyButtonText: "No",
            confirmButtonText: "Si",
            denyButtonColor: '#d2d2d7',
            confirmButtonColor: '#0071e3',
          }).then((result) => {
            if (result.isConfirmed) {
              this.cambiarPagina('products', this.selectedProduct.tipo);;
            } else if (result.isDenied) {
              this.cambiarPagina('cart');;
            }
          });
       });
       //manejo de errores y notificaciones requeridas
      } catch (error) {
        if (error.response && error.response.status === 409) {
          // Manejo del Caso: Producto no disponible en cantidades solicitadas
          Swal.fire({
            icon: 'error',
            title: '¡Stock insuficiente!',
            text: 'Este producto no se encuentra disponible en las cantidades que solicita. ¿Deseas agregar este mismo producto con una cantidad distinta?',
            showDenyButton: true,
            denyButtonText: "No",
            confirmButtonText: "Si",
            denyButtonColor: '#d2d2d7',
            confirmButtonColor: '#0071e3',
          }).then((result) => {
            if (result.isConfirmed) {
              return;;
            } else if (result.isDenied) {
              this.cambiarPagina('products', this.selectedProduct.tipo);;
            }
          });
        } else if (error.response && error.response.status === 404) {
            // Manejo del Caso: Producto no disponible
          Swal.fire({
            icon: 'error',
            title: 'Producto No Disponible',
            text: 'Producto no disponible, inténtelo más tarde.',
            confirmButtonColor: '#0071e3',
          })
          this.cambiarPagina('products', this.selectedProduct.tipo);
        }  else {
          Swal.fire({
            icon: 'error',
            title: 'Se ha producido un error al añadir un producto',
            confirmButtonColor: '#0071e3',
          })
          this.cambiarPagina('products', this.selectedProduct.tipo);
        }
      }
    },

    cambiarModoAutenticacion(mode) {
      this.authMode = mode
      this.currentPage = 'auth'
    },

    abrirPerfil() {
      if (this.user) {
        this.cambiarPagina('profile')
      } else {
        Swal.fire({
          icon: 'info',
          title: 'Inicia sesión para ver tu perfil',
          confirmButtonColor: '#0071e3',
        })
        this.cambiarModoAutenticacion('login')
      }
    },

    manejarLoginExitoso(loginData) {
      this.user = loginData.usuario;
      this.token = loginData.token;

      localStorage.setItem('userToken', loginData.token);
      localStorage.setItem('userData', JSON.stringify(loginData.usuario));
      apiClient.defaults.headers.common['Authorization'] = `Bearer ${loginData.token}`;

      this.currentPage = 'home'

      Swal.fire({
        icon: 'success',
        title: `¡Bienvenido, ${loginData.usuario.nombre}!`,
        timer: 2000,
        showConfirmButton: false,
      })
    },

    manejarRegistroExitoso() {
      // No hace nada, Registro.vue maneja la alerta y el cambio
    },

    manejarActualizacionPerfil(updatedUser) {
      this.user = updatedUser
      localStorage.setItem('userData', JSON.stringify(updatedUser));
      this.currentPage = 'home'
      Swal.fire({
        icon: 'success',
        title: 'Perfil actualizado',
        timer: 2000,
        showConfirmButton: false,
      })
    },

    manejarProductoCreado() {
      this.cambiarPagina('products', this.currentCategory)
      Swal.fire({
        icon: 'success',
        title: 'Producto creado exitosamente',
        timer: 2000,
        showConfirmButton: false,
      })
    },

    manejarHomeActualizado() {
      this.cambiarPagina('home')
      Swal.fire({
        icon: 'success',
        title: 'Página de inicio actualizada',
        timer: 2000,
        showConfirmButton: false,
      })
    },

    manejarEditarProducto(product) {
      Swal.fire({
        icon: 'info',
        title: `Cargando editor para: ${product.serie}`,
        timer: 2000,
        showConfirmButton: false,
      })
      // (Aquí iría la lógica futura para ir a la página de edición)
    },

    async cerrarSesion(saltarConfirmacion = false) {
      if (!saltarConfirmacion) {
        const result = await Swal.fire({
          title: '¿Cerrar sesión?',
          text: "¿Estás seguro de que quieres salir?",
          icon: 'question',
          showCancelButton: true,
          confirmButtonColor: '#0071e3',
          cancelButtonColor: '#6e6e73',
          confirmButtonText: 'Sí, cerrar sesión',
          cancelButtonText: 'Cancelar'
        })
        if (!result.isConfirmed) return;
      }

      this.user = null
      this.token = null
      localStorage.removeItem('userToken');
      localStorage.removeItem('userData');
      delete apiClient.defaults.headers.common['Authorization'];

      this.currentPage = 'home'

      if (!saltarConfirmacion) {
        Swal.fire({
          icon: 'success',
          title: 'Sesión cerrada',
          timer: 2000,
          showConfirmButton: false,
        })
      }
    },

    revisarSesionPersistente() {
      const token = localStorage.getItem('userToken');
      const userData = localStorage.getItem('userData');

      if (token && userData) {
        this.token = token;
        this.user = JSON.parse(userData);
        apiClient.defaults.headers.common['Authorization'] = `Bearer ${token}`;
      }
    }
  },
  created() {
    this.revisarSesionPersistente();
  }
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family:
    -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Segoe UI Emoji', 'Segoe UI Symbol',
    'Helvetica Neue', Arial, sans-serif;
}

body {
  background: white;
  min-height: 100vh;
  color: #1d1d1f;
  line-height: 1.6;
}

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.container {
  max-width: 1450px;
  margin: 0 auto;
  padding: 0 20px;
  flex: 1;
}

.main-content {
  flex: 1;
  padding: 20px 0;
}

.btn {
  background-color: #0071e3;
  color: white;
  border: none;
  border-radius: 20px;
  padding: 8px 16px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.btn:hover {
  background-color: #0077ed;
}

.btn-secondary {
  background-color: transparent;
  color: #0071e3;
  border: 1px solid #d2d2d7;
}

.btn-secondary:hover {
  background-color: #f5f5f7;
}

/* Estilos para la página de acceso denegado */
.admin-required {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 60vh;
}

.admin-message {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 40px;
  text-align: center;
  max-width: 400px;
  width: 100%;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
}

.admin-message h2 {
  font-size: 24px;
  font-weight: 700;
  color: #ff3b30;
  margin-bottom: 15px;
}

.admin-message p {
  color: #6e6e73;
  margin-bottom: 25px;
  font-size: 16px;
}

.btn-home {
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

.btn-home:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.2);
}
</style>
