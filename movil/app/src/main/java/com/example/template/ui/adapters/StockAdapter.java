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
    private OnIncidenciaClickListener listener;

    public interface OnIncidenciaClickListener {
        void onIncidenciaClick(Stock stock);
    }

    public StockAdapter(List<Stock> list, OnIncidenciaClickListener listener) {
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

        holder.btnIncidencia.setOnClickListener(v -> {
            if (listener != null) {
                listener.onIncidenciaClick(s);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSku, tvNombre, tvStock, tvSucursal, tvCostoFijo, tvValuacion;
        android.widget.Button btnIncidencia;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSku = itemView.findViewById(R.id.tvSku);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvStock = itemView.findViewById(R.id.tvStock);
            tvSucursal = itemView.findViewById(R.id.tvSucursal);
            tvCostoFijo = itemView.findViewById(R.id.tvCostoFijo);
            tvValuacion = itemView.findViewById(R.id.tvValuacion);
            btnIncidencia = itemView.findViewById(R.id.btnIncidencia);
        }
    }
}
