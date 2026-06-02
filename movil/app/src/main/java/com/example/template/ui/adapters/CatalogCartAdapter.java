package com.example.template.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.template.R;
import com.example.template.network.models.CatalogCartItem;

import java.util.List;

public class CatalogCartAdapter extends RecyclerView.Adapter<CatalogCartAdapter.ViewHolder> {

    private List<CatalogCartItem> list;
    private OnCartActionListener listener;

    public interface OnCartActionListener {
        void onQuantityChange(CatalogCartItem item, int newQty);
        void onDelete(CatalogCartItem item);
    }

    public CatalogCartAdapter(List<CatalogCartItem> list, OnCartActionListener listener) {
        this.list = list;
        this.listener = listener;
    }

    public void updateData(List<CatalogCartItem> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_product, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CatalogCartItem item = list.get(position);

        holder.tvNombre.setText(item.getProduct().getName());
        holder.tvPriceUnit.setText(String.format("Bs %.2f c/u", item.getProduct().getPrecioVenta()));
        holder.tvQty.setText(String.valueOf(item.getQuantity()));

        double subtotal = item.getProduct().getPrecioVenta() * item.getQuantity();
        holder.tvSubtotal.setText(String.format("Bs %.2f", subtotal));

        holder.btnPlus.setOnClickListener(v -> {
            if (listener != null) {
                listener.onQuantityChange(item, item.getQuantity() + 1);
            }
        });

        holder.btnMinus.setOnClickListener(v -> {
            if (listener != null && item.getQuantity() > 1) {
                listener.onQuantityChange(item, item.getQuantity() - 1);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvPriceUnit, tvQty, tvSubtotal;
        ImageButton btnPlus, btnMinus, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvPriceUnit = itemView.findViewById(R.id.tvPriceUnit);
            tvQty = itemView.findViewById(R.id.tvQty);
            tvSubtotal = itemView.findViewById(R.id.tvSubtotal);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
