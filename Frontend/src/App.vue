<template>
  <div id="app">
    <!-- Barra de navegación -->
    <Navbar
      :currentPage="currentPage"
      :user="user"
      @change-page="changePage"
      @open-login="changeAuthMode('login')"
      @open-profile="openProfile"
      @logout="logout"
    />

    <!-- Contenido principal -->
    <main class="main-content">
      <div class="container">
        <!-- Página de inicio -->
        <Home
          v-if="currentPage === 'home'"
          @change-page="changePage"
          :user="user"
          @edit-home="changePage('edit-home')"
        />

        <!-- Página de productos -->
        <Products
          v-else-if="currentPage === 'products'"
          :category="currentCategory"
          :user="user"
          @view-product="viewProduct"
          @change-page="changePage"
          @create-product="changePage('create-product')"
        />

        <!-- Página de detalles del producto -->
        <ProductDetail
          v-else-if="currentPage === 'product-detail'"
          :product="selectedProduct"
          @add-to-cart="addToCart"
          @change-page="changePage"
        />

        <!-- Página del carrito -->
        <Cart v-else-if="currentPage === 'cart'" :cartItems="cartItems" @change-page="changePage" />

        <!-- Página de proveedores (solo admin) -->
        <Suppliers
          v-else-if="currentPage === 'suppliers' && user?.role === 'admin'"
          @change-page="changePage"
        />

        <!-- Página de ventas (solo admin) -->
        <Sales
          v-else-if="currentPage === 'sales' && user?.role === 'admin'"
          @change-page="changePage"
        />

        <!-- Página de crear producto (solo admin) -->
        <CreateProduct
          v-else-if="currentPage === 'create-product' && user?.role === 'admin'"
          :category="currentCategory"
          @change-page="changePage"
          @product-created="handleProductCreated"
        />

        <!-- Página de editar home (solo admin) -->
        <EditHome
          v-else-if="currentPage === 'edit-home' && user?.role === 'admin'"
          @change-page="changePage"
          @home-updated="handleHomeUpdated"
        />

        <!-- Página de autenticación -->
        <div v-else-if="currentPage === 'auth'">
          <Login
            v-if="authMode === 'login'"
            @login-success="handleLoginSuccess"
            @change-auth-mode="changeAuthMode"
          />
          <Register
            v-else
            @register-success="handleRegisterSuccess"
            @change-auth-mode="changeAuthMode"
          />
        </div>

        <!-- Página de perfil (SOLO para usuarios logeados) -->
        <Profile
          v-else-if="currentPage === 'profile' && user"
          :user="user"
          @profile-updated="handleProfileUpdate"
          @change-page="changePage"
        />

        <!-- Redirección si intenta acceder a secciones admin sin permisos -->
        <div v-else-if="requiresAdmin && (!user || user.role !== 'admin')" class="admin-required">
          <div class="admin-message">
            <h2>Acceso Denegado</h2>
            <p>Se requieren permisos de administrador para acceder a esta sección</p>
            <button class="btn-home" @click="changePage('home')">Volver al Inicio</button>
          </div>
        </div>
      </div>
    </main>

    <!-- Pie de página -->
    <MyFooter />
  </div>
</template>

<script>
import Navbar from './components/Navbar.vue'
import MyFooter from './components/MyFooter.vue'
import Home from './components/Home.vue'
import Products from './components/Products.vue'
import ProductDetail from './components/ProductDetail.vue'
import Cart from './components/Cart.vue'
import Login from './components/Login.vue'
import Register from './components/Register.vue'
import Profile from './components/Profile.vue'
import Suppliers from './components/Suppliers.vue'
import Sales from './components/Sales.vue'
import CreateProduct from './components/CreateProduct.vue'
import EditHome from './components/EditHome.vue'

export default {
  name: 'App',
  components: {
    Navbar,
    MyFooter,
    Home,
    Products,
    ProductDetail,
    Cart,
    Login,
    Register,
    Profile,
    Suppliers,
    Sales,
    CreateProduct,
    EditHome,
  },
  data() {
    return {
      currentPage: 'home',
      currentCategory: '',
      selectedProduct: null,
      cartItems: [],
      authMode: 'login',
      user: null,
    }
  },
  computed: {
    requiresAdmin() {
      const adminPages = ['suppliers', 'sales', 'create-product', 'edit-home']
      return adminPages.includes(this.currentPage)
    },
  },
  methods: {
    changePage(page, category = '') {
      this.currentPage = page
      this.currentCategory = category

      if (page === 'products') {
        this.selectedProduct = null
      }
    },

    viewProduct(product) {
      this.selectedProduct = product
      this.currentPage = 'product-detail'
    },

    addToCart(productWithOptions) {
      this.cartItems.push(productWithOptions)
      this.changePage('products', this.selectedProduct.category)
    },

    changeAuthMode(mode) {
      this.authMode = mode
      this.currentPage = 'auth'
    },

    openProfile() {
      if (this.user) {
        this.changePage('profile')
      } else {
        this.changeAuthMode('login')
        alert('Por favor, inicia sesión para acceder a tu perfil')
      }
    },

    handleLoginSuccess(userData) {
      this.user = userData
      this.currentPage = 'home'
      alert(`¡Bienvenido ${userData.name}!`)
    },

    handleRegisterSuccess(userData) {
      this.user = userData
      this.currentPage = 'home'
      alert(`¡Cuenta creada exitosamente! Bienvenido ${userData.name}`)
    },

    handleProfileUpdate(updatedUser) {
      this.user = updatedUser
      this.currentPage = 'home'
      alert('Perfil actualizado exitosamente!')
    },

    handleProductCreated() {
      this.changePage('products', this.currentCategory)
      alert('Producto creado exitosamente!')
    },

    handleHomeUpdated() {
      this.changePage('home')
      alert('Página de inicio actualizada exitosamente!')
    },

    logout() {
      this.user = null
      this.currentPage = 'home'
      alert('Sesión cerrada exitosamente')
    },
  },
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
