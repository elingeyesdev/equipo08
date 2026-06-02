package com.example.template.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.template.R;
import com.example.template.network.models.CatalogProducto;
import com.example.template.utils.ImageLoader;

import java.util.List;

public class CatalogProductAdapter extends RecyclerView.Adapter<CatalogProductAdapter.ViewHolder> {

    private List<CatalogProducto> list;
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(CatalogProducto product);
    }

    public CatalogProductAdapter(List<CatalogProducto> list, OnProductClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    public void updateData(List<CatalogProducto> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_catalog_producto, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CatalogProducto p = list.get(position);

        holder.tvName.setText(p.getName());
        holder.tvPrice.setText(String.format("Bs %.2f", p.getPrecioVenta()));

        if (p.getCategory() != null && !p.getCategory().trim().isEmpty()) {
            holder.tvCategory.setText(p.getCategory().toUpperCase());
            holder.tvCategory.setVisibility(View.VISIBLE);
        } else {
            holder.tvCategory.setVisibility(View.GONE);
        }

        if (p.getDescription() != null && !p.getDescription().trim().isEmpty()) {
            holder.tvDesc.setText(p.getDescription());
            holder.tvDesc.setVisibility(View.VISIBLE);
        } else {
            holder.tvDesc.setVisibility(View.GONE);
        }

        ImageLoader.loadImage(p.getImagenUrl(), holder.ivImage);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductClick(p);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvCategory, tvName, tvDesc, tvPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivProductImage);
            tvCategory = itemView.findViewById(R.id.tvProductCategory);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvDesc = itemView.findViewById(R.id.tvProductDescription);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
        }
    }
}
