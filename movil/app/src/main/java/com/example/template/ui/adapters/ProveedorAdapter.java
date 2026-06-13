package com.example.template.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.template.R;
import com.example.template.network.models.Proveedor;
import java.util.List;

public class ProveedorAdapter extends RecyclerView.Adapter<ProveedorAdapter.ViewHolder> {

    private List<Proveedor> list;
    private OnActionClickListener actionListener;

    public interface OnActionClickListener {
        void onDeleteClick(Proveedor proveedor);
        void onEditClick(Proveedor proveedor);
    }

    public ProveedorAdapter(List<Proveedor> list, OnActionClickListener listener) {
        this.list = list;
        this.actionListener = listener;
    }

    public void updateData(List<Proveedor> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_proveedor, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Proveedor p = list.get(position);
        holder.tvRazonSocial.setText(p.getName());
        holder.tvNit.setText("NIT: " + p.getTaxId());
        holder.tvEmail.setText(p.getContactEmail() != null && !p.getContactEmail().isEmpty() ? p.getContactEmail() : "Sin Email");

        holder.btnDelete.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onDeleteClick(p);
            }
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onEditClick(p);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRazonSocial, tvNit, tvEmail;
        ImageButton btnDelete, btnEdit;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRazonSocial = itemView.findViewById(R.id.tvRazonSocial);
            tvNit = itemView.findViewById(R.id.tvNit);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}
