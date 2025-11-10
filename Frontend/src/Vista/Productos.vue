<template>
  <div class="products">
    <h1 style="text-align: center; margin-bottom: 20px">{{ category }}</h1>

    <div v-if="loading" class="loading-state">
      <p>Cargando productos...</p>
    </div>

    <div v-else class="product-list">
      <div
        v-for="product in filteredProducts"
        :key="product.id"
        class="product-card"
        @click="verProducto(product)"
      >
        <img
          :src="obtenerImagenProducto(product)"
          :alt="product.serie"
          class="product-image"
        />

        <div class="product-name">{{ product.serie }} {{ product.modelo }}</div>

        <div class="product-details">
          <span class="product-price"
          >${{ formatearPrecio(product.precio) }}</span
          >
          <!--<span class="product-stock" :class="obtenerClaseStock(product)">
            {{ obtenerTextoStock(product) }}
          </span>//-->
        </div>
        <button class="btn">Ver detalles</button>
      </div>

      <div v-if="filteredProducts.length === 0" class="no-products">
        <p>No hay productos disponibles en esta categoría</p>
        <button
          v-if="user?.role === 'admin'"
          class="btn-create-first"
          @click="$emit('change-page', 'create-product')"
        >
          Crear Primer Producto
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import apiClient from '@/api/axiosConfig.js'
import Swal from 'sweetalert2'

export default {
  name: 'ProductsList',
  props: {
    category: String,
    user: Object,
  },
  data() {
    return {
      allProducts: [],
      loading: true,
    }
  },
  computed: {
    filteredProducts() {
      if (!this.category) {
        return this.allProducts;
      }
      return this.allProducts.filter(
        (product) => product.tipo === this.category
      );
    },
  },
  watch: {
    category: {
      immediate: true,
      handler() {
        this.cargarProductos(); // <-- Traducido
      },
    },
  },
  mounted() {
    this.cargarProductos(); // <-- Traducido
  },
  // --- MÉTODOS TRADUCIDOS ---
  methods: {
    async cargarProductos() {
      this.loading = true;
      try {
        const response = await apiClient.get('/productos');
        this.allProducts = response.data;
      } catch (error) {
        // El interceptor maneja 401/403
        if (error.response?.status !== 401 && error.response?.status !== 403) {
          console.error('Error cargando productos:', error);
          Swal.fire({
            icon: 'error',
            title: 'No se pudieron cargar los productos',
            confirmButtonColor: '#0071e3',
          });
        }
      } finally {
        this.loading = false;
      }
    },

    obtenerImagenProducto(product) {
      if (product.imagen) {
        return product.imagen;
      }
      return '/img/default-placeholder.png';
    },

    formatearPrecio(price) {
      return Number(price).toFixed(2);
    },

    obtenerTextoStock(product) {
      const stock = product.cantidad;
      if (stock > 10) {
        return 'En stock';
      }
      if (stock > 0) {
        return `¡Últimos ${stock}!`;
      }
      return 'Agotado';
    },

    obtenerClaseStock(product) {
      const stock = product.cantidad;
      if (stock > 0) {
        return 'in-stock';
      }
      return 'out-of-stock';
    },

    verProducto(product) {
      this.$emit('view-product', product);
    },
  },
}
</script>

<style scoped>
.products {
  padding: 20px;
}

.loading-state {
  text-align: center;
  padding: 80px;
  font-size: 20px;
  color: #8e8e93;
}

.admin-actions {
  display: flex;
  justify-content: center;
  margin-bottom: 30px;
}

.btn-create-product {
  background: linear-gradient(135deg, #34c759, #30d158);
  color: white;
  border: none;
  border-radius: 12px;
  padding: 12px 24px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-create-product:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(52, 199, 89, 0.3);
}

.product-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
  margin-top: 20px;
  padding: 0 40px;
}

.product-card {
  background-color: #fff;
  border-radius: 10px;
  padding: 20px;
  text-align: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
}

.product-image {
  width: 100%;
  height: 200px;
  object-fit: contain;
  margin-bottom: 15px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.product-name {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 10px;
  line-height: 1.4;
  flex-grow: 1;
}

.product-details {
  margin-bottom: 15px;
}

.product-price {
  display: block;
  font-size: 20px;
  font-weight: 700;
  color: #0071e3;
  margin-bottom: 5px;
}

.product-stock {
  display: block;
  font-size: 12px;
}
.product-stock.in-stock {
  color: #34c759;
  font-weight: 500;
}
.product-stock.out-of-stock {
  color: #ff3b30;
  font-weight: 700;
}

.btn {
  background-color: #007aff;
  color: white;
  border: none;
  border-radius: 20px;
  padding: 10px 20px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.btn:hover {
  background-color: #0056b3;
}

.no-products {
  grid-column: 1 / -1;
  text-align: center;
  padding: 60px 20px;
  background: #f9f9f9;
  border-radius: 15px;
  border: 2px dashed #d2d2d7;
}

.no-products p {
  font-size: 18px;
  color: #6e6e73;
  margin-bottom: 20px;
}

.btn-create-first {
  background: linear-gradient(135deg, #0071e3, #0077ed);
  color: white;
  border: none;
  border-radius: 12px;
  padding: 12px 24px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-create-first:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(0, 113, 227, 0.3);
}

@media (max-width: 768px) {
  .product-list {
    padding: 0 20px;
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  }

  .admin-actions {
    margin-bottom: 20px;
  }
}
</style>
