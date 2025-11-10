<template>
  <div class="cart">
    <div class="cart-container">
      <h2 class="cart-title">Carrito</h2>

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

            <button class="btn-delete" @click="manejarEliminacion(item.productoId)">
              &times;
            </button>
          </div>
        </div>

        <div class="cart-summary">
          <div class="cart-total">
            TOTAL (Impuestos incluidos):
            <span>${{ formatearPrecio(carrito.total) }}</span>
          </div>

          <div class="cart-actions">
            <button class="btn-secondary" @click="irATienda">Seguir Viendo</button>
            <button
              class="btn-primary"
              @click="manejarCheckout"
              :disabled="loadingCheckout">
              {{ loadingCheckout ? 'Procesando...' : 'Ir a Pagar' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import apiClient from '@/api/axiosConfig.js'
import Swal from 'sweetalert2'

export default {
  name: 'CartView',
  data() {
    return {
      carrito: null,
      loading: true,
      loadingCheckout: false,
    }
  },
  mounted() {
    this.cargarCarrito(); // <-- Traducido
  },
  // --- MÉTODOS TRADUCIDOS ---
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

    async manejarEliminacion(productoId) {
      const token = localStorage.getItem('userToken');

      const result = await Swal.fire({
        title: '¿Eliminar producto?',
        text: "El producto se quitará de tu carrito.",
        icon: 'question',
        showCancelButton: true,
        confirmButtonColor: '#0071e3',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Sí, eliminar',
        cancelButtonText: 'Cancelar'
      })

      if (result.isConfirmed) {
        try {
          const response = await apiClient.delete(`/carrito/${productoId}`, {
            headers: { Authorization: `Bearer ${token}` }
          });

          this.carrito = response.data; // Actualiza el carrito
          Swal.fire({
            icon: 'success',
            title: 'Producto eliminado',
            timer: 2000,
            showConfirmButton: false,
          });

        } catch (error) {
          console.error("Error eliminando item:", error);
          Swal.fire({
            icon: 'error',
            title: 'No se pudo eliminar',
            confirmButtonColor: '#0071e3',
          });
        }
      }
    },

    async manejarCheckout() {
      const token = localStorage.getItem('userToken');

      const result = await Swal.fire({
        title: '¿Confirmar Compra?',
        text: `El total es $${this.formatearPrecio(this.carrito.total)}. Esta acción generará una factura y descontará el stock.`,
        icon: 'question',
        showCancelButton: true,
        confirmButtonColor: '#0071e3',
        cancelButtonColor: '#6e6e73',
        confirmButtonText: 'Sí, pagar',
        cancelButtonText: 'Cancelar'
      })

      if (!result.isConfirmed) return;

      this.loadingCheckout = true;
      try {
        const response = await apiClient.post('/carrito/facturar', {}, {
          headers: { Authorization: `Bearer ${token}` }
        });

        await Swal.fire({
          icon: 'success',
          title: '¡Compra exitosa!',
          text: `Se ha generado tu factura ID: ${response.data.id}`,
          confirmButtonColor: '#0071e3',
        });

        this.irATienda(); // Envía al inicio

      } catch (error) {
        if (error.response && error.response.status === 409) {
          Swal.fire({
            icon: 'error',
            title: 'Stock insuficiente',
            text: 'Uno de los productos en tu carrito se ha agotado.',
            confirmButtonColor: '#0071e3',
          });
          this.cargarCarrito(); // Recarga el carrito
        } else if (error.response && error.response.status === 400) {
          Swal.fire({
            icon: 'error',
            title: 'Tu carrito está vacío',
            confirmButtonColor: '#0071e3',
          });
        } else {
          Swal.fire({
            icon: 'error',
            title: 'Error al procesar la compra',
            confirmButtonColor: '#0071e3',
          });
        }
      } finally {
        this.loadingCheckout = false;
      }
    },

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
}

.btn-delete {
  background: transparent;
  border: none;
  color: #ff3b30;
  font-size: 24px;
  font-weight: 700;
  cursor: pointer;
  padding: 0 10px;
  transition: transform 0.2s ease;
}
.btn-delete:hover {
  transform: scale(1.2);
}


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

.cart-actions {
  display: flex;
  gap: 15px;
  margin-top: 30px;
}

.btn-primary,
.btn-secondary {
  border: none;
  border-radius: 8px;
  padding: 12px 25px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-primary {
  background-color: #007aff;
  color: white;
}
.btn-primary:hover {
  background-color: #0056b3;
}
.btn-primary:disabled {
  background-color: #8e8e93;
  cursor: not-allowed;
}

.btn-secondary {
  background-color: #e5e5ea;
  color: #1d1d1f;
}
.btn-secondary:hover {
  background-color: #d2d2d7;
}
</style>
