<template>
  <div class="products">
    <h1 style="text-align: center; margin-bottom: 20px">{{ category }}</h1>

    <!-- Botón para crear producto (solo admin) -->
    <div v-if="user?.role === 'admin'" class="admin-actions">
      <button class="btn-create-product" @click="$emit('create-product')">
        ➕ Crear Nuevo Producto
      </button>
    </div>

    <div class="product-list">
      <div
        v-for="product in uniqueFilteredProducts"
        :key="product.id"
        class="product-card"
        @click="viewProduct(product)"
      >
        <img
          :src="getProductImage(product)"
          :alt="product.name || product.serie"
          class="product-image"
        />

        <div class="product-name">{{ product.name || product.serie }}</div>
        <div class="product-details" v-if="product.precio || product.stock">
          <span class="product-price" v-if="product.precio"
            >${{ formatPrice(product.precio) }}</span
          >
          <span class="product-stock">{{ getStockText(product) }}</span>
        </div>
        <button class="btn">Ver detalles</button>
      </div>

      <!-- Mensaje cuando no hay productos -->
      <div v-if="uniqueFilteredProducts.length === 0" class="no-products">
        <p>No hay productos disponibles en esta categoría</p>
        <button
          v-if="user?.role === 'admin'"
          class="btn-create-first"
          @click="$emit('create-product')"
        >
          Crear Primer Producto
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ProductsList',
  props: {
    category: String,
    user: Object,
  },
  data() {
    return {
      // Productos por defecto (demo) con stock de 10 unidades
      defaultProducts: [
        { id: 1, name: 'iPhone 16', category: 'iPhone', image: '/img/iPhone-16.png', stock: 10 },
        { id: 2, name: 'iPhone 16e', category: 'iPhone', image: '/img/iPhone-16e.png', stock: 10 },
        { id: 3, name: 'iPhone 17', category: 'iPhone', image: '/img/iPhone-17.png', stock: 10 },
        {
          id: 4,
          name: 'iPhone 17 Pro',
          category: 'iPhone',
          image: '/img/iPhone-17-Pro.png',
          stock: 10,
        },

        { id: 5, name: 'MacBook Air', category: 'Mac', image: '/img/MacBook-Air.png', stock: 10 },
        { id: 6, name: 'MacBook Pro', category: 'Mac', image: '/img/macbook-pro.png', stock: 10 },
        { id: 7, name: 'iMac', category: 'Mac', image: '/img/imac.png', stock: 10 },

        { id: 8, name: 'iPad Air', category: 'iPad', image: '/img/ipad-air.png', stock: 10 },
        { id: 9, name: 'iPad Pro', category: 'iPad', image: '/img/ipad-pro.png', stock: 10 },
        { id: 10, name: 'iPad Mini', category: 'iPad', image: '/img/ipad-mini.png', stock: 10 },

        { id: 11, name: 'AirPods', category: 'Accesorios', image: '/img/airpods.png', stock: 10 },
        {
          id: 12,
          name: 'Apple Watch',
          category: 'Accesorios',
          image: '/img/apple-watch.png',
          stock: 10,
        },
      ],
      // Productos cargados desde localStorage
      customProducts: [],
    }
  },
  computed: {
    // Combinar productos por defecto con productos personalizados y eliminar duplicados
    allProducts() {
      const all = [...this.defaultProducts, ...this.customProducts]

      // Eliminar duplicados basados en ID y nombre/serie
      const uniqueProducts = []
      const seen = new Set()

      all.forEach((product) => {
        const identifier = product.id + (product.serie || product.name)
        if (!seen.has(identifier)) {
          seen.add(identifier)
          uniqueProducts.push(product)
        }
      })

      return uniqueProducts
    },

    // Filtrar productos únicos por categoría
    uniqueFilteredProducts() {
      return this.allProducts.filter((product) => product.category === this.category)
    },
  },
  watch: {
    // Observar cambios en la categoría para recargar productos
    category: {
      immediate: true,
      handler() {
        this.loadCustomProducts()
      },
    },
  },
  mounted() {
    this.loadCustomProducts()
  },
  methods: {
    loadCustomProducts() {
      try {
        const savedProducts = localStorage.getItem('products')
        if (savedProducts) {
          const parsedProducts = JSON.parse(savedProducts)

          // Mapear los productos guardados al formato esperado
          this.customProducts = parsedProducts.map((product) => ({
            ...product,
            // Asegurar que tenga category (usar type si existe)
            category: product.category || product.type || 'iPhone',
            // Crear un nombre si no existe
            name: product.name || product.serie || `Nuevo ${product.type || 'Producto'}`,
            // Usar imagen por defecto si no hay imagen específica
            image: product.image || this.getDefaultImage(product.type || product.category),
            // Asegurar stock
            stock: product.cantidad || product.stock || 0,
          }))
        } else {
          this.customProducts = []
        }
      } catch (error) {
        console.error('Error loading custom products:', error)
        this.customProducts = []
      }
    },

    getDefaultImage(category) {
      const defaultImages = {
        iPhone: '/img/iPhone-16.png',
        Mac: '/img/macbook-pro.png',
        iPad: '/img/iPad.png',
        Accesorios: '/img/airpods.png',
      }
      return defaultImages[category] || '/img/iPhone-16.png'
    },

    getProductImage(product) {
      // Si el producto tiene imagen, usarla
      if (product.image) {
        return product.image
      }

      // Si es un producto creado, usar imagen por defecto según categoría
      return this.getDefaultImage(product.category || product.type)
    },

    formatPrice(price) {
      if (typeof price === 'number') {
        return price.toFixed(2)
      }
      if (typeof price === 'string') {
        return parseFloat(price).toFixed(2)
      }
      return '0.00'
    },

    getStockText(product) {
      const stock = product.cantidad || product.stock
      if (stock > 0) {
        return `${stock} en stock`
      }
      return 'Sin stock'
    },

    viewProduct(product) {
      this.$emit('view-product', product)
    },

    changePage(page, category) {
      this.$emit('change-page', page, category)
    },
  },
}
</script>

<style scoped>
.products {
  padding: 20px;
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
  color: #34c759;
  font-weight: 500;
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
