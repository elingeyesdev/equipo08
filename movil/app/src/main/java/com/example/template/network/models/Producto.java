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
    private int stockMinimo;

    @com.google.gson.annotations.SerializedName("imagen_url")
    private String imagenUrl;

    @com.google.gson.annotations.SerializedName("attributes")
    private java.util.Map<String, String> attributes;

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
    public void setDescription(String description) { this.description = description; }
    public int getStockMinimo() { return stockMinimo <= 0 ? 10 : stockMinimo; }
    public void setStockMinimo(int stockMinimo) { this.stockMinimo = stockMinimo; }
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    public java.util.Map<String, String> getAttributes() { return attributes; }
    public void setAttributes(java.util.Map<String, String> attributes) { this.attributes = attributes; }

    @Override
    public String toString() {
        if (description != null && !description.isEmpty()) {
            return name + " (" + description + ")";
        }
        return name; 
    }
}
