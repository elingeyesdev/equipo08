package com.example.template.network.models;

import com.google.gson.annotations.SerializedName;

public class Empleado {
    private String id;
    
    @SerializedName("name")
    private String nombreCompleto;
    
    @SerializedName("email")
    private String correo;
    
    @SerializedName("password")
    private String password;
    
    @SerializedName("role")
    private String rol;
    
    @SerializedName("sucursal_id")
    private String sucursalId;
    
    @SerializedName("sucursal")
    private Sucursal nestedSucursal;
    
    @SerializedName("isActive")
    private boolean isActive;

    public Empleado() {}

    public Empleado(String nombreCompleto, String correo, String password, String rol, String sucursalId) {
        this.nombreCompleto = nombreCompleto;
        this.correo = correo;
        this.password = password;
        this.rol = rol;
        this.sucursalId = sucursalId;
        this.isActive = true;
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
    
    public String getSucursalNombre() { 
        return nestedSucursal != null ? nestedSucursal.getName() : null; 
    }
    public void setSucursalNombre(String sucursalNombre) { 
        if (this.nestedSucursal == null) {
            this.nestedSucursal = new Sucursal(sucursalNombre, null, null, true);
        }
    }
    
    public String getEstado() { return isActive ? "Activo" : "Inactivo"; }
    public void setEstado(String estado) { this.isActive = "Activo".equalsIgnoreCase(estado); }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
