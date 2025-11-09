<template>
  <div class="create-product">
    <div class="create-product-header">
      <h1>Crear Nuevo Producto</h1>
      <button class="btn-back" @click="$emit('change-page', 'products', selectedType || category)">
        ← Volver a Productos
      </button>
    </div>

    <div class="product-type-selection" v-if="!selectedType">
      <h2>¿Qué tipo de producto deseas crear?</h2>
      <div class="type-buttons">
        <button class="type-btn" @click="selectType('iPhone')">
          <span class="type-icon">📱</span>
          <span class="type-name">iPhone</span>
        </button>
        <button class="type-btn" @click="selectType('Mac')">
          <span class="type-icon">💻</span>
          <span class="type-name">Mac</span>
        </button>
        <button class="type-btn" @click="selectType('iPad')">
          <span class="type-icon">📟</span>
          <span class="type-name">iPad</span>
        </button>
        <button class="type-btn" @click="selectType('Accesorios')">
          <span class="type-icon">🎧</span>
          <span class="type-name">Accesorios</span>
        </button>
      </div>
    </div>

    <!-- Selección de tipo de Mac -->
    <div v-else-if="selectedType === 'Mac' && !macSubtype" class="mac-subtype-selection">
      <h2>¿Qué tipo de Mac deseas crear?</h2>
      <div class="subtype-buttons">
        <button class="subtype-btn" @click="selectMacSubtype('MacBook')">
          <span class="subtype-icon">💻</span>
          <span class="subtype-name">MacBook</span>
          <span class="subtype-description">Portátil</span>
        </button>
        <button class="subtype-btn" @click="selectMacSubtype('MacEscritorio')">
          <span class="subtype-icon">🖥️</span>
          <span class="subtype-name">Mac de Escritorio</span>
          <span class="subtype-description">iMac, Mac Pro, Mac Mini</span>
        </button>
      </div>
      <button class="btn-back-subtype" @click="selectedType = null">← Volver a tipos</button>
    </div>

    <!-- Formulario para iPhone -->
    <div v-else-if="selectedType === 'iPhone'" class="product-form">
      <h2>Creación de iPhone</h2>

      <form @submit.prevent="createProduct">
        <!-- Sección de imagen -->
        <div class="image-upload-section">
          <label class="image-upload-label">Imagen del Producto *</label>
          <div class="image-upload-container">
            <div
              class="image-upload-area"
              :class="{ 'has-image': productData.imagePreview }"
              @click="triggerFileInput"
            >
              <div v-if="!productData.imagePreview" class="upload-placeholder">
                <span class="upload-icon">📷</span>
                <span class="upload-text">Haz clic para subir una imagen</span>
                <span class="upload-hint">Recomendado: 500x500 px</span>
              </div>
              <img
                v-else
                :src="productData.imagePreview"
                alt="Vista previa"
                class="image-preview"
              />
              <input
                type="file"
                ref="fileInput"
                @change="handleImageUpload"
                accept="image/*"
                class="file-input"
              />
            </div>
            <div v-if="productData.imagePreview" class="image-actions">
              <button type="button" class="btn-change-image" @click="triggerFileInput">
                Cambiar Imagen
              </button>
              <button type="button" class="btn-remove-image" @click="removeImage">Eliminar</button>
            </div>
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="iphone-serie">Serie *</label>
            <input
              type="text"
              id="iphone-serie"
              v-model="productData.serie"
              required
              placeholder="Ej: iPhone 16"
            />
          </div>

          <div class="form-group">
            <label for="iphone-modelo">Modelo *</label>
            <input
              type="text"
              id="iphone-modelo"
              v-model="productData.modelo"
              required
              placeholder="Ej: A2882"
            />
          </div>
        </div>

        <div class="form-group">
          <label>Colores Disponibles *</label>
          <div class="color-selection">
            <div
              v-for="color in availableColors"
              :key="color"
              class="color-option"
              :class="{ selected: productData.colores.includes(color) }"
              @click="toggleColor(color)"
            >
              <div class="color-swatch" :style="{ backgroundColor: color }"></div>
              <span class="color-name">{{ getColorName(color) }}</span>
            </div>
          </div>
        </div>

        <div class="form-group">
          <label>Capacidades *</label>
          <div class="capacity-selection">
            <div
              v-for="capacity in iphoneCapacities"
              :key="capacity"
              class="capacity-option"
              :class="{ selected: productData.capacidades.includes(capacity) }"
              @click="toggleCapacity(capacity)"
            >
              {{ capacity }}
            </div>
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="iphone-sim">Configuración SIM *</label>
            <select id="iphone-sim" v-model="productData.sim" required>
              <option value="">Seleccionar...</option>
              <option value="eSIM">eSIM</option>
              <option value="Nano-SIM">Nano-SIM</option>
              <option value="Dual SIM">Dual SIM</option>
            </select>
          </div>

          <div class="form-group">
            <label for="iphone-precio">Precio ($) *</label>
            <input
              type="number"
              id="iphone-precio"
              v-model="productData.precio"
              required
              min="0"
              step="0.01"
              placeholder="0.00"
            />
          </div>
        </div>

        <div class="form-group">
          <label for="iphone-cantidad">Cantidad en Stock *</label>
          <input
            type="number"
            id="iphone-cantidad"
            v-model="productData.cantidad"
            required
            min="0"
            placeholder="0"
          />
        </div>

        <div class="form-actions">
          <button type="button" class="btn-cancel" @click="cancelCreation">Cancelar</button>
          <button type="submit" class="btn-create" :disabled="loading || !productData.imageFile">
            {{ loading ? 'Creando...' : 'Crear iPhone' }}
          </button>
        </div>
      </form>
    </div>

    <!-- Formulario para MacBook -->
    <div v-else-if="macSubtype === 'MacBook'" class="product-form">
      <h2>Creación de MacBook</h2>

      <form @submit.prevent="createProduct">
        <!-- Sección de imagen -->
        <div class="image-upload-section">
          <label class="image-upload-label">Imagen del Producto *</label>
          <div class="image-upload-container">
            <div
              class="image-upload-area"
              :class="{ 'has-image': productData.imagePreview }"
              @click="triggerFileInput"
            >
              <div v-if="!productData.imagePreview" class="upload-placeholder">
                <span class="upload-icon">📷</span>
                <span class="upload-text">Haz clic para subir una imagen</span>
                <span class="upload-hint">Recomendado: 500x500 px</span>
              </div>
              <img
                v-else
                :src="productData.imagePreview"
                alt="Vista previa"
                class="image-preview"
              />
              <input
                type="file"
                ref="fileInput"
                @change="handleImageUpload"
                accept="image/*"
                class="file-input"
              />
            </div>
            <div v-if="productData.imagePreview" class="image-actions">
              <button type="button" class="btn-change-image" @click="triggerFileInput">
                Cambiar Imagen
              </button>
              <button type="button" class="btn-remove-image" @click="removeImage">Eliminar</button>
            </div>
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="macbook-serie">Serie *</label>
            <input
              type="text"
              id="macbook-serie"
              v-model="productData.serie"
              required
              placeholder="Ej: MacBook Pro"
            />
          </div>

          <div class="form-group">
            <label for="macbook-modelo">Modelo *</label>
            <input
              type="text"
              id="macbook-modelo"
              v-model="productData.modelo"
              required
              placeholder="Ej: M3 Pro"
            />
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="macbook-chip">Chip *</label>
            <select id="macbook-chip" v-model="productData.chip" required>
              <option value="">Seleccionar...</option>
              <option value="M3">Apple M3</option>
              <option value="M3 Pro">Apple M3 Pro</option>
              <option value="M3 Max">Apple M3 Max</option>
              <option value="M2">Apple M2</option>
              <option value="M2 Pro">Apple M2 Pro</option>
              <option value="M2 Max">Apple M2 Max</option>
            </select>
          </div>

          <div class="form-group">
            <label for="macbook-ram">Memoria RAM *</label>
            <select id="macbook-ram" v-model="productData.ram" required>
              <option value="">Seleccionar...</option>
              <option value="8GB">8GB</option>
              <option value="16GB">16GB</option>
              <option value="24GB">24GB</option>
              <option value="32GB">32GB</option>
              <option value="64GB">64GB</option>
              <option value="96GB">96GB</option>
              <option value="128GB">128GB</option>
            </select>
          </div>
        </div>

        <div class="form-group">
          <label>Capacidades de Almacenamiento *</label>
          <div class="capacity-selection">
            <div
              v-for="capacity in macCapacities"
              :key="capacity"
              class="capacity-option"
              :class="{ selected: productData.capacidades.includes(capacity) }"
              @click="toggleCapacity(capacity)"
            >
              {{ capacity }}
            </div>
          </div>
        </div>

        <div class="form-group">
          <label>Colores Disponibles *</label>
          <div class="color-selection">
            <div
              v-for="color in macbookColors"
              :key="color"
              class="color-option"
              :class="{ selected: productData.colores.includes(color) }"
              @click="toggleColor(color)"
            >
              <div class="color-swatch" :style="{ backgroundColor: color }"></div>
              <span class="color-name">{{ getColorName(color) }}</span>
            </div>
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="macbook-teclado">Distribución de Teclado *</label>
            <select id="macbook-teclado" v-model="productData.teclado" required>
              <option value="">Seleccionar...</option>
              <option value="ES">Español</option>
              <option value="US">Inglés (US)</option>
              <option value="UK">Inglés (UK)</option>
              <option value="FR">Francés</option>
              <option value="DE">Alemán</option>
            </select>
          </div>

          <div class="form-group">
            <label for="macbook-precio">Precio ($) *</label>
            <input
              type="number"
              id="macbook-precio"
              v-model="productData.precio"
              required
              min="0"
              step="0.01"
              placeholder="0.00"
            />
          </div>
        </div>

        <div class="form-group">
          <label for="macbook-cantidad">Cantidad en Stock *</label>
          <input
            type="number"
            id="macbook-cantidad"
            v-model="productData.cantidad"
            required
            min="0"
            placeholder="0"
          />
        </div>

        <div class="form-actions">
          <button type="button" class="btn-cancel" @click="cancelCreation">Cancelar</button>
          <button type="submit" class="btn-create" :disabled="loading || !productData.imageFile">
            {{ loading ? 'Creando...' : 'Crear MacBook' }}
          </button>
        </div>
      </form>
    </div>

    <!-- Formulario para Mac de Escritorio -->
    <div v-else-if="macSubtype === 'MacEscritorio'" class="product-form">
      <h2>Creación de Mac de Escritorio</h2>

      <form @submit.prevent="createProduct">
        <!-- Sección de imagen -->
        <div class="image-upload-section">
          <label class="image-upload-label">Imagen del Producto *</label>
          <div class="image-upload-container">
            <div
              class="image-upload-area"
              :class="{ 'has-image': productData.imagePreview }"
              @click="triggerFileInput"
            >
              <div v-if="!productData.imagePreview" class="upload-placeholder">
                <span class="upload-icon">📷</span>
                <span class="upload-text">Haz clic para subir una imagen</span>
                <span class="upload-hint">Recomendado: 500x500 px</span>
              </div>
              <img
                v-else
                :src="productData.imagePreview"
                alt="Vista previa"
                class="image-preview"
              />
              <input
                type="file"
                ref="fileInput"
                @change="handleImageUpload"
                accept="image/*"
                class="file-input"
              />
            </div>
            <div v-if="productData.imagePreview" class="image-actions">
              <button type="button" class="btn-change-image" @click="triggerFileInput">
                Cambiar Imagen
              </button>
              <button type="button" class="btn-remove-image" @click="removeImage">Eliminar</button>
            </div>
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="macdesk-serie">Serie *</label>
            <input
              type="text"
              id="macdesk-serie"
              v-model="productData.serie"
              required
              placeholder="Ej: iMac, Mac Pro, Mac Mini"
            />
          </div>

          <div class="form-group">
            <label for="macdesk-modelo">Modelo *</label>
            <input
              type="text"
              id="macdesk-modelo"
              v-model="productData.modelo"
              required
              placeholder="Ej: 24 pulgadas M3"
            />
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="macdesk-chip">Chip *</label>
            <select id="macdesk-chip" v-model="productData.chip" required>
              <option value="">Seleccionar...</option>
              <option value="M3">Apple M3</option>
              <option value="M3 Pro">Apple M3 Pro</option>
              <option value="M3 Max">Apple M3 Max</option>
              <option value="M2">Apple M2</option>
              <option value="M2 Pro">Apple M2 Pro</option>
              <option value="M2 Ultra">Apple M2 Ultra</option>
            </select>
          </div>

          <div class="form-group">
            <label for="macdesk-ram">Memoria RAM *</label>
            <select id="macdesk-ram" v-model="productData.ram" required>
              <option value="">Seleccionar...</option>
              <option value="8GB">8GB</option>
              <option value="16GB">16GB</option>
              <option value="24GB">24GB</option>
              <option value="32GB">32GB</option>
              <option value="64GB">64GB</option>
              <option value="96GB">96GB</option>
              <option value="128GB">128GB</option>
              <option value="192GB">192GB</option>
            </select>
          </div>
        </div>

        <div class="form-group">
          <label>Capacidades de Almacenamiento *</label>
          <div class="capacity-selection">
            <div
              v-for="capacity in macCapacities"
              :key="capacity"
              class="capacity-option"
              :class="{ selected: productData.capacidades.includes(capacity) }"
              @click="toggleCapacity(capacity)"
            >
              {{ capacity }}
            </div>
          </div>
        </div>

        <div class="form-group">
          <label>Colores Disponibles *</label>
          <div class="color-selection">
            <div
              v-for="color in imacColors"
              :key="color"
              class="color-option"
              :class="{ selected: productData.colores.includes(color) }"
              @click="toggleColor(color)"
            >
              <div class="color-swatch" :style="{ backgroundColor: color }"></div>
              <span class="color-name">{{ getColorName(color) }}</span>
            </div>
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="macdesk-precio">Precio ($) *</label>
            <input
              type="number"
              id="macdesk-precio"
              v-model="productData.precio"
              required
              min="0"
              step="0.01"
              placeholder="0.00"
            />
          </div>

          <div class="form-group">
            <label for="macdesk-cantidad">Cantidad en Stock *</label>
            <input
              type="number"
              id="macdesk-cantidad"
              v-model="productData.cantidad"
              required
              min="0"
              placeholder="0"
            />
          </div>
        </div>

        <div class="form-actions">
          <button type="button" class="btn-cancel" @click="cancelCreation">Cancelar</button>
          <button type="submit" class="btn-create" :disabled="loading || !productData.imageFile">
            {{ loading ? 'Creando...' : 'Crear Mac de Escritorio' }}
          </button>
        </div>
      </form>
    </div>

    <!-- Formulario para iPad -->
    <div v-else-if="selectedType === 'iPad'" class="product-form">
      <h2>Creación de iPad</h2>

      <form @submit.prevent="createProduct">
        <!-- Sección de imagen -->
        <div class="image-upload-section">
          <label class="image-upload-label">Imagen del Producto *</label>
          <div class="image-upload-container">
            <div
              class="image-upload-area"
              :class="{ 'has-image': productData.imagePreview }"
              @click="triggerFileInput"
            >
              <div v-if="!productData.imagePreview" class="upload-placeholder">
                <span class="upload-icon">📷</span>
                <span class="upload-text">Haz clic para subir una imagen</span>
                <span class="upload-hint">Recomendado: 500x500 px</span>
              </div>
              <img
                v-else
                :src="productData.imagePreview"
                alt="Vista previa"
                class="image-preview"
              />
              <input
                type="file"
                ref="fileInput"
                @change="handleImageUpload"
                accept="image/*"
                class="file-input"
              />
            </div>
            <div v-if="productData.imagePreview" class="image-actions">
              <button type="button" class="btn-change-image" @click="triggerFileInput">
                Cambiar Imagen
              </button>
              <button type="button" class="btn-remove-image" @click="removeImage">Eliminar</button>
            </div>
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="ipad-serie">Serie *</label>
            <input
              type="text"
              id="ipad-serie"
              v-model="productData.serie"
              required
              placeholder="Ej: iPad Air"
            />
          </div>

          <div class="form-group">
            <label for="ipad-modelo">Modelo *</label>
            <input
              type="text"
              id="ipad-modelo"
              v-model="productData.modelo"
              required
              placeholder="Ej: 5ta generación"
            />
          </div>
        </div>

        <div class="form-group">
          <label>Colores Disponibles *</label>
          <div class="color-selection">
            <div
              v-for="color in ipadColors"
              :key="color"
              class="color-option"
              :class="{ selected: productData.colores.includes(color) }"
              @click="toggleColor(color)"
            >
              <div class="color-swatch" :style="{ backgroundColor: color }"></div>
              <span class="color-name">{{ getColorName(color) }}</span>
            </div>
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="ipad-pantalla">Tamaño de Pantalla *</label>
            <select id="ipad-pantalla" v-model="productData.pantalla" required>
              <option value="">Seleccionar...</option>
              <option value="10.9">10.9 pulgadas</option>
              <option value="11">11 pulgadas</option>
              <option value="12.9">12.9 pulgadas</option>
              <option value="8.3">8.3 pulgadas</option>
            </select>
          </div>

          <div class="form-group">
            <label for="ipad-sim">Configuración SIM</label>
            <select id="ipad-sim" v-model="productData.sim">
              <option value="">Sin SIM</option>
              <option value="eSIM">eSIM</option>
              <option value="Nano-SIM">Nano-SIM</option>
            </select>
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="ipad-precio">Precio ($) *</label>
            <input
              type="number"
              id="ipad-precio"
              v-model="productData.precio"
              required
              min="0"
              step="0.01"
              placeholder="0.00"
            />
          </div>

          <div class="form-group">
            <label for="ipad-cantidad">Cantidad en Stock *</label>
            <input
              type="number"
              id="ipad-cantidad"
              v-model="productData.cantidad"
              required
              min="0"
              placeholder="0"
            />
          </div>
        </div>

        <div class="form-actions">
          <button type="button" class="btn-cancel" @click="cancelCreation">Cancelar</button>
          <button type="submit" class="btn-create" :disabled="loading || !productData.imageFile">
            {{ loading ? 'Creando...' : 'Crear iPad' }}
          </button>
        </div>
      </form>
    </div>

    <!-- Formulario para Accesorios -->
    <div v-else-if="selectedType === 'Accesorios'" class="product-form">
      <h2>Creación de Accesorio</h2>

      <form @submit.prevent="createProduct">
        <!-- Sección de imagen -->
        <div class="image-upload-section">
          <label class="image-upload-label">Imagen del Producto *</label>
          <div class="image-upload-container">
            <div
              class="image-upload-area"
              :class="{ 'has-image': productData.imagePreview }"
              @click="triggerFileInput"
            >
              <div v-if="!productData.imagePreview" class="upload-placeholder">
                <span class="upload-icon">📷</span>
                <span class="upload-text">Haz clic para subir una imagen</span>
                <span class="upload-hint">Recomendado: 500x500 px</span>
              </div>
              <img
                v-else
                :src="productData.imagePreview"
                alt="Vista previa"
                class="image-preview"
              />
              <input
                type="file"
                ref="fileInput"
                @change="handleImageUpload"
                accept="image/*"
                class="file-input"
              />
            </div>
            <div v-if="productData.imagePreview" class="image-actions">
              <button type="button" class="btn-change-image" @click="triggerFileInput">
                Cambiar Imagen
              </button>
              <button type="button" class="btn-remove-image" @click="removeImage">Eliminar</button>
            </div>
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="accesorio-nombre">Nombre *</label>
            <input
              type="text"
              id="accesorio-nombre"
              v-model="productData.nombre"
              required
              placeholder="Ej: AirPods Pro"
            />
          </div>

          <div class="form-group">
            <label for="accesorio-modelo">Modelo *</label>
            <input
              type="text"
              id="accesorio-modelo"
              v-model="productData.modelo"
              required
              placeholder="Ej: 2da generación"
            />
          </div>
        </div>

        <div class="form-group">
          <label for="accesorio-compatible">Compatible con *</label>
          <select id="accesorio-compatible" v-model="productData.compatible" required multiple>
            <option value="iPhone">iPhone</option>
            <option value="Mac">Mac</option>
            <option value="iPad">iPad</option>
            <option value="Apple Watch">Apple Watch</option>
            <option value="Todos">Todos los dispositivos</option>
          </select>
          <small>Mantén Ctrl (Cmd en Mac) para seleccionar múltiples opciones</small>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="accesorio-precio">Precio ($) *</label>
            <input
              type="number"
              id="accesorio-precio"
              v-model="productData.precio"
              required
              min="0"
              step="0.01"
              placeholder="0.00"
            />
          </div>

          <div class="form-group">
            <label for="accesorio-cantidad">Cantidad en Stock *</label>
            <input
              type="number"
              id="accesorio-cantidad"
              v-model="productData.cantidad"
              required
              min="0"
              placeholder="0"
            />
          </div>
        </div>

        <div class="form-actions">
          <button type="button" class="btn-cancel" @click="cancelCreation">Cancelar</button>
          <button type="submit" class="btn-create" :disabled="loading || !productData.imageFile">
            {{ loading ? 'Creando...' : 'Crear Accesorio' }}
          </button>
        </div>
      </form>
    </div>

    <!-- Mensaje de éxito -->
    <div v-if="showSuccess" class="success-message">
      <h3>✅ Producto creado exitosamente!</h3>
      <p>El {{ getProductTypeName() }} ha sido registrado en el inventario.</p>
      <div class="success-actions">
        <button class="btn-another" @click="createAnother">Crear Otro Producto</button>
        <button class="btn-finish" @click="finishCreation">Finalizar</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CreateProductPage',
  props: {
    category: String,
  },
  data() {
    return {
      selectedType: null,
      macSubtype: null,
      loading: false,
      showSuccess: false,

      // Colores disponibles por categoría
      availableColors: [
        '#000000',
        '#FFFFFF',
        '#FF3B30',
        '#007AFF',
        '#34C759',
        '#FF9500',
        '#8E8E93',
        '#FF2D55',
      ],
      macbookColors: ['#000000', '#FFFFFF', '#FF9500', '#007AFF', '#34C759'],
      imacColors: ['#FF3B30', '#007AFF', '#34C759', '#FF9500', '#8E8E93', '#FF2D55'],
      ipadColors: ['#000000', '#FFFFFF', '#8E8E93', '#007AFF', '#34C759'],

      // Capacidades por categoría
      iphoneCapacities: ['128GB', '256GB', '512GB', '1TB'],
      macCapacities: ['256GB', '512GB', '1TB', '2TB', '4TB', '8TB'],

      // Datos del producto
      productData: this.getInitialProductData(),
    }
  },
  mounted() {
    if (this.category) {
      this.selectedType = this.category
    }
  },
  methods: {
    selectType(type) {
      this.selectedType = type
      this.macSubtype = null
      this.resetProductData()
    },

    selectMacSubtype(subtype) {
      this.macSubtype = subtype
      this.resetProductData()
    },

    toggleColor(color) {
      const index = this.productData.colores.indexOf(color)
      if (index > -1) {
        this.productData.colores.splice(index, 1)
      } else {
        this.productData.colores.push(color)
      }
    },

    toggleCapacity(capacity) {
      const index = this.productData.capacidades.indexOf(capacity)
      if (index > -1) {
        this.productData.capacidades.splice(index, 1)
      } else {
        this.productData.capacidades.push(capacity)
      }
    },

    getColorName(color) {
      const colorNames = {
        '#000000': 'Negro',
        '#FFFFFF': 'Blanco',
        '#FF3B30': 'Rojo',
        '#007AFF': 'Azul',
        '#34C759': 'Verde',
        '#FF9500': 'Naranja',
        '#8E8E93': 'Gris',
        '#FF2D55': 'Rosa',
      }
      return colorNames[color] || color
    },

    getInitialProductData() {
      return {
        serie: '',
        modelo: '',
        nombre: '',
        colores: [],
        capacidades: [],
        sim: '',
        chip: '',
        ram: '',
        teclado: '',
        pantalla: '',
        compatible: [],
        precio: '',
        cantidad: '',
        imageFile: null,
        imagePreview: null,
      }
    },

    // Métodos para manejo de imágenes
    triggerFileInput() {
      this.$refs.fileInput.click()
    },

    handleImageUpload(event) {
      const file = event.target.files[0]
      if (file) {
        // Validar tipo de archivo
        if (!file.type.startsWith('image/')) {
          alert('Por favor, selecciona un archivo de imagen válido.')
          return
        }

        // Validar tamaño (max 5MB)
        if (file.size > 5 * 1024 * 1024) {
          alert('La imagen es demasiado grande. El tamaño máximo permitido es 5MB.')
          return
        }

        this.productData.imageFile = file

        // Crear vista previa
        const reader = new FileReader()
        reader.onload = (e) => {
          this.productData.imagePreview = e.target.result
        }
        reader.readAsDataURL(file)
      }
    },

    removeImage() {
      this.productData.imageFile = null
      this.productData.imagePreview = null
      this.$refs.fileInput.value = ''
    },

    async createProduct() {
      if (!this.validateProductData()) {
        alert('Por favor, complete todos los campos requeridos correctamente.')
        return
      }

      if (!this.productData.imageFile) {
        alert('Por favor, sube una imagen del producto.')
        return
      }

      this.loading = true

      try {
        await new Promise((resolve) => setTimeout(resolve, 1000))

        // Cargar productos existentes
        const existingProducts = JSON.parse(localStorage.getItem('products') || '[]')

        // Determinar el tipo de producto final
        let finalType = this.selectedType
        let productName = this.productData.serie || this.productData.nombre

        if (this.selectedType === 'Mac') {
          finalType = this.macSubtype === 'MacBook' ? 'MacBook' : 'MacEscritorio'
          productName = this.productData.serie
        }

        // Verificar si ya existe un producto similar
        const existingProduct = existingProducts.find(
          (product) =>
            product.serie === this.productData.serie &&
            product.modelo === this.productData.modelo &&
            product.type === finalType,
        )

        if (existingProduct) {
          alert(`Este ${this.getProductTypeName()} ya existe en el inventario.`)
          return
        }

        // Crear nuevo producto con la imagen
        const newProduct = {
          id: Date.now(),
          type: finalType,
          category: this.selectedType, // Categoría principal
          name: productName,
          ...this.productData,
          image: this.productData.imagePreview, // Guardar la imagen como base64
          createdAt: new Date().toISOString(),
          precio: parseFloat(this.productData.precio),
          cantidad: parseInt(this.productData.cantidad),
          stock: parseInt(this.productData.cantidad),
        }

        // Eliminar el archivo de imagen del objeto para guardar en localStorage
        delete newProduct.imageFile

        // Guardar en localStorage
        existingProducts.push(newProduct)
        localStorage.setItem('products', JSON.stringify(existingProducts))

        this.showSuccess = true

        // Emitir evento para notificar a Products.vue
        this.$emit('product-created', newProduct)
      } catch (error) {
        console.error('Error creando producto:', error)
        alert('Error al crear el producto.')
      } finally {
        this.loading = false
      }
    },

    validateProductData() {
      const data = this.productData

      switch (this.selectedType) {
        case 'iPhone':
          return (
            data.serie &&
            data.modelo &&
            data.colores.length > 0 &&
            data.capacidades.length > 0 &&
            data.sim &&
            data.precio &&
            data.cantidad
          )

        case 'Mac':
          if (this.macSubtype === 'MacBook') {
            return (
              data.serie &&
              data.modelo &&
              data.chip &&
              data.ram &&
              data.capacidades.length > 0 &&
              data.colores.length > 0 &&
              data.teclado &&
              data.precio &&
              data.cantidad
            )
          } else {
            return (
              data.serie &&
              data.modelo &&
              data.chip &&
              data.ram &&
              data.capacidades.length > 0 &&
              data.colores.length > 0 &&
              data.precio &&
              data.cantidad
            )
          }

        case 'iPad':
          return (
            data.serie &&
            data.modelo &&
            data.colores.length > 0 &&
            data.pantalla &&
            data.precio &&
            data.cantidad
          )

        case 'Accesorios':
          return (
            data.nombre && data.modelo && data.compatible.length > 0 && data.precio && data.cantidad
          )

        default:
          return false
      }
    },

    getProductTypeName() {
      if (this.selectedType === 'Mac') {
        return this.macSubtype === 'MacBook' ? 'MacBook' : 'Mac de Escritorio'
      }
      return this.selectedType
    },

    cancelCreation() {
      this.selectedType = null
      this.macSubtype = null
      this.resetProductData()
      this.$emit('change-page', 'products', this.category)
    },

    createAnother() {
      this.showSuccess = false
      this.selectedType = null
      this.macSubtype = null
      this.resetProductData()
    },

    finishCreation() {
      this.$emit('change-page', 'products', this.selectedType || this.category)
    },

    resetProductData() {
      this.productData = this.getInitialProductData()
      if (this.$refs.fileInput) {
        this.$refs.fileInput.value = ''
      }
    },
  },
}
</script>

<style scoped>
/* Estilos para la subida de imágenes */
.image-upload-section {
  margin-bottom: 30px;
}

.image-upload-label {
  display: block;
  margin-bottom: 10px;
  font-weight: 600;
  color: #1d1d1f;
  font-size: 14px;
}

.image-upload-container {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.image-upload-area {
  border: 2px dashed #d2d2d7;
  border-radius: 12px;
  padding: 40px 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #f9f9f9;
  position: relative;
  min-height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.image-upload-area:hover {
  border-color: #0071e3;
  background: #f0f8ff;
}

.image-upload-area.has-image {
  border-style: solid;
  padding: 20px;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.upload-icon {
  font-size: 48px;
  opacity: 0.7;
}

.upload-text {
  font-size: 16px;
  font-weight: 600;
  color: #1d1d1f;
}

.upload-hint {
  font-size: 14px;
  color: #8e8e93;
}

.image-preview {
  max-width: 100%;
  max-height: 300px;
  border-radius: 8px;
  object-fit: contain;
}

.file-input {
  display: none;
}

.image-actions {
  display: flex;
  gap: 10px;
  justify-content: center;
}

.btn-change-image,
.btn-remove-image {
  padding: 8px 16px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-change-image {
  background: #0071e3;
  color: white;
}

.btn-change-image:hover {
  background: #0056b3;
}

.btn-remove-image {
  background: #ff3b30;
  color: white;
}

.btn-remove-image:hover {
  background: #e0352b;
}

/* Estilos existentes se mantienen igual */
.mac-subtype-selection {
  text-align: center;
  padding: 60px 20px;
}

.mac-subtype-selection h2 {
  font-size: 28px;
  font-weight: 700;
  color: #1d1d1f;
  margin-bottom: 40px;
}

.subtype-buttons {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  max-width: 600px;
  margin: 0 auto 40px;
}

.subtype-btn {
  background: white;
  border: 2px solid #e5e5e7;
  border-radius: 15px;
  padding: 30px 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.subtype-btn:hover {
  border-color: #0071e3;
  transform: translateY(-5px);
  box-shadow: 0 10px 25px rgba(0, 113, 227, 0.1);
}

.subtype-icon {
  font-size: 40px;
}

.subtype-name {
  font-size: 18px;
  font-weight: 600;
  color: #1d1d1f;
}

.subtype-description {
  font-size: 14px;
  color: #6e6e73;
}

.btn-back-subtype {
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

.btn-back-subtype:hover {
  background: #0071e3;
  color: white;
}

/* Estilos para select múltiple */
.form-group select[multiple] {
  height: 120px;
}

.form-group small {
  display: block;
  margin-top: 5px;
  font-size: 12px;
  color: #8e8e93;
  font-style: italic;
}

.create-product {
  padding: 40px 20px;
  max-width: 800px;
  margin: 0 auto;
}

.create-product-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
  flex-wrap: wrap;
  gap: 20px;
}

.create-product-header h1 {
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

.product-type-selection {
  text-align: center;
  padding: 60px 20px;
}

.product-type-selection h2 {
  font-size: 28px;
  font-weight: 700;
  color: #1d1d1f;
  margin-bottom: 40px;
}

.type-buttons {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  max-width: 600px;
  margin: 0 auto;
}

.type-btn {
  background: white;
  border: 2px solid #e5e5e7;
  border-radius: 15px;
  padding: 30px 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}

.type-btn:hover {
  border-color: #0071e3;
  transform: translateY(-5px);
  box-shadow: 0 10px 25px rgba(0, 113, 227, 0.1);
}

.type-icon {
  font-size: 40px;
}

.type-name {
  font-size: 18px;
  font-weight: 600;
  color: #1d1d1f;
}

.product-form {
  background: white;
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.product-form h2 {
  font-size: 24px;
  font-weight: 700;
  color: #1d1d1f;
  margin-bottom: 30px;
  text-align: center;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.form-group {
  margin-bottom: 25px;
}

.form-group label {
  display: block;
  margin-bottom: 10px;
  font-weight: 600;
  color: #1d1d1f;
  font-size: 14px;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #d2d2d7;
  border-radius: 12px;
  font-size: 16px;
  transition: all 0.3s ease;
  background: white;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: #0071e3;
  box-shadow: 0 0 0 4px rgba(0, 113, 227, 0.1);
}

.color-selection {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 10px;
}

.color-option {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px;
  border: 2px solid #e5e5e7;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.color-option.selected {
  border-color: #0071e3;
  background: #f0f8ff;
}

.color-swatch {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  border: 2px solid #f0f0f0;
}

.color-name {
  font-size: 12px;
  font-weight: 500;
  color: #1d1d1f;
}

.capacity-selection {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.capacity-option {
  padding: 10px 16px;
  border: 2px solid #e5e5e7;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
  font-weight: 500;
}

.capacity-option.selected {
  border-color: #0071e3;
  background: #0071e3;
  color: white;
}

.form-actions {
  display: flex;
  gap: 15px;
  justify-content: flex-end;
  margin-top: 40px;
}

.btn-cancel {
  background: transparent;
  color: #6e6e73;
  border: 2px solid #d2d2d7;
  padding: 12px 24px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-cancel:hover {
  background: #f5f5f7;
}

.btn-create {
  background: linear-gradient(135deg, #34c759, #30d158);
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-create:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(52, 199, 89, 0.3);
}

.btn-create:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.success-message {
  text-align: center;
  background: white;
  border-radius: 20px;
  padding: 60px 40px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.success-message h3 {
  font-size: 24px;
  font-weight: 700;
  color: #34c759;
  margin-bottom: 15px;
}

.success-message p {
  font-size: 16px;
  color: #6e6e73;
  margin-bottom: 30px;
}

.success-actions {
  display: flex;
  gap: 15px;
  justify-content: center;
}

.btn-another {
  background: #0071e3;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-another:hover {
  background: #0056b3;
}

.btn-finish {
  background: #34c759;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-finish:hover {
  background: #2daa4a;
}

@media (max-width: 768px) {
  .create-product-header {
    flex-direction: column;
    text-align: center;
  }

  .form-row {
    grid-template-columns: 1fr;
  }

  .type-buttons,
  .subtype-buttons {
    grid-template-columns: 1fr;
  }

  .color-selection {
    grid-template-columns: repeat(auto-fit, minmax(100px, 1fr));
  }

  .form-actions,
  .success-actions {
    flex-direction: column;
  }

  .product-form {
    padding: 30px 20px;
  }

  .image-upload-area {
    min-height: 150px;
    padding: 30px 15px;
  }

  .image-actions {
    flex-direction: column;
  }
}
</style>
