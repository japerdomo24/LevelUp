<template>
  <div class="sales">
    <div class="sales-header">
      <h1>Gestión de Ventas</h1>
      <p class="sales-subtitle">Consulta el historial y los reportes de ventas.</p>
    </div>

    <div v-if="reporte && !loading" class="sales-report">
      <h3>Reporte General</h3>
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-number">{{ reporte.totalVentas }}</div>
          <div class="stat-label">Ventas Totales</div>
        </div>
        <div class="stat-card">
          <div class="stat-number">${{ formatearPrecio(reporte.ingresosTotales) }}</div>
          <div class="stat-label">Ingresos Totales</div>
        </div>
        <div class="stat-card">
          <div class="stat-number">{{ Object.keys(reporte.ventasPorProducto).length }}</div>
          <div class="stat-label">Productos Únicos Vendidos</div>
        </div>
      </div>
    </div>

    <div class="sales-list">
      <h2>Historial de Ventas</h2>

      <div v-if="loading" class="empty-state">
        <p>Cargando ventas...</p>
      </div>

      <div v-else-if="ventas.length === 0" class="empty-state">
        <p>No hay ventas registradas todavía.</p>
      </div>

      <div v-else class="sales-grid">
        <div v-for="venta in ventas" :key="venta.id" class="sale-card">
          <div class="sale-card-header">
            <span>Factura ID: <strong>{{ venta.id.substring(0, 8) }}...</strong></span>
            <span>{{ formatearFecha(venta.fecha) }}</span>
          </div>
          <div class="sale-card-body">
            <p><strong>Cliente:</strong> {{ venta.correoUsuario }}</p>

            <p class="items-title"><strong>Items Vendidos:</strong></p>
            <ul class="items-list">
              <li v-for="item in venta.items" :key="item.productoId">
                {{ item.cantidad }}x {{ item.serie }}
                <em>(${{ formatearPrecio(item.precioUnitario) }} c/u)</em>
              </li>
            </ul>
          </div>
          <div class="sale-card-footer">
            <span class="sale-total">Total: ${{ formatearPrecio(venta.totalVenta) }}</span>
            <button class="btn-delete" @click="manejarEliminacion(venta.id)">🗑️ Eliminar</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import apiClient from '@/api/axiosConfig.js'
import Swal from 'sweetalert2'

export default {
  name: 'SalesPage',
  data() {
    return {
      ventas: [],
      reporte: null, // <--- Traducido
      loading: true,
    }
  },
  mounted() {
    this.cargarDatosVentas(); // <--- Traducido
  },
  methods: {
    /**
     * Carga tanto las ventas como el reporte desde la API.
     */
    async cargarDatosVentas() { // <--- Traducido
      this.loading = true;
      const token = localStorage.getItem('userToken');
      if (!token) return;

      try {
        const [salesRes, reportRes] = await Promise.all([
          apiClient.get('/ventas', { headers: { Authorization: `Bearer ${token}` } }),
          apiClient.get('/ventas/reporte', { headers: { Authorization: `Bearer ${token}` } })
        ]);

        this.ventas = salesRes.data;
        this.reporte = reportRes.data; // <--- Traducido

      } catch (error) {
        if (error.response?.status !== 401 && error.response?.status !== 403) {
          Swal.fire({
            icon: 'error',
            title: 'Error al cargar datos',
            text: 'No se pudieron cargar las ventas y reportes.',
            confirmButtonColor: '#0071e3',
          });
        }
      } finally {
        this.loading = false;
      }
    },

    /**
     * Recarga solo el reporte (útil después de eliminar una venta).
     */
    async cargarReporte() { // <--- Traducido
      const token = localStorage.getItem('userToken');
      if (!token) return;
      try {
        const reportRes = await apiClient.get('/ventas/reporte', { headers: { Authorization: `Bearer ${token}` } });
        this.reporte = reportRes.data; // <--- Traducido
      } catch (e) {
        console.error("Error recargando reporte", e);
      }
    },

    /**
     * Maneja la eliminación de una venta con confirmación.
     */
    async manejarEliminacion(ventaId) { // <--- Traducido
      const token = localStorage.getItem('userToken');

      // 1. Confirmación (Modal Clickable)
      const result = await Swal.fire({
        title: '¿Eliminar Venta?',
        text: `¿Estás seguro de que deseas eliminar la factura ID: ${ventaId}? Esta acción no se puede revertir.`,
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#d33',
        cancelButtonColor: '#6e6e73',
        confirmButtonText: 'Sí, eliminar',
        cancelButtonText: 'Cancelar'
      });

      if (!result.isConfirmed) return;

      // 2. Acción
      try {
        await apiClient.delete(`/ventas/${ventaId}`, {
          headers: { Authorization: `Bearer ${token}` }
        });

        // 3. Éxito
        this.ventas = this.ventas.filter(v => v.id !== ventaId); // <--- Traducido
        this.cargarReporte(); // Actualiza el reporte

        Swal.fire({
          icon: 'success',
          title: '¡Eliminada!',
          text: 'La venta ha sido eliminada.',
          timer: 2000,
          showConfirmButton: false,
        });

      } catch (error) {
        // 4. Error
        console.error('Error eliminando venta:', error);
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'No se pudo eliminar la venta.',
          confirmButtonColor: '#0071e3',
        });
      }
    },

    /**
     * Helpers de formato
     */
    formatearPrecio(value) { // <--- Traducido
      if (!value) return '0.00';
      return Number(value).toFixed(2);
    },
    formatearFecha(isoString) { // <--- Traducido
      if (!isoString) return 'Fecha inválida';
      const date = new Date(isoString);
      return date.toLocaleDateString('es-VE'); // Formato DD/MM/YYYY
    }
  }
}
</script>

<style scoped>
.sales {
  padding: 40px 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.sales-header {
  text-align: center;
  margin-bottom: 40px;
}

.sales-header h1 {
  font-size: 32px;
  font-weight: 700;
  color: #1d1d1f;
  margin-bottom: 10px;
}

.sales-subtitle {
  font-size: 18px;
  color: #6e6e73;
}

/* --- Sección de Reporte --- */
.sales-report {
  background: white;
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.07);
  margin-bottom: 40px;
}
.sales-report h3 {
  font-size: 20px;
  font-weight: 600;
  color: #1d1d1f;
  margin-bottom: 20px;
  text-align: center;
}
.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}
.stat-card {
  background: linear-gradient(135deg, #f5f5f7, #ffffff);
  border: 1px solid #e5e5e7;
  border-radius: 12px;
  padding: 20px;
  text-align: center;
}
.stat-number {
  font-size: 24px;
  font-weight: 700;
  color: #0071e3;
  margin-bottom: 8px;
}
.stat-label {
  font-size: 14px;
  color: #6e6e73;
}

/* --- Sección de Lista de Ventas --- */
.sales-list h2 {
  font-size: 24px;
  font-weight: 600;
  color: #1d1d1f;
  margin-bottom: 20px;
  border-bottom: 1px solid #e5e5e7;
  padding-bottom: 10px;
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

.sales-grid {
  display: grid;
  grid-template-columns: 1fr; /* Una columna para las tarjetas de venta */
  gap: 20px;
}

.sale-card {
  background: white;
  border-radius: 15px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  border: 1px solid #e5e5e7;
  overflow: hidden; /* Para que los bordes redondeados se apliquen bien */
}

.sale-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #f9f9f9;
  padding: 15px 20px;
  border-bottom: 1px solid #e5e5e7;
  font-size: 14px;
  color: #6e6e73;
}

.sale-card-body {
  padding: 20px;
}
.sale-card-body > p {
  font-size: 16px;
  color: #1d1d1f;
  margin-bottom: 15px;
}
.items-title {
  font-size: 14px;
  margin-bottom: 10px !important;
}
.items-list {
  list-style: none;
  padding-left: 15px;
  border-left: 2px solid #0071e3;
}
.items-list li {
  margin-bottom: 5px;
  font-size: 14px;
  color: #1d1d1f;
}
.items-list li em {
  color: #6e6e73;
  font-style: italic;
}

.sale-card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background: #f9f9f9;
  border-top: 1px solid #e5e5e7;
}

.sale-total {
  font-size: 18px;
  font-weight: 700;
  color: #1d1d1f;
}
.sale-total span {
  color: #0071e3;
}

.btn-delete {
  padding: 8px 16px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #ff3b30;
  color: white;
}
.btn-delete:hover {
  background: #c82333;
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>
