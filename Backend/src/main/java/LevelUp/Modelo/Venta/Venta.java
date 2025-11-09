package LevelUp.Modelo.Venta;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class Venta {

    private String id;
    private String fecha; // Guardamos la fecha/hora en formato ISO (ej: "2025-11-08T18:30:00Z")
    private String correoUsuario;
    private List<ItemVenta> items;
    private double totalVenta;

    // Constructor vacío para Jackson
    public Venta() {
    }

    // Constructor para crear una nueva venta
    public Venta(String correoUsuario, List<ItemVenta> items, double totalVenta) {
        this.id = UUID.randomUUID().toString();
        this.fecha = Instant.now().toString(); // Captura la fecha y hora actual
        this.correoUsuario = correoUsuario;
        this.items = items;
        this.totalVenta = totalVenta;
    }

    // --- Getters y Setters ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getCorreoUsuario() {
        return correoUsuario;
    }

    public void setCorreoUsuario(String correoUsuario) {
        this.correoUsuario = correoUsuario;
    }

    public List<ItemVenta> getItems() {
        return items;
    }

    public void setItems(List<ItemVenta> items) {
        this.items = items;
    }

    public double getTotalVenta() {
        return totalVenta;
    }

    public void setTotalVenta(double totalVenta) {
        this.totalVenta = totalVenta;
    }
}