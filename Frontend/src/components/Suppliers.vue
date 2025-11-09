<template>
  <div class="suppliers">
    <div class="suppliers-header">
      <h1>Gestión de Proveedores</h1>
      <button class="btn-create-supplier" @click="showCreateForm = true">➕ Nuevo Proveedor</button>
    </div>

    <!-- Formulario de crear proveedor -->
    <div v-if="showCreateForm" class="supplier-form-overlay">
      <div class="supplier-form">
        <h3>Registro de Nuevo Proveedor</h3>

        <form @submit.prevent="createSupplier">
          <div class="form-row">
            <div class="form-group">
              <label for="name">Nombre del Proveedor *</label>
              <input
                type="text"
                id="name"
                v-model="newSupplier.name"
                required
                placeholder="Ej: Apple Inc."
              />
            </div>

            <div class="form-group">
              <label for="rif">RIF *</label>
              <input
                type="text"
                id="rif"
                v-model="newSupplier.rif"
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
                v-model="newSupplier.phone"
                required
                placeholder="Ej: +58 412-1234567"
              />
            </div>

            <div class="form-group">
              <label for="email">Correo Electrónico *</label>
              <input
                type="email"
                id="email"
                v-model="newSupplier.email"
                required
                placeholder="Ej: contacto@proveedor.com"
              />
            </div>
          </div>

          <div class="form-group">
            <label for="address">Dirección *</label>
            <textarea
              id="address"
              v-model="newSupplier.address"
              required
              placeholder="Dirección completa del proveedor"
              rows="3"
            ></textarea>
          </div>

          <div class="form-group">
            <label for="products">Productos que Suministra *</label>
            <input
              type="text"
              id="products"
              v-model="newSupplier.products"
              required
              placeholder="Ej: iPhone, MacBook, iPad"
            />
          </div>

          <div class="form-actions">
            <button type="button" class="btn-cancel" @click="cancelCreate">Cancelar</button>
            <button type="submit" class="btn-save" :disabled="loading">
              {{ loading ? 'Registrando...' : 'Registrar Proveedor' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Lista de proveedores -->
    <div class="suppliers-list">
      <div v-if="suppliers.length === 0" class="empty-state">
        <p>No hay proveedores registrados</p>
        <button class="btn-create-first" @click="showCreateForm = true">
          Registrar Primer Proveedor
        </button>
      </div>

      <div v-else class="suppliers-grid">
        <div v-for="supplier in suppliers" :key="supplier.id" class="supplier-card">
          <div class="supplier-info">
            <h3>{{ supplier.name }}</h3>
            <div class="supplier-details">
              <p><strong>RIF:</strong> {{ supplier.rif }}</p>
              <p><strong>Teléfono:</strong> {{ supplier.phone }}</p>
              <p><strong>Email:</strong> {{ supplier.email }}</p>
              <p><strong>Dirección:</strong> {{ supplier.address }}</p>
              <p><strong>Productos:</strong> {{ supplier.products }}</p>
            </div>
          </div>

          <div class="supplier-actions">
            <button class="btn-edit" @click="editSupplier(supplier)">✏️ Editar</button>
            <button class="btn-delete" @click="deleteSupplier(supplier.id)">🗑️ Eliminar</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal de edición -->
    <div v-if="showEditForm" class="supplier-form-overlay">
      <div class="supplier-form">
        <h3>Editar Proveedor</h3>

        <form @submit.prevent="updateSupplier">
          <div class="form-row">
            <div class="form-group">
              <label for="edit-name">Nombre del Proveedor *</label>
              <input type="text" id="edit-name" v-model="editSupplierData.name" required />
            </div>

            <div class="form-group">
              <label for="edit-rif">RIF *</label>
              <input type="text" id="edit-rif" v-model="editSupplierData.rif" required />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="edit-phone">Teléfono *</label>
              <input type="tel" id="edit-phone" v-model="editSupplierData.phone" required />
            </div>

            <div class="form-group">
              <label for="edit-email">Correo Electrónico *</label>
              <input type="email" id="edit-email" v-model="editSupplierData.email" required />
            </div>
          </div>

          <div class="form-group">
            <label for="edit-address">Dirección *</label>
            <textarea
              id="edit-address"
              v-model="editSupplierData.address"
              required
              rows="3"
            ></textarea>
          </div>

          <div class="form-group">
            <label for="edit-products">Productos que Suministra *</label>
            <input type="text" id="edit-products" v-model="editSupplierData.products" required />
          </div>

          <div class="form-actions">
            <button type="button" class="btn-cancel" @click="cancelEdit">Cancelar</button>
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
export default {
  name: 'SuppliersPage',
  data() {
    return {
      suppliers: [],
      showCreateForm: false,
      showEditForm: false,
      loading: false,
      newSupplier: {
        name: '',
        rif: '',
        phone: '',
        email: '',
        address: '',
        products: '',
      },
      editSupplierData: {
        id: null,
        name: '',
        rif: '',
        phone: '',
        email: '',
        address: '',
        products: '',
      },
    }
  },
  mounted() {
    this.loadSuppliers()
  },
  methods: {
    loadSuppliers() {
      const savedSuppliers = localStorage.getItem('suppliers')
      if (savedSuppliers) {
        this.suppliers = JSON.parse(savedSuppliers)
      }
    },

    saveSuppliers() {
      localStorage.setItem('suppliers', JSON.stringify(this.suppliers))
    },

    async createSupplier() {
      // Validación básica
      if (!this.validateSupplier(this.newSupplier)) {
        alert('Por favor, complete todos los campos correctamente.')
        return
      }

      this.loading = true

      try {
        // Simular proceso de registro
        await new Promise((resolve) => setTimeout(resolve, 1000))

        // Verificar si ya existe el proveedor
        const exists = this.suppliers.some(
          (s) => s.rif === this.newSupplier.rif || s.email === this.newSupplier.email,
        )

        if (exists) {
          alert('Este proveedor ya existe en el sistema.')
          return
        }

        // Crear nuevo proveedor
        const newSupplier = {
          id: Date.now(),
          ...this.newSupplier,
          createdAt: new Date().toISOString(),
        }

        this.suppliers.push(newSupplier)
        this.saveSuppliers()

        // Limpiar formulario
        this.resetNewSupplier()
        this.showCreateForm = false

        alert('Proveedor registrado exitosamente!')
      } catch (error) {
        console.error('Error registrando proveedor:', error)
        alert('Error al registrar el proveedor.')
      } finally {
        this.loading = false
      }
    },

    editSupplier(supplier) {
      this.editSupplierData = { ...supplier }
      this.showEditForm = true
    },

    async updateSupplier() {
      if (!this.validateSupplier(this.editSupplierData)) {
        alert('Por favor, complete todos los campos correctamente.')
        return
      }

      this.loading = true

      try {
        await new Promise((resolve) => setTimeout(resolve, 1000))

        const index = this.suppliers.findIndex((s) => s.id === this.editSupplierData.id)
        if (index !== -1) {
          this.suppliers[index] = { ...this.editSupplierData }
          this.saveSuppliers()

          this.showEditForm = false
          alert('Proveedor actualizado exitosamente!')
        }
      } catch (error) {
        console.error('Error actualizando proveedor:', error)
        alert('Error al actualizar el proveedor.')
      } finally {
        this.loading = false
      }
    },

    deleteSupplier(supplierId) {
      if (confirm('¿Está seguro de que desea eliminar este proveedor?')) {
        this.suppliers = this.suppliers.filter((s) => s.id !== supplierId)
        this.saveSuppliers()
        alert('Proveedor eliminado exitosamente!')
      }
    },

    validateSupplier(supplier) {
      return (
        supplier.name &&
        supplier.rif &&
        supplier.phone &&
        supplier.email &&
        supplier.address &&
        supplier.products
      )
    },

    resetNewSupplier() {
      this.newSupplier = {
        name: '',
        rif: '',
        phone: '',
        email: '',
        address: '',
        products: '',
      }
    },

    cancelCreate() {
      this.showCreateForm = false
      this.resetNewSupplier()
    },

    cancelEdit() {
      this.showEditForm = false
      this.editSupplierData = {
        id: null,
        name: '',
        rif: '',
        phone: '',
        email: '',
        address: '',
        products: '',
      }
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
