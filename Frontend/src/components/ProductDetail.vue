<template>
  <div class="product-detail-layout">
    <div classs="product-gallery">
      <div class="gallery-placeholder">
        <img v-if="product.image" :src="product.image" :alt="product.name" />
        <span v-else>Galería de Imágenes</span>
      </div>
    </div>

    <div class="product-config">
      <h2 class="product-detail-title">{{ product.name }}</h2>
      <h3 class="product-detail-subtitle">Observa tu futuro {{ product.category }}</h3>

      <div v-if="showColor" class="option-group">
        <div class="option-title">Selecciona un color:</div>
        <div class="color-options">
          <div
            v-for="color in colors"
            :key="color.name"
            :class="['color-option', { selected: selectedOptions.color === color.value }]"
            :style="{ backgroundColor: color.value }"
            @click="selectedOptions.color = color.value"
          >
            <span class="color-tooltip">{{ color.name }}</span>
          </div>
        </div>
      </div>

      <div v-if="showCapacity" class="option-group">
        <div class="option-title">Selecciona una capacidad:</div>
        <div class="choice-options">
          <div
            v-for="capacity in capacities"
            :key="capacity"
            :class="['choice-option', { selected: selectedOptions.capacity === capacity }]"
            @click="selectedOptions.capacity = capacity"
          >
            {{ capacity }}
          </div>
        </div>
      </div>

      <div v-if="showSim" class="option-group">
        <div class="option-title">Elije una configuración SIM:</div>
        <div class="choice-options">
          <div
            v-for="sim in simOptions"
            :key="sim"
            :class="['choice-option', { selected: selectedOptions.sim === sim }]"
            @click="selectedOptions.sim = sim"
          >
            {{ sim }}
          </div>
        </div>
      </div>

      <div v-if="showRam" class="option-group">
        <div class="option-title">Selecciona la memoria RAM:</div>
        <div class="choice-options">
          <div
            v-for="ram in ramOptions"
            :key="ram"
            :class="['choice-option', { selected: selectedOptions.ram === ram }]"
            @click="selectedOptions.ram = ram"
          >
            {{ ram }}
          </div>
        </div>
      </div>

      <div v-if="showProcessor" class="option-group">
        <div class="option-title">Selecciona el procesador:</div>
        <div class="choice-options">
          <div
            v-for="processor in processorOptions"
            :key="processor"
            :class="['choice-option', { selected: selectedOptions.processor === processor }]"
            @click="selectedOptions.processor = processor"
          >
            {{ processor }}
          </div>
        </div>
      </div>

      <div v-if="showScreen" class="option-group">
        <div class="option-title">Selecciona el tamaño de pantalla:</div>
        <div class="choice-options">
          <div
            v-for="screen in screenOptions"
            :key="screen"
            :class="['choice-option', { selected: selectedOptions.screen === screen }]"
            @click="selectedOptions.screen = screen"
          >
            {{ screen }}
          </div>
        </div>
      </div>

      <div v-if="showKeyboard" class="option-group">
        <div class="option-title">Selecciona el idioma del teclado:</div>
        <div class="choice-options">
          <div
            v-for="keyboard in keyboardOptions"
            :key="keyboard"
            :class="['choice-option', { selected: selectedOptions.keyboard === keyboard }]"
            @click="selectedOptions.keyboard = keyboard"
          >
            {{ keyboard }}
          </div>
        </div>
      </div>

      <div class="option-group final-step">
        <div class="option-title">¿Todo listo?</div>
        <div class="action-buttons">
          <button class="btn btn-primary" @click="addToCart">AÑADIR AL CARRITO</button>
          <a href="#" class="btn-link" @click.prevent="goBack">ATRÁS</a>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ProductDetail',
  props: {
    product: Object,
  },
  data() {
    return {
      // Objeto para guardar todas las selecciones
      selectedOptions: {
        color: '',
        capacity: '',
        sim: '',
        ram: '',
        processor: '',
        screen: '',
        keyboard: '',
      },

      // --- Listas Maestras de Opciones ---
      // (En una app real, esto vendría del 'product' prop)
      colors: [
        { name: 'Naranja', value: '#FF9500' },
        { name: 'Azul', value: '#007AFF' },
        { name: 'Verde', value: '#34C759' },
        { name: 'Rojo', value: '#FF3B30' },
        { name: 'Gris', value: '#8E8E93' },
      ],
      capacities: ['256GB', '512GB', '1TB', '2TB'],
      simOptions: ['ESIM', 'SIM'],
      ramOptions: ['8GB', '16GB', '32GB', '64GB'],
      processorOptions: ['M3', 'M3 Pro', 'M3 Max'],
      screenOptions: ['11"', '13"', '14"', '16"'],
      keyboardOptions: ['Español', 'Inglés (EE.UU.)'],
    }
  },
  computed: {
    // --- Lógica para mostrar/ocultar opciones ---

    isMacDesktop() {
      // Asumimos que los Mac de escritorio no incluyen "Book" o "iMac" en el nombre
      return (
        this.product.category === 'Mac' &&
        !this.product.name.includes('Book') &&
        !this.product.name.includes('iMac')
      )
    },
    isMacBook() {
      return this.product.category === 'Mac' && this.product.name.includes('Book')
    },
    isIMac() {
      return this.product.category === 'Mac' && this.product.name.includes('iMac')
    },

    showColor() {
      return ['iPhone', 'iPad', 'Mac'].includes(this.product.category)
    },
    showCapacity() {
      return ['iPhone', 'iPad', 'Mac'].includes(this.product.category)
    },
    showSim() {
      return ['iPhone', 'iPad'].includes(this.product.category)
    },
    showRam() {
      return this.product.category === 'Mac'
    },
    showProcessor() {
      return this.product.category === 'Mac'
    },
    showScreen() {
      return ['iPad', 'Mac'].includes(this.product.category) && !this.isMacDesktop
    },
    showKeyboard() {
      return this.isMacBook || this.isIMac
    },
  },
  methods: {
    // El 'select' ahora es dinámico con v-model o @click

    addToCart() {
      // --- Validación Dinámica ---
      const requiredOptions = []
      if (this.showColor) requiredOptions.push({ key: 'color', name: 'Color' })
      if (this.showCapacity) requiredOptions.push({ key: 'capacity', name: 'Capacidad' })
      if (this.showSim) requiredOptions.push({ key: 'sim', name: 'SIM' })
      if (this.showRam) requiredOptions.push({ key: 'ram', name: 'RAM' })
      if (this.showProcessor) requiredOptions.push({ key: 'processor', name: 'Procesador' })
      if (this.showScreen) requiredOptions.push({ key: 'screen', name: 'Pantalla' })
      if (this.showKeyboard) requiredOptions.push({ key: 'keyboard', name: 'Teclado' })

      for (const option of requiredOptions) {
        if (!this.selectedOptions[option.key]) {
          alert(`Por favor, selecciona una opción para: ${option.name}.`)
          return
        }
      }

      // --- Precio (Lógica de ejemplo) ---
      let basePrice = 1099.99 // Precio base
      // (Aquí iría tu lógica de precios complejos)
      if (this.selectedOptions.capacity === '1TB') basePrice += 200
      if (this.selectedOptions.ram === '32GB') basePrice += 400

      const priceWithIVA = basePrice * 1.16

      // --- Creación Dinámica del Item ---
      const cartItem = {
        id: Date.now(),
        name: this.product.name,
        price: priceWithIVA,
        basePrice: basePrice,
        image: this.product.image || '/img/default.png', // Añadir imagen al carrito
      }

      // Añade solo las opciones que fueron seleccionadas
      // Dentro de addToCart() en ProductDetail.vue

      // Añade solo las opciones que fueron seleccionadas
      for (const option of requiredOptions) {
        // --- INICIO DEL CAMBIO ---
        if (option.key === 'color') {
          // Si la opción es 'color', busca el nombre del color
          const selectedColorObject = this.colors.find(
            (c) => c.value === this.selectedOptions.color,
          )

          // Guarda el NOMBRE (ej: "Naranja") en lugar del valor (ej: "#FF9500")
          cartItem.color = selectedColorObject
            ? selectedColorObject.name
            : this.selectedOptions.color
        } else {
          // Para todas las demás opciones, guarda el valor como siempre
          cartItem[option.key] = this.selectedOptions[option.key]
        }
        // --- FIN DEL CAMBIO ---
      }

      console.log('Añadiendo al carrito:', cartItem)
      this.$emit('add-to-cart', cartItem)
    },
    goBack() {
      this.$emit('change-page', 'products', this.product.category)
    },
  },
}
</script>

<style scoped>
/* --- Nueva Estructura de 2 Columnas --- */
.product-detail-layout {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 50px;
  max-width: 1200px;
  margin: 40px auto;
  padding: 0 20px;
}

/* --- Columna Izquierda: Galería --- */
.gallery-placeholder {
  width: 100%;
  height: 500px;
  background-color: #f5f5f7;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: #888;
  position: sticky;
  top: 100px; /* Para que se quede fija al hacer scroll */
}
.gallery-placeholder img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  padding: 20px;
}

/* --- Columna Derecha: Configuración --- */
.product-config {
  background-color: #fff;
  border-radius: 10px;
  padding: 30px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.07);
}

.product-detail-title {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 10px;
  color: #1d1d1f;
  text-align: left; /* Alineado a la izquierda como el PDF */
}

.product-detail-subtitle {
  font-size: 18px;
  font-weight: 400;
  color: #555;
  margin-bottom: 30px;
  text-align: left;
}

.option-group {
  margin-bottom: 25px;
}

.option-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 15px; /* Más espacio */
}

/* --- Opciones de Botones (Capacidad, SIM, etc.) --- */
.choice-options {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.choice-option {
  padding: 12px 20px;
  border: 1px solid #d2d2d7;
  border-radius: 8px; /* Botones rectangulares como el PDF */
  cursor: pointer;
  transition: all 0.3s;
  font-weight: 500;
  background-color: #fcfcfc;
}
.choice-option:hover {
  border-color: #888;
}
.choice-option.selected {
  background-color: #0071e3;
  color: white;
  border-color: #0071e3;
  box-shadow: 0 2px 5px rgba(0, 113, 227, 0.3);
}

/* --- Opciones de Color --- */
.color-options {
  display: flex;
  gap: 15px;
  margin-bottom: 15px;
}

.color-option {
  width: 35px;
  height: 35px;
  border-radius: 50%;
  cursor: pointer;
  border: 3px solid #fff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
  transition: all 0.2s ease;
  position: relative; /* Para el tooltip */
}
.color-option:hover {
  transform: scale(1.1);
}
.color-option.selected {
  border-color: #0071e3;
  box-shadow: 0 0 0 2px #0071e3;
}

/* Tooltip de color (opcional pero útil) */
.color-option .color-tooltip {
  visibility: hidden;
  width: 120px;
  background-color: #333;
  color: #fff;
  text-align: center;
  border-radius: 6px;
  padding: 5px 0;
  position: absolute;
  z-index: 1;
  bottom: 125%; /* Position above the element */
  left: 50%;
  margin-left: -60px; /* Use half of the width */
  opacity: 0;
  transition: opacity 0.3s;
}
.color-option:hover .color-tooltip {
  visibility: visible;
  opacity: 1;
}

/* --- Botones de Acción Finales --- */
.final-step {
  border-top: 1px solid #eee;
  padding-top: 25px;
  margin-top: 35px;
}

.action-buttons {
  display: flex;
  align-items: center; /* Alinea "Atrás" con el botón */
  gap: 20px;
  margin-top: 15px;
}

.btn-primary {
  background-color: #007aff;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 15px 30px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.3s ease;
}
.btn-primary:hover {
  background-color: #0056b3;
}

.btn-link {
  background: none;
  border: none;
  color: #007aff;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  text-decoration: none;
}
.btn-link:hover {
  text-decoration: underline;
}

/* --- Responsive (Celulares) --- */
@media (max-width: 768px) {
  .product-detail-layout {
    grid-template-columns: 1fr; /* Apila las columnas */
  }
  .gallery-placeholder {
    height: 300px;
    position: static; /* Quita el sticky en móvil */
  }
  .product-config {
    box-shadow: none;
    padding: 10px;
  }
}
</style>
