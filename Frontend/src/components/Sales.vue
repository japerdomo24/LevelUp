<template>
  <div class="sales">
    <div class="sales-header">
      <h1>Gestión de Ventas</h1>
      <p class="sales-subtitle">Módulo en desarrollo</p>
    </div>

    <div class="sales-content">
      <div class="coming-soon">
        <div class="coming-soon-icon">📊</div>
        <h2>Próximamente</h2>
        <p>
          Estamos trabajando en el módulo de ventas. Esta funcionalidad estará disponible pronto.
        </p>
        <div class="features-list">
          <div class="feature-item">
            <span class="feature-icon">💰</span>
            <span>Registro de ventas</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon">📈</span>
            <span>Reportes y estadísticas</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon">🧾</span>
            <span>Facturación electrónica</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon">📦</span>
            <span>Gestión de inventario</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SalesPage',
}
</script>

<style scoped>
.sales {
  padding: 40px 20px;
  max-width: 800px;
  margin: 0 auto;
  min-height: 60vh;
}

.sales-header {
  text-align: center;
  margin-bottom: 50px;
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

.sales-content {
  display: flex;
  justify-content: center;
  align-items: center;
}

.coming-soon {
  text-align: center;
  background: white;
  border-radius: 20px;
  padding: 60px 40px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  border: 2px solid #f0f0f0;
  max-width: 500px;
  width: 100%;
}

.coming-soon-icon {
  font-size: 80px;
  margin-bottom: 20px;
}

.coming-soon h2 {
  font-size: 28px;
  font-weight: 700;
  color: #1d1d1f;
  margin-bottom: 15px;
}

.coming-soon p {
  font-size: 16px;
  color: #6e6e73;
  line-height: 1.6;
  margin-bottom: 30px;
}

.features-list {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
  margin-top: 30px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px;
  background: #f9f9f9;
  border-radius: 10px;
  font-size: 14px;
  color: #1d1d1f;
  font-weight: 500;
}

.feature-icon {
  font-size: 20px;
}

@media (max-width: 768px) {
  .features-list {
    grid-template-columns: 1fr;
  }

  .coming-soon {
    padding: 40px 20px;
  }

  .coming-soon-icon {
    font-size: 60px;
  }
}
</style>
