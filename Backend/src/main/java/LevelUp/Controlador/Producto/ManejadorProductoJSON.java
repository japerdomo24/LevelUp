package LevelUp.Controlador.Producto;

import LevelUp.Modelo.Producto.Producto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestiona la lectura y escritura de objetos Producto
 * hacia y desde un archivo JSON.
 */
public class ManejadorProductoJSON {

    private ObjectMapper mapper;
    private String rutaArchivo;

    /**
     * Constructor
     * @param rutaArchivo La ruta al archivo JSON (ej. "src/main/java/LevelUp/Data/productos.json").
     */
    public ManejadorProductoJSON(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * Lee el archivo JSON y lo convierte en una Lista de Productos.
     */
    public List<Producto> leerProductos() {
        try {
            File archivo = new File(rutaArchivo);
            if (!archivo.exists() || archivo.length() == 0) {
                return new ArrayList<>();
            }
            
            return mapper.readValue(
                archivo,
                new TypeReference<List<Producto>>() {} // <-- Tipo Producto
            );
            
        } catch (IOException e) {
            System.err.println("Error al leer el archivo JSON de productos: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Guarda (sobrescribe) una lista de productos en el archivo JSON.
     */
    public boolean guardarProductos(List<Producto> listaDeProductos) {
        try {
            mapper.writeValue(new File(rutaArchivo), listaDeProductos);
            return true; // Éxito

        } catch (IOException e) {
            System.err.println("Error al guardar el archivo JSON de productos: " + e.getMessage());
            e.printStackTrace();
            return false; // Falla
        }
    }
}