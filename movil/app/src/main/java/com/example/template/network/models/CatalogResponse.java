package com.example.template.network.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CatalogResponse {
    @SerializedName("tienda")
    private Tienda tienda;

    @SerializedName("productos")
    private List<CatalogProducto> productos;

    public CatalogResponse(Tienda tienda, List<CatalogProducto> productos) {
        this.tienda = tienda;
        this.productos = productos;
    }

    public Tienda getTienda() { return tienda; }
    public List<CatalogProducto> getProductos() { return productos; }
}
