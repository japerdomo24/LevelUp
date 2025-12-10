package LevelUp.Controlador;

import LevelUp.AccesoJSON.AcessoUsuarioJSON;
import LevelUp.Modelo.Usuario;
import LevelUp.Seguridad.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; 
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/usuarios")
public class UsuarioControlador {

    AcessoUsuarioJSON Acesso;

    @Autowired
    private JwtService jwtService;

    public UsuarioControlador() {
        this.Acesso = new AcessoUsuarioJSON("/Users/jose/Desktop/Uni/LevelUp/Backend/src/main/java/LevelUp/Data/usuarios.json");
    }

    // --- DTOs (Data Transfer Objects) COMPLETOS ---
    
    /** DTO de respuesta (datos públicos) */
    record UsuarioResponseDTO(String correo, String rol, 
                              String nombre, String apellido, String numero, String direccion) {}

    /** DTO para registrar un nuevo usuario */
    record UsuarioRegistroDTO(String correo, String clavePlana, String rol, 
                              String nombre, String apellido, String numero, String direccion) {}

    /** DTO para InicioSesion (sin cambios) */
    record LoginDTO(String correo, String clave) {}
    
    /** DTO de respuesta de InicioSesion (definición sin cambios, pero uso actualizado) */
    record LoginResponseDTO(String token, UsuarioResponseDTO usuario) {}

    /** ¡NUEVO DTO! Para actualizar un usuario (todos los campos opcionales) */
    record UsuarioActualizarDTO(String rol, String clavePlana, 
                                String nombre, String apellido, String numero, String direccion) {}


    // --- Endpoints PÚBLICOS (Crear y Consultar) ---
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginDTO loginDTO) {
        Optional<Usuario> usuarioOpt = encontrarUsuarioPorCorreo(loginDTO.correo());

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Usuario usuario = usuarioOpt.get();

        if (usuario.verificarClave(loginDTO.clave())) {
            String token = jwtService.generarToken(usuario);
            
            UsuarioResponseDTO usuarioDTO = new UsuarioResponseDTO(
                    usuario.getCorreo(), usuario.getRol(),
                    usuario.getNombre(), usuario.getApellido(),
                    usuario.getNumero(), usuario.getDireccion()
            );
            LoginResponseDTO respuestaLogin = new LoginResponseDTO(token, usuarioDTO);
            return ResponseEntity.ok(respuestaLogin);
            
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crearUsuario(@RequestBody UsuarioRegistroDTO dto) {
        List<Usuario> usuarios = Acesso.leerUsuarios();

        boolean yaExiste = usuarios.stream()
                .anyMatch(u -> u.getCorreo().equalsIgnoreCase(dto.correo()));
        
        if (yaExiste) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); 
        }

        Usuario nuevoUsuario = new Usuario(
                dto.correo(), dto.clavePlana(), dto.rol(),
                dto.nombre(), dto.apellido(), dto.numero(), dto.direccion()
        );

        usuarios.add(nuevoUsuario);
        Acesso.guardarUsuarios(usuarios);

        UsuarioResponseDTO responseDTO = new UsuarioResponseDTO(
                nuevoUsuario.getCorreo(), nuevoUsuario.getRol(),
                nuevoUsuario.getNombre(), nuevoUsuario.getApellido(),
                nuevoUsuario.getNumero(), nuevoUsuario.getDireccion()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO); 
    }

    // --- Endpoints PROTEGIDOS (Consultar, Actualizar, Eliminar) ---
    
    @GetMapping
    public List<UsuarioResponseDTO> getUsuarios(){
        List<Usuario> usuarios = Acesso.leerUsuarios();
        
        return usuarios.stream()
                .map(u -> new UsuarioResponseDTO(
                        u.getCorreo(), u.getRol(), u.getNombre(),
                        u.getApellido(), u.getNumero(), u.getDireccion()
                ))
                .collect(Collectors.toList());
    }
    
    @GetMapping("/{correo}")
    public ResponseEntity<UsuarioResponseDTO> getUsuarioPorCorreo(@PathVariable String correo) {
        
        return encontrarUsuarioPorCorreo(correo)
                .map(usuario -> ResponseEntity.ok(
                        new UsuarioResponseDTO(
                            usuario.getCorreo(), usuario.getRol(), usuario.getNombre(),
                            usuario.getApellido(), usuario.getNumero(), usuario.getDireccion()
                        )
                ))
                .orElse(ResponseEntity.notFound().build()); 
    }

    
    // --- ¡MÉTODOS RESTAURADOS! ---

    /**
     * [PUT /usuarios/{correo}] - ACTUALIZAR un usuario.
     * REGLAS:
     * 1. Un Admin puede editar a CUALQUIERA (rol, clave, perfil).
     * 2. Un User solo puede editarse a SÍ MISMO (clave, perfil, PERO NO ROL).
     */
    @PutMapping("/{correo}")
    public ResponseEntity<UsuarioResponseDTO> actualizarUsuario(
            @PathVariable String correo,
            @RequestBody UsuarioActualizarDTO dto,
            Authentication authentication) { 

        String correoAutenticado = authentication.getName();
        boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_admin"));

        List<Usuario> usuarios = Acesso.leerUsuarios();
        Optional<Usuario> usuarioOpt = usuarios.stream()
                .filter(u -> u.getCorreo().equalsIgnoreCase(correo))
                .findFirst();

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.notFound().build(); // 404
        }
        Usuario usuarioAEditar = usuarioOpt.get();

        // --- Aplicar Reglas de Autorización ---
        if (esAdmin) {
            // El Admin puede cambiar todo
            if (dto.rol() != null) usuarioAEditar.setRol(dto.rol());
            if (dto.clavePlana() != null && !dto.clavePlana().isEmpty()) usuarioAEditar.setClavePlana(dto.clavePlana());
            if (dto.nombre() != null) usuarioAEditar.setNombre(dto.nombre());
            if (dto.apellido() != null) usuarioAEditar.setApellido(dto.apellido());
            if (dto.numero() != null) usuarioAEditar.setNumero(dto.numero());
            if (dto.direccion() != null) usuarioAEditar.setDireccion(dto.direccion());

        } else if (correoAutenticado.equalsIgnoreCase(correo)) {
            // El User solo puede editarse a SÍ MISMO
            
            // No puede cambiar su propio rol
            if (dto.rol() != null && !dto.rol().equals(usuarioAEditar.getRol())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Prohibido
            }
            // Sí puede cambiar su clave y perfil
            if (dto.clavePlana() != null && !dto.clavePlana().isEmpty()) usuarioAEditar.setClavePlana(dto.clavePlana());
            if (dto.nombre() != null) usuarioAEditar.setNombre(dto.nombre());
            if (dto.apellido() != null) usuarioAEditar.setApellido(dto.apellido());
            if (dto.numero() != null) usuarioAEditar.setNumero(dto.numero());
            if (dto.direccion() != null) usuarioAEditar.setDireccion(dto.direccion());
        } else {
            // Un User intentando editar a OTRO usuario
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Prohibido
        }

        Acesso.guardarUsuarios(usuarios);

        // Devolver el usuario actualizado
        UsuarioResponseDTO responseDTO = new UsuarioResponseDTO(
                usuarioAEditar.getCorreo(), usuarioAEditar.getRol(),
                usuarioAEditar.getNombre(), usuarioAEditar.getApellido(),
                usuarioAEditar.getNumero(), usuarioAEditar.getDireccion()
        );
        return ResponseEntity.ok(responseDTO);
    }


    /**
     * [DELETE /usuarios/{correo}] - ELIMINAR un usuario.
     * REGLAS:
     * 1. Un Admin puede eliminar a CUALQUIERA.
     * 2. Un User solo puede eliminarse a SÍ MISMO.
     */
    @DeleteMapping("/{correo}")
    public ResponseEntity<Void> eliminarUsuario(
            @PathVariable String correo,
            Authentication authentication) { 

        String correoAutenticado = authentication.getName();
        boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_admin"));

        // --- Aplicar Reglas de Autorización ---
        if (!esAdmin && !correoAutenticado.equalsIgnoreCase(correo)) {
            // No eres admin Y no estás intentando borrarte a ti mismo
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Prohibido
        }
        
        List<Usuario> usuarios = Acesso.leerUsuarios();
        boolean eliminado = usuarios.removeIf(
                u -> u.getCorreo().equalsIgnoreCase(correo)
        );

        if (!eliminado) {
            return ResponseEntity.notFound().build(); // 404
        }

        Acesso.guardarUsuarios(usuarios);
        return ResponseEntity.noContent().build(); // 204
    }
    
    
    // --- Método de Ayuda (Helper) ---
    private Optional<Usuario> encontrarUsuarioPorCorreo(String correo) {
        if (correo == null || correo.isEmpty()) {
            return Optional.empty();
        }
        List<Usuario> usuarios = Acesso.leerUsuarios();
        return usuarios.stream()
                .filter(u -> u.getCorreo().equalsIgnoreCase(correo))
                .findFirst();
    }
}