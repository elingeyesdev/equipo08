package com.example.template.network.models;

import com.google.gson.annotations.SerializedName;

public class TrasladoRequest {

    @SerializedName("from_sucursal_id")
    private String fromSucursalId;

    @SerializedName("to_sucursal_id")
    private String toSucursalId;

    @SerializedName("producto_id")
    private String productoId;

    @SerializedName("cantidad")
    private int cantidad;

    public TrasladoRequest(String fromSucursalId, String toSucursalId, String productoId, int cantidad) {
        this.fromSucursalId = fromSucursalId;
        this.toSucursalId = toSucursalId;
        this.productoId = productoId;
        this.cantidad = cantidad;
    }

    public String getFromSucursalId() {
        return fromSucursalId;
    }

    public String getToSucursalId() {
        return toSucursalId;
    }

    public String getProductoId() {
        return productoId;
    }

    public int getCantidad() {
        return cantidad;
    }
}
