package com.example.template.network.models;

public class Producto {
    private String id;
    private String name;
    private String sku;
    private String proveedor_id;
    private String category;
    private double precioCosto;
    private double precioVenta;
    private Proveedor proveedor; 
    private String description;

    public Producto(String name, String sku, String category, double precioCosto, double precioVenta, String proveedor_id, String description) {
        this.name = name;
        this.sku = sku;
        this.category = category;
        this.precioCosto = precioCosto;
        this.precioVenta = precioVenta;
        this.proveedor_id = proveedor_id;
        this.description = description;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getSku() { return sku; }
    public String getProveedorId() { return proveedor_id; }
    public String getCategory() { return category; }
    public double getPrecioCosto() { return precioCosto; }
    public double getPrecioVenta() { return precioVenta; }
    public Proveedor getProveedor() { return proveedor; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        if (description != null && !description.isEmpty()) {
            return name + " (" + description + ")";
        }
        return name; 
    }
}
