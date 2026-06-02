package com.example.template.network.models;

import com.google.gson.annotations.SerializedName;

public class PermisosRoles {
    @SerializedName("role")
    private String role;

    @SerializedName("sucursales_ver")
    private boolean sucursalesVer;
    @SerializedName("sucursales_crear")
    private boolean sucursalesCrear;
    @SerializedName("sucursales_editar")
    private boolean sucursalesEditar;
    @SerializedName("sucursales_eliminar")
    private boolean sucursalesEliminar;

    @SerializedName("catalogo_ver")
    private boolean catalogoVer;
    @SerializedName("catalogo_crear")
    private boolean catalogoCrear;
    @SerializedName("catalogo_editar")
    private boolean catalogoEditar;
    @SerializedName("catalogo_eliminar")
    private boolean catalogoEliminar;

    @SerializedName("proveedores_ver")
    private boolean proveedoresVer;
    @SerializedName("proveedores_crear")
    private boolean proveedoresCrear;
    @SerializedName("proveedores_editar")
    private boolean proveedoresEditar;
    @SerializedName("proveedores_eliminar")
    private boolean proveedoresEliminar;

    @SerializedName("sourcing_ver")
    private boolean sourcingVer;
    @SerializedName("sourcing_crear")
    private boolean sourcingCrear;
    @SerializedName("sourcing_editar")
    private boolean sourcingEditar;
    @SerializedName("sourcing_eliminar")
    private boolean sourcingEliminar;

    @SerializedName("inventario_ver")
    private boolean inventarioVer;
    @SerializedName("inventario_crear")
    private boolean inventarioCrear;
    @SerializedName("inventario_editar")
    private boolean inventarioEditar;
    @SerializedName("inventario_eliminar")
    private boolean inventarioEliminar;

    @SerializedName("usuarios_ver")
    private boolean usuariosVer;
    @SerializedName("usuarios_crear")
    private boolean usuariosCrear;
    @SerializedName("usuarios_editar")
    private boolean usuariosEditar;
    @SerializedName("usuarios_eliminar")
    private boolean usuariosEliminar;

    @SerializedName("ventas_ver")
    private boolean ventasVer;
    @SerializedName("ventas_crear")
    private boolean ventasCrear;
    @SerializedName("ventas_editar")
    private boolean ventasEditar;
    @SerializedName("ventas_eliminar")
    private boolean ventasEliminar;

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isSucursalesVer() { return sucursalesVer; }
    public void setSucursalesVer(boolean sucursalesVer) { this.sucursalesVer = sucursalesVer; }
    public boolean isSucursalesCrear() { return sucursalesCrear; }
    public void setSucursalesCrear(boolean sucursalesCrear) { this.sucursalesCrear = sucursalesCrear; }
    public boolean isSucursalesEditar() { return sucursalesEditar; }
    public void setSucursalesEditar(boolean sucursalesEditar) { this.sucursalesEditar = sucursalesEditar; }
    public boolean isSucursalesEliminar() { return sucursalesEliminar; }
    public void setSucursalesEliminar(boolean sucursalesEliminar) { this.sucursalesEliminar = sucursalesEliminar; }

    public boolean isCatalogoVer() { return catalogoVer; }
    public void setCatalogoVer(boolean catalogoVer) { this.catalogoVer = catalogoVer; }
    public boolean isCatalogoCrear() { return catalogoCrear; }
    public void setCatalogoCrear(boolean catalogoCrear) { this.catalogoCrear = catalogoCrear; }
    public boolean isCatalogoEditar() { return catalogoEditar; }
    public void setCatalogoEditar(boolean catalogoEditar) { this.catalogoEditar = catalogoEditar; }
    public boolean isCatalogoEliminar() { return catalogoEliminar; }
    public void setCatalogoEliminar(boolean catalogoEliminar) { this.catalogoEliminar = catalogoEliminar; }

    public boolean isProveedoresVer() { return proveedoresVer; }
    public void setProveedoresVer(boolean proveedoresVer) { this.proveedoresVer = proveedoresVer; }
    public boolean isProveedoresCrear() { return proveedoresCrear; }
    public void setProveedoresCrear(boolean proveedoresCrear) { this.proveedoresCrear = proveedoresCrear; }
    public boolean isProveedoresEditar() { return proveedoresEditar; }
    public void setProveedoresEditar(boolean proveedoresEditar) { this.proveedoresEditar = proveedoresEditar; }
    public boolean isProveedoresEliminar() { return proveedoresEliminar; }
    public void setProveedoresEliminar(boolean proveedoresEliminar) { this.proveedoresEliminar = proveedoresEliminar; }

    public boolean isSourcingVer() { return sourcingVer; }
    public void setSourcingVer(boolean sourcingVer) { this.sourcingVer = sourcingVer; }
    public boolean isSourcingCrear() { return sourcingCrear; }
    public void setSourcingCrear(boolean sourcingCrear) { this.sourcingCrear = sourcingCrear; }
    public boolean isSourcingEditar() { return sourcingEditar; }
    public void setSourcingEditar(boolean sourcingEditar) { this.sourcingEditar = sourcingEditar; }
    public boolean isSourcingEliminar() { return sourcingEliminar; }
    public void setSourcingEliminar(boolean sourcingEliminar) { this.sourcingEliminar = sourcingEliminar; }

    public boolean isInventarioVer() { return inventarioVer; }
    public void setInventarioVer(boolean inventarioVer) { this.inventarioVer = inventarioVer; }
    public boolean isInventarioCrear() { return inventarioCrear; }
    public void setInventarioCrear(boolean inventarioCrear) { this.inventarioCrear = inventarioCrear; }
    public boolean isInventarioEditar() { return inventarioEditar; }
    public void setInventarioEditar(boolean inventarioEditar) { this.inventarioEditar = inventarioEditar; }
    public boolean isInventarioEliminar() { return inventarioEliminar; }
    public void setInventarioEliminar(boolean inventarioEliminar) { this.inventarioEliminar = inventarioEliminar; }

    public boolean isUsuariosVer() { return usuariosVer; }
    public void setUsuariosVer(boolean usuariosVer) { this.usuariosVer = usuariosVer; }
    public boolean isUsuariosCrear() { return usuariosCrear; }
    public void setUsuariosCrear(boolean usuariosCrear) { this.usuariosCrear = usuariosCrear; }
    public boolean isUsuariosEditar() { return usuariosEditar; }
    public void setUsuariosEditar(boolean usuariosEditar) { this.usuariosEditar = usuariosEditar; }
    public boolean isUsuariosEliminar() { return usuariosEliminar; }
    public void setUsuariosEliminar(boolean usuariosEliminar) { this.usuariosEliminar = usuariosEliminar; }

    public boolean isVentasVer() { return ventasVer; }
    public void setVentasVer(boolean ventasVer) { this.ventasVer = ventasVer; }
    public boolean isVentasCrear() { return ventasCrear; }
    public void setVentasCrear(boolean ventasCrear) { this.ventasCrear = ventasCrear; }
    public boolean isVentasEditar() { return ventasEditar; }
    public void setVentasEditar(boolean ventasEditar) { this.ventasEditar = ventasEditar; }
    public boolean isVentasEliminar() { return ventasEliminar; }
    public void setVentasEliminar(boolean ventasEliminar) { this.ventasEliminar = ventasEliminar; }

    // Helper methods for backwards compatibility with other fragments
    public boolean isSucursalesGestionar() { return sucursalesCrear || sucursalesEditar || sucursalesEliminar; }
    public boolean isCatalogoGestionar() { return catalogoCrear || catalogoEditar || catalogoEliminar; }
    public boolean isProveedoresGestionar() { return proveedoresCrear || proveedoresEditar || proveedoresEliminar; }
    public boolean isSourcingGestionar() { return sourcingCrear || sourcingEditar || sourcingEliminar; }
    public boolean isInventarioGestionar() { return inventarioCrear || inventarioEditar || inventarioEliminar; }
    public boolean isUsuariosGestionar() { return usuariosCrear || usuariosEditar || usuariosEliminar; }
    public boolean isVentasGestionar() { return ventasCrear || ventasEditar || ventasEliminar; }
}
