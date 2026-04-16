package com.example.template.network.models;

public class Producto {
    private String id;
    private String name;
    private String sku;
    private String proveedor_id;
    private Proveedor proveedor; 

    public Producto(String name, String sku, String proveedor_id) {
        this.name = name;
        this.sku = sku;
        this.proveedor_id = proveedor_id;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getSku() { return sku; }
    public String getProveedorId() { return proveedor_id; }
    public Proveedor getProveedor() { return proveedor; }

    @Override
    public String toString() {
        return name; 
    }
}
