package com.example.template.ui.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.template.R;
import com.example.template.network.models.Sucursal;
import com.example.template.utils.SessionManager;
import java.util.List;

public class SucursalAdapter extends RecyclerView.Adapter<SucursalAdapter.ViewHolder> {

    private List<Sucursal> list;
    private OnActionClickListener actionListener;
    private boolean canManage = true;

    public interface OnActionClickListener {
        void onDeleteClick(Sucursal sucursal);
        void onEditClick(Sucursal sucursal);
    }

    public SucursalAdapter(List<Sucursal> list, OnActionClickListener listener) {
        this.list = list;
        this.actionListener = listener;
    }

    public void setCanManage(boolean canManage) {
        this.canManage = canManage;
        notifyDataSetChanged();
    }

    public void updateData(List<Sucursal> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sucursal, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Sucursal s = list.get(position);
        
        SessionManager sessionManager = new SessionManager(holder.itemView.getContext());
        holder.tvTienda.setText(sessionManager.getTenantName() + " -");

        holder.tvNombre.setText(s.getName());
        holder.tvDireccion.setText(s.getAddress() != null && !s.getAddress().isEmpty() ? s.getAddress() : "Sin Dirección");
        holder.tvTelefono.setText("Teléfono: " + (s.getPhone() != null && !s.getPhone().isEmpty() ? s.getPhone() : "N/A"));
        
        if (s.getHorarios() != null && !s.getHorarios().isEmpty()) {
            holder.tvHorarios.setVisibility(View.VISIBLE);
            try {
                com.google.gson.JsonArray array = new com.google.gson.JsonParser().parse(s.getHorarios()).getAsJsonArray();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < array.size(); i++) {
                    com.google.gson.JsonObject obj = array.get(i).getAsJsonObject();
                    com.google.gson.JsonArray days = obj.getAsJsonArray("days");
                    String start = obj.get("start").getAsString();
                    String end = obj.get("end").getAsString();
                    
                    if (days.size() == 7) {
                        sb.append("Todos los días: ");
                    } else {
                        for (int d = 0; d < days.size(); d++) {
                            String day = days.get(d).getAsString();
                            sb.append(day.substring(0, Math.min(day.length(), 3)));
                            if (d < days.size() - 1) sb.append(", ");
                        }
                        sb.append(": ");
                    }
                    sb.append(start).append(" - ").append(end);
                    if (i < array.size() - 1) sb.append("\n");
                }
                holder.tvHorarios.setText("Horario: " + sb.toString());
            } catch (Exception e) {
                holder.tvHorarios.setText("Horario: " + s.getHorarios());
            }
        } else {
            holder.tvHorarios.setVisibility(View.GONE);
        }
        
        if (s.isActive()) {
            holder.tvEstado.setText("Operativa");
            holder.tvEstado.setTextColor(Color.parseColor("#0d9488")); // Green
        } else {
            holder.tvEstado.setText("Cerrada");
            holder.tvEstado.setTextColor(Color.parseColor("#0d9488")); // Red
        }

        if (canManage) {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);
        } else {
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        }

        holder.btnDelete.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onDeleteClick(s);
            }
        });
        
        holder.btnEdit.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onEditClick(s);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTienda, tvNombre, tvDireccion, tvTelefono, tvEstado, tvHorarios;
        ImageButton btnDelete, btnEdit;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTienda = itemView.findViewById(R.id.tvTienda);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvDireccion = itemView.findViewById(R.id.tvDireccion);
            tvTelefono = itemView.findViewById(R.id.tvTelefono);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            tvHorarios = itemView.findViewById(R.id.tvHorarios);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}
