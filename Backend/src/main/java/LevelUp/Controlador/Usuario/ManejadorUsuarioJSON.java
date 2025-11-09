package LevelUp.Controlador.Usuario;

import LevelUp.Modelo.Usuario.Usuario;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestiona la lectura (deserialización) y escritura (serialización)
 * de objetos Usuario hacia y desde un archivo JSON.
 */
public class ManejadorUsuarioJSON {

    private ObjectMapper mapper;
    private String rutaArchivo;

    /**
     * Constructor que inicializa el manejador para un archivo específico.
     * @param rutaArchivo La ruta al archivo JSON (ej. "src/main/java/LevelUp/Data/usuarios.json").
     */
    public ManejadorUsuarioJSON(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
        this.mapper = new ObjectMapper();
        
        // Configura Jackson para que el JSON guardado sea legible (indentado)
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * Lee el archivo JSON especificado en el constructor y lo convierte 
     * en una Lista de Usuarios.
     */
    public List<Usuario> leerUsuarios() {
        try {
            File archivo = new File(rutaArchivo);
            if (!archivo.exists() || archivo.length() == 0) {
                // Si el archivo no existe o está vacío, devuelve lista vacía
                return new ArrayList<>();
            }
            
            return mapper.readValue(
                archivo,
                new TypeReference<List<Usuario>>() {}
            );
            
        } catch (IOException e) {
            System.err.println("Error al leer el archivo JSON (" + rutaArchivo + "): " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Guarda (o sobrescribe) una lista de usuarios en el archivo JSON
     * especificado en el constructor.
     */
    public boolean guardarUsuarios(List<Usuario> listaDeUsuarios) {
        try {
            mapper.writeValue(new File(rutaArchivo), listaDeUsuarios);
            return true; // Éxito

        } catch (IOException e) {
            System.err.println("Error al guardar el archivo JSON (" + rutaArchivo + "): " + e.getMessage());
            e.printStackTrace();
            return false; // Falla
        }
    }
}