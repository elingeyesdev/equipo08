package com.example.template.network.models;

public class RegisterRequest {
    private String name;
    private String domain;
    private String email;
    private String password;
    private String phone;
    private String ubicacion;
    private String nit;
    private String razonSocial;

    public RegisterRequest(String name, String domain, String email, String password, String phone, String ubicacion, String nit, String razonSocial) {
        this.name = name;
        this.domain = domain;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.ubicacion = ubicacion;
        this.nit = nit;
        this.razonSocial = razonSocial;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public String getNit() { return nit; }
    public void setNit(String nit) { this.nit = nit; }
    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }
}
