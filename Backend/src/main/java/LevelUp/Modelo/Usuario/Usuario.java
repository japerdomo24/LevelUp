package LevelUp.Modelo.Usuario;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Representa a un usuario del sistema.
 * La contraseña se almacena internamente como un hash de BCrypt.
 */
public class Usuario {
    
    private String correo;
    private String clave; // Almacena el HASH de BCrypt
    private String rol;

    // --- Constructores ---
    
    /**
     * Constructor vacío.
     * ¡REQUERIDO por Jackson para la deserialización!
     */
    public Usuario() {
    }

    /**
     * Constructor para crear un NUEVO usuario desde código.
     * Hashea la clave en texto plano automáticamente.
     */
    public Usuario(String correo, String clavePlana, String rol) {
        this.correo = correo;
        this.setClavePlana(clavePlana); // Hashea la clave al crear
        this.rol = rol;
    }

    // --- Getters y Setters (Usados por Jackson) ---
    
    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    /**
     * Obtiene la clave HASHEADA.
     * Jackson usará este método al *guardar* en JSON.
     */
    public String getClave() {
        return clave;
    }

    /**
     * Establece la clave.
     * Jackson usará este método al *leer* desde JSON.
     * @param claveHash La clave ya hasheada proveniente del JSON.
     */
    public void setClave(String claveHash) {
        this.clave = claveHash;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    // --- Métodos de Lógica de Negocio ---

    /**
     * Método helper para establecer la clave desde texto plano.
     * Genera un nuevo hash y lo guarda.
     */
    public void setClavePlana(String clavePlana) {
        this.clave = BCrypt.hashpw(clavePlana, BCrypt.gensalt());
    }

    /**
     * Compara una contraseña candidata (en texto plano) con el hash
     * almacenado en este objeto.
     */
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

    // --- Método toString (Opcional para depuración) ---
    
    @Override
    public String toString() {
        return "Usuario{" +
                "correo='" + correo + '\'' +
                ", clave='[HASHEADA]'" + 
                ", rol='" + rol + '\'' +
                '}';
    }
}