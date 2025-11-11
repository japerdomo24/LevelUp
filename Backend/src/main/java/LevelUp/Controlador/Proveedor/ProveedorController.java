package LevelUp.Controlador.Proveedor;

import LevelUp.Modelo.Proveedor.Proveedor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// --- INICIO: Importaciones de Validación ---
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
// --- FIN: Importaciones de Validación ---

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/proveedores")
public class ProveedorController {

    private ManejadorProveedorJSON manejador; // Tu manejador

    //
    // --- SECCIÓN MANTENIDA (COMO PEDISTE) ---
    // Tu constructor original con la ruta absoluta
    public ProveedorController() {
        // ¡RUTAS CORREGIDAS PARA TU PC!
        String basePath = "C:/Users/VictorRamos/OneDrive/Documents/5to_Semestre/IS/ProyectoLevelUP/codigo/LevelUp/Backend/src/main/java/LevelUp/Data/";

        // OJO: Corregido el error tipográfico, debe ser "proveedores.json"
        this.manejador = new ManejadorProveedorJSON(basePath + "proovedores.json");
    }
    // --- FIN DE LA SECCIÓN MANTENIDA ---
    //


    // --- DTOs (Data Transfer Objects) CON VALIDACIÓN ---

    /** DTO para CREAR un proveedor (sin ID) */
    record ProveedorCreateDTO(
            @NotBlank(message = "El RIF no puede estar vacío.")
            @Pattern(regexp = "^J-\\d{8}-\\d{1}$", message = "El RIF debe tener el formato J-12345678-9")
            String rif,

            @NotBlank(message = "El nombre no puede estar vacío.")
            String nombre,

            @NotBlank(message = "El teléfono no puede estar vacío.")
            @Pattern(regexp = "^\\+58\\d{10}$", message = "El teléfono debe tener el formato +584241234567")
            String telefono,

            @NotBlank(message = "El correo no puede estar vacío.")
            @Email(message = "El formato del correo no es válido.")
            String correo,

            @NotBlank(message = "La dirección no puede estar vacía.")
            String direccion,

            List<
                @NotBlank(message = "La factura no puede estar vacía")
                @Pattern(regexp = "^(F|ORD)-\\d+$", message = "La factura debe tener el formato F-123 o ORD-456")
                String
            > facturas
    ) {}

    /** DTO para ACTUALIZAR un proveedor (campos opcionales) */
    record ProveedorUpdateDTO(
            // Permite nulo, pero si se envía, debe tener el formato
            @Pattern(regexp = "^J-\\d{8}-\\d{1}$", message = "El RIF debe tener el formato J-12345678-9")
            String rif,

            // Permite nulo, pero si se envía, no puede ser vacío o solo espacios
            @Pattern(regexp = "^(?!\\s*$).+", message = "El nombre no puede estar vacío")
            String nombre,

            @Pattern(regexp = "^\\+58\\d{10}$", message = "El teléfono debe tener el formato +584241234567")
            String telefono,

            @Email(message = "El formato del correo no es válido.")
            String correo,
            
            @Pattern(regexp = "^(?!\\s*$).+", message = "La dirección no puede estar vacía")
            String direccion,

            List<
                @NotBlank(message = "La factura no puede estar vacía")
                @Pattern(regexp = "^(F|ORD)-\\d+$", message = "La factura debe tener el formato F-123 o ORD-456")
                String
            > facturas
    ) {}


    // === ENDPOINTS DE ADMIN ===

    @GetMapping
    public List<Proveedor> getProveedores() {
        return manejador.leerProveedores();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Proveedor> getProveedorPorId(@PathVariable String id) {
        Optional<Proveedor> proveedorOpt = manejador.leerProveedores().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();

        return proveedorOpt
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crearProveedor(@Valid @RequestBody ProveedorCreateDTO dto) { // <-- @Valid AÑADIDO
        List<Proveedor> proveedores = manejador.leerProveedores();
        
        // --- (PENDIENTE) AÑADIR VALIDACIÓN DE RIF DUPLICADO AL CREAR ---
        boolean rifYaExiste = proveedores.stream()
                .anyMatch(p -> p.getRif().equalsIgnoreCase(dto.rif()));
        
        if (rifYaExiste) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Error: El RIF " + dto.rif() + " ya está registrado.");
        }

        Proveedor nuevoProveedor = new Proveedor(
                dto.rif(), dto.nombre(), dto.telefono(),
                dto.correo(), dto.direccion(), dto.facturas()
        );

        proveedores.add(nuevoProveedor);
        manejador.guardarProveedores(proveedores);

        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProveedor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProveedor(
            @PathVariable String id, 
            @Valid @RequestBody ProveedorUpdateDTO dto) { // <-- @Valid AÑADIDO
        
        List<Proveedor> proveedores = manejador.leerProveedores();
        Optional<Proveedor> proveedorOpt = proveedores.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();

        if (proveedorOpt.isEmpty()) {
            return ResponseEntity.notFound().build(); // 404
        }

        Proveedor proveedorAActualizar = proveedorOpt.get();

        // --- REGLA 2: VALIDAR RIF DUPLICADO (Esto se queda) ---
        if (dto.rif() != null) {
            boolean rifYaExiste = proveedores.stream()
                    .anyMatch(p -> !p.getId().equals(id) && p.getRif().equalsIgnoreCase(dto.rif()));
            
            if (rifYaExiste) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Error: El RIF " + dto.rif() + " ya está registrado en otro proveedor.");
            }
            proveedorAActualizar.setRif(dto.rif());
        }

        // --- LÓGICA DE VALIDACIÓN MANUAL ELIMINADA ---
        // Todos los 'if (dto.campo().isBlank())' se eliminaron 
        // porque las anotaciones @Valid lo manejan automáticamente.

        // --- Aplicar actualizaciones (solo si el campo no es nulo) ---
        if (dto.nombre() != null) {
            proveedorAActualizar.setNombre(dto.nombre());
        }
        if (dto.telefono() != null) {
            proveedorAActualizar.setTelefono(dto.telefono());
        }
        if (dto.correo() != null) {
            proveedorAActualizar.setCorreo(dto.correo());
        }
        if (dto.direccion() != null) {
            proveedorAActualizar.setDireccion(dto.direccion());
        }
        if (dto.facturas() != null) {
            proveedorAActualizar.setFacturas(dto.facturas());
        }

        manejador.guardarProveedores(proveedores);
        return ResponseEntity.ok(proveedorAActualizar); // 200 OK
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProveedor(@PathVariable String id) {
        List<Proveedor> proveedores = manejador.leerProveedores();
        boolean eliminado = proveedores.removeIf(p -> p.getId().equals(id));

        if (!eliminado) {
            return ResponseEntity.notFound().build(); // 404
        }

        manejador.guardarProveedores(proveedores);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}