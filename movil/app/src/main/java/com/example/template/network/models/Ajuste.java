package com.example.template.network.models;

public class Ajuste {
    private String id;
    private String tenant_id;
    private String sucursal_id;
    private String producto_id;
    private String usuario_id;
    private String fecha;
    private int cantidad_sistema;
    private int cantidad_fisica;
    private String motivo;
    private String observaciones;
    private String valor_perdido;
    
    private Sucursal sucursal;
    private Producto producto;
    private Empleado usuario;

    public String getId() { return id; }
    public String getTenantId() { return tenant_id; }
    public String getSucursalId() { return sucursal_id; }
    public String getProductoId() { return producto_id; }
    public String getUsuarioId() { return usuario_id; }
    public String getFecha() { return fecha; }
    public int getCantidadSistema() { return cantidad_sistema; }
    public int getCantidadFisica() { return cantidad_fisica; }
    public String getMotivo() { return motivo; }
    public String getObservaciones() { return observaciones; }
    public String getValorPerdido() { return valor_perdido; }

    public Sucursal getSucursal() { return sucursal; }
    public Producto getProducto() { return producto; }
    public Empleado getUsuario() { return usuario; }
}
