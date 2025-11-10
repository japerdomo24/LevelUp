<template>
  <div class="home">
    <div class="home-container">
      <div class="category-card iphone theme-light" @click="cambiarPagina('products', 'iPhone')">
        <div class="category-content">
          <div class="category-title">{{ homeData.iphone.title }}</div>
          <div class="category-description">{{ homeData.iphone.description }}</div>
          <div class="button-group">
            <button class="btn-buy" @click.stop="comprarProducto('iphone')">Comprar</button>
            <button class="btn-more">Ver más</button>
          </div>
        </div>
      </div>

      <div class="category-card mac theme-dark" @click="cambiarPagina('products', 'Mac')">
        <div class="category-content">
          <div class="category-title">{{ homeData.mac.title }}</div>
          <div class="category-description">{{ homeData.mac.description }}</div>
          <div class="button-group">
            <button class="btn-buy" @click.stop="comprarProducto('mac')">Comprar</button>
            <button class="btn-more">Ver más</button>
          </div>
        </div>
      </div>

      <div class="category-card ipad theme-light" @click="cambiarPagina('products', 'iPad')">
        <div class="category-content">
          <div class="category-title">{{ homeData.ipad.title }}</div>
          <div class="category-description">{{ homeData.ipad.description }}</div>
          <div class="button-group">
            <button class="btn-buy" @click.stop="comprarProducto('ipad')">Comprar</button>
            <button class="btn-more">Ver más</button>
          </div>
        </div>
      </div>

      <div
        class="category-card accesorios theme-dark"
        @click="cambiarPagina('products', 'Accesorios')"
      >
        <div class="category-content">
          <div class="category-title">{{ homeData.accesorios.title }}</div>
          <div class="category-description">{{ homeData.accesorios.description }}</div>
          <div class="button-group">
            <button class="btn-buy" @click.stop="comprarProducto('accesorios')">Comprar</button>
            <button class="btn-more">Ver más</button>
          </div>
        </div>
      </div>
    </div>

    <button v-if="user?.rol === 'admin'" class="edit-home-btn" @click="$emit('edit-home')">
      ✏️ Editar Página
    </button>
  </div>
</template>

<script>
export default {
  name: 'HomePage',
  props: {
    user: Object,
  },
  data() {
    return {
      homeData: {
        iphone: {
          title: 'iPhone',
          description: 'Cámaras, potencia y eficiencia juntas',
          image: '/img/iPhone.png',
        },
        mac: {
          title: 'Mac',
          description: '¿Podrás domar estas bestias?',
          image: '/img/Mac.png',
        },
        ipad: {
          title: 'iPad',
          description: 'Avanza a los apuntes digitales',
          image: '/img/iPad.png',
        },
        accesorios: {
          title: 'Accesorios',
          description: 'Viste a la moda, define tu esencia',
          image: '/img/accesorios.png',
        },
      },
    }
  },
  mounted() {
    this.cargarDatosHome()
  },
  methods: {
    cambiarPagina(page, category) {
      this.$emit('change-page', page, category)
    },

    // --- ¡MÉTODO ACTUALIZADO! ---
    comprarProducto(claveProducto) {
      let categoria = '';

      // Mapea la clave simple (ej: 'iphone') a la categoría real de la API (ej: 'iPhone')
      switch (claveProducto) {
        case 'iphone':
          categoria = 'iPhone';
          break;
        case 'mac':
          categoria = 'Mac';
          break;
        case 'ipad':
          categoria = 'iPad';
          break;
        case 'accesorios':
          categoria = 'Accesorios';
          break;
      }

      if (categoria) {
        // Llama al mismo método que "Ver más"
        this.cambiarPagina('products', categoria);
      } else {
        console.warn('Clave de producto desconocida:', claveProducto);
      }
    },
    // --- FIN DE LA ACTUALIZACIÓN ---

    cargarDatosHome() {
      const savedData = localStorage.getItem('homeData')
      if (savedData) {
        this.homeData = JSON.parse(savedData)
      }
    },
  },
}
</script>

<style scoped>
.home {
  padding: 40px 20px;
  min-height: 80vh;
  background-color: #fff;
  position: relative;
}

.home-container {
  display: flex;
  flex-direction: column;
  gap: 30px;
  max-width: 1200px;
  margin: 0 auto;
}

.category-card {
  border-radius: 20px;
  padding: 60px 50px;
  cursor: pointer;
  transition: all 0.3s ease;
  min-height: 500px;
  display: flex;
  align-items: center;
  position: relative;
  overflow: hidden;
  background-size: contain;
  background-repeat: no-repeat;
  background-position: 95% center;
}

.category-card:hover {
  transform: scale(1.02);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
}

.category-content {
  flex-basis: 50%;
  z-index: 2;
  position: relative;
}

.category-title {
  font-size: 48px;
  font-weight: 700;
  margin-bottom: 15px;
}

.category-description {
  font-size: 24px;
  margin-bottom: 30px;
  max-width: 350px;
  line-height: 1.4;
}

.button-group {
  display: flex;
  gap: 15px;
  align-items: center;
}

.btn-buy,
.btn-more {
  padding: 12px 25px;
  border-radius: 25px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
}

.btn-buy:hover,
.btn-more:hover {
  opacity: 0.8;
  transform: scale(1.05);
}

.btn-buy {
  background: #e000c2;
  color: white;
}

.theme-dark {
  background-color: #000;
  color: #fff;
}
.theme-dark .btn-more {
  background: #333;
  color: white;
}

.theme-light {
  background-color: #f5f5f7;
  color: #1d1d1f;
}
.theme-light .btn-more {
  background-color: #ddd;
  color: #1d1d1f;
}

.iphone {
  background-image: url('/img/iPhone.png');
}

.mac {
  background-image: url('/img/Mac.png');
}

.ipad {
  background-image: url('/img/iPad.png');
}

.accesorios {
  background-image: url('/img/accesorios.png');
  background-position: 85% center;
  background-size: 45%;
}

/* Botón de edición para administradores */
.edit-home-btn {
  position: fixed;
  bottom: 30px;
  right: 30px;
  background: linear-gradient(135deg, #0071e3, #0077ed);
  color: white;
  border: none;
  border-radius: 25px;
  padding: 12px 20px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  box-shadow: 0 4px 15px rgba(0, 113, 227, 0.3);
  transition: all 0.3s ease;
  z-index: 1000;
}

.edit-home-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 113, 227, 0.4);
}

@media (max-width: 768px) {
  .category-card {
    flex-direction: column;
    padding: 40px 20px;
    min-height: 400px;
    background-position: center 20%;
    background-size: 60%;
  }

  .category-content {
    flex-basis: auto;
    text-align: center;
    margin-top: 55%;
  }

  .category-title {
    font-size: 32px;
  }

  .category-description {
    font-size: 18px;
    max-width: 100%;
  }

  .button-group {
    justify-content: center;
  }

  .accesorios {
    background-position: center 20%;
    background-size: 60%;
  }

  .edit-home-btn {
    bottom: 20px;
    right: 20px;
    padding: 10px 16px;
    font-size: 12px;
  }
}
</style>
