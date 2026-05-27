package com.example.template.network.models;

public class VentaItem {
    private String producto_id;
    private int cantidad;

    public VentaItem(String producto_id, int cantidad) {
        this.producto_id = producto_id;
        this.cantidad = cantidad;
    }

    public String getProductoId() { return producto_id; }
    public void setProductoId(String producto_id) { this.producto_id = producto_id; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
}
