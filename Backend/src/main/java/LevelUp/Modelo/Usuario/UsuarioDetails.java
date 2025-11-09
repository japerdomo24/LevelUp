package LevelUp.Modelo.Usuario;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Clase "wrapper" que adapta nuestro modelo Usuario a la interfaz UserDetails
 * que Spring Security requiere.
 */
public class UsuarioDetails implements UserDetails {

    private Usuario usuario;

    public UsuarioDetails(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // El prefijo "ROLE_" es una convención de Spring Security
        return List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol()));
    }

    @Override
    public String getPassword() {
        return usuario.getClave(); // Devuelve el HASH de bcrypt
    }

    @Override
    public String getUsername() {
        return usuario.getCorreo(); // Usamos el correo como username
    }

    // Para este ejemplo, asumimos que las cuentas siempre están activas
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}