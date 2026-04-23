package com.example.template.network.models;

import com.google.gson.annotations.SerializedName;

public class Empleado {
    private String id;
    
    @SerializedName("nombreCompleto")
    private String nombreCompleto;
    
    @SerializedName("correo")
    private String correo;
    
    @SerializedName("password")
    private String password;
    
    @SerializedName("rol")
    private String rol;
    
    @SerializedName("sucursalId")
    private String sucursalId;
    
    @SerializedName("sucursalNombre")
    private String sucursalNombre;
    
    @SerializedName("estado")
    private String estado;

    public Empleado() {}

    public Empleado(String nombreCompleto, String correo, String password, String rol, String sucursalId) {
        this.nombreCompleto = nombreCompleto;
        this.correo = correo;
        this.password = password;
        this.rol = rol;
        this.sucursalId = sucursalId;
        this.estado = "Activo";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    
    public String getSucursalId() { return sucursalId; }
    public void setSucursalId(String sucursalId) { this.sucursalId = sucursalId; }
    
    public String getSucursalNombre() { return sucursalNombre; }
    public void setSucursalNombre(String sucursalNombre) { this.sucursalNombre = sucursalNombre; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
