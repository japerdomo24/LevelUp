package LevelUp.Controlador;

// Modelos
import LevelUp.AccesoJSON.AcessoCarritoJSON;
import LevelUp.Modelo.Carrito;
import LevelUp.Modelo.ItemCarrito;
import LevelUp.Modelo.Producto;
import LevelUp.Modelo.Venta;
import LevelUp.Modelo.ItemVenta;

// Acessoes
import LevelUp.AccesoJSON.AcessoProductoJSON;
import LevelUp.AccesoJSON.AcessoVentaJSON;

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
public class CarritoControlador {

    private AcessoCarritoJSON AcessoCarrito;
    private AcessoProductoJSON AcessoProducto;
    private AcessoVentaJSON AcessoVenta; // <-- ¡NUEVA DEPENDENCIA!

    // DTO para agregar un item
    record AgregarItemDTO(String productoId, int cantidad) {}

    public CarritoControlador() {
        // Asegúrate que las rutas a tus archivos de datos son correctas
        this.AcessoCarrito = new AcessoCarritoJSON("/Users/jose/Desktop/Uni/LevelUp/Backend/src/main/java/LevelUp/Data/carritos.json");
        this.AcessoProducto = new AcessoProductoJSON("/Users/jose/Desktop/Uni/LevelUp/Backend/src/main/java/LevelUp/Data/productos.json");
        this.AcessoVenta = new AcessoVentaJSON("/Users/jose/Desktop/Uni/LevelUp/Backend/src/main/java/LevelUp/Data/ventas.json"); // <-- NUEVO
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
        List<Carrito> todosLosCarritos = AcessoCarrito.leerCarritos();
        List<Producto> productos = AcessoProducto.leerProductos();
        Carrito miCarrito = obtenerOCrearCarrito(todosLosCarritos, correoUsuario);

        // 1. Validar producto y stock
        Optional<Producto> productoOpt = AcessoProducto.leerProductos().stream()
                .filter(p -> p.getId().equals(dto.productoId()))
                .findFirst();
        if (productoOpt.isEmpty()) { //404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); 
        }
        Producto producto = productoOpt.get();

        // 2. Determinar la cantidad total deseada y el stock actual
        Optional<ItemCarrito> itemExistenteOpt = miCarrito.getItems().stream()
                .filter(item -> item.getProductoId().equals(dto.productoId()))
                .findFirst();

        int cantidadEnCarrito = itemExistenteOpt.map(ItemCarrito::getCantidad).orElse(0);
        int cantidadTotalDeseada = cantidadEnCarrito + dto.cantidad();
        int stockDisponible = producto.getCantidad();

        // 3. Validar el stock total
        if (stockDisponible < cantidadTotalDeseada) {
            // Caso 2: Stock insuficiente
            // Se utiliza 409 CONFLICT para indicar un problema de stock
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }

        //4. Agregar o actualizar item

        if (itemExistenteOpt.isPresent()) {
            // Actualizar la cantidad sumando la nueva cantidad
            itemExistenteOpt.get().setCantidad(cantidadTotalDeseada);
        } else {
            // Crear nuevo item
            ItemCarrito nuevoItem = new ItemCarrito(
                    producto.getId(), producto.getSerie(),
                    dto.cantidad(), producto.getPrecio()
            );
            miCarrito.getItems().add(nuevoItem);
        }

        // 5. Recalcular y guardar
        miCarrito.calcularTotal();
        AcessoCarrito.guardarCarritos(todosLosCarritos);

        return ResponseEntity.ok(miCarrito);
    }

    /**
     * [DELETE /carrito/{productoId}] - Elimina un item del carrito.
     */
    @DeleteMapping("/{productoId}")
    public ResponseEntity<Carrito> eliminarItem(Authentication authentication, 
                                              @PathVariable String productoId) {
        
        String correoUsuario = authentication.getName();
        List<Carrito> todosLosCarritos = AcessoCarrito.leerCarritos();
        Carrito miCarrito = obtenerOCrearCarrito(todosLosCarritos, correoUsuario);

        boolean eliminado = miCarrito.getItems().removeIf(
                item -> item.getProductoId().equals(productoId)
        );

        if (!eliminado) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); 
        }

        miCarrito.calcularTotal();
        AcessoCarrito.guardarCarritos(todosLosCarritos);

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
        List<Carrito> carritos = AcessoCarrito.leerCarritos();
        List<Producto> productos = AcessoProducto.leerProductos();
        List<Venta> ventas = AcessoVenta.leerVentas();

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
        AcessoProducto.guardarProductos(productos); // Stock actualizado
        AcessoCarrito.guardarCarritos(carritos); // Carrito vacío
        AcessoVenta.guardarVentas(ventas); // Nueva venta

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
        List<Carrito> todosLosCarritos = AcessoCarrito.leerCarritos();
        Carrito carrito = obtenerOCrearCarrito(todosLosCarritos, correoUsuario);
        AcessoCarrito.guardarCarritos(todosLosCarritos);
        return carrito;
    }
}