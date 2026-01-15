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
              <div class="phone-input-group">
                 <div class="fixed-prefix">J-</div>
                 <input
                  type="text"
                  id="rif"
                  v-model="nuevoProveedor.rif"
                  required
                  placeholder="12345678-9"
                  maxlength="10"
                  class="phone-body-input"
                  @input="formatearRifInput($event, 'crear')"
                />
              </div>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="phone">Teléfono *</label>
              <div class="phone-input-group">
                 <div class="fixed-prefix">🇻🇪 +58</div>
                 <input
                  type="tel"
                  id="phone"
                  v-model="nuevoProveedor.telefono"
                  required
                  placeholder="4121234567"
                  maxlength="10"
                  class="phone-body-input"
                />
              </div>
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
            <label>Facturas / Órdenes (Prefijo + 4 dígitos)</label>

            <div class="invoice-input-group">
              <select v-model="tempFactura.tipo" class="prefix-select">
                <option value="F-">F-</option>
                <option value="ORD-">ORD-</option>
              </select>

              <input
                type="text"
                v-model="tempFactura.numero"
                placeholder="0001"
                maxlength="4"
                class="invoice-body-input"
                @keypress="soloNumeros"
              />

              <button type="button" class="btn-add-invoice" @click="agregarFactura('crear')">
                Agregar
              </button>
            </div>

            <div class="tags-container" v-if="nuevoProveedor.facturas.length > 0">
              <div v-for="(factura, index) in nuevoProveedor.facturas" :key="index" class="tag-chip">
                {{ factura }}
                <span class="tag-remove" @click="eliminarFactura(index, 'crear')">×</span>
              </div>
            </div>
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

              <div class="facturas-list">
                <strong>Facturas:</strong>
                <span v-if="proveedor.facturas.length === 0" style="color:#888; font-size:14px;"> N/A</span>
                <div class="mini-tags">
                   <span v-for="fac in proveedor.facturas" :key="fac" class="mini-tag">{{ fac }}</span>
                </div>
              </div>

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
              <div class="phone-input-group">
                 <div class="fixed-prefix">J-</div>
                 <input
                  type="text"
                  id="edit-rif"
                  v-model="datosEditarProveedor.rif"
                  required
                  placeholder="12345678-9"
                  maxlength="10"
                  class="phone-body-input"
                  @input="formatearRifInput($event, 'editar')"
                />
              </div>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="edit-phone">Teléfono *</label>
              <div class="phone-input-group">
                 <div class="fixed-prefix">🇻🇪 +58</div>
                 <input
                  type="tel"
                  id="edit-phone"
                  v-model="datosEditarProveedor.telefono"
                  required
                  maxlength="10"
                  class="phone-body-input"
                />
              </div>
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
            <label>Facturas / Órdenes (Prefijo + 4 dígitos)</label>

            <div class="invoice-input-group">
              <select v-model="tempFactura.tipo" class="prefix-select">
                <option value="F-">F-</option>
                <option value="ORD-">ORD-</option>
              </select>

              <input
                type="text"
                v-model="tempFactura.numero"
                placeholder="0001"
                maxlength="4"
                class="invoice-body-input"
                @keypress="soloNumeros"
              />

              <button type="button" class="btn-add-invoice" @click="agregarFactura('editar')">
                Agregar
              </button>
            </div>

            <div class="tags-container" v-if="datosEditarProveedor.facturas.length > 0">
              <div v-for="(factura, index) in datosEditarProveedor.facturas" :key="index" class="tag-chip">
                {{ factura }}
                <span class="tag-remove" @click="eliminarFactura(index, 'editar')">×</span>
              </div>
            </div>
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

      // Estado temporal para agregar una factura
      tempFactura: {
        tipo: 'F-', // Default
        numero: ''
      },

      nuevoProveedor: {
        rif: '',
        nombre: '',
        telefono: '',
        correo: '',
        direccion: '',
        facturas: [], // Array de strings ["F-001", "ORD-1234"]
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
    // --- NUEVO: AGREGAR FACTURA A LA LISTA ---
    agregarFactura(modo) {
        // Validar que tenga 4 dígitos
        if (!/^\d{4}$/.test(this.tempFactura.numero)) {
             Swal.fire({
                icon: 'info',
                title: 'Formato inválido',
                text: 'Debes ingresar exactamente 4 números (Ej: 0045).',
                confirmButtonColor: '#0071e3',
                timer: 2000
             });
             return;
        }

        const facturaCompleta = `${this.tempFactura.tipo}${this.tempFactura.numero}`;

        if (modo === 'crear') {
            // Evitar duplicados
            if (!this.nuevoProveedor.facturas.includes(facturaCompleta)) {
                this.nuevoProveedor.facturas.push(facturaCompleta);
            }
        } else {
            if (!this.datosEditarProveedor.facturas.includes(facturaCompleta)) {
                this.datosEditarProveedor.facturas.push(facturaCompleta);
            }
        }

        // Limpiar input (mantener el tipo seleccionado para comodidad)
        this.tempFactura.numero = '';
    },

    eliminarFactura(index, modo) {
        if (modo === 'crear') {
            this.nuevoProveedor.facturas.splice(index, 1);
        } else {
            this.datosEditarProveedor.facturas.splice(index, 1);
        }
    },

    soloNumeros(event) {
       // Solo permite escribir números (0-9)
       const charCode = (event.which) ? event.which : event.keyCode;
       if ((charCode > 31 && (charCode < 48 || charCode > 57))) {
          event.preventDefault();
       }
    },
    // -----------------------------------------

    formatearRifInput(event, modo) {
        let valor = event.target.value.replace(/\D/g, '');
        if (valor.length > 8) {
            valor = valor.slice(0, 8) + '-' + valor.slice(8, 9);
        }
        if (modo === 'crear') this.nuevoProveedor.rif = valor;
        else this.datosEditarProveedor.rif = valor;
        event.target.value = valor;
    },

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
            console.error(error);
         }
      } finally {
        this.loading = false;
      }
    },

    async crearProveedor() {
      const errorValidacion = this.validarProveedor(this.nuevoProveedor);
      if (errorValidacion) {
        Swal.fire({ icon: 'warning', title: 'Datos inválidos', text: errorValidacion, confirmButtonColor: '#0071e3' });
        return;
      }

      const result = await Swal.fire({
        title: '¿Crear Proveedor?',
        text: `Se creará el proveedor ${this.nuevoProveedor.nombre}.`,
        icon: 'question',
        showCancelButton: true,
        confirmButtonColor: '#0071e3',
        cancelButtonColor: '#6e6e73',
        confirmButtonText: 'Sí, crear'
      })

      if (!result.isConfirmed) return;

      this.loading = true
      const token = localStorage.getItem('userToken');

      try {
        const rifFinal = 'J-' + this.nuevoProveedor.rif;
        const telefonoFinal = '+58' + this.nuevoProveedor.telefono.trim();

        // Enviamos el array de facturas DIRECTAMENTE (ya está formado)
        const payload = {
          ...this.nuevoProveedor,
          rif: rifFinal,
          telefono: telefonoFinal,
          facturas: this.nuevoProveedor.facturas
        };

        const response = await apiClient.post('/proveedores', payload, {
          headers: { Authorization: `Bearer ${token}` }
        });

        this.proveedores.push(response.data);
        this.cancelarCreacion();

        Swal.fire({ icon: 'success', title: '¡Registrado!', timer: 2000, showConfirmButton: false });

      } catch (error) {
        console.error('Error:', error);
        Swal.fire({ icon: 'error', title: 'Error', text: 'No se pudo registrar.' });
      } finally {
        this.loading = false
      }
    },

    editarProveedor(proveedor) {
      // Clonar objeto (y arrays internos con JSON parse/stringify para romper referencias)
      this.datosEditarProveedor = JSON.parse(JSON.stringify(proveedor));

      // Limpiezas de prefijos
      if (this.datosEditarProveedor.telefono?.startsWith('+58')) {
          this.datosEditarProveedor.telefono = this.datosEditarProveedor.telefono.replace('+58', '');
      }
      if (this.datosEditarProveedor.rif?.startsWith('J-')) {
          this.datosEditarProveedor.rif = this.datosEditarProveedor.rif.replace('J-', '');
      }

      // Reiniciar inputs temporales
      this.tempFactura = { tipo: 'F-', numero: '' };

      this.mostrarFormularioEditar = true
    },

    async actualizarProveedor() {
      const errorValidacion = this.validarProveedor(this.datosEditarProveedor);
      if (errorValidacion) {
        Swal.fire({ icon: 'warning', title: 'Datos inválidos', text: errorValidacion, confirmButtonColor: '#0071e3' });
        return;
      }

      const result = await Swal.fire({
        title: '¿Actualizar?',
        text: `Se actualizará ${this.datosEditarProveedor.nombre}.`,
        icon: 'question',
        showCancelButton: true,
        confirmButtonColor: '#0071e3',
        confirmButtonText: 'Sí, actualizar'
      })

      if (!result.isConfirmed) return;

      this.loading = true
      const token = localStorage.getItem('userToken');

      try {
        const rifFinal = 'J-' + this.datosEditarProveedor.rif;
        const telefonoFinal = '+58' + this.datosEditarProveedor.telefono.trim();

        const payload = {
          ...this.datosEditarProveedor,
          rif: rifFinal,
          telefono: telefonoFinal,
          facturas: this.datosEditarProveedor.facturas
        };

        await apiClient.put(`/proveedores/${this.datosEditarProveedor.id}`, payload, {
          headers: { Authorization: `Bearer ${token}` }
        });

        const index = this.proveedores.findIndex((s) => s.id === this.datosEditarProveedor.id)
        if (index !== -1) {
          this.proveedores[index] = { ...payload };
        }

        this.cancelarEdicion();
        Swal.fire({ icon: 'success', title: '¡Actualizado!', timer: 2000, showConfirmButton: false });

      } catch (error) {
        console.error('Error:', error);
        Swal.fire({ icon: 'error', title: 'Error', text: 'No se pudo actualizar.' });
      } finally {
        this.loading = false
      }
    },

async eliminarProveedor(id) {
      const result = await Swal.fire({
        title: '¿Eliminar proveedor?',
        text: "No podrás revertir esta acción.",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#ff3b30',
        cancelButtonColor: '#6e6e73',
        confirmButtonText: 'Sí, eliminar',
        cancelButtonText: 'Cancelar'
      });

      if (result.isConfirmed) {
        this.loading = true;
        const token = localStorage.getItem('userToken');

        // AQUÍ EMPIEZA EL BLOQUE TRY QUE TE FALTABA
        try {
          await apiClient.delete(`/proveedores/${id}`, {
            headers: { Authorization: `Bearer ${token}` }
          });

          // Actualizar la lista localmente
          this.proveedores = this.proveedores.filter(p => p.id !== id);

          Swal.fire(
            '¡Eliminado!',
            'El proveedor ha sido eliminado correctamente.',
            'success'
          );
        } catch (error) {
          console.error("Error al eliminar proveedor:", error);
          let mensajeError = 'No se pudo eliminar el proveedor. Intente nuevamente.';

          if(error.response && (error.response.status === 409 || error.response.status === 500)) {
              mensajeError = 'No se puede eliminar este proveedor porque tiene productos asociados.';
          }

          Swal.fire({
            icon: 'error',
            title: 'Error',
            text: mensajeError,
            confirmButtonColor: '#0071e3',
          });
        } finally {
          this.loading = false;
        }
      }
    },

    validarProveedor(proveedor) {
      if (!proveedor.nombre?.trim()) return 'Nombre obligatorio.';
      if (!proveedor.rif?.trim()) return 'RIF obligatorio.';
      if (!/^\d{8}-\d{1}$/.test(proveedor.rif)) return 'RIF inválido (Ej: 12345678-9).';
      if (!proveedor.telefono?.trim()) return 'Teléfono obligatorio.';
      if (!/^\d{10}$/.test(proveedor.telefono)) return 'Teléfono debe tener 10 dígitos.';
      if (!proveedor.correo?.trim()) return 'Correo obligatorio.';
      if (!/^\S+@\S+\.\S+$/.test(proveedor.correo)) return 'Correo inválido.';
      if (!proveedor.direccion?.trim()) return 'Dirección obligatoria.';
      return null;
    },

    limpiarFormularioNuevo() {
      this.nuevoProveedor = { rif: '', nombre: '', telefono: '', correo: '', direccion: '', facturas: [] };
      this.tempFactura = { tipo: 'F-', numero: '' };
    },
    cancelarCreacion() {
      this.mostrarFormularioCrear = false
      this.limpiarFormularioNuevo()
    },
    cancelarEdicion() {
      this.mostrarFormularioEditar = false
      this.datosEditarProveedor = { id: null, rif: '', nombre: '', telefono: '', correo: '', direccion: '', facturas: [] }
      this.tempFactura = { tipo: 'F-', numero: '' };
    },
  },
}
</script>

<style scoped>
/* ESTILOS DE INPUT PERSONALIZADOS (Telefono y RIF) */
.phone-input-group {
  display: flex;
  align-items: center;
  gap: 10px;
}
.fixed-prefix {
  background-color: #f5f5f7;
  color: #6e6e73;
  padding: 12px 16px;
  border-radius: 12px;
  border: 1px solid #d2d2d7;
  font-size: 16px;
  font-weight: 600;
  white-space: nowrap;
  user-select: none;
  min-width: 60px;
  text-align: center;
}
.phone-body-input {
  flex: 1;
  padding: 12px 16px;
  border: 2px solid #d2d2d7;
  border-radius: 12px;
  font-size: 16px;
  transition: all 0.3s ease;
  background: white;
  font-family: inherit;
  width: 100%;
}
.phone-body-input:focus {
  outline: none;
  border-color: #0071e3;
  box-shadow: 0 0 0 4px rgba(0, 113, 227, 0.1);
}

/* === NUEVOS ESTILOS PARA FACTURAS === */
.invoice-input-group {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}
.prefix-select {
  background-color: #f5f5f7;
  color: #1d1d1f;
  padding: 12px 16px;
  border-radius: 12px;
  border: 1px solid #d2d2d7;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  outline: none;
}
.invoice-body-input {
  flex: 1;
  padding: 12px 16px;
  border: 2px solid #d2d2d7;
  border-radius: 12px;
  font-size: 16px;
  background: white;
  width: 100%;
}
.invoice-body-input:focus {
    outline: none;
    border-color: #0071e3;
    box-shadow: 0 0 0 4px rgba(0, 113, 227, 0.1);
}

.btn-add-invoice {
    background-color: #0071e3;
    color: white;
    border: none;
    padding: 0 20px;
    border-radius: 12px;
    font-weight: 600;
    cursor: pointer;
    transition: background 0.3s;
}
.btn-add-invoice:hover {
    background-color: #005bb5;
}

/* Estilos de las etiquetas (chips) */
.tags-container {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    margin-top: 5px;
}
.tag-chip {
    background-color: #e5e5e7;
    color: #1d1d1f;
    padding: 6px 12px;
    border-radius: 20px;
    font-size: 14px;
    font-weight: 500;
    display: flex;
    align-items: center;
    gap: 8px;
}
.tag-remove {
    cursor: pointer;
    font-weight: bold;
    color: #86868b;
    font-size: 16px;
    line-height: 1;
}
.tag-remove:hover {
    color: #ff3b30;
}

/* Chips pequeños para la lista */
.mini-tags { display: flex; flex-wrap: wrap; gap: 5px; margin-top: 5px; }
.mini-tag {
    background: #f0f0f0;
    padding: 3px 8px;
    border-radius: 6px;
    font-size: 12px;
    color: #555;
    border: 1px solid #ddd;
}
/* ==================================== */

/* RESTO DE ESTILOS (IGUAL QUE ANTES) */
.suppliers { padding: 40px 20px; max-width: 1200px; margin: 0 auto; }
.suppliers-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 40px; flex-wrap: wrap; gap: 20px; }
.suppliers-header h1 { font-size: 32px; font-weight: 700; color: #1d1d1f; }
.btn-create-supplier { background: linear-gradient(135deg, #0071e3, #0077ed); color: white; border: none; border-radius: 12px; padding: 12px 24px; font-size: 16px; font-weight: 600; cursor: pointer; }
.supplier-form-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0, 0, 0, 0.5); display: flex; justify-content: center; align-items: center; z-index: 1000; padding: 20px; }
.supplier-form { background: white; border-radius: 20px; padding: 40px; width: 100%; max-width: 600px; max-height: 90vh; overflow-y: auto; box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2); }
.form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
.form-group { margin-bottom: 20px; }
.form-group label { display: block; margin-bottom: 8px; font-weight: 600; color: #1d1d1f; font-size: 14px; }
.form-group input:not(.phone-body-input):not(.invoice-body-input), .form-group textarea { width: 100%; padding: 12px 16px; border: 2px solid #d2d2d7; border-radius: 12px; font-size: 16px; background: white; }
.form-group input:focus, .form-group textarea:focus { outline: none; border-color: #0071e3; box-shadow: 0 0 0 4px rgba(0, 113, 227, 0.1); }
.form-actions { display: flex; gap: 15px; justify-content: flex-end; margin-top: 30px; }
.btn-cancel { background: transparent; color: #6e6e73; border: 2px solid #d2d2d7; padding: 12px 24px; border-radius: 12px; cursor: pointer; font-weight: 600; }
.btn-save { background: linear-gradient(135deg, #34c759, #30d158); color: white; border: none; padding: 12px 24px; border-radius: 12px; cursor: pointer; font-weight: 600; }
.btn-save:disabled { opacity: 0.7; }
.suppliers-list { margin-top: 40px; }
.empty-state { text-align: center; padding: 60px 20px; background: #f9f9f9; border-radius: 20px; border: 2px dashed #d2d2d7; }
.btn-create-first { background: linear-gradient(135deg, #0071e3, #0077ed); color: white; border: none; border-radius: 12px; padding: 12px 24px; font-weight: 600; cursor: pointer; }
.suppliers-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(400px, 1fr)); gap: 20px; }
.supplier-card { background: white; border-radius: 15px; padding: 25px; box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1); border: 1px solid #e5e5e7; display: flex; flex-direction: column; justify-content: space-between; }
.supplier-info h3 { font-size: 20px; font-weight: 700; margin-bottom: 15px; }
.supplier-details p { margin-bottom: 8px; color: #6e6e73; font-size: 14px; }
.supplier-actions { display: flex; gap: 10px; margin-top: 20px; padding-top: 20px; border-top: 1px solid #e5e5e7; }
.btn-edit, .btn-delete { padding: 8px 16px; border: none; border-radius: 8px; font-weight: 600; cursor: pointer; flex: 1; }
.btn-edit { background: #ff9500; color: white; }
.btn-delete { background: #ff3b30; color: white; }
@media (max-width: 768px) { .suppliers-header { flex-direction: column; text-align: center; } .form-row { grid-template-columns: 1fr; } .suppliers-grid { grid-template-columns: 1fr; } }
</style>
