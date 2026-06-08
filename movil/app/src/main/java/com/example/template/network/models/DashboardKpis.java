package com.example.template.network.models;

import java.util.List;

public class DashboardKpis {
    private int totalVentas;
    private double ingresosTotales;
    private double costoTotal;
    private double utilidadTotal;
    private List<Venta> recentSales;

    public int getTotalVentas() {
        return totalVentas;
    }

    public double getIngresosTotales() {
        return ingresosTotales;
    }

    public double getCostoTotal() {
        return costoTotal;
    }

    public double getUtilidadTotal() {
        return utilidadTotal;
    }

    public List<Venta> getRecentSales() {
        return recentSales;
    }
}
