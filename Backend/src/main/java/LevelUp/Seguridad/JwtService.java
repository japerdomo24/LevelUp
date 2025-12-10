package LevelUp.Seguridad; // <-- PAQUETE ACTUALIZADO

import LevelUp.Modelo.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // ¡¡IMPORTANTE!! CAMBIA ESTA CLAVE
    // Genera una en: https://www.allkeysgenerator.com/ (elige 256-bit Base64)
    // Guárdala en 'application.properties' en un proyecto real.
    private static final String SECRET_KEY = "h4of9WjK4wB8I3bY7X9pZ6vT5u2s1q0g+H4oF9wJk4w=";

    // 1 hora de expiración
    private static final long EXPIRATION_TIME_MS = 1000 * 60 * 60; 

    /**
     * Genera un token JWT para un usuario.
     */
    public String generarToken(Usuario usuario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", usuario.getRol());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(usuario.getCorreo()) // El "dueño" del token
                .setIssuedAt(new Date(System.currentTimeMillis())) // Fecha de creación
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_MS)) // Fecha de expiración
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Firma el token
                .compact();
    }
    
    /**
     * Extrae el correo (subject) del token JWT.
     */
    public String extraerCorreo(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    /**
     * Valida si un token es correcto y pertenece a un usuario.
     */
    public boolean esTokenValido(String token, UserDetails userDetails) {
        final String correo = extraerCorreo(token);
        return (correo.equals(userDetails.getUsername()) && !esTokenExpirado(token));
    }

    private boolean esTokenExpirado(String token) {
        return extraerExpiracion(token).before(new Date());
    }

    private Date extraerExpiracion(String token) {
        return extraerClaim(token, Claims::getExpiration);
    }

    /**
     * Método genérico para extraer CUALQUIER información (claim) del token.
     */
    public <T> T extraerClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extraerTodosClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extraerTodosClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Obtiene la clave de firma a partir de la cadena secreta en Base64.
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}