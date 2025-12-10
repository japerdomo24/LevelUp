package LevelUp.AccesoJSON;

import LevelUp.Modelo.Proveedor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestiona la lectura y escritura de objetos Proveedor
 * hacia y desde un archivo JSON.
 */
public class AcessoProveedorJSON {

    private ObjectMapper mapper;
    private String rutaArchivo;

    public AcessoProveedorJSON(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * Lee el archivo JSON y lo convierte en una Lista de Proveedores.
     */
    public List<Proveedor> leerProveedores() {
        try {
            File archivo = new File(rutaArchivo);
            if (!archivo.exists() || archivo.length() == 0) {
                return new ArrayList<>();
            }
            
            return mapper.readValue(
                archivo,
                new TypeReference<List<Proveedor>>() {} // <-- Tipo Proveedor
            );
            
        } catch (IOException e) {
            System.err.println("Error al leer el archivo JSON de proveedores: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Guarda (sobrescribe) una lista de proveedores en el archivo JSON.
     */
    public boolean guardarProveedores(List<Proveedor> listaDeProveedores) {
        try {
            mapper.writeValue(new File(rutaArchivo), listaDeProveedores);
            return true; 
        } catch (IOException e) {
            System.err.println("Error al guardar el archivo JSON de proveedores: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}