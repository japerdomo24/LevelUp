package LevelUp.Modelo.Producto;

import java.util.List;
import java.util.UUID;

public class Producto {

    private String id;
    private String tipo; // "iPhone", "Mac", "iPad", "Accesorio"
    private String serie; // "iPhone 15", "MacBook Pro M3"
    private String modelo; // "Pro Max", "14-inch"
    private List<String> colores;
    private List<String> capacidades; // "128GB", "256GB"
    private String configSim; // "Dual SIM", "eSIM", "N/A"
    private double precio;
    private int cantidad; // Stock
    private String imagen; // <-- ¡ACTUALIZADO A UNA SOLA IMAGEN (String)!

    /**
     * Constructor vacío (requerido por Jackson)
     */
    public Producto() {
    }

    /**
     * Constructor para crear un nuevo producto.
     * Genera un ID único automáticamente.
     */
    public Producto(String tipo, String serie, String modelo, List<String> colores, 
                    List<String> capacidades, String configSim, double precio, int cantidad,
                    String imagen) { // <-- ACTUALIZADO
        
        this.id = UUID.randomUUID().toString(); // Genera un ID único
        this.tipo = tipo;
        this.serie = serie;
        this.modelo = modelo;
        this.colores = colores;
        this.capacidades = capacidades;
        this.configSim = configSim;
        this.precio = precio;
        this.cantidad = cantidad;
        this.imagen = imagen; // <-- ACTUALIZADO
    }

    // --- Getters y Setters ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public List<String> getColores() {
        return colores;
    }

    public void setColores(List<String> colores) {
        this.colores = colores;
    }

    public List<String> getCapacidades() {
        return capacidades;
    }

    public void setCapacidades(List<String> capacidades) {
        this.capacidades = capacidades;
    }

    public String getConfigSim() {
        return configSim;
    }

    public void setConfigSim(String configSim) {
        this.configSim = configSim;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    // Getter y Setter para la imagen única
    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}