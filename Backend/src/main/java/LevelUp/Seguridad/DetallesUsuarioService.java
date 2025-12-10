package LevelUp.Seguridad; // <-- PAQUETE ACTUALIZADO

import LevelUp.AccesoJSON.AcessoUsuarioJSON;
import LevelUp.Modelo.Usuario;
import LevelUp.Modelo.DetallesUsuario;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DetallesUsuarioService implements UserDetailsService {

    // Ruta al archivo JSON. 
    private final AcessoUsuarioJSON Acesso;

    public DetallesUsuarioService() {
        // Asegúrate que esta ruta es correcta para tu proyecto
        this.Acesso = new AcessoUsuarioJSON("/Users/jose/Desktop/Uni/LevelUp/Backend/src/main/java/LevelUp/Data/usuarios.json");
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        List<Usuario> usuarios = Acesso.leerUsuarios();
        
        Optional<Usuario> usuarioOpt = usuarios.stream()
                .filter(u -> u.getCorreo().equalsIgnoreCase(correo))
                .findFirst();

        if (usuarioOpt.isEmpty()) {
            throw new UsernameNotFoundException("Usuario no encontrado con correo: " + correo);
        }

        return new DetallesUsuario(usuarioOpt.get());
    }
}