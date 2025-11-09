package LevelUp.Controlador.Usuario; // <-- PAQUETE ACTUALIZADO

// import LevelUp.Service.UsuarioDetailsService; <-- CAMBIADO

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter; // Ya está en la carpeta 'Controlador'
    @Autowired
    private UsuarioDetailsService usuarioDetailsService; // Ya está en la carpeta 'Controlador'

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // --- Endpoints PÚBLICOS ---
                        .requestMatchers(HttpMethod.POST, "/usuarios/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/usuarios").permitAll()
                        .requestMatchers(HttpMethod.GET, "/productos", "/productos/**").permitAll()

                        // --- Endpoints de ADMIN ---
                        .requestMatchers(HttpMethod.POST, "/productos").hasRole("admin")
                        .requestMatchers("/proveedores", "/proveedores/**").hasRole("admin")

                        // ¡NUEVAS REGLAS DE VENTA (ADMIN)!
                        .requestMatchers(HttpMethod.GET, "/ventas", "/ventas/**").hasRole("admin") // Consultar y Reporte
                        .requestMatchers(HttpMethod.DELETE, "/ventas/**").hasRole("admin") // Eliminar

                        // --- Endpoints de USUARIO (Protegidos) ---
                        .requestMatchers("/carrito", "/carrito/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/usuarios", "/usuarios/**").authenticated()

                        // ¡NUEVA REGLA DE VENTA (USUARIO)!
                        .requestMatchers(HttpMethod.POST, "/ventas/checkout").authenticated() // Checkout

                        // Denegar todo lo demás
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(usuarioDetailsService); // Cómo encontrar usuarios
        authProvider.setPasswordEncoder(passwordEncoder()); // Cómo verificar contraseñas
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Usa BCrypt para las contraseñas
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}