<template>
  <footer class="footer">
    <div class="footer-content">
      Universidad Católica Andrés Bello<br />
      Escuela de Ingeniería Informática<br />
      Ingeniería de Software Sección 19833<br />
      Grupo 33.5<br />
      © 2025 Level Up. Todos los derechos reservados.<br />
      Germán Moreno, Andres Camacho, Victor Ramos, Samuel Sangroni y José Perdomo
    </div>
  </footer>
</template>

<script>
export default {
  name: 'MyFooter',
}
</script>

<style scoped>
.footer {
  background-color: #f5f5f7;
  color: #6e6e73;
  padding: 20px 0;
  border-top: 1px solid #d2d2d7;
  text-align: center;
  font-size: 12px;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}
</style>
