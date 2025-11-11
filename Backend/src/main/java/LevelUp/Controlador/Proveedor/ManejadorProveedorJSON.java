package LevelUp.Controlador.Proveedor;


import LevelUp.Modelo.Proveedor.Proveedor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase maneja la lectura y escritura de proveedores en un archivo JSON.
* Funciona como la capa de acceso a datos (una "mini base de datos").
 */
public class ManejadorProveedorJSON {

// Herramienta de Jackson para convertir Java <-> JSON.
    private ObjectMapper mapper;

    // Ruta al archivo JSON (ej: "C:/.../data/proovedores.json").
    private String rutaArchivo;

/**
     * Constructor. Prepara el manejador.
     * @param rutaArchivo La ruta absoluta al archivo JSON.
     */
    public ManejadorProveedorJSON(String rutaArchivo) {
// 1. Guarda la ruta que nos pasaron para usarla después.
        this.rutaArchivo = rutaArchivo;
// 2. Inicializa la herramienta de conversión.
        this.mapper = new ObjectMapper();
// 3. Configura la escritura de JSON para que sea legible (indentada).
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * Lee la lista de proveedores desde el archivo JSON.
     * @return Una List<Proveedor>. Si el archivo no existe o está vacío, devuelve una lista vacía.
     */
    public List<Proveedor> leerProveedores() {
        try {
            File archivo = new File(rutaArchivo);

            // Si el archivo no existe o está vacío, devuelve una lista vacía para evitar errores.
            if (!archivo.exists() || archivo.length() == 0) {
                return new ArrayList<>();
            }
            
// Lee el archivo y lo convierte (mapea) en una Lista de Proveedores.
            return mapper.readValue(
                archivo,
                new TypeReference<List<Proveedor>>() {}
            );
            
        } catch (IOException e) {
            System.err.println("Error al leer el archivo JSON de proveedores: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Guarda (sobrescribe) una lista de proveedores en el archivo JSON.
* @param listaDeProveedores La lista completa de proveedores que se va a guardar.
     * @return true si se guardó con éxito, false si ocurrió un error.
     */
    public boolean guardarProveedores(List<Proveedor> listaDeProveedores) {
        try {

            // --- ¡AQUÍ SE GUARDAN LOS DATOS! ---
            // Esta línea convierte la 'listaDeProveedores' (Java) a texto JSON
            // y sobrescribe el archivo físico en la 'rutaArchivo'.
            mapper.writeValue(new File(rutaArchivo), listaDeProveedores);
            return true; 
        } catch (IOException e) {
            System.err.println("Error al guardar el archivo JSON de proveedores: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}