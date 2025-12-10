<template>
  <div class="create-product">
    <div class="create-product-header">
      <h1>Crear Nuevo Producto</h1>
      <button class="btn-back" @click="$emit('change-page', 'products')">
        ← Volver a Productos
      </button>
    </div>

    <div class="product-form">
      <h2>Datos del Producto</h2>
      <p class="form-description">
        Completa los campos para registrar un nuevo producto en el inventario.
      </p>

      <form @submit.prevent="crearProducto">
        <div class="form-row">
          <div class="form-group">
            <label for="tipo">Tipo de Producto *</label>
            <select id="tipo" v-model="productData.tipo" required @change="validarCamposCruzados">
              <option disabled value="">Seleccionar...</option>
              <option value="iPhone">iPhone</option>
              <option value="Mac">Mac</option>
              <option value="iPad">iPad</option>
              <option value="Accesorios">Accesorios</option>
            </select>
            <div v-if="errores.tipo" class="error-message">
              {{ errores.tipo }}
            </div>
          </div>
          <div class="form-group">
            <label for="serie">Serie *</label>
            <input
              type="text"
              id="serie"
              v-model="productData.serie"
              required
              placeholder="Ej: iPhone 15 Pro"
              @blur="validarSerie"
            />
            <div v-if="errores.serie" class="error-message">
              {{ errores.serie }}
            </div>
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="modelo">Modelo *</label>
            <input
              type="text"
              id="modelo"
              v-model="productData.modelo"
              required
              placeholder="Ej: Pro Max"
              @blur="validarModelo"
            />
            <div v-if="errores.modelo" class="error-message">
              {{ errores.modelo }}
            </div>
          </div>

          <div class="form-group" v-if="mostrarConfigSim">
            <label for="configSim">Configuración SIM *</label>
            <input
              type="text"
              id="configSim"
              v-model="productData.configSim"
              placeholder="Ej: eSIM, Dual SIM"
              @blur="validarConfigSim"
            />
            <div v-if="errores.configSim" class="error-message">
              {{ errores.configSim }}
            </div>
          </div>
        </div>

        <div class="form-group">
          <label for="colores">Colores (separados por coma) *</label>
          <input
            type="text"
            id="colores"
            v-model="coloresInput"
            required
            placeholder="Ej: Titanio Natural, Azul, Negro"
            @blur="validarColores"
          />
          <small>El usuario verá esto como una lista de opciones.</small>
          <div v-if="errores.colores" class="error-message">
            {{ errores.colores }}
          </div>
        </div>

        <div class="form-group" v-if="mostrarCapacidades">
          <label for="capacidades">Capacidades (separadas por coma) *</label>
          <input
            type="text"
            id="capacidades"
            v-model="capacidadesInput"
            placeholder="Ej: 256GB, 512GB, 1TB"
            @blur="validarCapacidades"
          />
          <div v-if="errores.capacidades" class="error-message">
            {{ errores.capacidades }}
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="precio">Precio ($) *</label>
            <input
              type="number"
              id="precio"
              v-model.number="productData.precio"
              required
              min="0"
              step="0.01"
              placeholder="0.00"
              @blur="validarPrecio"
            />
            <div v-if="errores.precio" class="error-message">
              {{ errores.precio }}
            </div>
          </div>
          <div class="form-group">
            <label for="cantidad">Cantidad en Stock *</label>
            <input
              type="number"
              id="cantidad"
              v-model.number="productData.cantidad"
              required
              min="0"
              step="1"
              placeholder="0"
              @blur="validarCantidad"
            />
            <div v-if="errores.cantidad" class="error-message">
              {{ errores.cantidad }}
            </div>
          </div>
        </div>

        <div class="form-group">
          <label for="imagen">Subir Imagen *</label>
          <input
            type="file"
            id="imagen"
            @change="manejarSubidaImagen"
            required
            accept="image/png, image/jpeg, image/webp"
          />
          <div v-if="errores.imagen" class="error-message">
            {{ errores.imagen }}
          </div>
        </div>

        <div v-if="vistaPreviaImagen" class="image-preview-container">
          <label>Vista Previa:</label>
          <img :src="vistaPreviaImagen" alt="Vista previa" class="image-preview" />
        </div>
        <div class="form-actions">
          <button type="button" class="btn-cancel" @click="cancelarCreacion">Cancelar</button>
          <button type="submit" class="btn-create" :disabled="loading || !esFormularioValido">
            {{ loading ? 'Creando...' : 'Crear Producto' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import apiClient from '@/api/axiosConfig.js'
import Swal from 'sweetalert2'

export default {
  name: 'CreateProductPage',
  data() {
    return {
      loading: false,
      coloresInput: '',
      capacidadesInput: '',
      vistaPreviaImagen: null,
      productData: {
        tipo: '',
        serie: '',
        modelo: '',
        colores: [],
        capacidades: [],
        configSim: '',
        precio: 0,
        cantidad: 0,
        imagen: '',
      },
      errores: {
        tipo: null,
        serie: null,
        modelo: null,
        configSim: null,
        colores: null,
        capacidades: null,
        precio: null,
        cantidad: null,
        imagen: null,
      }
    }
  },
  computed: {
    mostrarConfigSim() {
      return ['iPhone', 'iPad'].includes(this.productData.tipo);
    },
    mostrarCapacidades() {
      return this.productData.tipo !== 'Accesorios';
    },
    esFormularioValido() {
      // Revisa que no haya ningún mensaje de error activo
      return !this.errores.tipo &&
        !this.errores.serie &&
        !this.errores.modelo &&
        !this.errores.configSim &&
        !this.errores.colores &&
        !this.errores.capacidades &&
        !this.errores.precio &&
        !this.errores.cantidad &&
        !this.errores.imagen;
    }
  },
  methods: {
    // --- MÉTODOS DE VALIDACIÓN (CON CONGRUENCIA) ---

    // Este método se llama cuando el TIPO cambia, para re-validar los otros campos
    validarCamposCruzados() {
      this.validarTipo();
      this.validarSerie();
      this.validarModelo();
      this.validarConfigSim();
      this.validarCapacidades();
    },

    validarTipo() {
      this.errores.tipo = this.productData.tipo ? null : 'Debes seleccionar un tipo.';
    },

    validarSerie() {
      const serie = this.productData.serie.toLowerCase();
      const tipo = this.productData.tipo;

      if (!serie.trim()) {
        this.errores.serie = 'La serie es obligatoria.';
        return;
      }

      const regex = /^[a-zA-Z0-9\- ]+$/;
      if (!regex.test(serie)) {
        this.errores.serie = 'Serie inválida (solo letras, números, -).';
        return;
      }

      // Validación de congruencia
      if (tipo === 'Mac' && (serie.includes('iphone') || serie.includes('ipad') || serie.includes('airpods'))) {
        this.errores.serie = 'La serie no parece ser de un Mac.';
      } else if (tipo === 'iPhone' && (serie.includes('mac') || serie.includes('ipad') || serie.includes('airpods'))) {
        this.errores.serie = 'La serie no parece ser de un iPhone.';
      } else if (tipo === 'iPad' && (serie.includes('mac') || serie.includes('iphone') || serie.includes('airpods'))) {
        this.errores.serie = 'La serie no parece ser de un iPad.';
      } else if (tipo === 'Accesorios' && (serie.includes('mac') || serie.includes('iphone') || serie.includes('ipad'))) {
        this.errores.serie = 'La serie no parece ser de un Accesorio.';
      } else {
        this.errores.serie = null; // Válido
      }
    },

    validarModelo() {
      const modelo = this.productData.modelo.toLowerCase();
      const tipo = this.productData.tipo;

      if (!modelo.trim()) {
        this.errores.modelo = 'El modelo es obligatorio.';
        return;
      }

      const regex = /^[a-zA-Z0-9\- ()]+$/;
      if (!regex.test(modelo)) {
        this.errores.modelo = 'Modelo inválido (solo letras, números, -, ()).';
        return;
      }

      // Validación de congruencia
      if (tipo === 'Mac' && (modelo.includes('iphone') || modelo.includes('ipad') || modelo.includes('airpods'))) {
        this.errores.modelo = 'El modelo no parece ser de un Mac.';
      } else if (tipo === 'iPhone' && (modelo.includes('mac') || modelo.includes('ipad') || modelo.includes('airpods'))) {
        this.errores.modelo = 'El modelo no parece ser de un iPhone.';
      } else if (tipo === 'iPad' && (modelo.includes('mac') || modelo.includes('iphone') || modelo.includes('airpods'))) {
        this.errores.modelo = 'El modelo no parece ser de un iPad.';
      } else if (tipo === 'Accesorios' && (modelo.includes('mac') || modelo.includes('iphone') || modelo.includes('ipad'))) {
        this.errores.modelo = 'El modelo no parece ser de un Accesorio.';
      } else {
        this.errores.modelo = null; // Válido
      }
    },

    validarConfigSim() {
      if (this.mostrarConfigSim && !this.productData.configSim.trim()) {
        this.errores.configSim = 'La config. SIM es obligatoria.';
      } else {
        this.errores.configSim = null;
      }
    },

    validarColores() {
      if (!this.coloresInput.trim()) {
        this.errores.colores = 'Debes ingresar al menos un color.';
      } else if (/^,+$/.test(this.coloresInput.trim())) {
        this.errores.colores = 'Formato de colores inválido.';
      } else {
        this.errores.colores = null;
      }
    },

    validarCapacidades() {
      if (this.mostrarCapacidades && !this.capacidadesInput.trim()) {
        this.errores.capacidades = 'Debes ingresar al menos una capacidad.';
      } else if (this.mostrarCapacidades && /^,+$/.test(this.capacidadesInput.trim())) {
        this.errores.capacidades = 'Formato de capacidades inválido.';
      } else {
        this.errores.capacidades = null;
      }
    },

    validarPrecio() {
      if (this.productData.precio <= 0) {
        this.errores.precio = 'El precio debe ser mayor a 0.';
      } else {
        this.errores.precio = null;
      }
    },

    validarCantidad() {
      if (this.productData.cantidad < 0) {
        this.errores.cantidad = 'La cantidad no puede ser negativa.';
      } else {
        this.errores.cantidad = null;
      }
    },

    validarImagen() {
      if (!this.productData.imagen) {
        this.errores.imagen = 'Debes subir una imagen.';
      } else {
        this.errores.imagen = null;
      }
    },

    manejarSubidaImagen(event) {
      const file = event.target.files[0];
      if (!file) {
        this.productData.imagen = '';
        this.vistaPreviaImagen = null;
        this.validarImagen();
        return;
      }

      const reader = new FileReader();
      reader.onload = (e) => {
        this.productData.imagen = e.target.result;
        this.vistaPreviaImagen = e.target.result;
        this.validarImagen();
      };
      reader.onerror = (error) => {
        console.error('Error leyendo el archivo:', error);
        Swal.fire({
          icon: 'error',
          title: 'Error de Archivo',
          text: 'No se pudo leer el archivo de imagen.',
          confirmButtonColor: '#0071e3',
        });
      };
      reader.readAsDataURL(file);
    },

    async crearProducto() {
      // --- ¡VALIDACIÓN FINAL ANTES DE ENVIAR! ---
      this.validarTipo();
      this.validarSerie();
      this.validarModelo();
      this.validarConfigSim();
      this.validarColores();
      this.validarCapacidades();
      this.validarPrecio();
      this.validarCantidad();
      this.validarImagen();

      if (!this.esFormularioValido) {
        Swal.fire({
          icon: 'warning',
          title: 'Formulario Incompleto',
          text: 'Por favor, revisa los campos marcados en rojo.',
          confirmButtonColor: '#0071e3',
        });
        return;
      }
      // --- FIN DE LA VALIDACIÓN ---

      this.loading = true;
      const token = localStorage.getItem('userToken');

      if (!token) {
        Swal.fire({
          icon: 'error',
          title: 'Acceso Denegado',
          text: 'No estás autenticado como administrador.',
          confirmButtonColor: '#0071e3',
        });
        this.loading = false;
        return;
      }

      try {
        const payload = {
          ...this.productData,
          colores: this.coloresInput.split(',').map(s => s.trim()).filter(Boolean),
        };

        if (this.mostrarCapacidades) {
          payload.capacidades = this.capacidadesInput.split(',').map(s => s.trim()).filter(Boolean);
        } else {
          payload.capacidades = ['N/A'];
        }

        if (this.mostrarConfigSim) {
          payload.configSim = this.productData.configSim;
        } else {
          payload.configSim = 'N/A';
        }

        await apiClient.post(
          '/productos',
          payload,
          {
            headers: { Authorization: `Bearer ${token}` }
          }
        );

        this.$emit('product-created'); // App.vue maneja el modal de éxito

      } catch (error) {
        console.error('Error creando producto:', error);
        Swal.fire({
          icon: 'error',
          title: 'Error al crear el producto',
          confirmButtonColor: '#0071e3',
        });
      } finally {
        this.loading = false;
      }
    },

    cancelarCreacion() {
      this.$emit('change-page', 'products');
    },
  },
}
</script>

<style scoped>
/* Estilos para el formulario simplificado */
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
  margin-bottom: 10px;
  text-align: center;
}

.form-description {
  text-align: center;
  color: #6e6e73;
  margin-bottom: 30px;
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
.form-group select,
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

/* Estilo para el input de archivo */
.form-group input[type="file"] {
  padding: 10px 12px;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #0071e3;
  box-shadow: 0 0 0 4px rgba(0, 113, 227, 0.1);
}

.form-group small {
  display: block;
  margin-top: 5px;
  font-size: 12px;
  color: #8e8e93;
  font-style: italic;
}

/* ¡NUEVO! Estilo de mensaje de error */
.error-message {
  color: #ff3b30;
  font-size: 12px;
  margin-top: 5px;
  font-weight: 500;
}


.image-preview-container {
  margin-bottom: 25px;
}
.image-preview-container label {
  display: block;
  margin-bottom: 10px;
  font-weight: 600;
  color: #1d1d1f;
  font-size: 14px;
}
.image-preview {
  max-width: 200px;
  height: auto;
  border-radius: 8px;
  border: 1px solid #d2d2d7;
  object-fit: contain;
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

@media (max-width: 768px) {
  .form-row {
    grid-template-columns: 1fr;
  }
  .form-actions {
    flex-direction: column;
  }
  .product-form {
    padding: 30px 20px;
  }
}
</style>
