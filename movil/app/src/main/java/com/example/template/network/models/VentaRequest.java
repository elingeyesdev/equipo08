package com.example.template.network.models;

import java.util.List;

public class VentaRequest {
    private String sucursal_id;
    private String clienteNombre;
    private String clienteDocumento;
    private List<VentaItem> items;

    public VentaRequest(String sucursal_id, String clienteNombre, String clienteDocumento, List<VentaItem> items) {
        this.sucursal_id = sucursal_id;
        this.clienteNombre = clienteNombre;
        this.clienteDocumento = clienteDocumento;
        this.items = items;
    }

    public String getSucursalId() { return sucursal_id; }
    public void setSucursalId(String sucursal_id) { this.sucursal_id = sucursal_id; }

    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }

    public String getClienteDocumento() { return clienteDocumento; }
    public void setClienteDocumento(String clienteDocumento) { this.clienteDocumento = clienteDocumento; }

    public List<VentaItem> getItems() { return items; }
    public void setItems(List<VentaItem> items) { this.items = items; }
}
