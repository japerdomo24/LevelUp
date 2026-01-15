<!-- eslint-disable no-unused-vars -->
<template>
  <div class="cart">
    <div class="cart-container">
      <h2 class="cart-title">Mi Carrito</h2>

      <div v-if="loading" class="cart-empty">
        <p>Cargando tu carrito...</p>
      </div>

      <!-- Estado Vacío -->
      <div v-else-if="!carrito || !carrito.items || carrito.items.length === 0" class="cart-empty">
        <div class="empty-icon">🛒</div>
        <p>Tu carrito está vacío</p>
        <button class="btn-secondary" @click="irATienda">Ir a la tienda</button>
      </div>

      <!-- Contenido del Carrito -->
      <div v-else>

        <!-- Cabecera de acciones (Vaciar) -->
        <div class="cart-actions-header">
           <span class="item-count">{{ carrito.items.length }} artículo(s)</span>
           <button class="btn-text-danger" @click="vaciarCarrito">Vaciar Carrito</button>
        </div>

        <div class="cart-items-list">
          <div v-for="item in carrito.items" :key="item.productoId" class="cart-item">
            <div class="item-details">
              <div class="item-name">{{ item.serie }}</div>
              <div class="item-specs">
                <span v-if="item.color">{{ item.color }} | </span>
                <span v-if="item.capacity">{{ item.capacity }} | </span>
                Cant: {{ item.cantidad }}
              </div>
            </div>

            <div class="item-right">
                <div class="item-price">
                  ${{ formatearPrecio(item.precioUnitario * item.cantidad) }}
                </div>
                <!-- Botón eliminar individual -->
                <button class="btn-delete-item" @click="manejarEliminacion(item.productoId)" title="Eliminar del carrito">
                   ✕
                </button>
            </div>
          </div>
        </div>

        <!-- SECCIÓN DE PAGOS / TARJETAS -->
        <div class="payment-section">
            <h3 class="section-title">Método de Pago</h3>

            <!-- Lista de tarjetas añadidas -->
            <div v-if="cards.length > 0" class="cards-list">
                <div
                    v-for="(card, index) in cards"
                    :key="index"
                    class="card-item-display"
                    :class="{ 'selected': selectedCardIndex === index }"
                    @click="selectedCardIndex = index"
                >
                    <div class="card-icon">💳</div>
                    <div class="card-info">
                        <div class="card-number">{{ card.masked }}</div>
                        <div class="card-name">{{ card.name }}</div>
                    </div>
                    <div v-if="selectedCardIndex === index" class="check-icon">✓</div>
                </div>
            </div>

            <div v-else class="no-cards-msg">
                <p>No tienes tarjetas agregadas. Añade una para continuar.</p>
            </div>

            <!-- Formulario para agregar tarjeta -->
            <div v-if="showCardForm" class="add-card-form">
                <div class="form-title">Nueva Tarjeta</div>
                <div class="form-group">
                    <input v-model="newCard.number" type="text" placeholder="Número de tarjeta (16-20 dígitos )" maxlength="19" class="input-field" />
                </div>
                <div class="form-row">
                    <input v-model="newCard.name" type="text" placeholder="Nombre titular" class="input-field" />
                    <input v-model="newCard.expiry" type="text" placeholder="MM/YY" maxlength="5" class="input-field small" />
                    <input v-model="newCard.cvv" type="text" placeholder="CVV" maxlength="3" class="input-field small" />
                </div>
                <div class="form-actions">
                    <button class="btn-save-card" @click="agregarTarjeta">Guardar Tarjeta</button>
                    <button class="btn-cancel-card" @click="cancelarTarjeta">Cancelar</button>
                </div>
            </div>

            <!-- Botón para mostrar formulario (solo si no está visible) -->
            <button v-if="!showCardForm" class="btn-add-card" @click="showCardForm = true">
                + Agregar nueva tarjeta
            </button>
        </div>

        <!-- Resumen y Checkout -->
        <div class="cart-summary">
          <div class="cart-total">
            TOTAL (Impuestos incluidos):
            <span>${{ formatearPrecio(carrito.total) }}</span>
          </div>

          <button
            class="btn-checkout"
            @click="manejarCheckout"
            :disabled="loadingCheckout"
            :class="{ 'disabled': loadingCheckout }"
          >
             {{ loadingCheckout ? 'Procesando...' : 'Facturar y Pagar' }}
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
  name: 'CartView',
  data() {
    return {
      carrito: null,
      loading: true,
      loadingCheckout: false,

      // Gestión de Tarjetas
      cards: [], // Array para almacenar las tarjetas localmente
      showCardForm: false,
      selectedCardIndex: -1, // Índice de la tarjeta seleccionada para pagar
      newCard: {
        number: '',
        name: '',
        expiry: '',
        cvv: ''
      }
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

    formatearPrecio(value) {
      if (!value) return '0,00'
      return Number(value).toFixed(2).replace('.', ',')
    },

    irATienda() {
      this.$emit('change-page', 'home')
    },

    // --- NUEVAS FUNCIONALIDADES ---

    // 1. Eliminar un producto
    async manejarEliminacion(productoId) {
        const result = await Swal.fire({
            title: '¿Eliminar producto?',
            text: "¿Estás seguro de que quieres quitar este artículo del carrito?",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#ff3b30',
            cancelButtonColor: '#86868b',
            confirmButtonText: 'Sí, eliminar',
            cancelButtonText: 'Cancelar'
        });

        if (result.isConfirmed) {
            const token = localStorage.getItem('userToken');
            try {
                // Llamada a la API (Asumiendo endpoint REST estándar)
                // await apiClient.delete(`/carrito/item/${productoId}`);
                const response = await apiClient.delete(`/carrito/${productoId}`, {headers: { Authorization: `Bearer ${token}` }
                });

                // Actualización Local (Simulación para que funcione la UI inmediatamente)
                //const itemIndex = this.carrito.items.findIndex(i => i.productoId === productoId);
                //if (itemIndex > -1) {
                  //  const item = this.carrito.items[itemIndex];
                    //this.carrito.total -= (item.precioUnitario * item.cantidad);
                    //this.carrito.items.splice(itemIndex, 1);
                //}
                this.carrito = response.data; // Actualizar con la respuesta del back

                Swal.fire({
                    icon: 'success',
                    title: 'Eliminado',
                    toast: true,
                    position: 'top-end',
                    showConfirmButton: false,
                    timer: 2000
                });
            // eslint-disable-next-line no-unused-vars
            } catch (error) {
                Swal.fire('Error', 'No se pudo eliminar el producto', 'error');
            }
        }
    },

    // 2. Vaciar carrito
    async vaciarCarrito() {
        const result = await Swal.fire({
            title: '¿Vaciar todo el carrito?',
            text: "Esta acción no se puede deshacer.",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#ff3b30',
            cancelButtonColor: '#86868b',
            confirmButtonText: 'Sí, vaciar todo'
        });

        if (result.isConfirmed) {
            try {
                // await apiClient.delete('/carrito');
                this.carrito.items = [];
                this.carrito.total = 0;

                Swal.fire('Carrito Vaciado', '', 'success');
            // eslint-disable-next-line no-unused-vars
            } catch (error) {
                Swal.fire('Error', 'Hubo un problema al vaciar el carrito', 'error');
            }
        }
    },

    // 3. Agregar Tarjeta
    agregarTarjeta() {
        // Validación básica
        if (!this.newCard.number || this.newCard.number.length < 13 || !this.newCard.name || !this.newCard.cvv) {
            Swal.fire('Datos incompletos', 'Por favor revisa los datos de la tarjeta.', 'warning');
            return;
        }

        // Crear objeto tarjeta (Simulado)
        const maskedNumber = '**** **** **** ' + this.newCard.number.slice(-4);

        this.cards.push({
            id: Date.now(),
            name: this.newCard.name,
            masked: maskedNumber,
            // No guardamos el número real ni CVV por seguridad en esta demo
        });

        // Seleccionar automáticamente la nueva tarjeta
        this.selectedCardIndex = this.cards.length - 1;

        // Limpiar y cerrar formulario
        this.newCard = { number: '', name: '', expiry: '', cvv: '' };
        this.showCardForm = false;

        Swal.fire({
            icon: 'success',
            title: 'Tarjeta Agregada',
            toast: true,
            position: 'top-end',
            showConfirmButton: false,
            timer: 2000
        });
    },

    cancelarTarjeta() {
        this.newCard = { number: '', name: '', expiry: '', cvv: '' };
        this.showCardForm = false;
    },

    // 4. Facturar (Checkout)
    async manejarCheckout() {
        // Validación: Debe haber tarjetas
        if (this.cards.length === 0) {
            Swal.fire({
                icon: 'warning',
                title: 'Falta método de pago',
                text: 'Debes agregar al menos una tarjeta para poder facturar.',
                confirmButtonColor: '#0071e3'
            });
            return;
        }

        // Validación: Debe haber una tarjeta seleccionada (opcional, pero buena UX)
        if (this.selectedCardIndex === -1) {
             Swal.fire('Selecciona una tarjeta', 'Haz clic sobre la tarjeta con la que deseas pagar.', 'info');
             return;
        }

        this.loadingCheckout = true;
        const token = localStorage.getItem('userToken');

        try {
            // Simular proceso de pago
            await new Promise(resolve => setTimeout(resolve, 2000));
            const response = await apiClient.post('/carrito/facturar', {}, {headers: { Authorization: `Bearer ${token}` }
            });

            await Swal.fire({
                icon: 'success',
                title: '¡Compra realizada!',
                text: `Factura generada con ID: ${response.data.id}`,
                confirmButtonColor: '#34c759'
            });

            // Limpiar carrito tras éxito
            this.carrito = { items: [], total: 0 };
            this.$emit('change-page', 'home');

        // eslint-disable-next-line no-unused-vars
        } catch (error) {
            Swal.fire('Error', 'No se pudo procesar el pago. Intenta nuevamente.', 'error');
        } finally {
            this.loadingCheckout = false;
        }
    }
  },
}
</script>

<style scoped>
.cart {
  padding-top: 40px;
  display: flex;
  justify-content: center;
  padding-bottom: 40px;
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
  margin-bottom: 20px;
  text-align: center;
  color: #1d1d1f;
}

.cart-empty {
  text-align: center;
  padding: 60px 20px;
  color: #6e6e73;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}
.empty-icon { font-size: 40px; margin-bottom: 10px; }

/* Header acciones */
.cart-actions-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;
    padding-bottom: 10px;
    border-bottom: 1px solid #eee;
}
.item-count { font-weight: 600; color: #1d1d1f; }
.btn-text-danger {
    background: none; border: none; color: #ff3b30; cursor: pointer; font-size: 14px; font-weight: 500;
}
.btn-text-danger:hover { text-decoration: underline; }

/* Lista de Items */
.cart-items-list {
  border-bottom: 1px solid #d2d2d7;
  margin-bottom: 30px;
}

.cart-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 0;
  border-bottom: 1px solid #eee;
}
.cart-item:last-child {
  border-bottom: none;
}

.item-details { flex: 1; }
.item-name { font-size: 18px; font-weight: 600; color: #1d1d1f; }
.item-specs { color: #6e6e73; margin-top: 5px; font-size: 14px; }

.item-right { display: flex; align-items: center; gap: 20px; }
.item-price { font-weight: 700; font-size: 18px; color: #1d1d1f; }

/* Botón Eliminar Item */
.btn-delete-item {
    background: #f5f5f7; border: none; color: #86868b;
    width: 30px; height: 30px; border-radius: 50%;
    cursor: pointer; display: flex; align-items: center; justify-content: center;
    transition: all 0.2s;
}
.btn-delete-item:hover { background: #ff3b30; color: white; }

/* Sección Pagos */
.payment-section { margin-bottom: 30px; padding: 20px; background: #fbfbfd; border-radius: 12px; }
.section-title { font-size: 18px; font-weight: 600; margin-bottom: 15px; color: #1d1d1f; }
.no-cards-msg { color: #86868b; font-size: 14px; margin-bottom: 15px; font-style: italic; }

.cards-list { display: flex; flex-direction: column; gap: 10px; margin-bottom: 15px; }
.card-item-display {
    display: flex; align-items: center; gap: 15px;
    padding: 12px; background: white; border: 1px solid #d2d2d7; border-radius: 8px;
    cursor: pointer; transition: all 0.2s;
}
.card-item-display:hover { border-color: #0071e3; }
.card-item-display.selected { border-color: #0071e3; background-color: #f0f8ff; box-shadow: 0 0 0 1px #0071e3; }
.card-icon { font-size: 24px; }
.card-info { flex: 1; }
.card-number { font-weight: 600; color: #1d1d1f; font-family: monospace; }
.card-name { font-size: 12px; color: #6e6e73; text-transform: uppercase; }
.check-icon { color: #0071e3; font-weight: bold; }

.btn-add-card {
    background: none; border: 1px dashed #d2d2d7; width: 100%; padding: 12px;
    border-radius: 8px; color: #0071e3; font-weight: 500; cursor: pointer;
}
.btn-add-card:hover { background: #f5f5f7; border-style: solid; }

/* Formulario Tarjeta */
.add-card-form { background: white; padding: 15px; border-radius: 8px; border: 1px solid #d2d2d7; margin-top: 10px; }
.form-title { font-size: 14px; font-weight: 600; margin-bottom: 10px; color: #1d1d1f; }
.form-group { margin-bottom: 10px; }
.form-row { display: flex; gap: 10px; margin-bottom: 15px; }
.input-field {
    width: 100%; padding: 10px; border: 1px solid #d2d2d7; border-radius: 6px; font-size: 14px;
}
.input-field.small { width: 80px; text-align: center; }
.form-actions { display: flex; gap: 10px; }
.btn-save-card {
    background: #0071e3; color: white; border: none; padding: 8px 16px; border-radius: 6px; cursor: pointer; font-size: 13px; font-weight: 500;
}
.btn-cancel-card {
    background: #e5e5ea; color: #1d1d1f; border: none; padding: 8px 16px; border-radius: 6px; cursor: pointer; font-size: 13px;
}

/* Resumen y Checkout */
.cart-summary {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  border-top: 1px solid #d2d2d7;
  padding-top: 20px;
}

.cart-total {
  font-size: 18px;
  font-weight: 600;
  color: #1d1d1f;
  margin-bottom: 20px;
}
.cart-total span {
  font-size: 24px;
  font-weight: 700;
  margin-left: 10px;
}

.btn-checkout {
    background-color: #34c759;
    color: white;
    border: none;
    border-radius: 8px;
    padding: 15px 40px;
    font-size: 18px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
    width: 100%;
}
.btn-checkout:hover { background-color: #2eb14f; transform: translateY(-1px); }
.btn-checkout:active { transform: translateY(0); }
.btn-checkout.disabled { opacity: 0.7; cursor: not-allowed; }

.btn-secondary {
  border: none;
  border-radius: 8px;
  padding: 12px 25px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  background-color: #e5e5ea;
  color: #1d1d1f;
}
.btn-secondary:hover { background-color: #d2d2d7; }
</style>
