package com.example.template.ui.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.template.R;
import com.example.template.network.models.Stock;
import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.ViewHolder> {

    private List<Stock> list;
    private OnTrasladoClickListener listener;

    public interface OnTrasladoClickListener {
        void onTrasladoClick(Stock stock);
    }

    public StockAdapter(List<Stock> list, OnTrasladoClickListener listener) {
        this.list = list;
        this.listener = listener;
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
        
        holder.tvStock.setText(s.getCantidadTotal() + " U");
        
        if (s.getSucursal() != null) {
            holder.tvSucursal.setText(s.getSucursal().getName());
        } else {
            holder.tvSucursal.setText("Huérfana");
        }

        double costoFijo = s.getProducto() != null ? s.getProducto().getPrecioCosto() : 0.0;
        double valuacion = s.getCantidadTotal() * costoFijo;

        holder.tvCostoFijo.setText(String.format("Bs %.2f", costoFijo));
        holder.tvValuacion.setText(String.format("Bs %.2f", valuacion));

        // Low stock alert visual styling
        int minStock = s.getProducto() != null ? s.getProducto().getStockMinimo() : 10;
        boolean isAlerta = s.getCantidadTotal() < minStock;
        
        holder.tvMinStock.setText("Min: " + minStock);
        
        if (isAlerta) {
            holder.tvStock.setTextColor(Color.parseColor("#dc2626"));
            holder.cardView.setCardBackgroundColor(Color.parseColor("#FFF5F5"));
        } else {
            holder.tvStock.setTextColor(Color.parseColor("#16a34a"));
            holder.cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        holder.btnTrasladar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTrasladoClick(s);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSku, tvNombre, tvStock, tvSucursal, tvCostoFijo, tvValuacion, tvMinStock;
        android.widget.Button btnTrasladar;
        CardView cardView;
        ConstraintLayout layoutContainer;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSku = itemView.findViewById(R.id.tvSku);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvStock = itemView.findViewById(R.id.tvStock);
            tvSucursal = itemView.findViewById(R.id.tvSucursal);
            tvCostoFijo = itemView.findViewById(R.id.tvCostoFijo);
            tvValuacion = itemView.findViewById(R.id.tvValuacion);
            tvMinStock = itemView.findViewById(R.id.tvMinStock);
            btnTrasladar = itemView.findViewById(R.id.btnTrasladar);
            cardView = itemView.findViewById(R.id.cardView);
            layoutContainer = itemView.findViewById(R.id.layoutContainer);
        }
    }
}
