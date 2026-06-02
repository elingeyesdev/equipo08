package com.example.template.network.models;

public class CatalogCartItem {
    private CatalogProducto product;
    private int quantity;

    public CatalogCartItem(CatalogProducto product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public CatalogProducto getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
