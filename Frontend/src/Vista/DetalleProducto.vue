<template>
  <div class="product-detail-layout">
    <div classs="product-gallery">
      <div class="gallery-placeholder">
        <img v-if="product.imagen" :src="product.imagen" :alt="product.serie" />
        <span v-else>Imagen no disponible</span>
      </div>
    </div>

    <div class="product-config">
      <h2 class="product-detail-title">{{ product.serie }} {{ product.modelo }}</h2>
      <h3 class="product-detail-subtitle">Observa tu futuro {{ product.tipo }}</h3>

      <div class="option-group" v-if="mostrarColores">
        <div class="option-title">Selecciona un color:</div>
        <div class="choice-options">
          <div
            v-for="color in product.colores"
            :key="color"
            :class="['choice-option', { selected: opcionesSeleccionadas.color === color }]"
            @click="opcionesSeleccionadas.color = color"
          >
            {{ color }}
          </div>
        </div>
      </div>

      <div class="option-group" v-if="mostrarCapacidades">
        <div class="option-title">Selecciona una capacidad:</div>
        <div class="choice-options">
          <div
            v-for="capacity in product.capacidades"
            :key="capacity"
            :class="['choice-option', { selected: opcionesSeleccionadas.capacity === capacity }]"
            @click="opcionesSeleccionadas.capacity = capacity"
          >
            {{ capacity }}
          </div>
        </div>
      </div>

      <div class="option-group" v-if="mostrarConfigSim">
        <div class="option-title">Configuración SIM:</div>
        <div class="info-tag">{{ product.configSim }}</div>
      </div>

      <div class="option-group" v-if="user?.rol !== 'admin'">
        <div class="option-title">Cantidad:</div>
        <input
          type="number"
          v-model.number="quantity"
          class="ingreso-cantidad"
          min="1"
          max="99"
        />
</div>

      <div class="option-group final-step">
        <div class="option-title">¿Todo listo?</div>

        <div class="action-buttons">
          <button
            v-if="user?.rol === 'admin'"
            class="btn btn-admin-edit"
            @click="editarProducto">
            ✏️ Editar Producto
          </button>

          <button
            v-else
            class="btn btn-primary"
            @click="agregarAlCarrito">
            AÑADIR AL CARRITO
          </button>

          <a href="#" class="btn-link" @click.prevent="volverAtras">ATRÁS</a>
        </div>

      </div>
    </div>
  </div>
</template>

<script>
import Swal from 'sweetalert2';

export default {
  name: 'ProductDetail',
  props: {
    product: Object,
    user: Object,
  },
  data() {
    return {
      opcionesSeleccionadas: { // <-- Traducido
        color: '',
        capacity: '',
      },
      quantity: 1,
    }
  },
  computed: {
    mostrarColores() { // <-- Traducido
      return this.product.colores &&
        this.product.colores.length > 0 &&
        !(this.product.colores.length === 1 && this.product.colores[0] === 'N/A');
    },
    mostrarCapacidades() { // <-- Traducido
      return this.product.capacidades &&
        this.product.capacidades.length > 0 &&
        !(this.product.capacidades.length === 1 && this.product.capacidades[0] === 'N/A');
    },
    mostrarConfigSim() {
      return this.product.configSim && this.product.configSim !== 'N/A';
    }
  },

  methods: {
    agregarAlCarrito() {
      const opcionesRequeridas = []
      if (this.mostrarColores) {
        opcionesRequeridas.push({ key: 'color', name: 'Color' });
      }
      if (this.mostrarCapacidades) {
        opcionesRequeridas.push({ key: 'capacity', name: 'Capacidad' });
      }

      for (const opcion of opcionesRequeridas) {
        if (!this.opcionesSeleccionadas[opcion.key]) {
          Swal.fire({
            icon: 'warning',
            title: 'Opción requerida',
            text: `Por favor, selecciona una opción para: ${opcion.name}.`,
            confirmButtonColor: '#0071e3',
          });
          return;
        }
      }

      const itemCarrito = { // <-- Traducido
        ...this.product,
        selectedColor: this.opcionesSeleccionadas.color,
        selectedCapacity: this.opcionesSeleccionadas.capacity,
        quantity: this.quantity,
      }

      this.$emit('add-to-cart', itemCarrito)
    },

    editarProducto() {
      this.$emit('edit-product', this.product);
    },

    volverAtras() {
      this.$emit('change-page', 'products', this.product.tipo)
    },
  },
}
</script>

<style scoped>
.product-detail-layout {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 50px;
  max-width: 1200px;
  margin: 40px auto;
  padding: 0 20px;
}
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
  top: 100px;
}
.gallery-placeholder img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  padding: 20px;
}
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
  text-align: left;
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
  margin-bottom: 15px;
}
.choice-options {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}
.choice-option {
  padding: 12px 20px;
  border: 1px solid #d2d2d7;
  border-radius: 8px;
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
.info-tag {
  padding: 12px 20px;
  border: 1px solid #d2d2d7;
  border-radius: 8px;
  font-weight: 500;
  background-color: #f5f5f7;
  color: #1d1d1f;
  display: inline-block;
}
.ingreso-cantidad{
  width: 80px;
  padding: 10px;
  border: 1px solid #d2d2d7;
  border-radius: 8px;
  font-size: 16px;
}
.final-step {
  border-top: 1px solid #eee;
  padding-top: 25px;
  margin-top: 35px;
}
.action-buttons {
  display: flex;
  align-items: center;
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
.btn-admin-edit {
  background-color: #ff9500; /* Naranja */
  color: white;
  border: none;
  border-radius: 8px;
  padding: 15px 30px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.3s ease;
}
.btn-admin-edit:hover {
  background-color: #f59b0d;
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
@media (max-width: 768px) {
  .product-detail-layout {
    grid-template-columns: 1fr;
  }
  .gallery-placeholder {
    height: 300px;
    position: static;
  }
  .product-config {
    box-shadow: none;
    padding: 10px;
  }
}
</style>
