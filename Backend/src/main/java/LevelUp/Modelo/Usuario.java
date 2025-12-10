package LevelUp.Modelo;

import org.mindrot.jbcrypt.BCrypt;

public class Usuario {
    
    private String correo;
    private String clave; // Almacena el HASH de BCrypt
    private String rol;
    
    // --- NUEVOS CAMPOS ---
    private String nombre;
    private String apellido;
    private String numero;
    private String direccion;

    // Constructor vacío (para Jackson)
    public Usuario() {
    }

    // --- CONSTRUCTOR ACTUALIZADO ---
    public Usuario(String correo, String clavePlana, String rol, 
                   String nombre, String apellido, String numero, String direccion) {
        
        this.correo = correo;
        this.setClavePlana(clavePlana); 
        this.rol = rol;
        this.nombre = nombre;
        this.apellido = apellido;
        this.numero = numero;
        this.direccion = direccion;
    }

    // --- Getters y Setters (Existentes) ---
    
    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String claveHash) {
        this.clave = claveHash;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    // --- Getters y Setters (Nuevos) ---

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    // --- Lógica de Negocio (Existente) ---

    public void setClavePlana(String clavePlana) {
        this.clave = BCrypt.hashpw(clavePlana, BCrypt.gensalt());
    }

    public boolean verificarClave(String claveCandidata) {
        if (claveCandidata == null || this.clave == null || this.clave.isEmpty()) {
            return false;
        }
        try {
            return BCrypt.checkpw(claveCandidata, this.clave);
        } catch (IllegalArgumentException e) {
            System.err.println("Error al verificar clave (hash malformado?): " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public String toString() {
        return "Usuario{" +
                "correo='" + correo + '\'' +
                ", rol='" + rol + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                '}';
    }
}