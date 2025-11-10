package LevelUp.Controlador.Usuario;

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

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;
    @Autowired
    private UsuarioDetailsService usuarioDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) 
            .csrf(csrf -> csrf.disable()) 
            .authorizeHttpRequests(auth -> auth
                    
                    // --- A) Endpoints PÚBLICOS ---
                    .requestMatchers(HttpMethod.POST, "/usuarios/login").permitAll()
                    .requestMatchers(HttpMethod.POST, "/usuarios").permitAll()
                    .requestMatchers(HttpMethod.GET, "/productos", "/productos/**").permitAll() 
                    
                    // --- B) Endpoints de ADMIN ---
                    .requestMatchers(HttpMethod.POST, "/productos").hasRole("admin")
                    .requestMatchers(HttpMethod.PUT, "/productos/**").hasRole("admin")
                    .requestMatchers(HttpMethod.DELETE, "/productos/**").hasRole("admin")
                    .requestMatchers("/proveedores", "/proveedores/**").hasRole("admin")
                    .requestMatchers(HttpMethod.POST, "/ventas").hasRole("admin") 
                    .requestMatchers(HttpMethod.GET, "/ventas", "/ventas/**").hasRole("admin") 
                    .requestMatchers(HttpMethod.DELETE, "/ventas/**").hasRole("admin")

                    // --- C) Endpoints de USUARIO (Autenticados) ---
                    
                    // ¡¡REGLA MODIFICADA!!
                    // Solo los usuarios con rol "user" pueden acceder al carrito
                    .requestMatchers("/carrito", "/carrito/**").hasRole("user") 
                    
                    .requestMatchers(HttpMethod.GET, "/usuarios", "/usuarios/**").authenticated()
                    .requestMatchers(HttpMethod.PUT, "/usuarios/**").authenticated()
                    .requestMatchers(HttpMethod.DELETE, "/usuarios/**").authenticated()
                    .requestMatchers("/pago", "/pago/**").authenticated() 
                    
                    .anyRequest().authenticated() 
            )
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS) 
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Bean para el proveedor de autenticación
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(usuarioDetailsService); 
        authProvider.setPasswordEncoder(passwordEncoder()); 
        return authProvider;
    }

    /**
     * Bean para el encriptador de contraseñas
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * Bean para el Authentication Manager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    /**
     * Configuración de CORS
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173")); 
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); 
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type")); 
        configuration.setAllowCredentials(true); 
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); 
        
        return source;
    }
}