package LevelUp.Controlador.Usuario; // <-- PAQUETE ACTUALIZADO

import LevelUp.Modelo.Usuario.Usuario;
import LevelUp.Modelo.Usuario.UsuarioDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    // Ruta al archivo JSON. 
    private final ManejadorUsuarioJSON manejador;

    public UsuarioDetailsService() {
        // Asegúrate que esta ruta es correcta para tu proyecto
        this.manejador = new ManejadorUsuarioJSON("/Users/jose/Desktop/Uni/LevelUp/Backend/src/main/java/LevelUp/Data/usuarios.json");
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        List<Usuario> usuarios = manejador.leerUsuarios();
        
        Optional<Usuario> usuarioOpt = usuarios.stream()
                .filter(u -> u.getCorreo().equalsIgnoreCase(correo))
                .findFirst();

        if (usuarioOpt.isEmpty()) {
            throw new UsernameNotFoundException("Usuario no encontrado con correo: " + correo);
        }

        return new UsuarioDetails(usuarioOpt.get());
    }
}