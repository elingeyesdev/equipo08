package com.example.template.network.models;

public class Sucursal {
    private String id;
    private String name;
    private String address;
    private String phone;
    @com.google.gson.annotations.SerializedName("isActive")
    private boolean isActive;

    public Sucursal(String name, String address, String phone, boolean isActive) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.isActive = isActive;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public boolean isActive() { return isActive; }

    @Override
    public String toString() {
        return name; // Useful for potential Spinners
    }
}
