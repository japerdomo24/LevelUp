package LevelUp.Controlador.Usuario;

import LevelUp.Modelo.Usuario.Usuario;
// import LevelUp.Service.JwtService; <-- CAMBIADO
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
public class UsuarioController {

    // Instancia del manejador
    ManejadorUsuarioJSON manejador;

    @Autowired
    private JwtService jwtService; 

    // Constructor para inicializar el manejador
    public UsuarioController() {
         // Asegúrate que esta ruta es correcta para tu proyecto
        this.manejador = new ManejadorUsuarioJSON("src/main/java/LevelUp/Data/usuarios.json");
    }

    // --- DTOs (Data Transfer Objects) ---
    record UsuarioResponseDTO(String correo, String rol) {}
    record UsuarioRegistroDTO(String correo, String clavePlana, String rol) {}
    record LoginDTO(String correo, String clave) {}
    record UsuarioActualizarDTO(String rol, String clavePlana) {}
    record LoginResponseDTO(String token, UsuarioResponseDTO usuario) {}


    // --- Endpoints PÚBLICOS (Permitidos por SecurityConfig) ---
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginDTO loginDTO) {
        Optional<Usuario> usuarioOpt = encontrarUsuarioPorCorreo(loginDTO.correo());

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Usuario usuario = usuarioOpt.get();

        if (usuario.verificarClave(loginDTO.clave())) {
            // Clave correcta: Generar token
            String token = jwtService.generarToken(usuario);
            UsuarioResponseDTO usuarioDTO = new UsuarioResponseDTO(usuario.getCorreo(), usuario.getRol());
            LoginResponseDTO respuestaLogin = new LoginResponseDTO(token, usuarioDTO);
            return ResponseEntity.ok(respuestaLogin);
        } else {
            // Clave incorrecta
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crearUsuario(@RequestBody UsuarioRegistroDTO registroDTO) {
        List<Usuario> usuarios = manejador.leerUsuarios();

        boolean yaExiste = usuarios.stream()
                .anyMatch(u -> u.getCorreo().equalsIgnoreCase(registroDTO.correo()));
        
        if (yaExiste) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // 409
        }

        Usuario nuevoUsuario = new Usuario(
                registroDTO.correo(), 
                registroDTO.clavePlana(), 
                registroDTO.rol()
        );

        usuarios.add(nuevoUsuario);
        manejador.guardarUsuarios(usuarios);

        UsuarioResponseDTO responseDTO = new UsuarioResponseDTO(
                nuevoUsuario.getCorreo(), 
                nuevoUsuario.getRol()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO); // 201
    }

    // --- Endpoints PROTEGIDOS (Requieren Token) ---
    
    @GetMapping
    public List<UsuarioResponseDTO> getUsuarios(){
        List<Usuario> usuarios = manejador.leerUsuarios();
        // Devuelve la lista sin las claves
        return usuarios.stream()
                .map(u -> new UsuarioResponseDTO(u.getCorreo(), u.getRol()))
                .collect(Collectors.toList());
    }
    
    @GetMapping("/{correo}")
    public ResponseEntity<UsuarioResponseDTO> getUsuarioPorCorreo(@PathVariable String correo) {
        return encontrarUsuarioPorCorreo(correo)
                .map(usuario -> ResponseEntity.ok(
                        new UsuarioResponseDTO(usuario.getCorreo(), usuario.getRol()))
                )
                .orElse(ResponseEntity.notFound().build()); // 404
    }

    @PutMapping("/{correo}")
    public ResponseEntity<UsuarioResponseDTO> actualizarUsuario(
            @PathVariable String correo,
            @RequestBody UsuarioActualizarDTO datosActualizar,
            Authentication authentication) { 

        // 1. Identidad del usuario que hace la petición (del token)
        String correoAutenticado = authentication.getName();
        boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_admin"));

        // 2. Usuario que se va a modificar (de la URL)
        List<Usuario> usuarios = manejador.leerUsuarios();
        Optional<Usuario> usuarioOpt = usuarios.stream()
                .filter(u -> u.getCorreo().equalsIgnoreCase(correo))
                .findFirst();

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.notFound().build(); // 404
        }
        Usuario usuarioAEditar = usuarioOpt.get();

        // 3. Aplicar Reglas de Autorización
        if (esAdmin) {
            // Admin puede cambiar rol y clave de CUALQUIERA
            if (datosActualizar.rol() != null) {
                usuarioAEditar.setRol(datosActualizar.rol());
            }
            if (datosActualizar.clavePlana() != null && !datosActualizar.clavePlana().isEmpty()) {
                usuarioAEditar.setClavePlana(datosActualizar.clavePlana());
            }
        } else if (correoAutenticado.equalsIgnoreCase(correo)) {
            // User solo puede editarse a SÍ MISMO
            if (datosActualizar.rol() != null && !datosActualizar.rol().equals(usuarioAEditar.getRol())) {
                // No puede cambiar su propio rol
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403
            }
            if (datosActualizar.clavePlana() != null && !datosActualizar.clavePlana().isEmpty()) {
                // Sí puede cambiar su clave
                usuarioAEditar.setClavePlana(datosActualizar.clavePlana());
            }
        } else {
            // Un User intentando editar a OTRO
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403
        }

        // 4. Guardar y responder
        manejador.guardarUsuarios(usuarios);
        UsuarioResponseDTO responseDTO = new UsuarioResponseDTO(usuarioAEditar.getCorreo(), usuarioAEditar.getRol());
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{correo}")
    public ResponseEntity<Void> eliminarUsuario(
            @PathVariable String correo,
            Authentication authentication) { 

        // 1. Identidad del usuario que hace la petición (del token)
        String correoAutenticado = authentication.getName();
        boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_admin"));

        // 2. Aplicar Reglas de Autorización
        if (!esAdmin && !correoAutenticado.equalsIgnoreCase(correo)) {
            // No eres admin Y no estás intentando borrarte a ti mismo
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403
        }
        
        // 3. Proceder con la eliminación
        List<Usuario> usuarios = manejador.leerUsuarios();
        boolean eliminado = usuarios.removeIf(
                u -> u.getCorreo().equalsIgnoreCase(correo)
        );

        if (!eliminado) {
            return ResponseEntity.notFound().build(); // 404
        }

        manejador.guardarUsuarios(usuarios);
        return ResponseEntity.noContent().build(); // 204
    }

    
    // --- Método de Ayuda (Helper) ---
    private Optional<Usuario> encontrarUsuarioPorCorreo(String correo) {
        if (correo == null || correo.isEmpty()) {
            return Optional.empty();
        }
        List<Usuario> usuarios = manejador.leerUsuarios();
        return usuarios.stream()
                .filter(u -> u.getCorreo().equalsIgnoreCase(correo))
                .findFirst();
    }
}