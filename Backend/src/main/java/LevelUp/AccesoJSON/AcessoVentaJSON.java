package LevelUp.AccesoJSON;

import LevelUp.Modelo.Venta;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestiona la lectura y escritura de objetos Venta
 * hacia y desde un archivo JSON.
 */
public class AcessoVentaJSON {

    private ObjectMapper mapper;
    private String rutaArchivo;

    public AcessoVentaJSON(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * Lee el archivo JSON y lo convierte en una Lista de Ventas.
     */
    public List<Venta> leerVentas() {
        try {
            File archivo = new File(rutaArchivo);
            if (!archivo.exists() || archivo.length() == 0) {
                return new ArrayList<>();
            }
            
            return mapper.readValue(
                archivo,
                new TypeReference<List<Venta>>() {} // <-- Tipo Venta
            );
            
        } catch (IOException e) {
            System.err.println("Error al leer el archivo JSON de ventas: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Guarda (sobrescribe) una lista de ventas en el archivo JSON.
     */
    public boolean guardarVentas(List<Venta> listaDeVentas) {
        try {
            mapper.writeValue(new File(rutaArchivo), listaDeVentas);
            return true; 
        } catch (IOException e) {
            System.err.println("Error al guardar el archivo JSON de ventas: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}