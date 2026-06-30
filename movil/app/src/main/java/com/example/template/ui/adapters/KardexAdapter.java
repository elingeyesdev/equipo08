package com.example.template.ui.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.template.R;
import com.example.template.network.models.KardexResponse;

import java.util.List;
import java.util.Map;

public class KardexAdapter extends RecyclerView.Adapter<KardexAdapter.ViewHolder> {

    private final List<KardexResponse> movementsList;

    public KardexAdapter(List<KardexResponse> movementsList) {
        this.movementsList = movementsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kardex, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        KardexResponse movement = movementsList.get(position);

        
        holder.tvFecha.setText(formatDate(movement.getFecha()));

        
        holder.tvSucursal.setText("Sucursal: " + (movement.getSucursalNombre() != null ? movement.getSucursalNombre() : "General"));
        holder.tvOperador.setText("Operador: " + (movement.getUsuarioNombre() != null ? movement.getUsuarioNombre() : "Sistema"));

        
        StringBuilder skuBuilder = new StringBuilder();
        if (movement.getSku() != null && !movement.getSku().isEmpty()) {
            skuBuilder.append("SKU: ").append(movement.getSku());
        }
        if (movement.getVariacionDetalle() != null && !movement.getVariacionDetalle().isEmpty()) {
            StringBuilder varBuilder = new StringBuilder();
            for (Map.Entry<String, String> entry : movement.getVariacionDetalle().entrySet()) {
                if (varBuilder.length() > 0) varBuilder.append(", ");
                varBuilder.append(entry.getKey()).append(": ").append(entry.getValue());
            }
            if (skuBuilder.length() > 0) skuBuilder.append(" (");
            skuBuilder.append(varBuilder.toString());
            if (movement.getSku() != null && !movement.getSku().isEmpty()) skuBuilder.append(")");
        }

        if (skuBuilder.length() > 0) {
            holder.tvSku.setVisibility(View.VISIBLE);
            holder.tvSku.setText(skuBuilder.toString());
        } else {
            holder.tvSku.setVisibility(View.GONE);
        }

        
        String tipo = movement.getTipo() != null ? movement.getTipo().toUpperCase() : "AJUSTE";
        int delta = movement.getCantidadDelta();
        
        holder.tvOpBadge.setText(tipo);

        
        if (tipo.contains("VENTA") || tipo.contains("EGRESO") || delta < 0) {
            holder.llLeftBadge.setBackgroundColor(Color.parseColor("#E0E7FF")); 
            holder.tvDelta.setTextColor(Color.parseColor("#4F46E5")); 
            holder.tvOpBadge.setTextColor(Color.parseColor("#3730A3"));
            holder.tvDelta.setText(String.valueOf(delta));
        } else if (tipo.contains("COMPRA") || tipo.contains("INGRESO") || delta > 0) {
            holder.llLeftBadge.setBackgroundColor(Color.parseColor("#D1FAE5")); 
            holder.tvDelta.setTextColor(Color.parseColor("#059669")); 
            holder.tvOpBadge.setTextColor(Color.parseColor("#065F46"));
            holder.tvDelta.setText("+" + delta);
        } else if (tipo.contains("TRANSFER")) {
            holder.llLeftBadge.setBackgroundColor(Color.parseColor("#DBEAFE")); 
            holder.tvDelta.setTextColor(Color.parseColor("#2563EB")); 
            holder.tvOpBadge.setTextColor(Color.parseColor("#1E40AF"));
            holder.tvDelta.setText(delta > 0 ? "+" + delta : String.valueOf(delta));
        } else {
            
            holder.llLeftBadge.setBackgroundColor(Color.parseColor("#FEF3C7")); 
            holder.tvDelta.setTextColor(Color.parseColor("#D97706")); 
            holder.tvOpBadge.setTextColor(Color.parseColor("#92400E"));
            holder.tvDelta.setText(delta > 0 ? "+" + delta : String.valueOf(delta));
        }

        
        holder.tvStockChange.setText("Stock: " + movement.getStockAnterior() + " ➔ " + movement.getStockResultante());

        
        holder.tvCostoUnitario.setText("Costo: Bs " + String.format(java.util.Locale.US, "%.2f", movement.getCostoUnitario()));

        
        if (movement.getMotivo() != null && !movement.getMotivo().isEmpty()) {
            holder.tvMotivo.setVisibility(View.VISIBLE);
            holder.tvMotivo.setText("Ref: " + movement.getMotivo());
        } else {
            holder.tvMotivo.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return movementsList.size();
    }

    private String formatDate(String isoDate) {
        if (isoDate == null) return "";
        try {
            
            java.text.SimpleDateFormat parser = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US);
            parser.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            java.util.Date date = parser.parse(isoDate);
            java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("dd/MM/yyyy, HH:mm", java.util.Locale.getDefault());
            formatter.setTimeZone(java.util.TimeZone.getDefault());
            return formatter.format(date);
        } catch (Exception e) {
            try {
                java.text.SimpleDateFormat parser = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", java.util.Locale.US);
                java.util.Date date = parser.parse(isoDate);
                java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("dd/MM/yyyy, HH:mm", java.util.Locale.getDefault());
                return formatter.format(date);
            } catch (Exception e2) {
                try {
                    java.text.SimpleDateFormat parser = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.US);
                    java.util.Date date = parser.parse(isoDate);
                    java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("dd/MM/yyyy, HH:mm", java.util.Locale.getDefault());
                    return formatter.format(date);
                } catch (Exception e3) {
                    return isoDate;
                }
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout llLeftBadge;
        TextView tvDelta, tvOpBadge, tvSucursal, tvFecha, tvOperador, tvSku, tvStockChange, tvCostoUnitario, tvMotivo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            llLeftBadge = itemView.findViewById(R.id.llKardexLeftBadge);
            tvDelta = itemView.findViewById(R.id.tvKardexDelta);
            tvOpBadge = itemView.findViewById(R.id.tvKardexOpBadge);
            tvSucursal = itemView.findViewById(R.id.tvKardexSucursal);
            tvFecha = itemView.findViewById(R.id.tvKardexFecha);
            tvOperador = itemView.findViewById(R.id.tvKardexOperador);
            tvSku = itemView.findViewById(R.id.tvKardexSku);
            tvStockChange = itemView.findViewById(R.id.tvKardexStockChange);
            tvCostoUnitario = itemView.findViewById(R.id.tvKardexCostoUnitario);
            tvMotivo = itemView.findViewById(R.id.tvKardexMotivo);
        }
    }
}
