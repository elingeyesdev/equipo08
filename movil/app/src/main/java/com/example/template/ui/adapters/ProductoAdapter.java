package com.example.template.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.template.R;
import com.example.template.network.models.Producto;
import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ViewHolder> {

    private List<Producto> list;
    private OnActionClickListener actionListener;
    private boolean canManage = true;

    public interface OnActionClickListener {
        void onDeleteClick(Producto producto);
        void onEditClick(Producto producto);
    }

    public ProductoAdapter(List<Producto> list, OnActionClickListener listener) {
        this.list = list;
        this.actionListener = listener;
    }

    public void setCanManage(boolean canManage) {
        this.canManage = canManage;
        notifyDataSetChanged();
    }

    public void updateData(List<Producto> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Producto p = list.get(position);
        holder.tvSku.setText("SKU: " + p.getSku());
        holder.tvNombre.setText(p.getName());
        
        // El backend devuelve a veces el objeto Proveedor anidado
        if (p.getProveedor() != null) {
            holder.tvProveedor.setText("Proveedor: " + p.getProveedor().getName());
        } else {
            holder.tvProveedor.setText("Proveedor: N/A");
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
        TextView tvSku, tvNombre, tvProveedor;
        ImageButton btnDelete, btnEdit;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSku = itemView.findViewById(R.id.tvSku);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvProveedor = itemView.findViewById(R.id.tvProveedor);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}
