package com.example.template.network.models;

import com.google.gson.annotations.SerializedName;

public class LoteIngreso {
    private String id;
    private String sucursal_id;
    private String producto_id;
    private String proveedor_id;
    private int cantidad;
    private String fechaIngreso;
    private String fechaVencimiento;
    
    @SerializedName("fechaElaboracion")
    private String fechaProduccion;
    
    private Producto producto;
    private Proveedor proveedor;

    private Double costoUnitario;

    public LoteIngreso(String sucursal_id, String producto_id, String proveedor_id, int cantidad, String fechaVencimiento, String fechaProduccion) {
        this.sucursal_id = sucursal_id;
        this.producto_id = producto_id;
        this.proveedor_id = proveedor_id;
        this.cantidad = cantidad;
        this.fechaVencimiento = fechaVencimiento;
        this.fechaProduccion = fechaProduccion;
    }

    public Double getCostoUnitario() { return costoUnitario; }
    public void setCostoUnitario(Double costoUnitario) { this.costoUnitario = costoUnitario; }

    public String getId() { return id; }
    public String getSucursalId() { return sucursal_id; }
    public String getProductoId() { return producto_id; }
    public String getProveedorId() { return proveedor_id; }
    public int getCantidad() { return cantidad; }
    public String getFechaIngreso() { return fechaIngreso; }
    public String getFechaVencimiento() { return fechaVencimiento; }
    public String getFechaProduccion() { return fechaProduccion; }
    public Producto getProducto() { return producto; }
    public Proveedor getProveedor() { return proveedor; }
}
