<template>
  <div class="suppliers">
    <div class="suppliers-header">
      <h1>Gestión de Proveedores</h1>
      <button class="btn-create-supplier" @click="mostrarFormularioCrear = true">➕ Nuevo Proveedor</button>
    </div>

    <div v-if="mostrarFormularioCrear" class="supplier-form-overlay">
      <div class="supplier-form">
        <h3>Registro de Nuevo Proveedor</h3>

        <form @submit.prevent="crearProveedor">
          <div class="form-row">
            <div class="form-group">
              <label for="name">Nombre del Proveedor *</label>
              <input
                type="text"
                id="name"
                v-model="nuevoProveedor.nombre"
                required
                placeholder="Ej: Apple Inc."
              />
            </div>

            <div class="form-group">
              <label for="rif">RIF *</label>
              <input
                type="text"
                id="rif"
                v-model="nuevoProveedor.rif"
                required
                placeholder="Ej: J-123456789"
              />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="phone">Teléfono *</label>
              <input
                type="tel"
                id="phone"
                v-model="nuevoProveedor.telefono"
                required
                placeholder="Ej: +58 412-1234567"
              />
            </div>

            <div class="form-group">
              <label for="email">Correo Electrónico *</label>
              <input
                type="email"
                id="email"
                v-model="nuevoProveedor.correo"
                required
                placeholder="Ej: contacto@proveedor.com"
              />
            </div>
          </div>

          <div class="form-group">
            <label for="address">Dirección *</label>
            <textarea
              id="address"
              v-model="nuevoProveedor.direccion"
              required
              placeholder="Dirección completa del proveedor"
              rows="3"
            ></textarea>
          </div>

          <div class="form-group">
            <label for="facturas">Facturas / Órdenes (separadas por coma)</label>
            <input
              type="text"
              id="facturas"
              v-model="facturasInput"
              placeholder="Ej: F-001, F-002, ORD-003"
            />
          </div>

          <div class="form-actions">
            <button type="button" class="btn-cancel" @click="cancelarCreacion">Cancelar</button>
            <button type="submit" class="btn-save" :disabled="loading">
              {{ loading ? 'Registrando...' : 'Registrar Proveedor' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <div class="suppliers-list">
      <div v-if="loading && proveedores.length === 0" class="empty-state">
        <p>Cargando proveedores...</p>
      </div>

      <div v-else-if="proveedores.length === 0" class="empty-state">
        <p>No hay proveedores registrados</p>
        <button class="btn-create-first" @click="mostrarFormularioCrear = true">
          Registrar Primer Proveedor
        </button>
      </div>

      <div v-else class="suppliers-grid">
        <div v-for="proveedor in proveedores" :key="proveedor.id" class="supplier-card">
          <div class="supplier-info">
            <h3>{{ proveedor.nombre }}</h3>
            <div class="supplier-details">
              <p><strong>RIF:</strong> {{ proveedor.rif }}</p>
              <p><strong>Teléfono:</strong> {{ proveedor.telefono }}</p>
              <p><strong>Email:</strong> {{ proveedor.correo }}</p>
              <p><strong>Dirección:</strong> {{ proveedor.direccion }}</p>
              <p><strong>Facturas:</strong> {{ proveedor.facturas.join(', ') || 'N/A' }}</p>
            </div>
          </div>

          <div class="supplier-actions">
            <button class="btn-edit" @click="editarProveedor(proveedor)">✏️ Editar</button>
            <button class="btn-delete" @click="eliminarProveedor(proveedor.id)">🗑️ Eliminar</button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="mostrarFormularioEditar" class="supplier-form-overlay">
      <div class="supplier-form">
        <h3>Editar Proveedor</h3>

        <form @submit.prevent="actualizarProveedor">
          <div class="form-row">
            <div class="form-group">
              <label for="edit-name">Nombre del Proveedor *</label>
              <input type="text" id="edit-name" v-model="datosEditarProveedor.nombre" required />
            </div>

            <div class="form-group">
              <label for="edit-rif">RIF *</label>
              <input type="text" id="edit-rif" v-model="datosEditarProveedor.rif" required />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="edit-phone">Teléfono *</label>
              <input type="tel" id="edit-phone" v-model="datosEditarProveedor.telefono" required />
            </div>

            <div class="form-group">
              <label for="edit-email">Correo Electrónico *</label>
              <input type="email" id="edit-email" v-model="datosEditarProveedor.correo" required />
            </div>
          </div>

          <div class="form-group">
            <label for="edit-address">Dirección *</label>
            <textarea
              id="edit-address"
              v-model="datosEditarProveedor.direccion"
              required
              rows="3"
            ></textarea>
          </div>

          <div class="form-group">
            <label for="edit-facturas">Facturas / Órdenes (separadas por coma)</label>
            <input type="text" id="edit-facturas" v-model="facturasInput" />
          </div>

          <div class="form-actions">
            <button type="button" class="btn-cancel" @click="cancelarEdicion">Cancelar</button>
            <button type="submit" class="btn-save" :disabled="loading">
              {{ loading ? 'Actualizando...' : 'Actualizar Proveedor' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import apiClient from '@/api/axiosConfig.js'
import Swal from 'sweetalert2'

export default {
  name: 'SuppliersPage',
  data() {
    return {
      proveedores: [],
      mostrarFormularioCrear: false,
      mostrarFormularioEditar: false,
      loading: false,
      facturasInput: '',
      nuevoProveedor: {
        rif: '',
        nombre: '',
        telefono: '',
        correo: '',
        direccion: '',
        facturas: [],
      },
      datosEditarProveedor: {
        id: null,
        rif: '',
        nombre: '',
        telefono: '',
        correo: '',
        direccion: '',
        facturas: [],
      },
    }
  },
  mounted() {
    this.cargarProveedores()
  },
  methods: {
    async cargarProveedores() {
      this.loading = true;
      const token = localStorage.getItem('userToken');
      if (!token) return;

      try {
        const response = await apiClient.get('/proveedores', {
          headers: { Authorization: `Bearer ${token}` }
        });
        this.proveedores = response.data;
      } catch (error) {
        if (error.response?.status !== 401 && error.response?.status !== 403) {
          Swal.fire({
            icon: 'error',
            title: 'Error al cargar proveedores',
            text: error.message,
            confirmButtonColor: '#0071e3',
          });
        }
      } finally {
        this.loading = false;
      }
    },

    async crearProveedor() {
      if (!this.validarProveedor(this.nuevoProveedor)) {
        Swal.fire({
          icon: 'warning',
          title: 'Campos incompletos',
          text: 'Por favor, complete todos los campos requeridos (*).',
          confirmButtonColor: '#0071e3'
        });
        return
      }

      this.loading = true
      const token = localStorage.getItem('userToken');

      try {
        const payload = {
          ...this.nuevoProveedor,
          facturas: this.facturasInput.split(',').map(s => s.trim()).filter(Boolean)
        };

        const response = await apiClient.post('/proveedores', payload, {
          headers: { Authorization: `Bearer ${token}` }
        });

        this.proveedores.push(response.data);
        this.cancelarCreacion();

        Swal.fire({
          icon: 'success',
          title: '¡Registrado!',
          text: 'Proveedor registrado exitosamente.',
          timer: 2000,
          showConfirmButton: false,
        });

      } catch (error) {
        console.error('Error registrando proveedor:', error)
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'No se pudo registrar el proveedor.',
          confirmButtonColor: '#0071e3',
        });
      } finally {
        this.loading = false
      }
    },

    editarProveedor(proveedor) {
      this.datosEditarProveedor = { ...proveedor };
      this.facturasInput = proveedor.facturas.join(', ');
      this.mostrarFormularioEditar = true
    },

    async actualizarProveedor() {
      if (!this.validarProveedor(this.datosEditarProveedor)) {
        Swal.fire({
          icon: 'warning',
          title: 'Campos incompletos',
          text: 'Por favor, complete todos los campos requeridos (*).',
          confirmButtonColor: '#0071e3'
        });
        return
      }

      const result = await Swal.fire({
        title: '¿Actualizar proveedor?',
        text: `Se guardarán los cambios para ${this.datosEditarProveedor.nombre}.`,
        icon: 'question',
        showCancelButton: true,
        confirmButtonColor: '#0071e3',
        cancelButtonColor: '#6e6e73',
        confirmButtonText: 'Sí, actualizar',
        cancelButtonText: 'Cancelar'
      })

      if (!result.isConfirmed) return;

      this.loading = true
      const token = localStorage.getItem('userToken');

      try {
        const payload = {
          ...this.datosEditarProveedor,
          facturas: this.facturasInput.split(',').map(s => s.trim()).filter(Boolean)
        };

        await apiClient.put(`/proveedores/${this.datosEditarProveedor.id}`, payload, {
          headers: { Authorization: `Bearer ${token}` }
        });

        const index = this.proveedores.findIndex((s) => s.id === this.datosEditarProveedor.id)
        if (index !== -1) {
          this.proveedores[index] = { ...payload };
        }

        this.cancelarEdicion();
        Swal.fire({
          icon: 'success',
          title: '¡Actualizado!',
          text: 'Proveedor actualizado exitosamente.',
          timer: 2000,
          showConfirmButton: false,
        });

      } catch (error) {
        console.error('Error actualizando proveedor:', error)
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'No se pudo actualizar el proveedor.',
          confirmButtonColor: '#0071e3',
        });
      } finally {
        this.loading = false
      }
    },

    async eliminarProveedor(proveedorId) {
      const result = await Swal.fire({
        title: '¿Estás seguro?',
        text: "¡No podrás revertir esta acción!",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#d33',
        cancelButtonColor: '#6e6e73',
        confirmButtonText: 'Sí, eliminarlo',
        cancelButtonText: 'Cancelar'
      })

      if (result.isConfirmed) {
        this.loading = true;
        const token = localStorage.getItem('userToken');

        try {
          await apiClient.delete(`/proveedores/${proveedorId}`, {
            headers: { Authorization: `Bearer ${token}` }
          });

          this.proveedores = this.proveedores.filter((s) => s.id !== proveedorId)
          Swal.fire({
            icon: 'success',
            title: '¡Eliminado!',
            text: 'El proveedor ha sido eliminado.',
            timer: 2000,
            showConfirmButton: false,
          });

        } catch (error) {
          console.error('Error eliminando proveedor:', error);
          Swal.fire({
            icon: 'error',
            title: 'Error',
            text: 'No se pudo eliminar el proveedor.',
            confirmButtonColor: '#0071e3',
          });
        } finally {
          this.loading = false;
        }
      }
    },

    validarProveedor(proveedor) {
      return (
        proveedor.nombre &&
        proveedor.rif &&
        proveedor.telefono &&
        proveedor.correo &&
        proveedor.direccion
      )
    },

    limpiarFormularioNuevo() {
      this.nuevoProveedor = {
        rif: '',
        nombre: '',
        telefono: '',
        correo: '',
        direccion: '',
        facturas: [],
      }
      this.facturasInput = '';
    },

    cancelarCreacion() {
      this.mostrarFormularioCrear = false
      this.limpiarFormularioNuevo()
    },

    cancelarEdicion() {
      this.mostrarFormularioEditar = false
      this.datosEditarProveedor = {
        id: null,
        rif: '',
        nombre: '',
        telefono: '',
        correo: '',
        direccion: '',
        facturas: [],
      }
      this.facturasInput = '';
    },
  },
}
</script>

<style scoped>
.suppliers {
  padding: 40px 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.suppliers-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
  flex-wrap: wrap;
  gap: 20px;
}

.suppliers-header h1 {
  font-size: 32px;
  font-weight: 700;
  color: #1d1d1f;
}

.btn-create-supplier {
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

.btn-create-supplier:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(0, 113, 227, 0.3);
}

.supplier-form-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  padding: 20px;
}

.supplier-form {
  background: white;
  border-radius: 20px;
  padding: 40px;
  width: 100%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
}

.supplier-form h3 {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 30px;
  text-align: center;
  color: #1d1d1f;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
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

.form-actions {
  display: flex;
  gap: 15px;
  justify-content: flex-end;
  margin-top: 30px;
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

.btn-save {
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

.btn-save:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(52, 199, 89, 0.3);
}

.btn-save:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.suppliers-list {
  margin-top: 40px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: #f9f9f9;
  border-radius: 20px;
  border: 2px dashed #d2d2d7;
}

.empty-state p {
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

.suppliers-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
}

.supplier-card {
  background: white;
  border-radius: 15px;
  padding: 25px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  border: 1px solid #e5e5e7;
  transition: all 0.3s ease;
  display: flex; /* Añadido */
  flex-direction: column; /* Añadido */
  justify-content: space-between; /* Añadido */
}

.supplier-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.supplier-info h3 {
  font-size: 20px;
  font-weight: 700;
  color: #1d1d1f;
  margin-bottom: 15px;
}

.supplier-details p {
  margin-bottom: 8px;
  color: #6e6e73;
  font-size: 14px;
  line-height: 1.4;
}

.supplier-details strong {
  color: #1d1d1f;
}

.supplier-actions {
  display: flex;
  gap: 10px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e5e5e7;
}

.btn-edit,
.btn-delete {
  padding: 8px 16px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  flex: 1;
}

.btn-edit {
  background: #ff9500;
  color: white;
}

.btn-edit:hover {
  background: #e08500;
  transform: translateY(-1px);
}

.btn-delete {
  background: #ff3b30;
  color: white;
}

.btn-delete:hover {
  background: #e0352b;
  transform: translateY(-1px);
}

@media (max-width: 768px) {
  .suppliers-header {
    flex-direction: column;
    text-align: center;
  }

  .form-row {
    grid-template-columns: 1fr;
  }

  .suppliers-grid {
    grid-template-columns: 1fr;
  }

  .supplier-form {
    padding: 30px 20px;
  }

  .form-actions {
    flex-direction: column;
  }
}
</style>
