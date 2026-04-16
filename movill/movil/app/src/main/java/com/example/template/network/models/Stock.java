package com.example.template.network.models;

public class Stock {
    private String id;
    private String producto_id;
    private int cantidadTotal;
    private String ultimaActualizacion;
    
    private Producto producto;

    public String getId() { return id; }
    public String getProductoId() { return producto_id; }
    public int getCantidadTotal() { return cantidadTotal; }
    public String getUltimaActualizacion() { return ultimaActualizacion; }
    public Producto getProducto() { return producto; }
}
