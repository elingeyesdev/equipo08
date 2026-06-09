package com.example.template.network.models;

import java.util.List;

public class Venta {
    private String id;
    private String tenant_id;
    private String sucursal_id;
    private String numeroComprobante;
    private String clienteNombre;
    private String clienteDocumento;
    private String fecha;
    private String metodo_pago;
    private List<DetalleItem> detalle;
    private double total;
    private Sucursal sucursal;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTenantId() { return tenant_id; }
    public void setTenantId(String tenant_id) { this.tenant_id = tenant_id; }

    public String getSucursalId() { return sucursal_id; }
    public void setSucursalId(String sucursal_id) { this.sucursal_id = sucursal_id; }

    public String getNumeroComprobante() { return numeroComprobante; }
    public void setNumeroComprobante(String numeroComprobante) { this.numeroComprobante = numeroComprobante; }

    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }

    public String getClienteDocumento() { return clienteDocumento; }
    public void setClienteDocumento(String clienteDocumento) { this.clienteDocumento = clienteDocumento; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public List<DetalleItem> getDetalle() { return detalle; }
    public void setDetalle(List<DetalleItem> detalle) { this.detalle = detalle; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public Sucursal getSucursal() { return sucursal; }
    public void setSucursal(Sucursal sucursal) { this.sucursal = sucursal; }

    public String getMetodoPago() { return metodo_pago; }
    public void setMetodoPago(String metodo_pago) { this.metodo_pago = metodo_pago; }
}
