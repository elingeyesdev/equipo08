package com.example.template.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.template.R;
import com.example.template.network.models.LoteIngreso;
import java.util.List;
import java.util.Locale;

public class LoteAdapter extends RecyclerView.Adapter<LoteAdapter.ViewHolder> {

    private List<LoteIngreso> list;

    public LoteAdapter(List<LoteIngreso> list) {
        this.list = list;
    }

    public void updateData(List<LoteIngreso> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lote, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LoteIngreso l = list.get(position);
        
        holder.tvTrx.setText(l.getId() != null ? "#" + l.getId().substring(0, 8) : "#nuevo");
        holder.tvFecha.setText(l.getFechaIngreso() != null ? l.getFechaIngreso().substring(0, 10) : "");
        
        if (l.getProducto() != null) {
            holder.tvProducto.setText(l.getProducto().getName() + "\n" + l.getProducto().getSku());
        }
        
        if (l.getProveedor() != null) {
            holder.tvProveedor.setText(l.getProveedor().getName());
        }

        holder.tvVolumen.setText("+" + l.getCantidad() + " uds");
        holder.tvPrecioU.setText(String.format(Locale.US, "Bs %.2f", l.getCostoAdquisicion()));
        
        double inversion = l.getCantidad() * l.getCostoAdquisicion();
        holder.tvInversion.setText(String.format(Locale.US, "Bs %.2f", inversion));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTrx, tvFecha, tvProducto, tvProveedor, tvVolumen, tvPrecioU, tvInversion;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTrx = itemView.findViewById(R.id.tvTrx);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvProducto = itemView.findViewById(R.id.tvProducto);
            tvProveedor = itemView.findViewById(R.id.tvProveedor);
            tvVolumen = itemView.findViewById(R.id.tvVolumen);
            tvPrecioU = itemView.findViewById(R.id.tvPrecioU);
            tvInversion = itemView.findViewById(R.id.tvInversion);
        }
    }
}
