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
import java.util.List;

public class SucursalAdapter extends RecyclerView.Adapter<SucursalAdapter.ViewHolder> {

    private List<Sucursal> list;
    private OnDeleteClickListener deleteListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(Sucursal sucursal);
    }

    public SucursalAdapter(List<Sucursal> list, OnDeleteClickListener listener) {
        this.list = list;
        this.deleteListener = listener;
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
        holder.tvNombre.setText(s.getName());
        holder.tvDireccion.setText(s.getAddress() != null && !s.getAddress().isEmpty() ? s.getAddress() : "Sin Dirección");
        holder.tvTelefono.setText("Teléfono: " + (s.getPhone() != null && !s.getPhone().isEmpty() ? s.getPhone() : "N/A"));
        
        if (s.isActive()) {
            holder.tvEstado.setText("Operativa");
            holder.tvEstado.setTextColor(Color.parseColor("#10b981")); // Green
        } else {
            holder.tvEstado.setText("Cerrada");
            holder.tvEstado.setTextColor(Color.parseColor("#ef4444")); // Red
        }

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteClick(s);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDireccion, tvTelefono, tvEstado;
        ImageButton btnDelete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvDireccion = itemView.findViewById(R.id.tvDireccion);
            tvTelefono = itemView.findViewById(R.id.tvTelefono);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
