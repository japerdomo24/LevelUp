package LevelUp.Controlador.Venta;

// Modelos
import LevelUp.Modelo.Venta.Venta;
import LevelUp.Modelo.Venta.ItemVenta;

// Spring
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ventas")
public class VentaController {

    private ManejadorVentaJSON manejadorVenta;

    // DTO para el reporte (igual que antes)
    record ReporteVentasDTO(
            int totalVentas,
            double ingresosTotales,
            Map<String, Integer> ventasPorProducto
    ) {}

    // ¡NUEVO DTO para que el admin cree una venta!
    record VentaCreateDTO(
            String correoUsuario,
            List<ItemVenta> items, // El admin debe proveer los items
            double totalVenta
    ) {}

    public VentaController() {
        this.manejadorVenta = new ManejadorVentaJSON("src/main/java/LevelUp/Data/ventas.json");
        // Ya no necesita manejador de carrito ni producto
    }

    // --- ¡MÉTODO ELIMINADO! ---
    // El método [POST /ventas/checkout] se eliminó de aquí y se movió a CarritoController.


    // --- ¡NUEVO MÉTODO PARA ADMIN! ---
    /**
     * [POST /ventas] - (Admin) Crea un registro de Venta manualmente.
     * NO afecta stock, es solo para registro.
     */
    @PostMapping
    public ResponseEntity<Venta> crearVentaManual(@RequestBody VentaCreateDTO dto) {
        List<Venta> ventas = manejadorVenta.leerVentas();

        // Usamos el constructor de Venta, que le asignará un ID y fecha
        Venta nuevaVenta = new Venta(
                dto.correoUsuario(),
                dto.items(),
                dto.totalVenta()
        );

        ventas.add(nuevaVenta);
        manejadorVenta.guardarVentas(ventas);

        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaVenta);
    }


    // --- ENDPOINTS DE ADMIN (igual que antes) ---

    /**
     * [GET /ventas] - (Admin) Consultar todas las ventas.
     */
    @GetMapping
    public List<Venta> getVentas() {
        return manejadorVenta.leerVentas();
    }

    /**
     * [GET /ventas/reporte] - (Admin) Generar un reporte de ventas.
     */
    @GetMapping("/reporte")
    public ReporteVentasDTO getReporteVentas() {
        List<Venta> ventas = manejadorVenta.leerVentas();

        double ingresosTotales = ventas.stream()
                .mapToDouble(Venta::getTotalVenta)
                .sum();

        Map<String, Integer> ventasPorProducto = ventas.stream()
                .flatMap(venta -> venta.getItems().stream())
                .collect(Collectors.groupingBy(
                        ItemVenta::getProductoId,
                        Collectors.summingInt(ItemVenta::getCantidad)
                ));

        return new ReporteVentasDTO(
                ventas.size(),
                ingresosTotales,
                ventasPorProducto
        );
    }

    /**
     * [DELETE /ventas/{id}] - (Admin) Eliminar una venta.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarVenta(@PathVariable String id) {
        List<Venta> ventas = manejadorVenta.leerVentas();

        boolean eliminado = ventas.removeIf(v -> v.getId().equals(id));

        if (!eliminado) {
            return ResponseEntity.notFound().build(); 
        }

        manejadorVenta.guardarVentas(ventas);
        return ResponseEntity.noContent().build();
    }
}