<template>
  <div class="cart">
    <div class="cart-container">
      <h2 class="cart-title">Mi Carrito</h2>

      <div v-if="loading" class="cart-empty">
        <p>Cargando tu carrito...</p>
      </div>

      <div v-else-if="!carrito || carrito.items.length === 0" class="cart-empty">
        <p>Tu carrito está vacío</p>
        <button class="btn-secondary" @click="irATienda">Ir a la tienda</button>
      </div>

      <div v-else>
        <div class="cart-items-list">

          <div v-for="item in carrito.items" :key="item.productoId" class="cart-item">
            <div class="item-details">
              <div class="item-name">{{ item.serie }}</div>
              <div class="item-specs">
                {{ item.cantidad }} unidad(es)
              </div>
            </div>

            <div class="item-price">
              ${{ formatearPrecio(item.precioUnitario * item.cantidad) }}
            </div>

          </div>
        </div>

        <div class="cart-summary">
          <div class="cart-total">
            TOTAL (Impuestos incluidos):
            <span>${{ formatearPrecio(carrito.total) }}</span>
          </div>

        </div>
      </div>
    </div>
  </div>
</template>

<script>
import apiClient from '@/api/axiosConfig'
import Swal from 'sweetalert2'

export default {
  name: 'CartView',
  data() {
    return {
      carrito: null,
      loading: true,
      // loadingCheckout: false, // Ya no es necesario
    }
  },
  mounted() {
    this.cargarCarrito();
  },
  methods: {
    async cargarCarrito() {
      this.loading = true;
      const token = localStorage.getItem('userToken');
      if (!token) {
        this.loading = false;
        return;
      }

      try {
        const response = await apiClient.get('/carrito', {
          headers: { Authorization: `Bearer ${token}` }
        });
        this.carrito = response.data;
      } catch (error) {
        if (error.response?.status !== 401 && error.response?.status !== 403) {
          console.error("Error cargando el carrito:", error);
          Swal.fire({
            icon: 'error',
            title: 'No se pudo cargar tu carrito',
            confirmButtonColor: '#0071e3',
          });
        }
      } finally {
        this.loading = false;
      }
    },

    // --- Métodos ELIMINADOS ---
    // async manejarEliminacion(productoId) { ... }
    // async manejarCheckout() { ... }

    formatearPrecio(value) {
      if (!value) return '0,00'
      return Number(value).toFixed(2).replace('.', ',')
    },

    irATienda() {
      this.$emit('change-page', 'home')
    },
  },
}
</script>

<style scoped>
.cart {
  padding-top: 40px;
  display: flex;
  justify-content: center;
}

.cart-container {
  background-color: #fff;
  border-radius: 10px;
  padding: 30px 40px;
  max-width: 800px;
  width: 100%;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.07);
}

.cart-title {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 30px;
  text-align: center;
  color: #1d1d1f;
}

.cart-empty {
  text-align: center;
  padding: 40px;
  font-size: 18px;
  color: #6e6e73;
  display: flex;
  flex-direction: column;
  gap: 20px;
  align-items: center;
}

.cart-items-list {
  border-bottom: 1px solid #d2d2d7;
  margin-bottom: 20px;
}

.cart-item {
  display: flex;
  align-items: center;
  padding: 20px 0;
  border-bottom: 1px solid #eee;
  gap: 15px; /* Espacio entre elementos */
}
.cart-item:last-child {
  border-bottom: none;
}

.item-details {
  flex: 1; /* Ocupa el espacio disponible */
}

.item-name {
  font-size: 18px;
  font-weight: 600;
  color: #1d1d1f;
}

.item-specs {
  color: #6e6e73;
  margin-top: 5px;
  font-size: 14px;
}

.item-price {
  font-weight: 700;
  font-size: 18px;
  color: #1d1d1f;
  margin-left: 20px; /* Añade espacio a la derecha */
}

/* --- Estilos ELIMINADOS --- */
/* .btn-delete { ... } */


.cart-summary {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.cart-total {
  margin-top: 20px;
  font-size: 18px;
  font-weight: 600;
  color: #1d1d1f;
}
.cart-total span {
  font-size: 22px;
  font-weight: 700;
  margin-left: 10px;
}

/* --- Estilos ELIMINADOS --- */
/* .cart-actions { ... } */
/* .btn-primary, .btn-secondary { ... } */

/* Estilo para el botón de "Ir a la tienda" en el estado vacío */
.btn-secondary {
  border: none;
  border-radius: 8px;
  padding: 12px 25px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  background-color: #e5e5ea;
  color: #1d1d1f;
}
.btn-secondary:hover {
  background-color: #d2d2d7;
}
</style>
