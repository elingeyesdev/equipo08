package com.example.template.network.models;

import com.google.gson.annotations.SerializedName;

public class CatalogProducto {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("sku")
    private String sku;

    @SerializedName("description")
    private String description;

    @SerializedName("category")
    private String category;

    @SerializedName("precioVenta")
    private double precioVenta;

    @SerializedName("imagen_url")
    private String imagenUrl;

    @SerializedName("stockTotal")
    private int stockTotal;

    public CatalogProducto(String id, String name, String sku, String description, String category, double precioVenta, String imagenUrl, int stockTotal) {
        this.id = id;
        this.name = name;
        this.sku = sku;
        this.description = description;
        this.category = category;
        this.precioVenta = precioVenta;
        this.imagenUrl = imagenUrl;
        this.stockTotal = stockTotal;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getSku() { return sku; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public double getPrecioVenta() { return precioVenta; }
    public String getImagenUrl() { return imagenUrl; }
    public int getStockTotal() { return stockTotal; }
}
