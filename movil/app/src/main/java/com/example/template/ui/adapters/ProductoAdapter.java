package com.example.template.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.template.R;
import com.example.template.network.models.Producto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProductoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_SINGLE = 0;
    public static final int TYPE_PARENT = 1;
    public static final int TYPE_CHILD = 2;

    private List<Producto> originalList;
    private List<Item> displayList = new ArrayList<>();
    private Set<String> expandedGroups = new HashSet<>();
    private OnActionClickListener actionListener;
    private boolean canManage = true;

    public interface OnActionClickListener {
        void onDeleteClick(Producto producto);
        void onEditClick(Producto producto);
        void onCopyClick(Producto producto);
        void onProductLongClick(Producto producto);
    }

    public static class Item {
        public int type;
        public String groupName;
        public Producto producto;
        public List<Producto> variants;
        public boolean isExpanded;

        public Item(int type, String groupName, Producto producto, List<Producto> variants, boolean isExpanded) {
            this.type = type;
            this.groupName = groupName;
            this.producto = producto;
            this.variants = variants;
            this.isExpanded = isExpanded;
        }
    }

    public ProductoAdapter(List<Producto> list, OnActionClickListener listener) {
        this.originalList = list;
        this.actionListener = listener;
        rebuildDisplayList();
    }

    public void setCanManage(boolean canManage) {
        this.canManage = canManage;
        notifyDataSetChanged();
    }

    public void updateData(List<Producto> newList) {
        this.originalList = newList;
        rebuildDisplayList();
    }

    private void rebuildDisplayList() {
        displayList.clear();
        if (originalList == null) {
            notifyDataSetChanged();
            return;
        }

        
        Map<String, List<Producto>> groups = new LinkedHashMap<>();
        for (Producto p : originalList) {
            String name = p.getName();
            if (name == null || name.trim().isEmpty()) {
                name = "Sin Nombre";
            }
            if (!groups.containsKey(name)) {
                groups.put(name, new ArrayList<>());
            }
            groups.get(name).add(p);
        }

        for (Map.Entry<String, List<Producto>> entry : groups.entrySet()) {
            String name = entry.getKey();
            List<Producto> variants = entry.getValue();
            if (variants.size() == 1) {
                displayList.add(new Item(TYPE_SINGLE, name, variants.get(0), null, false));
            } else {
                boolean isExpanded = expandedGroups.contains(name);
                displayList.add(new Item(TYPE_PARENT, name, null, variants, isExpanded));
                if (isExpanded) {
                    for (Producto p : variants) {
                        displayList.add(new Item(TYPE_CHILD, name, p, null, false));
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
            View v = inflater.inflate(R.layout.item_producto, parent, false);
            return new SingleViewHolder(v);
        } else if (viewType == TYPE_PARENT) {
            View v = inflater.inflate(R.layout.item_producto_parent, parent, false);
            return new ParentViewHolder(v);
        } else {
            View v = inflater.inflate(R.layout.item_producto_child, parent, false);
            return new ChildViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Item item = displayList.get(position);

        if (holder instanceof SingleViewHolder) {
            SingleViewHolder h = (SingleViewHolder) holder;
            Producto p = item.producto;

            h.tvNombre.setText(p.getName());
            
            if (p.getSku() != null && !p.getSku().isEmpty()) {
                h.tvSku.setVisibility(View.VISIBLE);
                h.tvSku.setText("SKU: " + p.getSku());
            } else {
                h.tvSku.setVisibility(View.GONE);
            }

            bindAttributesText(h.tvVariant, p);

            if (p.getProveedor() != null) {
                h.tvProveedor.setText("Proveedor: " + p.getProveedor().getName());
            } else {
                h.tvProveedor.setText("Proveedor: N/A");
            }

            
            double costo = p.getPrecioCosto();
            double venta = p.getPrecioVenta();
            h.tvPrecioCosto.setText(String.format(java.util.Locale.US, "Bs %.2f", costo));
            h.tvPrecioVenta.setText(String.format(java.util.Locale.US, "Bs %.2f", venta));
            
            double profit = venta - costo;
            double pct = venta > 0 ? (profit / venta) * 100 : 0;
            h.tvMargen.setText(String.format(java.util.Locale.US, "%.0f%%", pct));

            
            if (canManage) {
                h.btnCopy.setVisibility(View.VISIBLE);
                h.btnEdit.setVisibility(View.VISIBLE);
                h.btnDelete.setVisibility(View.VISIBLE);
            } else {
                h.btnCopy.setVisibility(View.GONE);
                h.btnEdit.setVisibility(View.GONE);
                h.btnDelete.setVisibility(View.GONE);
            }

            h.btnCopy.setOnClickListener(v -> {
                if (actionListener != null) actionListener.onCopyClick(p);
            });
            h.btnEdit.setOnClickListener(v -> {
                if (actionListener != null) actionListener.onEditClick(p);
            });
            h.btnDelete.setOnClickListener(v -> {
                if (actionListener != null) actionListener.onDeleteClick(p);
            });
            h.itemView.setOnLongClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onProductLongClick(p);
                    return true;
                }
                return false;
            });

        } else if (holder instanceof ParentViewHolder) {
            ParentViewHolder h = (ParentViewHolder) holder;
            List<Producto> variants = item.variants;
            Producto first = variants.get(0);

            h.tvNombre.setText(item.groupName);
            h.tvVariantCount.setText(variants.size() + " variantes");

            if (first.getProveedor() != null) {
                h.tvProveedor.setText("Proveedor: " + first.getProveedor().getName());
            } else {
                h.tvProveedor.setText("Proveedor: N/A");
            }

            
            double minCosto = Double.MAX_VALUE;
            double maxCosto = -Double.MAX_VALUE;
            double minVenta = Double.MAX_VALUE;
            double maxVenta = -Double.MAX_VALUE;
            double minMargen = Double.MAX_VALUE;
            double maxMargen = -Double.MAX_VALUE;

            for (Producto p : variants) {
                double c = p.getPrecioCosto();
                double v = p.getPrecioVenta();
                if (c < minCosto) minCosto = c;
                if (c > maxCosto) maxCosto = c;
                if (v < minVenta) minVenta = v;
                if (v > maxVenta) maxVenta = v;

                double profit = v - c;
                double pct = v > 0 ? (profit / v) * 100 : 0;
                if (pct < minMargen) minMargen = pct;
                if (pct > maxMargen) maxMargen = pct;
            }

            String displayCosto = minCosto == maxCosto ? String.format(java.util.Locale.US, "Bs %.2f", minCosto) : String.format(java.util.Locale.US, "Bs %.0f - Bs %.0f", minCosto, maxCosto);
            String displayVenta = minVenta == maxVenta ? String.format(java.util.Locale.US, "Bs %.2f", minVenta) : String.format(java.util.Locale.US, "Bs %.0f - Bs %.0f", minVenta, maxVenta);
            String displayMargen = minMargen == maxMargen ? String.format(java.util.Locale.US, "%.0f%%", minMargen) : String.format(java.util.Locale.US, "%.0f%% - %.0f%%", minMargen, maxMargen);

            h.tvPrecioCosto.setText(displayCosto);
            h.tvPrecioVenta.setText(displayVenta);
            h.tvMargen.setText(displayMargen);

            
            h.ivChevron.setImageResource(item.isExpanded ? R.drawable.ic_chevron_down : R.drawable.ic_chevron_down);
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

            
            if (canManage) {
                h.btnCopy.setVisibility(View.VISIBLE);
            } else {
                h.btnCopy.setVisibility(View.GONE);
            }

            h.btnCopy.setOnClickListener(v -> {
                if (actionListener != null) actionListener.onCopyClick(first);
            });

        } else if (holder instanceof ChildViewHolder) {
            ChildViewHolder h = (ChildViewHolder) holder;
            Producto p = item.producto;

            h.tvSku.setText(p.getSku() != null ? "SKU: " + p.getSku() : "SKU: N/A");
            bindAttributesText(h.tvVariant, p);

            if (p.getProveedor() != null) {
                h.tvProveedor.setText("Proveedor: " + p.getProveedor().getName());
            } else {
                h.tvProveedor.setText("Proveedor: N/A");
            }

            
            double costo = p.getPrecioCosto();
            double venta = p.getPrecioVenta();
            h.tvPrecioCosto.setText(String.format(java.util.Locale.US, "Bs %.2f", costo));
            h.tvPrecioVenta.setText(String.format(java.util.Locale.US, "Bs %.2f", venta));

            double profit = venta - costo;
            double pct = venta > 0 ? (profit / venta) * 100 : 0;
            h.tvMargen.setText(String.format(java.util.Locale.US, "%.0f%%", pct));

            
            if (canManage) {
                h.btnEdit.setVisibility(View.VISIBLE);
                h.btnDelete.setVisibility(View.VISIBLE);
            } else {
                h.btnEdit.setVisibility(View.GONE);
                h.btnDelete.setVisibility(View.GONE);
            }

            h.btnEdit.setOnClickListener(v -> {
                if (actionListener != null) actionListener.onEditClick(p);
            });
            h.btnDelete.setOnClickListener(v -> {
                if (actionListener != null) actionListener.onDeleteClick(p);
            });
            h.itemView.setOnLongClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onProductLongClick(p);
                    return true;
                }
                return false;
            });
        }
    }

    private void bindAttributesText(TextView tvVariant, Producto p) {
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
        TextView tvSku, tvNombre, tvProveedor, tvVariant;
        TextView tvPrecioCosto, tvPrecioVenta, tvMargen;
        ImageButton btnDelete, btnEdit, btnCopy;

        public SingleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSku = itemView.findViewById(R.id.tvSku);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvVariant = itemView.findViewById(R.id.tvVariant);
            tvProveedor = itemView.findViewById(R.id.tvProveedor);
            tvPrecioCosto = itemView.findViewById(R.id.tvPrecioCosto);
            tvPrecioVenta = itemView.findViewById(R.id.tvPrecioVenta);
            tvMargen = itemView.findViewById(R.id.tvMargen);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnCopy = itemView.findViewById(R.id.btnCopy);
        }
    }

    public static class ParentViewHolder extends RecyclerView.ViewHolder {
        ImageView ivChevron;
        TextView tvNombre, tvVariantCount, tvProveedor;
        TextView tvPrecioCosto, tvPrecioVenta, tvMargen;
        ImageButton btnCopy;

        public ParentViewHolder(@NonNull View itemView) {
            super(itemView);
            ivChevron = itemView.findViewById(R.id.ivChevron);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvVariantCount = itemView.findViewById(R.id.tvVariantCount);
            tvProveedor = itemView.findViewById(R.id.tvProveedor);
            tvPrecioCosto = itemView.findViewById(R.id.tvPrecioCosto);
            tvPrecioVenta = itemView.findViewById(R.id.tvPrecioVenta);
            tvMargen = itemView.findViewById(R.id.tvMargen);
            btnCopy = itemView.findViewById(R.id.btnCopy);
        }
    }

    public static class ChildViewHolder extends RecyclerView.ViewHolder {
        TextView tvTreeLine, tvSku, tvProveedor, tvVariant;
        TextView tvPrecioCosto, tvPrecioVenta, tvMargen;
        ImageButton btnDelete, btnEdit;

        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTreeLine = itemView.findViewById(R.id.tvTreeLine);
            tvSku = itemView.findViewById(R.id.tvSku);
            tvVariant = itemView.findViewById(R.id.tvVariant);
            tvProveedor = itemView.findViewById(R.id.tvProveedor);
            tvPrecioCosto = itemView.findViewById(R.id.tvPrecioCosto);
            tvPrecioVenta = itemView.findViewById(R.id.tvPrecioVenta);
            tvMargen = itemView.findViewById(R.id.tvMargen);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}
