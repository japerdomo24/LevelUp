package LevelUp.Modelo;

import java.util.ArrayList;
import java.util.List;

public class Carrito {

    private String correoUsuario;
    private List<ItemCarrito> items;
    private double total;

    // Constructor vacío
    public Carrito() {
        this.items = new ArrayList<>();
        this.total = 0.0;
    }

    // Constructor para un nuevo carrito
    public Carrito(String correoUsuario) {
        this.correoUsuario = correoUsuario;
        this.items = new ArrayList<>();
        this.total = 0.0;
    }

    // --- Getters y Setters ---

    public String getCorreoUsuario() {
        return correoUsuario;
    }

    public void setCorreoUsuario(String correoUsuario) {
        this.correoUsuario = correoUsuario;
    }

    public List<ItemCarrito> getItems() {
        return items;
    }

    public void setItems(List<ItemCarrito> items) {
        this.items = items;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    // --- Métodos de Lógica del Carrito ---

    /**
     * Calcula y actualiza el precio total del carrito.
     */
    public void calcularTotal() {
        this.total = this.items.stream()
                .mapToDouble(item -> item.getPrecioUnitario() * item.getCantidad())
                .sum();
    }
}