package com.example.template.ui.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.template.R;
import com.example.template.network.models.Ajuste;
import java.util.List;

public class AuditAdapter extends RecyclerView.Adapter<AuditAdapter.ViewHolder> {

    private List<Ajuste> list;

    public AuditAdapter(List<Ajuste> list) {
        this.list = list;
    }

    public void updateData(List<Ajuste> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_audit, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ajuste a = list.get(position);

        String formattedDate = "N/A";
        if (a.getFecha() != null) {
            try {
                java.text.SimpleDateFormat utcFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US);
                utcFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                java.util.Date date = utcFormat.parse(a.getFecha());
                
                java.text.SimpleDateFormat localFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.US);
                localFormat.setTimeZone(java.util.TimeZone.getDefault());
                formattedDate = localFormat.format(date);
            } catch (Exception e) {
                formattedDate = a.getFecha().replace("T", " ").substring(0, 16);
            }
        }
        holder.tvDate.setText(formattedDate);
        holder.tvProductName.setText(a.getProducto() != null ? a.getProducto().getName() : "SKU D/C");
        holder.tvSucursal.setText(a.getSucursal() != null ? a.getSucursal().getName() : "Desconocida");
        holder.tvOperador.setText(a.getUsuario() != null ? a.getUsuario().getNombreCompleto() : "Operador");

        int deltaVal = a.getCantidadFisica() - a.getCantidadSistema();
        if (deltaVal == 0) {
            holder.tvDelta.setText("Sin Cambios");
            holder.tvDelta.setTextColor(Color.parseColor("#475569"));
            holder.tvDelta.setBackgroundColor(Color.parseColor("#f1f5f9"));
        } else if (deltaVal > 0) {
            holder.tvDelta.setText("+" + deltaVal + " U");
            holder.tvDelta.setTextColor(Color.parseColor("#166534"));
            holder.tvDelta.setBackgroundColor(Color.parseColor("#dcfce3"));
        } else {
            holder.tvDelta.setText(deltaVal + " U");
            holder.tvDelta.setTextColor(Color.parseColor("#991b1b"));
            holder.tvDelta.setBackgroundColor(Color.parseColor("#fee2e2"));
        }

        String motivoStr = "Desconocido";
        if ("ERROR_REGISTRO".equals(a.getMotivo())) motivoStr = "Error de Registro";
        else if ("DANO_MERMA".equals(a.getMotivo())) motivoStr = "Artículo Dañado / Extraviado";
        else if ("ROBO_O_PERDIDA".equals(a.getMotivo())) motivoStr = "Robo / No Habido";
        else if ("CADUCIDAD".equals(a.getMotivo())) motivoStr = "Vencido";
        
        holder.tvMotivo.setText(motivoStr);

        double loss = 0;
        if (a.getValorPerdido() != null && !a.getValorPerdido().isEmpty()) {
            try {
                loss = Double.parseDouble(a.getValorPerdido());
            } catch (Exception ignored) {}
        }
        holder.tvLoss.setText(String.format("Bs. %.2f", loss));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvProductName, tvDelta, tvSucursal, tvOperador, tvMotivo, tvLoss;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvDelta = itemView.findViewById(R.id.tvDelta);
            tvSucursal = itemView.findViewById(R.id.tvSucursal);
            tvOperador = itemView.findViewById(R.id.tvOperador);
            tvMotivo = itemView.findViewById(R.id.tvMotivo);
            tvLoss = itemView.findViewById(R.id.tvLoss);
        }
    }
}
