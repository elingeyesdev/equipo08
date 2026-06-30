package com.example.template.ui.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.template.R;
import com.example.template.network.models.Producto;
import com.example.template.network.models.Stock;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StockAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_SINGLE = 0;
    public static final int TYPE_PARENT = 1;
    public static final int TYPE_CHILD = 2;

    private List<Stock> originalList;
    private List<Item> displayList = new ArrayList<>();
    private Set<String> expandedGroups = new HashSet<>();
    private OnTrasladoClickListener listener;

    public interface OnTrasladoClickListener {
        void onTrasladoClick(Stock stock);
        void onStockLongClick(Stock stock);
    }

    public static class Item {
        public int type;
        public String groupName;
        public Stock stock;
        public List<Stock> variants;
        public boolean isExpanded;

        public Item(int type, String groupName, Stock stock, List<Stock> variants, boolean isExpanded) {
            this.type = type;
            this.groupName = groupName;
            this.stock = stock;
            this.variants = variants;
            this.isExpanded = isExpanded;
        }
    }

    public StockAdapter(List<Stock> list, OnTrasladoClickListener listener) {
        this.originalList = list;
        this.listener = listener;
        rebuildDisplayList();
    }

    public void updateData(List<Stock> newList) {
        this.originalList = newList;
        rebuildDisplayList();
    }

    private void rebuildDisplayList() {
        displayList.clear();
        if (originalList == null) {
            notifyDataSetChanged();
            return;
        }

        
        Map<String, List<Stock>> groups = new LinkedHashMap<>();
        for (Stock s : originalList) {
            if (s.getProducto() == null) continue;
            String name = s.getProducto().getName();
            if (name == null || name.trim().isEmpty()) {
                name = "Sin Nombre";
            }
            if (!groups.containsKey(name)) {
                groups.put(name, new ArrayList<>());
            }
            groups.get(name).add(s);
        }

        for (Map.Entry<String, List<Stock>> entry : groups.entrySet()) {
            String name = entry.getKey();
            List<Stock> variants = entry.getValue();
            if (variants.size() == 1) {
                displayList.add(new Item(TYPE_SINGLE, name, variants.get(0), null, false));
            } else {
                boolean isExpanded = expandedGroups.contains(name);
                displayList.add(new Item(TYPE_PARENT, name, null, variants, isExpanded));
                if (isExpanded) {
                    for (Stock s : variants) {
                        displayList.add(new Item(TYPE_CHILD, name, s, null, false));
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return displayList.get(position).type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_SINGLE) {
            View v = inflater.inflate(R.layout.item_stock, parent, false);
            return new SingleViewHolder(v);
        } else if (viewType == TYPE_PARENT) {
            View v = inflater.inflate(R.layout.item_stock_parent, parent, false);
            return new ParentViewHolder(v);
        } else {
            View v = inflater.inflate(R.layout.item_stock_child, parent, false);
            return new ChildViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Item item = displayList.get(position);

        if (holder instanceof SingleViewHolder) {
            SingleViewHolder h = (SingleViewHolder) holder;
            Stock s = item.stock;
            Producto p = s.getProducto();

            h.tvNombre.setText(p != null ? p.getName() : "N/A");
            h.tvSku.setText(p != null ? p.getSku() : "N/A");
            h.tvSucursal.setText(s.getSucursal() != null ? s.getSucursal().getName() : "Huérfana");
            h.tvStock.setText(s.getCantidadTotal() + " U");

            double costoFijo = p != null ? p.getPrecioCosto() : 0.0;
            double valuacion = s.getCantidadTotal() * costoFijo;

            h.tvCostoFijo.setText(String.format("Bs %.2f", costoFijo));
            h.tvValuacion.setText(String.format("Bs %.2f", valuacion));

            int minStock = p != null ? p.getStockMinimo() : 10;
            h.tvMinStock.setText("Min: " + minStock);

            boolean isAlerta = s.getCantidadTotal() < minStock;
            if (isAlerta) {
                h.tvStock.setTextColor(Color.parseColor("#0d9488"));
                h.cardView.setCardBackgroundColor(Color.parseColor("#F5EEF8"));
            } else {
                h.tvStock.setTextColor(Color.parseColor("#0d9488"));
                h.cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
            }

            if (s.getSucursalId() == null) {
                h.btnTrasladar.setVisibility(View.GONE);
            } else {
                h.btnTrasladar.setVisibility(View.VISIBLE);
                h.btnTrasladar.setOnClickListener(v -> {
                    if (listener != null) listener.onTrasladoClick(s);
                });
            }

            h.itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onStockLongClick(s);
                    return true;
                }
                return false;
            });

        } else if (holder instanceof ParentViewHolder) {
            ParentViewHolder h = (ParentViewHolder) holder;
            List<Stock> variants = item.variants;

            h.tvNombre.setText(item.groupName);
            h.tvVariantCount.setText(variants.size() + " variantes");

            
            int totalStock = 0;
            double totalValuacion = 0;
            for (Stock s : variants) {
                totalStock += s.getCantidadTotal();
                double costo = s.getProducto() != null ? s.getProducto().getPrecioCosto() : 0.0;
                totalValuacion += s.getCantidadTotal() * costo;
            }

            h.tvStock.setText(totalStock + " U");
            h.tvValuacion.setText(String.format("Bs %.2f", totalValuacion));

            h.ivChevron.setImageResource(R.drawable.ic_chevron_down);
            h.ivChevron.setRotation(item.isExpanded ? 180f : 0f);

            h.itemView.setOnClickListener(v -> {
                String groupName = item.groupName;
                if (expandedGroups.contains(groupName)) {
                    expandedGroups.remove(groupName);
                } else {
                    expandedGroups.add(groupName);
                }
                rebuildDisplayList();
            });

        } else if (holder instanceof ChildViewHolder) {
            ChildViewHolder h = (ChildViewHolder) holder;
            Stock s = item.stock;
            Producto p = s.getProducto();

            h.tvSku.setText(p != null ? "SKU: " + p.getSku() : "SKU: N/A");
            h.tvSucursal.setText(s.getSucursal() != null ? s.getSucursal().getName() : "Huérfana");
            h.tvStock.setText(s.getCantidadTotal() + " U");

            bindAttributesText(h.tvVariant, p);

            double costoFijo = p != null ? p.getPrecioCosto() : 0.0;
            double valuacion = s.getCantidadTotal() * costoFijo;

            h.tvCostoFijo.setText(String.format("Bs %.2f", costoFijo));
            h.tvValuacion.setText(String.format("Bs %.2f", valuacion));

            int minStock = p != null ? p.getStockMinimo() : 10;
            h.tvMinStock.setText("Min: " + minStock);

            boolean isAlerta = s.getCantidadTotal() < minStock;
            if (isAlerta) {
                h.tvStock.setTextColor(Color.parseColor("#0d9488"));
                h.cardView.setCardBackgroundColor(Color.parseColor("#F5EEF8"));
            } else {
                h.tvStock.setTextColor(Color.parseColor("#0d9488"));
                h.cardView.setCardBackgroundColor(Color.parseColor("#f8fafc"));
            }

            if (s.getSucursalId() == null) {
                h.btnTrasladar.setVisibility(View.GONE);
            } else {
                h.btnTrasladar.setVisibility(View.VISIBLE);
                h.btnTrasladar.setOnClickListener(v -> {
                    if (listener != null) listener.onTrasladoClick(s);
                });
            }

            h.itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onStockLongClick(s);
                    return true;
                }
                return false;
            });
        }
    }

    private void bindAttributesText(TextView tvVariant, Producto p) {
        if (p == null) {
            tvVariant.setVisibility(View.GONE);
            return;
        }
        if (p.getAttributes() != null && !p.getAttributes().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : p.getAttributes().entrySet()) {
                if (entry.getValue() != null && !entry.getValue().trim().isEmpty()) {
                    if (sb.length() > 0) sb.append(", ");
                    sb.append(entry.getKey()).append(": ").append(entry.getValue());
                }
            }
            if (sb.length() > 0) {
                tvVariant.setVisibility(View.VISIBLE);
                tvVariant.setText("Atributos: " + sb.toString());
            } else if (p.getDescription() != null && !p.getDescription().isEmpty()) {
                tvVariant.setVisibility(View.VISIBLE);
                tvVariant.setText("Variante: " + p.getDescription());
            } else {
                tvVariant.setVisibility(View.GONE);
            }
        } else if (p.getDescription() != null && !p.getDescription().isEmpty()) {
            tvVariant.setVisibility(View.VISIBLE);
            tvVariant.setText("Variante: " + p.getDescription());
        } else {
            tvVariant.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }

    public static class SingleViewHolder extends RecyclerView.ViewHolder {
        TextView tvSku, tvNombre, tvSucursal, tvStock, tvCostoFijo, tvValuacion, tvMinStock;
        Button btnTrasladar;
        CardView cardView;

        public SingleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSku = itemView.findViewById(R.id.tvSku);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvSucursal = itemView.findViewById(R.id.tvSucursal);
            tvStock = itemView.findViewById(R.id.tvStock);
            tvCostoFijo = itemView.findViewById(R.id.tvCostoFijo);
            tvValuacion = itemView.findViewById(R.id.tvValuacion);
            tvMinStock = itemView.findViewById(R.id.tvMinStock);
            btnTrasladar = itemView.findViewById(R.id.btnTrasladar);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }

    public static class ParentViewHolder extends RecyclerView.ViewHolder {
        ImageView ivChevron;
        TextView tvNombre, tvVariantCount, tvStock, tvValuacion;
        CardView cardView;

        public ParentViewHolder(@NonNull View itemView) {
            super(itemView);
            ivChevron = itemView.findViewById(R.id.ivChevron);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvVariantCount = itemView.findViewById(R.id.tvVariantCount);
            tvStock = itemView.findViewById(R.id.tvStock);
            tvValuacion = itemView.findViewById(R.id.tvValuacion);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }

    public static class ChildViewHolder extends RecyclerView.ViewHolder {
        TextView tvSku, tvSucursal, tvStock, tvCostoFijo, tvValuacion, tvMinStock, tvVariant;
        Button btnTrasladar;
        CardView cardView;

        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSku = itemView.findViewById(R.id.tvSku);
            tvSucursal = itemView.findViewById(R.id.tvSucursal);
            tvStock = itemView.findViewById(R.id.tvStock);
            tvCostoFijo = itemView.findViewById(R.id.tvCostoFijo);
            tvValuacion = itemView.findViewById(R.id.tvValuacion);
            tvMinStock = itemView.findViewById(R.id.tvMinStock);
            tvVariant = itemView.findViewById(R.id.tvVariant);
            btnTrasladar = itemView.findViewById(R.id.btnTrasladar);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
