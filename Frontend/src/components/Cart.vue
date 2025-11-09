<template>
  <div class="cart">
    <div class="cart-container">
      <h2 class="cart-title">Carrito</h2>

      <div v-if="cartItems.length === 0" class="cart-empty">Tu carrito está vacío</div>

      <div v-else>
        <div class="cart-items-list">
          <div v-for="item in cartItems" :key="item.id" class="cart-item">
            <img :src="item.image" :alt="item.name" class="item-image" />

            <div class="item-details">
              <div class="item-name">{{ item.name }}</div>
              <div class="item-specs">
                {{ formatSpecs(item) }}
              </div>
            </div>

            <div class="item-price">${{ formatPrice(item.basePrice) }}</div>
          </div>
        </div>

        <div class="cart-summary">
          <div class="cart-total">
            TOTAL (con IVA del 16%):
            <span>${{ formatPrice(calculateTotal()) }}</span>
          </div>

          <div class="cart-actions">
            <button class="btn-secondary" @click="goShopping">seguir viendo</button>
            <button class="btn-primary">ir a pagar</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CartView',
  props: {
    cartItems: Array,
  },
  methods: {
    // Calcula el total usando item.price (que es el precio final con IVA)
    calculateTotal() {
      return this.cartItems.reduce((total, item) => total + item.price, 0)
    },

    // Formatea el precio al estilo del PDF (con coma)
    formatPrice(value) {
      if (!value) return '0,00'
      return value.toFixed(2).replace('.', ',')
    },

    // Crea dinámicamente el string de especificaciones
    formatSpecs(item) {
      let specs = []

      // --- CAMBIO AQUÍ ---
      // Ahora 'item.color' contendrá el nombre (ej: "Naranja")
      // gracias al cambio en ProductDetail.vue
      if (item.color) specs.push(`Color ${item.color}`)

      if (item.capacity) specs.push(item.capacity)
      if (item.sim) specs.push(item.sim)
      if (item.ram) specs.push(item.ram)
      if (item.processor) specs.push(item.processor)
      if (item.screen) specs.push(item.screen)
      if (item.keyboard) specs.push(item.keyboard)

      return specs.join(' / ')
    },

    // Método para el botón "seguir viendo"
    goShopping() {
      this.$emit('change-page', 'home') // O 'products' si lo prefieres
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
  padding: 30px 40px; /* Más padding */
  max-width: 800px;
  width: 100%;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.07);
}

.cart-title {
  font-size: 28px; /* Más grande */
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
}

.cart-items-list {
  border-bottom: 1px solid #d2d2d7; /* Separador antes del total */
  margin-bottom: 20px;
}

.cart-item {
  display: flex;
  align-items: center; /* Alinea imagen y texto */
  padding: 20px 0;
  border-bottom: 1px solid #eee; /* Separador más suave entre items */
}
.cart-item:last-child {
  border-bottom: none;
}

/* --- ESTILOS DE IMAGEN AÑADIDOS --- */
.item-image {
  width: 80px;
  height: 80px;
  object-fit: contain;
  border-radius: 8px;
  background-color: #f5f5f7;
  margin-right: 20px;
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
  font-weight: 700; /* Más grueso */
  font-size: 18px;
  margin-left: 20px; /* Espacio para que no se pegue */
  color: #1d1d1f;
}

.cart-summary {
  /* Alinea el total y los botones */
  display: flex;
  flex-direction: column;
  align-items: flex-end; /* Alinea todo a la derecha */
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

/* --- ESTILOS DE BOTONES AÑADIDOS --- */
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
  background-color: #007aff; /* Azul Apple */
  color: white;
}
.btn-primary:hover {
  background-color: #0056b3;
}

.btn-secondary {
  background-color: #e5e5ea; /* Gris claro Apple */
  color: #1d1d1f;
}
.btn-secondary:hover {
  background-color: #d2d2d7;
}
</style>
