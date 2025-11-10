<template>
  <div class="edit-home">
    <div class="edit-home-header">
      <h1>Editar Página de Inicio</h1>
      <button class="btn-back" @click="$emit('change-page', 'home')">← Volver al Inicio</button>
    </div>

    <div class="edit-sections">
      <div v-for="(section, key) in homeData" :key="key" class="edit-section">
        <h3>{{ getSectionTitle(key) }}</h3>

        <div class="form-group">
          <label :for="`title-${key}`">Título</label>
          <input
            :id="`title-${key}`"
            type="text"
            v-model="section.title"
            :placeholder="`Título para ${getSectionTitle(key)}`"
          />
        </div>

        <div class="form-group">
          <label :for="`description-${key}`">Descripción</label>
          <textarea
            :id="`description-${key}`"
            v-model="section.description"
            :placeholder="`Descripción para ${getSectionTitle(key)}`"
            rows="3"
          ></textarea>
        </div>

        <div class="form-group">
          <label :for="`image-${key}`">URL de la Imagen</label>
          <input
            :id="`image-${key}`"
            type="text"
            v-model="section.image"
            placeholder="URL de la imagen"
          />
          <div class="image-preview" v-if="section.image">
            <img :src="section.image" :alt="section.title" @error="handleImageError" />
          </div>
        </div>

        <div class="theme-selection">
          <label>Tema:</label>
          <div class="theme-options">
            <label class="theme-option">
              <input type="radio" :name="`theme-${key}`" value="light" v-model="section.theme" />
              <span class="theme-demo light">Claro</span>
            </label>
            <label class="theme-option">
              <input type="radio" :name="`theme-${key}`" value="dark" v-model="section.theme" />
              <span class="theme-demo dark">Oscuro</span>
            </label>
          </div>
        </div>
      </div>
    </div>

    <div class="edit-actions">
      <button class="btn-preview" @click="previewChanges">👁️ Vista Previa</button>
      <button class="btn-reset" @click="resetChanges">🔄 Restablecer</button>
      <button class="btn-save" @click="saveChanges" :disabled="loading">
        {{ loading ? 'Guardando...' : '💾 Guardar Cambios' }}
      </button>
    </div>

    <!-- Vista previa -->
    <div v-if="showPreview" class="preview-overlay">
      <div class="preview-content">
        <div class="preview-header">
          <h3>Vista Previa</h3>
          <button class="btn-close-preview" @click="showPreview = false">✕</button>
        </div>
        <div class="preview-container">
          <div
            v-for="(section, key) in homeData"
            :key="key"
            class="preview-section"
            :class="[section.theme === 'dark' ? 'theme-dark' : 'theme-light']"
            :style="{ backgroundImage: `url(${section.image})` }"
          >
            <div class="preview-section-content">
              <h4>{{ section.title }}</h4>
              <p>{{ section.description }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'EditHomePage',
  data() {
    return {
      homeData: {},
      originalData: {},
      loading: false,
      showPreview: false,
    }
  },
  mounted() {
    this.loadHomeData()
  },
  methods: {
    loadHomeData() {
      const savedData = localStorage.getItem('homeData')
      const defaultData = {
        iphone: {
          title: 'iPhone',
          description: 'Cámaras, potencia y eficiencia juntas',
          image: '/img/iPhone.png',
          theme: 'light',
        },
        mac: {
          title: 'Mac',
          description: '¿Podrás domar estas bestias?',
          image: '/img/Mac.png',
          theme: 'dark',
        },
        ipad: {
          title: 'iPad',
          description: 'Avanza a los apuntes digitales',
          image: '/img/iPad.png',
          theme: 'light',
        },
        accesorios: {
          title: 'Accesorios',
          description: 'Viste a la moda, define tu esencia',
          image: '/img/accesorios.png',
          theme: 'dark',
        },
      }

      this.homeData = savedData ? JSON.parse(savedData) : defaultData
      this.originalData = JSON.parse(JSON.stringify(this.homeData))
    },

    getSectionTitle(key) {
      const titles = {
        iphone: 'iPhone',
        mac: 'Mac',
        ipad: 'iPad',
        accesorios: 'Accesorios',
      }
      return titles[key] || key
    },

    handleImageError(event) {
      event.target.style.display = 'none'
    },

    previewChanges() {
      this.showPreview = true
    },

    resetChanges() {
      if (confirm('¿Está seguro de que desea restablecer todos los cambios?')) {
        this.homeData = JSON.parse(JSON.stringify(this.originalData))
      }
    },

    async saveChanges() {
      this.loading = true

      try {
        await new Promise((resolve) => setTimeout(resolve, 1000))

        localStorage.setItem('homeData', JSON.stringify(this.homeData))
        this.originalData = JSON.parse(JSON.stringify(this.homeData))

        this.$emit('home-updated')
      } catch (error) {
        console.error('Error guardando cambios:', error)
        alert('Error al guardar los cambios.')
      } finally {
        this.loading = false
      }
    },
  },
}
</script>

<style scoped>
.edit-home {
  padding: 40px 20px;
  max-width: 1000px;
  margin: 0 auto;
}

.edit-home-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
  flex-wrap: wrap;
  gap: 20px;
}

.edit-home-header h1 {
  font-size: 32px;
  font-weight: 700;
  color: #1d1d1f;
}

.btn-back {
  background: transparent;
  color: #0071e3;
  border: 2px solid #0071e3;
  padding: 10px 20px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-back:hover {
  background: #0071e3;
  color: white;
}

.edit-sections {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 30px;
  margin-bottom: 40px;
}

.edit-section {
  background: white;
  border-radius: 15px;
  padding: 25px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  border: 1px solid #e5e5e7;
}

.edit-section h3 {
  font-size: 20px;
  font-weight: 700;
  color: #1d1d1f;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 2px solid #f0f0f0;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #1d1d1f;
  font-size: 14px;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #d2d2d7;
  border-radius: 12px;
  font-size: 16px;
  transition: all 0.3s ease;
  background: white;
  font-family: inherit;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #0071e3;
  box-shadow: 0 0 0 4px rgba(0, 113, 227, 0.1);
}

.image-preview {
  margin-top: 10px;
  text-align: center;
}

.image-preview img {
  max-width: 100%;
  max-height: 150px;
  border-radius: 8px;
  border: 2px solid #e5e5e7;
}

.theme-selection {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e5e5e7;
}

.theme-selection label {
  font-weight: 600;
  color: #1d1d1f;
  margin-bottom: 10px;
  display: block;
}

.theme-options {
  display: flex;
  gap: 15px;
}

.theme-option {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.theme-demo {
  padding: 8px 16px;
  border: 2px solid #e5e5e7;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.theme-demo.light {
  background: #f5f5f7;
  color: #1d1d1f;
}

.theme-demo.dark {
  background: #000;
  color: #fff;
}

.theme-option input:checked + .theme-demo {
  border-color: #0071e3;
  box-shadow: 0 0 0 2px rgba(0, 113, 227, 0.2);
}

.edit-actions {
  display: flex;
  gap: 15px;
  justify-content: center;
  padding: 30px;
  background: #f9f9f9;
  border-radius: 15px;
  flex-wrap: wrap;
}

.btn-preview,
.btn-reset,
.btn-save {
  padding: 12px 24px;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-preview {
  background: #ff9500;
  color: white;
}

.btn-preview:hover {
  background: #e08500;
  transform: translateY(-2px);
}

.btn-reset {
  background: #ff3b30;
  color: white;
}

.btn-reset:hover {
  background: #e0352b;
  transform: translateY(-2px);
}

.btn-save {
  background: linear-gradient(135deg, #34c759, #30d158);
  color: white;
}

.btn-save:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(52, 199, 89, 0.3);
}

.btn-save:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.preview-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  padding: 20px;
}

.preview-content {
  background: white;
  border-radius: 20px;
  width: 100%;
  max-width: 800px;
  max-height: 90vh;
  overflow-y: auto;
}

.preview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 30px;
  border-bottom: 1px solid #e5e5e7;
}

.preview-header h3 {
  font-size: 24px;
  font-weight: 700;
  color: #1d1d1f;
}

.btn-close-preview {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #6e6e73;
  padding: 5px;
}

.btn-close-preview:hover {
  color: #1d1d1f;
}

.preview-container {
  padding: 30px;
}

.preview-section {
  border-radius: 15px;
  padding: 40px;
  margin-bottom: 20px;
  min-height: 200px;
  background-size: contain;
  background-repeat: no-repeat;
  background-position: 95% center;
  display: flex;
  align-items: center;
}

.preview-section-content {
  flex-basis: 50%;
}

.preview-section h4 {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 10px;
}

.preview-section p {
  font-size: 16px;
  line-height: 1.4;
}

.theme-light {
  background-color: #f5f5f7;
  color: #1d1d1f;
}

.theme-dark {
  background-color: #000;
  color: #fff;
}

@media (max-width: 768px) {
  .edit-sections {
    grid-template-columns: 1fr;
  }

  .edit-actions {
    flex-direction: column;
  }

  .theme-options {
    flex-direction: column;
  }

  .preview-section {
    padding: 20px;
    min-height: 150px;
  }

  .preview-section h4 {
    font-size: 20px;
  }

  .preview-section p {
    font-size: 14px;
  }
}
</style>
