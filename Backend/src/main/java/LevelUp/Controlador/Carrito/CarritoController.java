package LevelUp.Controlador.Carrito;

// Modelos
import LevelUp.Modelo.Carrito.Carrito;
import LevelUp.Modelo.Carrito.ItemCarrito;
import LevelUp.Modelo.Producto.Producto;
import LevelUp.Modelo.Venta.Venta;
import LevelUp.Modelo.Venta.ItemVenta;

// Manejadores
import LevelUp.Controlador.Producto.ManejadorProductoJSON;
import LevelUp.Controlador.Venta.ManejadorVentaJSON;

// Spring
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; 
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/carrito")
public class CarritoController {

    private ManejadorCarritoJSON manejadorCarrito;
    private ManejadorProductoJSON manejadorProducto;
    private ManejadorVentaJSON manejadorVenta; // <-- ¡NUEVA DEPENDENCIA!

    // DTO para agregar un item
    record AgregarItemDTO(String productoId, int cantidad) {}

    public CarritoController() {
        // Asegúrate que las rutas a tus archivos de datos son correctas
        this.manejadorCarrito = new ManejadorCarritoJSON("/Users/jose/Desktop/Uni/LevelUp/Backend/src/main/java/LevelUp/Data/carritos.json");
        this.manejadorProducto = new ManejadorProductoJSON("/Users/jose/Desktop/Uni/LevelUp/Backend/src/main/java/LevelUp/Data/productos.json");
        this.manejadorVenta = new ManejadorVentaJSON("/Users/jose/Desktop/Uni/LevelUp/Backend/src/main/java/LevelUp/Data/ventas.json"); // <-- NUEVO
    }

    /**
     * [GET /carrito] - Obtiene el carrito del usuario autenticado.
     */
    @GetMapping
    public ResponseEntity<Carrito> getMiCarrito(Authentication authentication) {
        String correoUsuario = authentication.getName();
        Carrito miCarrito = obtenerOCrearCarrito(correoUsuario);
        miCarrito.calcularTotal();
        return ResponseEntity.ok(miCarrito);
    }

    /**
     * [POST /carrito] - Agrega un item al carrito.
     */
    @PostMapping
    public ResponseEntity<Carrito> agregarItem(Authentication authentication, 
                                             @RequestBody AgregarItemDTO dto) {
        
        String correoUsuario = authentication.getName();

        // 1. Validar producto y stock
        Optional<Producto> productoOpt = manejadorProducto.leerProductos().stream()
                .filter(p -> p.getId().equals(dto.productoId()))
                .findFirst();
        if (productoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); 
        }
        if (productoOpt.get().getCantidad() < dto.cantidad()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null); 
        }

        // 2. Obtener carrito y agregar item
        List<Carrito> todosLosCarritos = manejadorCarrito.leerCarritos();
        Carrito miCarrito = obtenerOCrearCarrito(todosLosCarritos, correoUsuario);

        Optional<ItemCarrito> itemExistente = miCarrito.getItems().stream()
                .filter(item -> item.getProductoId().equals(dto.productoId()))
                .findFirst();

        if (itemExistente.isPresent()) {
            itemExistente.get().setCantidad(itemExistente.get().getCantidad() + dto.cantidad());
        } else {
            Producto producto = productoOpt.get();
            ItemCarrito nuevoItem = new ItemCarrito(
                    producto.getId(), producto.getSerie(),
                    dto.cantidad(), producto.getPrecio()
            );
            miCarrito.getItems().add(nuevoItem);
        }

        // 3. Recalcular y guardar
        miCarrito.calcularTotal();
        manejadorCarrito.guardarCarritos(todosLosCarritos);

        return ResponseEntity.ok(miCarrito);
    }

    /**
     * [DELETE /carrito/{productoId}] - Elimina un item del carrito.
     */
    @DeleteMapping("/{productoId}")
    public ResponseEntity<Carrito> eliminarItem(Authentication authentication, 
                                              @PathVariable String productoId) {
        
        String correoUsuario = authentication.getName();
        List<Carrito> todosLosCarritos = manejadorCarrito.leerCarritos();
        Carrito miCarrito = obtenerOCrearCarrito(todosLosCarritos, correoUsuario);

        boolean eliminado = miCarrito.getItems().removeIf(
                item -> item.getProductoId().equals(productoId)
        );

        if (!eliminado) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); 
        }

        miCarrito.calcularTotal();
        manejadorCarrito.guardarCarritos(todosLosCarritos);

        return ResponseEntity.ok(miCarrito);
    }

    // --- ¡NUEVO ENDPOINT! ---
    /**
     * [POST /carrito/facturar] - "Facturar el carrito" (Checkout)
     * Convierte el carrito del usuario en una Venta, actualiza stock y vacía el carrito.
     */
    @PostMapping("/facturar")
    public ResponseEntity<Venta> facturarCarrito(Authentication authentication) {
        String correoUsuario = authentication.getName();
        
        // 1. Cargar todos los datos
        List<Carrito> carritos = manejadorCarrito.leerCarritos();
        List<Producto> productos = manejadorProducto.leerProductos();
        List<Venta> ventas = manejadorVenta.leerVentas();

        // 2. Encontrar el carrito del usuario
        Optional<Carrito> carritoOpt = carritos.stream()
                .filter(c -> c.getCorreoUsuario().equals(correoUsuario))
                .findFirst();

        if (carritoOpt.isEmpty() || carritoOpt.get().getItems().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Carrito vacío
        }

        Carrito miCarrito = carritoOpt.get();
        List<ItemVenta> itemsVenta = new ArrayList<>();

        // 3. Validar stock y actualizarlo
        for (ItemCarrito itemCarrito : miCarrito.getItems()) {
            Optional<Producto> productoOpt = productos.stream()
                    .filter(p -> p.getId().equals(itemCarrito.getProductoId()))
                    .findFirst();

            if (productoOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Producto ya no existe
            }

            Producto producto = productoOpt.get();
            if (producto.getCantidad() < itemCarrito.getCantidad()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(null); // Stock insuficiente
            }

            // ¡Actualizar stock!
            producto.setCantidad(producto.getCantidad() - itemCarrito.getCantidad());
            
            // Convertir ItemCarrito a ItemVenta
            itemsVenta.add(new ItemVenta(
                    itemCarrito.getProductoId(),
                    itemCarrito.getSerie(),
                    itemCarrito.getCantidad(),
                    itemCarrito.getPrecioUnitario()
            ));
        }

        // 4. Crear la Venta
        Venta nuevaVenta = new Venta(
                correoUsuario,
                itemsVenta,
                miCarrito.getTotal()
        );
        ventas.add(nuevaVenta);

        // 5. Vaciar el carrito
        miCarrito.getItems().clear();
        miCarrito.calcularTotal(); // Total ahora es 0

        // 6. Guardar TODOS los cambios en los archivos JSON
        manejadorProducto.guardarProductos(productos); // Stock actualizado
        manejadorCarrito.guardarCarritos(carritos); // Carrito vacío
        manejadorVenta.guardarVentas(ventas); // Nueva venta

        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaVenta);
    }


    // --- Métodos de Ayuda (Helpers) ---

    private Carrito obtenerOCrearCarrito(List<Carrito> carritos, String correoUsuario) {
        Optional<Carrito> carritoOpt = carritos.stream()
                .filter(c -> c.getCorreoUsuario().equals(correoUsuario))
                .findFirst();

        if (carritoOpt.isPresent()) {
            return carritoOpt.get();
        } else {
            Carrito nuevoCarrito = new Carrito(correoUsuario);
            carritos.add(nuevoCarrito);
            return nuevoCarrito;
        }
    }

    private Carrito obtenerOCrearCarrito(String correoUsuario) {
        List<Carrito> todosLosCarritos = manejadorCarrito.leerCarritos();
        Carrito carrito = obtenerOCrearCarrito(todosLosCarritos, correoUsuario);
        manejadorCarrito.guardarCarritos(todosLosCarritos);
        return carrito;
    }
}