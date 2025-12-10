package LevelUp.Modelo;

import java.util.List;
import java.util.UUID;

public class Proveedor {

    private String id;
    private String rif;
    private String nombre;
    private String telefono;
    private String correo;
    private String direccion;
    private List<String> facturas; // Lista de Nros de factura/orden

    // Constructor vacío para Jackson
    public Proveedor() {
    }

    // Constructor para crear un nuevo proveedor
    public Proveedor(String rif, String nombre, String telefono, String correo, String direccion, List<String> facturas) {
        this.id = UUID.randomUUID().toString();
        this.rif = rif;
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
        this.facturas = facturas;
    }

    // --- Getters y Setters ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRif() {
        return rif;
    }

    public void setRif(String rif) {
        this.rif = rif;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public List<String> getFacturas() {
        return facturas;
    }

    public void setFacturas(List<String> facturas) {
        this.facturas = facturas;
    }
}