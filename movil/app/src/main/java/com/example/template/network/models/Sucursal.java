package com.example.template.network.models;

public class Sucursal {
    private String id;
    private String name;
    private String address;
    private String phone;
    @com.google.gson.annotations.SerializedName("isActive")
    private boolean isActive;

    private String horarios;

    public Sucursal(String name, String address, String phone, boolean isActive) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.isActive = isActive;
        this.horarios = null;
    }

    public Sucursal(String name, String address, String phone, boolean isActive, String horarios) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.isActive = isActive;
        this.horarios = horarios;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public boolean isActive() { return isActive; }
    public String getHorarios() { return horarios; }

    @Override
    public String toString() {
        return name; 
    }
}
