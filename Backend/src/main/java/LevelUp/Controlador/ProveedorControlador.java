package LevelUp.Controlador;

import LevelUp.AccesoJSON.AcessoProveedorJSON;
import LevelUp.Modelo.Proveedor; // <-- IMPORT CORREGIDO
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/proveedores")
public class ProveedorControlador {

    private AcessoProveedorJSON Acesso;

    public ProveedorControlador() {
        // Asegúrate que esta ruta es correcta
        this.Acesso = new AcessoProveedorJSON("/Users/jose/Desktop/Uni/LevelUp/Backend/src/main/java/LevelUp/Data/proovedores.json");
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
        return Acesso.leerProveedores();
    }

    /**
     * [GET /proveedores/{id}] - Obtener un proveedor por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Proveedor> getProveedorPorId(@PathVariable String id) {
        
        Optional<Proveedor> proveedorOpt = Acesso.leerProveedores().stream()
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
        List<Proveedor> proveedores = Acesso.leerProveedores();
        
        Proveedor nuevoProveedor = new Proveedor(
                dto.rif(), dto.nombre(), dto.telefono(),
                dto.correo(), dto.direccion(), dto.facturas()
        );

        proveedores.add(nuevoProveedor);
        Acesso.guardarProveedores(proveedores);

        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProveedor);
    }

    /**
     * [PUT /proveedores/{id}] - Actualizar un proveedor existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Proveedor> actualizarProveedor(
            @PathVariable String id, 
            @RequestBody ProveedorUpdateDTO dto) {
        
        List<Proveedor> proveedores = Acesso.leerProveedores();
        Optional<Proveedor> proveedorOpt = proveedores.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();

        if (proveedorOpt.isEmpty()) {
            return ResponseEntity.notFound().build(); // 404
        }

        Proveedor proveedorAActualizar = proveedorOpt.get();

        // Actualizamos solo los campos que no sean nulos en el DTO
        if (dto.rif() != null) proveedorAActualizar.setRif(dto.rif());
        if (dto.nombre() != null) proveedorAActualizar.setNombre(dto.nombre());
        if (dto.telefono() != null) proveedorAActualizar.setTelefono(dto.telefono());
        if (dto.correo() != null) proveedorAActualizar.setCorreo(dto.correo());
        if (dto.direccion() != null) proveedorAActualizar.setDireccion(dto.direccion());
        if (dto.facturas() != null) proveedorAActualizar.setFacturas(dto.facturas());

        Acesso.guardarProveedores(proveedores);
        return ResponseEntity.ok(proveedorAActualizar); // 200 OK
    }

    /**
     * [DELETE /proveedores/{id}] - Eliminar un proveedor.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProveedor(@PathVariable String id) {
        List<Proveedor> proveedores = Acesso.leerProveedores();

        boolean eliminado = proveedores.removeIf(p -> p.getId().equals(id));

        if (!eliminado) {
            return ResponseEntity.notFound().build(); // 404
        }

        Acesso.guardarProveedores(proveedores);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}