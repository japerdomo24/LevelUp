package LevelUp.AccesoJSON;

import LevelUp.Modelo.Carrito;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestiona la lectura y escritura de una LISTA de Carritos
 * hacia y desde un archivo JSON.
 */
public class AcessoCarritoJSON {

    private ObjectMapper mapper;
    private String rutaArchivo;

    public AcessoCarritoJSON(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * Lee el archivo JSON y lo convierte en una Lista de Carritos.
     */
    public List<Carrito> leerCarritos() {
        try {
            File archivo = new File(rutaArchivo);
            if (!archivo.exists() || archivo.length() == 0) {
                return new ArrayList<>();
            }
            
            return mapper.readValue(
                archivo,
                new TypeReference<List<Carrito>>() {} 
            );
            
        } catch (IOException e) {
            System.err.println("Error al leer el archivo JSON de carritos: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Guarda (sobrescribe) una lista de carritos en el archivo JSON.
     */
    public boolean guardarCarritos(List<Carrito> listaDeCarritos) {
        try {
            mapper.writeValue(new File(rutaArchivo), listaDeCarritos);
            return true; 
        } catch (IOException e) {
            System.err.println("Error al guardar el archivo JSON de carritos: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}