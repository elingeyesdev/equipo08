package com.example.template.network.models;

public class DetalleItem {
    private String producto_id;
    private String sku;
    private String name;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;

    public DetalleItem(String producto_id, String sku, String name, int cantidad, double precioUnitario, double subtotal) {
        this.producto_id = producto_id;
        this.sku = sku;
        this.name = name;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }

    public String getProductoId() { return producto_id; }
    public void setProductoId(String producto_id) { this.producto_id = producto_id; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
}
