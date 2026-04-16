package com.example.template.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.template.R;
import com.example.template.network.models.Stock;
import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.ViewHolder> {

    private List<Stock> list;

    public StockAdapter(List<Stock> list) {
        this.list = list;
    }

    public void updateData(List<Stock> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stock, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Stock s = list.get(position);
        
        if (s.getProducto() != null) {
            holder.tvSku.setText(s.getProducto().getSku());
            holder.tvNombre.setText(s.getProducto().getName());
        } else {
            holder.tvSku.setText("N/A");
            holder.tvNombre.setText("N/A");
        }
        
        holder.tvStock.setText(s.getCantidadTotal() + " uds");
        holder.tvFecha.setText(s.getUltimaActualizacion() != null ? s.getUltimaActualizacion().replace("T", " ").substring(0, 19) : "");
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSku, tvNombre, tvStock, tvFecha;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSku = itemView.findViewById(R.id.tvSku);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvStock = itemView.findViewById(R.id.tvStock);
            tvFecha = itemView.findViewById(R.id.tvFecha);
        }
    }
}
