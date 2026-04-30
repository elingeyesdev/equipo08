package com.example.template.network.models;

public class Stock {
    private String id;
    private String producto_id;
    private String sucursal_id;
    private int cantidadTotal;
    private double valorAdquisicion;
    private String ultimaActualizacion;
    
    private Producto producto;
    private Sucursal sucursal;

    public String getId() { return id; }
    public String getProductoId() { return producto_id; }
    public String getSucursalId() { return sucursal_id; }
    public int getCantidadTotal() { return cantidadTotal; }
    public double getValorAdquisicion() { return valorAdquisicion; }
    public String getUltimaActualizacion() { return ultimaActualizacion; }
    public Producto getProducto() { return producto; }
    public Sucursal getSucursal() { return sucursal; }
}
