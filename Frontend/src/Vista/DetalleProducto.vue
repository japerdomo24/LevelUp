<template>
  <div class="product-detail-layout">
    <div class="product-gallery">
      <div class="gallery-placeholder">
        <img
          v-if="product.imagen || editForm.imagen"
          :src="isEditing ? (editForm.imagen || product.imagen) : product.imagen"
          :alt="product.serie"
        />
        <span v-else>Imagen no disponible</span>

        <div v-if="isEditing" style="margin-top: 15px;">
           <input type="file" @change="manejarSubidaImagen" accept="image/*" />
        </div>
      </div>
    </div>

    <div class="product-config">

      <div v-if="isEditing" class="edit-mode">
        <h2 style="margin-bottom: 20px; color: #0071e3;">Editar Producto</h2>

        <div class="form-group">
          <label>Serie:</label>
          <input v-model="editForm.serie" class="edit-input" type="text" />
        </div>
        <div class="form-group">
          <label>Modelo:</label>
          <input v-model="editForm.modelo" class="edit-input" type="text" />
        </div>
        <div class="form-group" v-if="mostrarConfigSim">
          <label>Config SIM:</label>
          <input v-model="editForm.configSim" class="edit-input" type="text" />
        </div>
        <div class="form-group">
          <label>Precio ($):</label>
          <input v-model.number="editForm.precio" class="edit-input" type="number" step="0.01" />
        </div>
        <div class="form-group">
          <label>Stock:</label>
          <input v-model.number="editForm.cantidad" class="edit-input" type="number" />
        </div>
        <div class="form-group">
          <label>Colores (separados por coma):</label>
          <input v-model="coloresInput" class="edit-input" type="text" />
        </div>
        <div class="form-group" v-if="mostrarCapacidades">
          <label>Capacidades (separadas por coma):</label>
          <input v-model="capacidadesInput" class="edit-input" type="text" />
        </div>

        <div class="action-buttons">
            <button class="btn btn-primary" @click="guardarCambios">Guardar Cambios</button>
            <button class="btn btn-secondary" @click="cancelarEdicion">Cancelar</button>
        </div>
      </div>

      <div v-else>
        <h2 class="product-detail-title">{{ product.serie }} {{ product.modelo }}</h2>
        <h3 class="product-detail-subtitle">Observa tu futuro {{ product.tipo }}</h3>

        <div v-if="user?.rol === 'admin'" style="background: #f5f5f7; padding: 15px; border-radius: 12px; margin-bottom: 20px;">
            <p><strong>ID:</strong> <span style="font-size: 12px; color: #888;">{{ product.id }}</span></p>
            <p><strong>Precio:</strong> ${{ product.precio }}</p>
            <p><strong>Stock:</strong> {{ product.cantidad }} unidades</p>
        </div>

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
          <div class="option-title">Acciones</div>

          <div class="action-buttons">
            <template v-if="user?.rol === 'admin'">
              <button class="btn btn-admin-edit" @click="activarEdicion">
                ✏️ Editar
              </button>
              <button class="btn btn-admin-delete" @click="eliminarProducto">
                🗑️ Eliminar
              </button>
            </template>

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
  </div>
</template>

<script>
import Swal from 'sweetalert2';
import apiClient from '@/api/axiosConfig';

export default {
  name: 'ProductDetail',
  props: {
    product: Object,
    user: Object,
  },
  data() {
    return {
      // Estado normal
      opcionesSeleccionadas: {
        color: '',
        capacity: '',
      },
      quantity: 1,

      // Estado de Edición
      isEditing: false,
      editForm: {},
      coloresInput: '',
      capacidadesInput: ''
    }
  },
  computed: {
    mostrarColores() {
      return this.product.colores &&
        this.product.colores.length > 0 &&
        !(this.product.colores.length === 1 && this.product.colores[0] === 'N/A');
    },
    mostrarCapacidades() {
      return this.product.capacidades &&
        this.product.capacidades.length > 0 &&
        !(this.product.capacidades.length === 1 && this.product.capacidades[0] === 'N/A');
    },
    mostrarConfigSim() {
      const tipo = this.isEditing ? this.editForm.tipo : this.product.tipo;
      return tipo && ['iPhone', 'iPad'].includes(tipo);
    }
  },

  methods: {
    // --- LÓGICA DE USUARIO ---
    agregarAlCarrito() {
      const opcionesRequeridas = []
      if (this.mostrarColores) opcionesRequeridas.push({ key: 'color', name: 'Color' });
      if (this.mostrarCapacidades) opcionesRequeridas.push({ key: 'capacity', name: 'Capacidad' });

      for (const opcion of opcionesRequeridas) {
        if (!this.opcionesSeleccionadas[opcion.key]) {
          Swal.fire({
            icon: 'warning',
            title: 'Opción requerida',
            text: `Por favor, selecciona: ${opcion.name}.`,
            confirmButtonColor: '#0071e3',
          });
          return;
        }
      }

      const itemCarrito = {
        ...this.product,
        selectedColor: this.opcionesSeleccionadas.color,
        selectedCapacity: this.opcionesSeleccionadas.capacity,
        quantity: this.quantity,
      }
      this.$emit('add-to-cart', itemCarrito)
    },

    volverAtras() {
      this.$emit('change-page', 'products', this.product.tipo)
    },

    // --- LÓGICA DE ADMIN (EDITAR / ELIMINAR) ---

    // 1. Activar Edición
    activarEdicion() {
        this.editForm = { ...this.product }; // Clonar datos
        this.coloresInput = this.editForm.colores ? this.editForm.colores.join(', ') : '';
        this.capacidadesInput = this.editForm.capacidades ? this.editForm.capacidades.join(', ') : '';
        this.isEditing = true;
    },

    // 2. Cancelar Edición
    cancelarEdicion() {
        this.isEditing = false;
        this.editForm = {};
    },

    // 3. Manejar Nueva Imagen
    manejarSubidaImagen(event) {
      const file = event.target.files[0];
      if (!file) return;
      const reader = new FileReader();
      reader.onload = (e) => {
        this.editForm.imagen = e.target.result; // Convertir a Base64
      };
      reader.readAsDataURL(file);
    },

    // 4. GUARDAR CAMBIOS (PUT)
    async guardarCambios() {
        try {
            const token = localStorage.getItem('userToken');

            // Preparar payload
            const payload = {
                ...this.editForm,
                colores: this.coloresInput.split(',').map(c => c.trim()).filter(Boolean),
                capacidades: this.capacidadesInput.split(',').map(c => c.trim()).filter(Boolean),
                imagen: this.editForm.imagen
            };

            // Petición al Backend
            await apiClient.put(`/productos/${this.product.id}`, payload, {
                headers: { Authorization: `Bearer ${token}` }
            });

            Swal.fire('Actualizado', 'El producto se actualizó correctamente', 'success');
            this.isEditing = false;

            // Avisar al padre para recargar la lista si volvemos atrás
            this.$emit('change-page', 'products', this.product.tipo);

        } catch (error) {
            console.error(error);
            Swal.fire('Error', 'No se pudo actualizar el producto.', 'error');
        }
    },

    // 5. ELIMINAR PRODUCTO (DELETE)
    async eliminarProducto() {
        const result = await Swal.fire({
            title: '¿Estás seguro?',
            text: "No podrás revertir esto. El producto se eliminará permanentemente.",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#ff3b30',
            cancelButtonColor: '#6e6e73',
            confirmButtonText: 'Sí, eliminarlo',
            cancelButtonText: 'Cancelar'
        });

        if (result.isConfirmed) {
            try {
                const token = localStorage.getItem('userToken');
                await apiClient.delete(`/productos/${this.product.id}`, {
                    headers: { Authorization: `Bearer ${token}` }
                });

                await Swal.fire('Eliminado!', 'El producto ha sido eliminado.', 'success');
                this.$emit('change-page', 'products', this.product.tipo);
            } catch (error) {
                console.error(error);
                Swal.fire('Error', 'No se pudo eliminar el producto.', 'error');
            }
        }
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
  flex-direction: column;
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
  flex-wrap: wrap;
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
.btn-secondary {
    background: transparent;
    color: #007aff;
    border: 1px solid #007aff;
    border-radius: 8px;
    padding: 15px 30px;
    font-size: 16px;
    font-weight: 600;
    cursor: pointer;
}
.btn-admin-edit {
  background-color: #ff9500;
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
.btn-admin-delete {
    background-color: transparent;
    color: #ff3b30;
    border: 2px solid #ff3b30;
    border-radius: 8px;
    padding: 13px 28px;
    font-size: 16px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
}
.btn-admin-delete:hover {
    background-color: #ff3b30;
    color: white;
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

/* ESTILOS DE FORMULARIO DE EDICIÓN */
.edit-input {
    width: 100%;
    padding: 12px;
    border: 1px solid #d2d2d7;
    border-radius: 8px;
    font-size: 16px;
    margin-bottom: 5px;
}
.form-group {
    margin-bottom: 15px;
}
.form-group label {
    display: block;
    margin-bottom: 5px;
    font-weight: 600;
    font-size: 14px;
    color: #1d1d1f;
}

@media (max-width: 768px) {
  .product-detail-layout {
    grid-template-columns: 1fr;
  }
  .gallery-placeholder {
    height: 300px;
    position: static;
  }
}
</style>
