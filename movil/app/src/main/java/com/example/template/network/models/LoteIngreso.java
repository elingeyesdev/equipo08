package com.example.template.network.models;

public class LoteIngreso {
    private String id;
    private String producto_id;
    private String proveedor_id;
    private int cantidad;
    private double costoAdquisicion;
    private String fechaIngreso;
    
    private Producto producto;
    private Proveedor proveedor;

    public LoteIngreso(String producto_id, String proveedor_id, int cantidad, double costoAdquisicion) {
        this.producto_id = producto_id;
        this.proveedor_id = proveedor_id;
        this.cantidad = cantidad;
        this.costoAdquisicion = costoAdquisicion;
    }

    public String getId() { return id; }
    public String getProductoId() { return producto_id; }
    public String getProveedorId() { return proveedor_id; }
    public int getCantidad() { return cantidad; }
    public double getCostoAdquisicion() { return costoAdquisicion; }
    public String getFechaIngreso() { return fechaIngreso; }
    public Producto getProducto() { return producto; }
    public Proveedor getProveedor() { return proveedor; }
}
