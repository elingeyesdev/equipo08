package com.example.template.network.models;

import com.google.gson.annotations.SerializedName;

public class PermisosRoles {
    @SerializedName("role")
    private String role;

    @SerializedName("sucursales_ver")
    private boolean sucursalesVer;
    @SerializedName("sucursales_gestionar")
    private boolean sucursalesGestionar;

    @SerializedName("catalogo_ver")
    private boolean catalogoVer;
    @SerializedName("catalogo_gestionar")
    private boolean catalogoGestionar;

    @SerializedName("sourcing_ver")
    private boolean sourcingVer;
    @SerializedName("sourcing_gestionar")
    private boolean sourcingGestionar;

    @SerializedName("inventario_ver")
    private boolean inventarioVer;

    @SerializedName("usuarios_ver")
    private boolean usuariosVer;
    @SerializedName("usuarios_gestionar")
    private boolean usuariosGestionar;

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isSucursalesVer() { return sucursalesVer; }
    public void setSucursalesVer(boolean sucursalesVer) { this.sucursalesVer = sucursalesVer; }

    public boolean isSucursalesGestionar() { return sucursalesGestionar; }
    public void setSucursalesGestionar(boolean sucursalesGestionar) { this.sucursalesGestionar = sucursalesGestionar; }

    public boolean isCatalogoVer() { return catalogoVer; }
    public void setCatalogoVer(boolean catalogoVer) { this.catalogoVer = catalogoVer; }

    public boolean isCatalogoGestionar() { return catalogoGestionar; }
    public void setCatalogoGestionar(boolean catalogoGestionar) { this.catalogoGestionar = catalogoGestionar; }

    public boolean isSourcingVer() { return sourcingVer; }
    public void setSourcingVer(boolean sourcingVer) { this.sourcingVer = sourcingVer; }

    public boolean isSourcingGestionar() { return sourcingGestionar; }
    public void setSourcingGestionar(boolean sourcingGestionar) { this.sourcingGestionar = sourcingGestionar; }

    public boolean isInventarioVer() { return inventarioVer; }
    public void setInventarioVer(boolean inventarioVer) { this.inventarioVer = inventarioVer; }

    public boolean isUsuariosVer() { return usuariosVer; }
    public void setUsuariosVer(boolean usuariosVer) { this.usuariosVer = usuariosVer; }

    public boolean isUsuariosGestionar() { return usuariosGestionar; }
    public void setUsuariosGestionar(boolean usuariosGestionar) { this.usuariosGestionar = usuariosGestionar; }
}
