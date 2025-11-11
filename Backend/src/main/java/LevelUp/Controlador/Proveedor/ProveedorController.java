package LevelUp.Controlador.Proveedor;

import LevelUp.Modelo.Proveedor.Proveedor; // <-- IMPORT CORREGIDO
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/proveedores")
public class ProveedorController {

    private ManejadorProveedorJSON manejador;

    public ProveedorController() {
        // Asegúrate que esta ruta es correcta
        this.manejador = new ManejadorProveedorJSON("/Users/jose/Desktop/Uni/LevelUp/Backend/src/main/java/LevelUp/Data/proovedores.json");
    }

    // --- DTOs (Data Transfer Objects) para Proveedores ---

    /** DTO para CREAR un proveedor (sin ID) */
    record ProveedorCreateDTO(
            String rif, String nombre, String telefono, 
            String correo, String direccion, List<String> facturas
    ) {}

    /** DTO para ACTUALIZAR un proveedor (campos opcionales) */
    record ProveedorUpdateDTO(
            String rif, String nombre, String telefono, 
            String correo, String direccion, List<String> facturas
    ) {}


    // === ENDPOINTS DE ADMIN ===
    // (SecurityConfig se encargará de que solo Admins entren aquí)

    /**
     * [GET /proveedores] - Obtener todos los proveedores.
     */
    @GetMapping
    public List<Proveedor> getProveedores() {
        return manejador.leerProveedores();
    }

    /**
     * [GET /proveedores/{id}] - Obtener un proveedor por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Proveedor> getProveedorPorId(@PathVariable String id) {
        
        Optional<Proveedor> proveedorOpt = manejador.leerProveedores().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();

        return proveedorOpt
                .map(ResponseEntity::ok) // Retorna 200 OK
                .orElse(ResponseEntity.notFound().build()); // Retorna 404
    }

    /**
     * [POST /proveedores] - Crear un nuevo proveedor.
     */
    @PostMapping
    public ResponseEntity<Proveedor> crearProveedor(@RequestBody ProveedorCreateDTO dto) {
        List<Proveedor> proveedores = manejador.leerProveedores();
        
        Proveedor nuevoProveedor = new Proveedor(
                dto.rif(), dto.nombre(), dto.telefono(),
                dto.correo(), dto.direccion(), dto.facturas()
        );

        proveedores.add(nuevoProveedor);
        manejador.guardarProveedores(proveedores);

        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProveedor);
    }

    /**
     * [PUT /proveedores/{id}] - Actualizar un proveedor existente.
* ¡CON VALIDACIONES DE LA HISTORIA DE USUARIO!
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProveedor( // <-- Cambiado a ResponseEntity<?>
            @PathVariable String id, 
            @RequestBody ProveedorUpdateDTO dto) {
        
        List<Proveedor> proveedores = manejador.leerProveedores();
        Optional<Proveedor> proveedorOpt = proveedores.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();

        if (proveedorOpt.isEmpty()) {
            return ResponseEntity.notFound().build(); // 404
        }

        Proveedor proveedorAActualizar = proveedorOpt.get();

        // --- REGLA 2: VALIDAR RIF DUPLICADO ---
        if (dto.rif() != null && !dto.rif().isBlank()) {
            // Revisar si OTRO proveedor (con ID diferente) ya usa este RIF
            boolean rifYaExiste = proveedores.stream()
                .anyMatch(p -> !p.getId().equals(id) && p.getRif().equalsIgnoreCase(dto.rif()));
            
            if (rifYaExiste) {
                // 409 Conflict: El RIF ya existe en otro proveedor.
                return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body("Error: El RIF " + dto.rif() + " ya está registrado en otro proveedor.");
            }
proveedorAActualizar.setRif(dto.rif());
        
        } else if (dto.rif() != null && dto.rif().isBlank()) {
             // --- REGLA 3: No se permite campo vacío ---
            return ResponseEntity.badRequest().body("Error: El RIF no puede estar vacío.");
        }

        // --- REGLA 3: VALIDAR CAMPOS VACÍOS Y FORMATO ---
        
        if (dto.nombre() != null) {
            if (dto.nombre().isBlank()) {
                return ResponseEntity.badRequest().body("Error: El nombre no puede estar vacío.");
            }
            proveedorAActualizar.setNombre(dto.nombre());
}

        if (dto.telefono() != null) {
            if (dto.telefono().isBlank()) {
                return ResponseEntity.badRequest().body("Error: El teléfono no puede estar vacío.");
            }
            proveedorAActualizar.setTelefono(dto.telefono());
}

        if (dto.correo() != null) {
            if (dto.correo().isBlank()) {
                return ResponseEntity.badRequest().body("Error: El correo no puede estar vacío.");
            }
            // Validación simple de formato
            if (!dto.correo().contains("@") || !dto.correo().contains(".")) {
                return ResponseEntity.badRequest().body("Error: El formato del correo no es válido.");
            }
            proveedorAActualizar.setCorreo(dto.correo());
}

        if (dto.direccion() != null) {
            if (dto.direccion().isBlank()) {
                return ResponseEntity.badRequest().body("Error: La dirección no puede estar vacía.");
            }
            proveedorAActualizar.setDireccion(dto.direccion());
}

        if (dto.facturas() != null) {
proveedorAActualizar.setFacturas(dto.facturas());
}

        manejador.guardarProveedores(proveedores);
        return ResponseEntity.ok(proveedorAActualizar); // 200 OK
    }

    /**
     * [DELETE /proveedores/{id}] - Eliminar un proveedor.
     */
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