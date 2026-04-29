package com.example.template.network.models;

public class AjusteRequest {
    private String sucursal_id;
    private String producto_id;
    private int cantidad_sistema;
    private int cantidad_fisica;
    private String motivo;
    private String observaciones;

    public AjusteRequest(String sucursal_id, String producto_id, int cantidad_sistema, int cantidad_fisica, String motivo, String observaciones) {
        this.sucursal_id = sucursal_id;
        this.producto_id = producto_id;
        this.cantidad_sistema = cantidad_sistema;
        this.cantidad_fisica = cantidad_fisica;
        this.motivo = motivo;
        this.observaciones = observaciones;
    }

    public String getSucursalId() { return sucursal_id; }
    public String getProductoId() { return producto_id; }
    public int getCantidadSistema() { return cantidad_sistema; }
    public int getCantidadFisica() { return cantidad_fisica; }
    public String getMotivo() { return motivo; }
    public String getObservaciones() { return observaciones; }
}
