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
import com.example.template.network.models.Empleado;
import java.util.List;

public class EmpleadosAdapter extends RecyclerView.Adapter<EmpleadosAdapter.ViewHolder> {

    private List<Empleado> list;
    private OnActionClickListener actionListener;

    public interface OnActionClickListener {
        void onDeleteClick(Empleado empleado);
        void onEditClick(Empleado empleado);
    }

    public EmpleadosAdapter(List<Empleado> list, OnActionClickListener listener) {
        this.list = list;
        this.actionListener = listener;
    }

    public void updateData(List<Empleado> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_empleado, parent, false);
        return new ViewHolder(v);
    }

    private void setRoleBackgroundColor(TextView tv, String colorHex) {
        android.graphics.drawable.Drawable background = tv.getBackground();
        if (background instanceof android.graphics.drawable.GradientDrawable) {
            android.graphics.drawable.GradientDrawable gradientDrawable = (android.graphics.drawable.GradientDrawable) background.mutate();
            gradientDrawable.setColor(Color.parseColor(colorHex));
        } else {
            tv.setBackgroundColor(Color.parseColor(colorHex));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Empleado e = list.get(position);
        holder.tvNombre.setText(e.getNombreCompleto() != null ? e.getNombreCompleto() : "Desconocido");
        holder.tvEmail.setText(e.getCorreo() != null ? e.getCorreo() : "N/A");
        
        String rol = e.getRol() != null ? e.getRol().toUpperCase() : "VENDEDOR";
        holder.tvRol.setText(rol);
        
        if ("OWNER".equals(rol)) {
            setRoleBackgroundColor(holder.tvRol, "#0f172a");
            holder.tvRol.setTextColor(Color.parseColor("#eef2f6"));
            holder.btnEdit.setVisibility(View.INVISIBLE);
            holder.btnDelete.setVisibility(View.INVISIBLE);
        } else {
            holder.tvRol.setTextColor(Color.WHITE);
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);
            if ("SUPERVISOR".equals(rol)) {
                setRoleBackgroundColor(holder.tvRol, "#5981DF"); 
            } else {
                setRoleBackgroundColor(holder.tvRol, "#0d9488"); 
            }
        }
        
        holder.tvSucursal.setText(e.getSucursalNombre() != null ? e.getSucursalNombre() : "Acceso Global (HQ)");

        String estado = e.getEstado() != null ? e.getEstado() : "Activo";
        holder.tvEstado.setText(estado);
        if ("Activo".equalsIgnoreCase(estado)) {
            holder.tvEstado.setTextColor(Color.parseColor("#0d9488")); 
        } else {
            holder.tvEstado.setTextColor(Color.parseColor("#475569")); 
        }

        holder.btnDelete.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onDeleteClick(e);
            }
        });
        
        holder.btnEdit.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onEditClick(e);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvEmail, tvRol, tvSucursal, tvEstado;
        ImageButton btnDelete, btnEdit;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvRol = itemView.findViewById(R.id.tvRol);
            tvSucursal = itemView.findViewById(R.id.tvSucursal);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}
