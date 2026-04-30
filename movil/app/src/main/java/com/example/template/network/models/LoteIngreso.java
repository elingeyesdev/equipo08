package com.example.template.network.models;

public class LoteIngreso {
    private String id;
    private String sucursal_id;
    private String producto_id;
    private String proveedor_id;
    private int cantidad;
    private String fechaIngreso;
    private String fechaVencimiento;
    
    private Producto producto;
    private Proveedor proveedor;

    public LoteIngreso(String sucursal_id, String producto_id, String proveedor_id, int cantidad, String fechaVencimiento) {
        this.sucursal_id = sucursal_id;
        this.producto_id = producto_id;
        this.proveedor_id = proveedor_id;
        this.cantidad = cantidad;
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getId() { return id; }
    public String getSucursalId() { return sucursal_id; }
    public String getProductoId() { return producto_id; }
    public String getProveedorId() { return proveedor_id; }
    public int getCantidad() { return cantidad; }
    public String getFechaIngreso() { return fechaIngreso; }
    public String getFechaVencimiento() { return fechaVencimiento; }
    public Producto getProducto() { return producto; }
    public Proveedor getProveedor() { return proveedor; }
}
