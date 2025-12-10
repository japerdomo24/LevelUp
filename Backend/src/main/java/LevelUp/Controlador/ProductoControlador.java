package LevelUp.Controlador;

import LevelUp.AccesoJSON.AcessoProductoJSON;
import LevelUp.Modelo.Producto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/productos")
public class ProductoControlador {

    private AcessoProductoJSON Acesso;

    public ProductoControlador() {
        // Asegúrate que esta ruta es correcta y existe la carpeta Data
        this.Acesso = new AcessoProductoJSON("/Users/jose/Desktop/Uni/LevelUp/Backend/src/main/java/LevelUp/Data/productos.json");
    }

    // --- DTOs (Data Transfer Objects) para Productos ---

    /** DTO para CREAR un producto (actualizado con imagen única) */
    record ProductoCreateDTO(
            String tipo, String serie, String modelo, List<String> colores,
            List<String> capacidades, String configSim, double precio, int cantidad,
            String imagen // <-- ACTUALIZADO
    ) {}

    /** DTO para ACTUALIZAR (actualizado con imagen única) */
    record ProductoUpdateDTO(
            String tipo, String serie, String modelo, List<String> colores,
            List<String> capacidades, String configSim, Double precio, Integer cantidad,
            String imagen // <-- ACTUALIZADO
    ) {}


    // === ENDPOINTS PÚBLICOS (para clientes) ===

    @GetMapping
    public List<Producto> getProductos() {
        return Acesso.leerProductos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> getProductoPorId(@PathVariable String id) {
        
        Optional<Producto> productoOpt = Acesso.leerProductos().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();

        return productoOpt
                .map(ResponseEntity::ok) 
                .orElse(ResponseEntity.notFound().build()); 
    }


    // === ENDPOINTS DE ADMIN (Protegidos) ===

    @PostMapping
    public ResponseEntity<Producto> crearProducto(@RequestBody ProductoCreateDTO dto) {
        List<Producto> productos = Acesso.leerProductos();

        Producto nuevoProducto = new Producto(
                dto.tipo(), dto.serie(), dto.modelo(), dto.colores(),
                dto.capacidades(), dto.configSim(), dto.precio(), dto.cantidad(),
                dto.imagen() // <-- ACTUALIZADO
        );

        productos.add(nuevoProducto);
        Acesso.guardarProductos(productos);

        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(
            @PathVariable String id, 
            @RequestBody ProductoUpdateDTO dto) {
        
        List<Producto> productos = Acesso.leerProductos();
        Optional<Producto> productoOpt = productos.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();

        if (productoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Producto productoAActualizar = productoOpt.get();

        // Actualizamos solo los campos que no sean nulos en el DTO
        if (dto.tipo() != null) productoAActualizar.setTipo(dto.tipo());
        if (dto.serie() != null) productoAActualizar.setSerie(dto.serie());
        if (dto.modelo() != null) productoAActualizar.setModelo(dto.modelo());
        if (dto.colores() != null) productoAActualizar.setColores(dto.colores());
        if (dto.capacidades() != null) productoAActualizar.setCapacidades(dto.capacidades());
        if (dto.configSim() != null) productoAActualizar.setConfigSim(dto.configSim());
        if (dto.precio() != null) productoAActualizar.setPrecio(dto.precio());
        if (dto.cantidad() != null) productoAActualizar.setCantidad(dto.cantidad());
        if (dto.imagen() != null) productoAActualizar.setImagen(dto.imagen()); // <-- ACTUALIZADO

        Acesso.guardarProductos(productos);
        return ResponseEntity.ok(productoAActualizar);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable String id) {
        List<Producto> productos = Acesso.leerProductos();

        boolean eliminado = productos.removeIf(p -> p.getId().equals(id));

        if (!eliminado) {
            return ResponseEntity.notFound().build();
        }

        Acesso.guardarProductos(productos);
        return ResponseEntity.noContent().build();
    }
}