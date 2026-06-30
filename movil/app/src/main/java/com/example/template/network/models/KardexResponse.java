package com.example.template.network.models;

import java.util.Map;

public class KardexResponse {
    private String id;
    private String fecha;
    private String tipo;
    private int cantidadDelta;
    private int stockAnterior;
    private int stockResultante;
    private double costoUnitario;
    private String motivo;
    private String usuarioNombre;
    private String referenciaTipo;
    private String referenciaId;
    private String sucursalId;
    private String sucursalNombre;
    private Map<String, String> variacionDetalle;
    private String sku;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public int getCantidadDelta() { return cantidadDelta; }
    public void setCantidadDelta(int cantidadDelta) { this.cantidadDelta = cantidadDelta; }

    public int getStockAnterior() { return stockAnterior; }
    public void setStockAnterior(int stockAnterior) { this.stockAnterior = stockAnterior; }

    public int getStockResultante() { return stockResultante; }
    public void setStockResultante(int stockResultante) { this.stockResultante = stockResultante; }

    public double getCostoUnitario() { return costoUnitario; }
    public void setCostoUnitario(double costoUnitario) { this.costoUnitario = costoUnitario; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getUsuarioNombre() { return usuarioNombre; }
    public void setUsuarioNombre(String usuarioNombre) { this.usuarioNombre = usuarioNombre; }

    public String getReferenciaTipo() { return referenciaTipo; }
    public void setReferenciaTipo(String referenciaTipo) { this.referenciaTipo = referenciaTipo; }

    public String getReferenciaId() { return referenciaId; }
    public void setReferenciaId(String referenciaId) { this.referenciaId = referenciaId; }

    public String getSucursalId() { return sucursalId; }
    public void setSucursalId(String sucursalId) { this.sucursalId = sucursalId; }

    public String getSucursalNombre() { return sucursalNombre; }
    public void setSucursalNombre(String sucursalNombre) { this.sucursalNombre = sucursalNombre; }

    public Map<String, String> getVariacionDetalle() { return variacionDetalle; }
    public void setVariacionDetalle(Map<String, String> variacionDetalle) { this.variacionDetalle = variacionDetalle; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
}
