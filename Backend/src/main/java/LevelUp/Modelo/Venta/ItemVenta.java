package LevelUp.Modelo.Venta;

public class ItemVenta {
    private String productoId;
    private String serie;
    private int cantidad;
    private double precioUnitario;

    // Constructor vacío para Jackson
    public ItemVenta() {
    }

    public ItemVenta(String productoId, String serie, int cantidad, double precioUnitario) {
        this.productoId = productoId;
        this.serie = serie;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    // --- Getters y Setters ---

    public String getProductoId() {
        return productoId;
    }

    public void setProductoId(String productoId) {
        this.productoId = productoId;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
}