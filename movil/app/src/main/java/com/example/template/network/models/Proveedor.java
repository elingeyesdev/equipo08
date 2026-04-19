package com.example.template.network.models;

public class Proveedor {
    private String id;
    private String name;
    private String contactEmail;
    private String taxId;

    public Proveedor(String name, String contactEmail, String taxId) {
        this.name = name;
        this.contactEmail = contactEmail;
        this.taxId = taxId;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getContactEmail() { return contactEmail; }
    public String getTaxId() { return taxId; }

    @Override
    public String toString() {
        return name; // El Spinner mostrará esto
    }
}
