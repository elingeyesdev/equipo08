package com.example.template.network.models;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class PermisosRoles {
    @SerializedName("rol")
    private String rol;

    @SerializedName("permisos")
    private Map<String, Boolean> permisos;

    public PermisosRoles(String rol, Map<String, Boolean> permisos) {
        this.rol = rol;
        this.permisos = permisos;
    }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public Map<String, Boolean> getPermisos() { return permisos; }
    public void setPermisos(Map<String, Boolean> permisos) { this.permisos = permisos; }
}
