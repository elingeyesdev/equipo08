package com.example.template.network.models;

public class Stock {
    private String id;
    private String producto_id;
    private String sucursal_id;
    private int cantidadTotal;
    private double valorAdquisicion;
    private String ultimaActualizacion;
    private double costoPromedio;
    
    private Producto producto;
    private Sucursal sucursal;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProductoId() { return producto_id; }
    public void setProductoId(String producto_id) { this.producto_id = producto_id; }

    public String getSucursalId() { return sucursal_id; }
    public void setSucursalId(String sucursal_id) { this.sucursal_id = sucursal_id; }

    public int getCantidadTotal() { return cantidadTotal; }
    public void setCantidadTotal(int cantidadTotal) { this.cantidadTotal = cantidadTotal; }

    public double getValorAdquisicion() { return valorAdquisicion; }
    public void setValorAdquisicion(double valorAdquisicion) { this.valorAdquisicion = valorAdquisicion; }

    public String getUltimaActualizacion() { return ultimaActualizacion; }
    public void setUltimaActualizacion(String ultimaActualizacion) { this.ultimaActualizacion = ultimaActualizacion; }

    public double getCostoPromedio() { return costoPromedio; }
    public void setCostoPromedio(double costoPromedio) { this.costoPromedio = costoPromedio; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Sucursal getSucursal() { return sucursal; }
    public void setSucursal(Sucursal sucursal) { this.sucursal = sucursal; }
}
