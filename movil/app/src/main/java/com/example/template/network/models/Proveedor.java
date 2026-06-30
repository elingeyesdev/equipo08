package com.example.template.network.models;

public class Proveedor {
    private String id;
    private String name;
    private String contactEmail;
    private String taxId;
    private String phone;

    public Proveedor(String name, String contactEmail, String taxId, String phone) {
        this.name = name;
        this.contactEmail = contactEmail;
        this.taxId = taxId;
        this.phone = phone;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public String getContactEmail() { return contactEmail; }
    public String getTaxId() { return taxId; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    @Override
    public String toString() {
        return name; 
    }
}
