<template>
  <div class="sales">
    <div class="sales-header">
      <h1>Gestión de Ventas</h1>
      <p class="sales-subtitle">Consulta el historial y los reportes de ventas.</p>
    </div>

    <!-- SECCIÓN DE REPORTE Y GRÁFICO -->
    <div v-if="reporte && !loading" class="sales-report">
      <h3>Reporte General y Contabilidad</h3>

      <!-- Tarjetas de Estadísticas -->
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-number">{{ reporte.totalVentas }}</div>
          <div class="stat-label">Ventas Totales</div>
        </div>
        <div class="stat-card">
          <div class="stat-number green-text">${{ formatearPrecio(reporte.ingresosTotales) }}</div>
          <div class="stat-label">Ingresos Totales</div>
        </div>
        <div class="stat-card">
          <div class="stat-number">{{ Object.keys(reporte.ventasPorProducto).length }}</div>
          <div class="stat-label">Productos Únicos</div>
        </div>
      </div>

      <!-- NUEVO: Gráfico de Barras CSS (Ingresos por Fecha) -->
      <div class="chart-section" v-if="chartData.length > 0">
        <h4>Tendencia de Ingresos</h4>
        <div class="bar-chart-container">
            <div class="bar-chart">
                <div
                    v-for="(data, index) in chartData"
                    :key="index"
                    class="bar-group"
                >
                    <div class="bar-value" :style="{ bottom: data.height }">${{ formatearCompacto(data.total) }}</div>
                    <div class="bar" :style="{ height: data.height }" :title="'$' + formatearPrecio(data.total)"></div>
                    <div class="bar-label">{{ data.date }}</div>
                </div>
            </div>
        </div>
      </div>
    </div>

    <!-- LISTA DE VENTAS -->
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

            <!-- NUEVO: Botón Eliminar -->
            <button class="btn-delete" @click="manejarEliminacion(venta.id)">
               <i class="fas fa-trash"></i> Eliminar
            </button>
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
      reporte: null,
      loading: true,
    }
  },
  computed: {
    // NUEVO: Procesa los datos de ventas para generar el gráfico
    chartData() {
        if (!this.ventas || this.ventas.length === 0) return [];

        // 1. Agrupar ventas por fecha
        const salesByDate = {};
        this.ventas.forEach(venta => {
            const dateKey = this.formatearFechaCorta(venta.fecha);
            salesByDate[dateKey] = (salesByDate[dateKey] || 0) + venta.totalVenta;
        });

        // 2. Convertir a array y ordenar
        const dataArr = Object.keys(salesByDate).map(date => ({
            date,
            total: salesByDate[date]
        }));

        // 3. Calcular altura relativa (porcentaje) para el gráfico CSS
        const maxVal = Math.max(...dataArr.map(d => d.total));

        return dataArr.map(d => ({
            ...d,
            height: maxVal > 0 ? `${(d.total / maxVal) * 100}%` : '0%'
        }));
    }
  },
  mounted() {
    this.cargarDatosVentas();
  },
  methods: {
    async cargarDatosVentas() {
      this.loading = true;
      const token = localStorage.getItem('userToken');
      if (!token) return;

      try {
        const [salesRes, reportRes] = await Promise.all([
          apiClient.get('/ventas', { headers: { Authorization: `Bearer ${token}` } }),
          apiClient.get('/ventas/reporte', { headers: { Authorization: `Bearer ${token}` } })
        ]);

        this.ventas = salesRes.data;
        this.reporte = reportRes.data;

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

    // NUEVO: Funcionalidad de eliminar venta
    async manejarEliminacion(ventaId) {
        const result = await Swal.fire({
            title: '¿Eliminar registro de venta?',
            text: "Esta acción eliminará la factura del historial contable. No se puede deshacer.",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#ff3b30',
            cancelButtonColor: '#86868b',
            confirmButtonText: 'Sí, eliminar',
            cancelButtonText: 'Cancelar'
        });

        if (result.isConfirmed) {
            try {
                // await apiClient.delete(`/ventas/${ventaId}`); // Descomentar para conectar a API
                // 1. Obtener el token (igual que en cargarDatosVentas)
                const token = localStorage.getItem('userToken');

                // 2. LLAMADA REAL AL BACKEND (Descomentada y configurada)
                await apiClient.delete(`/ventas/${ventaId}`, {
                    headers: { Authorization: `Bearer ${token}` }
                });

                // Actualización Local (Simulada)
                this.ventas = this.ventas.filter(v => v.id !== ventaId);

                // Recalcular reporte localmente para reflejar cambios en gráfico
                if (this.reporte) {
                    this.reporte.totalVentas--;
                    // Recalcular ingresos totales
                    this.reporte.ingresosTotales = this.ventas.reduce((acc, curr) => acc + curr.totalVenta, 0);
                }

                Swal.fire(
                    'Eliminado',
                    'El registro ha sido eliminado correctamente.',
                    'success'
                );
            // eslint-disable-next-line no-unused-vars
            } catch (error) {
                Swal.fire('Error', 'No se pudo eliminar la venta.', 'error');
            }
        }
    },

    /**
     * Helpers de formato
     */
    formatearPrecio(value) {
      if (!value) return '0.00';
      return Number(value).toFixed(2);
    },
    // Formato corto para números grandes en el gráfico (ej: 1.2k)
    formatearCompacto(num) {
        return Intl.NumberFormat('en-US', { notation: "compact", maximumFractionDigits: 1 }).format(num);
    },
    formatearFecha(isoString) {
      if (!isoString) return 'Fecha inválida';
      const date = new Date(isoString);
      return date.toLocaleDateString('es-VE'); // DD/MM/YYYY
    },
    // Solo DD/MM para el eje X del gráfico
    formatearFechaCorta(isoString) {
        if (!isoString) return '';
        const date = new Date(isoString);
        return date.toLocaleDateString('es-VE', { day: '2-digit', month: '2-digit' });
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
  margin-bottom: 40px;
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
.stat-number.green-text { color: #34c759; }
.stat-label {
  font-size: 14px;
  color: #6e6e73;
}

/* --- Gráfico CSS --- */
.chart-section {
    margin-top: 30px;
    padding-top: 20px;
    border-top: 1px solid #f5f5f7;
}
.chart-section h4 {
    text-align: left;
    color: #1d1d1f;
    margin-bottom: 20px;
    font-size: 16px;
}
.bar-chart-container {
    height: 200px;
    width: 100%;
    display: flex;
    align-items: flex-end;
    padding-bottom: 20px;
}
.bar-chart {
    display: flex;
    justify-content: space-around;
    align-items: flex-end;
    width: 100%;
    height: 100%;
    gap: 10px;
}
.bar-group {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: flex-end;
    height: 100%;
    flex: 1;
    position: relative;
    max-width: 60px; /* Ancho máximo de barra */
}
.bar {
    width: 100%;
    background: #0071e3;
    border-radius: 6px 6px 0 0;
    transition: height 0.5s ease-out;
    min-height: 4px; /* Siempre visible un poquito */
}
.bar:hover {
    background: #005bb5;
}
.bar-value {
    font-size: 12px;
    font-weight: 600;
    color: #1d1d1f;
    margin-bottom: 5px;
    position: absolute; /* Flotante sobre la barra */
    transform: translateY(-100%); /* Mover hacia arriba */
}
.bar-label {
    margin-top: 8px;
    font-size: 12px;
    color: #86868b;
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
  grid-template-columns: 1fr;
  gap: 20px;
}

.sale-card {
  background: white;
  border-radius: 15px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  border: 1px solid #e5e5e7;
  overflow: hidden;
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
  justify-content: space-between; /* Separar total y botón */
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

/* Botón Eliminar Estilizado */
.btn-delete {
    background: transparent;
    border: 1px solid #ff3b30;
    color: #ff3b30;
    padding: 8px 16px;
    border-radius: 8px;
    cursor: pointer;
    font-size: 14px;
    font-weight: 500;
    transition: all 0.2s;
}
.btn-delete:hover {
    background: #ff3b30;
    color: white;
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>
