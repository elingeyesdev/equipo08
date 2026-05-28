package com.example.template.ui;

import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.template.R;
import com.example.template.network.ApiClient;
import com.example.template.network.ApiService;
import com.example.template.network.models.DetalleItem;
import com.example.template.network.models.Stock;
import com.example.template.network.models.Sucursal;
import com.example.template.network.models.Venta;
import com.example.template.network.models.VentaItem;
import com.example.template.network.models.VentaRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SalesFragment extends Fragment {

    // Tab buttons & containers
    private Button btnTabPOS, btnTabHistory;
    private LinearLayout llTabPOS, llTabHistory;

    // Terminal POS Views
    private Spinner spinnerBranch;
    private EditText etSearch;
    private RecyclerView rvProducts;
    private SalesProductAdapter productAdapter;

    // Cart / Billing Views
    private CardView cvCartPanel;
    private TextView tvCartCount, tvCartTotal;
    private Button btnToggleCart, btnCheckout;
    private LinearLayout llCartDetails;
    private EditText etClienteNombre, etClienteDoc;
    private RecyclerView rvCartItems;
    private CartAdapter cartAdapter;

    // History Views
    private RecyclerView rvSalesHistory;
    private SalesHistoryAdapter historyAdapter;

    // API
    private ApiService apiService;

    // State Variables
    private List<Sucursal> sucursalesList = new ArrayList<>();
    private List<Stock> allStockList = new ArrayList<>();
    private List<Stock> filteredStockList = new ArrayList<>();
    private List<Venta> salesHistoryList = new ArrayList<>();
    
    // Cart products list in memory
    private List<CartProduct> cartProducts = new ArrayList<>();
    private boolean isCartExpanded = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sales, container, false);

        apiService = ApiClient.getClient(getContext()).create(ApiService.class);

        // Bind tabs
        btnTabPOS = view.findViewById(R.id.btnTabPOS);
        btnTabHistory = view.findViewById(R.id.btnTabHistory);
        llTabPOS = view.findViewById(R.id.llTabPOS);
        llTabHistory = view.findViewById(R.id.llTabHistory);

        // Bind POS
        spinnerBranch = view.findViewById(R.id.spinnerBranch);
        etSearch = view.findViewById(R.id.etSearch);
        rvProducts = view.findViewById(R.id.rvProducts);

        // Bind Cart
        cvCartPanel = view.findViewById(R.id.cvCartPanel);
        tvCartCount = view.findViewById(R.id.tvCartCount);
        tvCartTotal = view.findViewById(R.id.tvCartTotal);
        btnToggleCart = view.findViewById(R.id.btnToggleCart);
        llCartDetails = view.findViewById(R.id.llCartDetails);
        etClienteNombre = view.findViewById(R.id.etClienteNombre);
        etClienteDoc = view.findViewById(R.id.etClienteDoc);
        rvCartItems = view.findViewById(R.id.rvCartItems);
        btnCheckout = view.findViewById(R.id.btnCheckout);

        // Bind History
        rvSalesHistory = view.findViewById(R.id.rvSalesHistory);

        setupTabs();
        setupRecyclerViews();
        setupSearchAndFilters();

        loadSucursales();
        loadStock();

        return view;
    }

    private void setupTabs() {
        btnTabPOS.setOnClickListener(v -> {
            llTabPOS.setVisibility(View.VISIBLE);
            llTabHistory.setVisibility(View.GONE);
            btnTabPOS.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2b3b55")));
            btnTabPOS.setTextColor(Color.WHITE);
            btnTabHistory.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            btnTabHistory.setTextColor(Color.parseColor("#475569"));
        });

        btnTabHistory.setOnClickListener(v -> {
            llTabPOS.setVisibility(View.GONE);
            llTabHistory.setVisibility(View.VISIBLE);
            btnTabHistory.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2b3b55")));
            btnTabHistory.setTextColor(Color.WHITE);
            btnTabPOS.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            btnTabPOS.setTextColor(Color.parseColor("#475569"));
            loadSalesHistory();
        });

        // Toggle cart expand/collapse
        btnToggleCart.setOnClickListener(v -> {
            isCartExpanded = !isCartExpanded;
            if (isCartExpanded) {
                llCartDetails.setVisibility(View.VISIBLE);
                btnToggleCart.setText("Ocultar");
            } else {
                llCartDetails.setVisibility(View.GONE);
                btnToggleCart.setText("Ver Detalles");
            }
        });

        etClienteNombre.setText("Cliente Casual");

        btnCheckout.setOnClickListener(v -> handleCheckout());
    }

    private void setupRecyclerViews() {
        // POS Catalogue: 2 column grid
        rvProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        productAdapter = new SalesProductAdapter(filteredStockList, stockItem -> addToCart(stockItem));
        rvProducts.setAdapter(productAdapter);

        // Cart items: Linear layout
        rvCartItems.setLayoutManager(new LinearLayoutManager(getContext()));
        cartAdapter = new CartAdapter(cartProducts, new CartAdapter.CartActionListener() {
            @Override
            public void onQuantityChange(CartProduct product, int newQty) {
                product.setCantidad(newQty);
                updateCartTotals();
            }

            @Override
            public void onDelete(CartProduct product) {
                cartProducts.remove(product);
                updateCartTotals();
                cartAdapter.notifyDataSetChanged();
            }
        });
        rvCartItems.setAdapter(cartAdapter);

        // History items: Linear layout
        rvSalesHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        historyAdapter = new SalesHistoryAdapter(salesHistoryList, venta -> showComprobanteDialog(venta));
        rvSalesHistory.setAdapter(historyAdapter);
    }

    private void setupSearchAndFilters() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCatalog();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        spinnerBranch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cartProducts.clear();
                updateCartTotals();
                cartAdapter.notifyDataSetChanged();
                filterCatalog();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadSucursales() {
        apiService.getSucursales().enqueue(new Callback<List<Sucursal>>() {
            @Override
            public void onResponse(Call<List<Sucursal>> call, Response<List<Sucursal>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    sucursalesList.clear();
                    // Filter only active branches
                    for (Sucursal s : response.body()) {
                        if (s.isActive()) {
                            sucursalesList.add(s);
                        }
                    }
                    setupSpinner();
                }
            }

            @Override
            public void onFailure(Call<List<Sucursal>> call, Throwable t) {
                if(getContext() != null) Toast.makeText(getContext(), "Error cargando sucursales", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSpinner() {
        if (getContext() == null || sucursalesList.isEmpty()) return;

        List<String> options = new ArrayList<>();
        for (Sucursal s : sucursalesList) {
            options.add(s.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBranch.setAdapter(adapter);
    }

    private void loadStock() {
        apiService.getStock().enqueue(new Callback<List<Stock>>() {
            @Override
            public void onResponse(Call<List<Stock>> call, Response<List<Stock>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allStockList = response.body();
                    filterCatalog();
                }
            }

            @Override
            public void onFailure(Call<List<Stock>> call, Throwable t) {
                if(getContext() != null) Toast.makeText(getContext(), "Error cargando inventario", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadSalesHistory() {
        apiService.getVentas().enqueue(new Callback<List<Venta>>() {
            @Override
            public void onResponse(Call<List<Venta>> call, Response<List<Venta>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    salesHistoryList.clear();
                    salesHistoryList.addAll(response.body());
                    historyAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Venta>> call, Throwable t) {
                if(getContext() != null) Toast.makeText(getContext(), "Error al cargar historial", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterCatalog() {
        filteredStockList.clear();
        if (spinnerBranch.getSelectedItemPosition() < 0 || sucursalesList.isEmpty()) {
            productAdapter.notifyDataSetChanged();
            return;
        }

        Sucursal selectedBranch = sucursalesList.get(spinnerBranch.getSelectedItemPosition());
        String query = etSearch.getText().toString().toLowerCase().trim();

        for (Stock s : allStockList) {
            if (s.getSucursalId() != null && s.getSucursalId().equals(selectedBranch.getId()) && s.getCantidadTotal() > 0) {
                boolean matches = true;
                if (!query.isEmpty() && s.getProducto() != null) {
                    String name = s.getProducto().getName() != null ? s.getProducto().getName().toLowerCase() : "";
                    String sku = s.getProducto().getSku() != null ? s.getProducto().getSku().toLowerCase() : "";
                    if (!name.contains(query) && !sku.contains(query)) {
                        matches = false;
                    }
                }
                if (matches) {
                    filteredStockList.add(s);
                }
            }
        }
        productAdapter.notifyDataSetChanged();
    }

    private void addToCart(Stock item) {
        if (item.getProducto() == null) return;

        // Check if already in cart
        CartProduct existing = null;
        for (CartProduct cp : cartProducts) {
            if (cp.getProductoId().equals(item.getProductoId())) {
                existing = cp;
                break;
            }
        }

        if (existing != null) {
            if (existing.getCantidad() >= item.getCantidadTotal()) {
                Toast.makeText(getContext(), "No hay suficiente stock físico disponible", Toast.LENGTH_SHORT).show();
                return;
            }
            existing.setCantidad(existing.getCantidad() + 1);
        } else {
            cartProducts.add(new CartProduct(
                item.getProductoId(),
                item.getProducto().getName(),
                item.getProducto().getSku(),
                item.getProducto().getPrecioVenta(),
                1,
                item.getCantidadTotal()
            ));
        }

        updateCartTotals();
        cartAdapter.notifyDataSetChanged();
        Toast.makeText(getContext(), "Añadido al carrito: " + item.getProducto().getName(), Toast.LENGTH_SHORT).show();
    }

    private void updateCartTotals() {
        double total = 0;
        int totalItems = 0;

        for (CartProduct cp : cartProducts) {
            total += cp.getCantidad() * cp.getPrecioUnitario();
            totalItems += cp.getCantidad();
        }

        if (totalItems == 0) {
            tvCartCount.setText("Carrito vacío");
            cvCartPanel.setVisibility(View.GONE);
        } else {
            tvCartCount.setText(totalItems + " art. en carrito");
            cvCartPanel.setVisibility(View.VISIBLE);
        }
        tvCartTotal.setText(String.format("Bs %.2f", total));
    }

    private void handleCheckout() {
        if (spinnerBranch.getSelectedItemPosition() < 0 || sucursalesList.isEmpty()) {
            Toast.makeText(getContext(), "Selecciona una sucursal", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cartProducts.isEmpty()) {
            Toast.makeText(getContext(), "El carrito está vacío", Toast.LENGTH_SHORT).show();
            return;
        }

        String clienteNombre = etClienteNombre.getText().toString().trim();
        if (clienteNombre.isEmpty()) {
            Toast.makeText(getContext(), "Ingresa el nombre del cliente", Toast.LENGTH_SHORT).show();
            return;
        }

        String clienteDoc = etClienteDoc.getText().toString().trim();
        Sucursal selectedBranch = sucursalesList.get(spinnerBranch.getSelectedItemPosition());

        List<VentaItem> items = new ArrayList<>();
        for (CartProduct cp : cartProducts) {
            items.add(new VentaItem(cp.getProductoId(), cp.getCantidad()));
        }

        VentaRequest request = new VentaRequest(
            selectedBranch.getId(),
            clienteNombre,
            clienteDoc.isEmpty() ? null : clienteDoc,
            items
        );

        btnCheckout.setEnabled(false);
        btnCheckout.setText("Procesando cobro...");

        apiService.createVenta(request).enqueue(new Callback<Venta>() {
            @Override
            public void onResponse(Call<Venta> call, Response<Venta> response) {
                btnCheckout.setEnabled(true);
                btnCheckout.setText("Cobrar y Emitir Comprobante");

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), "¡Venta registrada exitosamente!", Toast.LENGTH_LONG).show();
                    
                    // Show receipt dialog
                    Venta savedVenta = response.body();
                    showComprobanteDialog(savedVenta);

                    // Reset cart
                    cartProducts.clear();
                    updateCartTotals();
                    cartAdapter.notifyDataSetChanged();
                    etClienteNombre.setText("Cliente Casual");
                    etClienteDoc.setText("");
                    
                    // Collapse cart
                    isCartExpanded = false;
                    llCartDetails.setVisibility(View.GONE);
                    btnToggleCart.setText("Ver Detalles");

                    // Reload inventories
                    loadStock();
                } else {
                    Toast.makeText(getContext(), "Error al procesar la venta en el servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Venta> call, Throwable t) {
                btnCheckout.setEnabled(true);
                btnCheckout.setText("Cobrar y Emitir Comprobante");
                Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showComprobanteDialog(Venta venta) {
        if (getContext() == null) return;

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_comprobante, null);
        TextView tvNro = dialogView.findViewById(R.id.tvComprobanteNro);
        TextView tvFecha = dialogView.findViewById(R.id.tvComprobanteFecha);
        TextView tvSuc = dialogView.findViewById(R.id.tvComprobanteSucursal);
        TextView tvCli = dialogView.findViewById(R.id.tvComprobanteCliente);
        TextView tvDoc = dialogView.findViewById(R.id.tvComprobanteDocumento);
        LinearLayout llDetalleContainer = dialogView.findViewById(R.id.llComprobanteDetalleContainer);
        TextView tvTotal = dialogView.findViewById(R.id.tvComprobanteTotal);
        Button btnCerrar = dialogView.findViewById(R.id.btnCerrarComprobante);

        // Fill headers
        tvNro.setText("Comprobante Nro: " + (venta.getNumeroComprobante() != null ? venta.getNumeroComprobante() : "N/A"));
        
        // Date formatting fallback
        String dateStr = venta.getFecha();
        if (dateStr != null && dateStr.contains("T")) {
            dateStr = dateStr.replace("T", " ").substring(0, 19);
        }
        tvFecha.setText("Fecha: " + (dateStr != null ? dateStr : "N/A"));
        tvSuc.setText("Sucursal: " + (venta.getSucursal() != null ? venta.getSucursal().getName() : "Principal"));
        tvCli.setText("Nombre/Razón Social: " + (venta.getClienteNombre() != null ? venta.getClienteNombre() : "Cliente Casual"));
        tvDoc.setText("NIT/CI: " + (venta.getClienteDocumento() != null ? venta.getClienteDocumento() : "N/A"));
        tvTotal.setText(String.format("Bs %.2f", venta.getTotal()));

        // Add dynamic items lines
        if (venta.getDetalle() != null) {
            llDetalleContainer.removeAllViews();
            for (DetalleItem item : venta.getDetalle()) {
                View row = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, null);
                TextView text = row.findViewById(android.R.id.text1);
                
                String label = String.format(java.util.Locale.US,
                    "%d x [%s] %s\n   P. Unit: Bs %.2f | Subtotal: Bs %.2f",
                    item.getCantidad(), item.getSku(), item.getName(), item.getPrecioUnitario(), item.getSubtotal()
                );
                
                text.setText(label);
                text.setTextColor(Color.parseColor("#475569"));
                text.setTextSize(11f);
                text.setPadding(0, 8, 0, 8);
                
                llDetalleContainer.addView(row);
            }
        }

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        btnCerrar.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    // --- INNER CLASS REPRESENTING CART PRODUCT DRAFT ---
    private static class CartProduct {
        private String productoId;
        private String name;
        private String sku;
        private double precioUnitario;
        private int cantidad;
        private int maxStock;

        public CartProduct(String productoId, String name, String sku, double precioUnitario, int cantidad, int maxStock) {
            this.productoId = productoId;
            this.name = name;
            this.sku = sku;
            this.precioUnitario = precioUnitario;
            this.cantidad = cantidad;
            this.maxStock = maxStock;
        }

        public String getProductoId() { return productoId; }
        public String getName() { return name; }
        public String getSku() { return sku; }
        public double getPrecioUnitario() { return precioUnitario; }
        public int getCantidad() { return cantidad; }
        public void setCantidad(int cantidad) { this.cantidad = cantidad; }
        public int getMaxStock() { return maxStock; }
    }

    // --- POS PRODUCT CATALOG LIST RECYCLER ADAPTER ---
    private static class SalesProductAdapter extends RecyclerView.Adapter<SalesProductAdapter.ViewHolder> {
        private List<Stock> list;
        private OnProductClickListener listener;

        public interface OnProductClickListener {
            void onProductClick(Stock stockItem);
        }

        public SalesProductAdapter(List<Stock> list, OnProductClickListener listener) {
            this.list = list;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sales_product, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Stock s = list.get(position);
            holder.tvSku.setText(s.getProducto() != null ? s.getProducto().getSku() : "N/A");
            holder.tvNombre.setText(s.getProducto() != null ? s.getProducto().getName() : "N/A");
            
            double precio = s.getProducto() != null ? s.getProducto().getPrecioVenta() : 0.0;
            holder.tvPrecio.setText(String.format("Bs %.2f", precio));
            holder.tvStockBadge.setText(s.getCantidadTotal() + " DISP.");

            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProductClick(s);
                }
            });
            holder.btnAddToCart.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProductClick(s);
                }
            });
        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvSku, tvNombre, tvPrecio, tvStockBadge;
            ImageButton btnAddToCart;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvSku = itemView.findViewById(R.id.tvSku);
                tvNombre = itemView.findViewById(R.id.tvNombre);
                tvPrecio = itemView.findViewById(R.id.tvPrecio);
                tvStockBadge = itemView.findViewById(R.id.tvStockBadge);
                btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
            }
        }
    }

    // --- BILLING SHOPPING CART RECYCLER ADAPTER ---
    private static class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
        private List<CartProduct> list;
        private CartActionListener listener;

        public interface CartActionListener {
            void onQuantityChange(CartProduct product, int newQty);
            void onDelete(CartProduct product);
        }

        public CartAdapter(List<CartProduct> list, CartActionListener listener) {
            this.list = list;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_product, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            CartProduct cp = list.get(position);
            holder.tvNombre.setText(cp.getName());
            holder.tvPriceUnit.setText(String.format("Bs %.2f c/u", cp.getPrecioUnitario()));
            holder.tvQty.setText(String.valueOf(cp.getCantidad()));
            
            double subtotal = cp.getCantidad() * cp.getPrecioUnitario();
            holder.tvSubtotal.setText(String.format("Bs %.2f", subtotal));

            holder.btnPlus.setOnClickListener(v -> {
                if (cp.getCantidad() >= cp.getMaxStock()) {
                    Toast.makeText(v.getContext(), "Límite de stock físico alcanzado", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (listener != null) {
                    listener.onQuantityChange(cp, cp.getCantidad() + 1);
                }
                notifyItemChanged(position);
            });

            holder.btnMinus.setOnClickListener(v -> {
                if (cp.getCantidad() > 1) {
                    if (listener != null) {
                        listener.onQuantityChange(cp, cp.getCantidad() - 1);
                    }
                    notifyItemChanged(position);
                }
            });

            holder.btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDelete(cp);
                }
            });
        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvNombre, tvPriceUnit, tvQty, tvSubtotal;
            ImageButton btnMinus, btnPlus, btnDelete;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvNombre = itemView.findViewById(R.id.tvNombre);
                tvPriceUnit = itemView.findViewById(R.id.tvPriceUnit);
                tvQty = itemView.findViewById(R.id.tvQty);
                tvSubtotal = itemView.findViewById(R.id.tvSubtotal);
                btnMinus = itemView.findViewById(R.id.btnMinus);
                btnPlus = itemView.findViewById(R.id.btnPlus);
                btnDelete = itemView.findViewById(R.id.btnDelete);
            }
        }
    }

    // --- RECENT SALES HISTORY RECYCLER ADAPTER ---
    private static class SalesHistoryAdapter extends RecyclerView.Adapter<SalesHistoryAdapter.ViewHolder> {
        private List<Venta> list;
        private OnSaleClickListener listener;

        public interface OnSaleClickListener {
            void onSaleClick(Venta venta);
        }

        public SalesHistoryAdapter(List<Venta> list, OnSaleClickListener listener) {
            this.list = list;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sales_history, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Venta v = list.get(position);
            holder.tvComprobante.setText(v.getNumeroComprobante() != null ? v.getNumeroComprobante() : "N/A");
            
            String label = (v.getClienteNombre() != null ? v.getClienteNombre() : "Cliente Casual");
            if (v.getSucursal() != null) {
                label += " (" + v.getSucursal().getName() + ")";
            }
            holder.tvCliente.setText(label);
            
            String dateStr = v.getFecha();
            if (dateStr != null && dateStr.contains("T")) {
                dateStr = dateStr.replace("T", " ").substring(0, 16);
            }
            holder.tvFecha.setText(dateStr != null ? dateStr : "N/A");
            
            holder.tvTotal.setText(String.format("Bs %.2f", v.getTotal()));

            holder.itemView.setOnClickListener(click -> {
                if (listener != null) {
                    listener.onSaleClick(v);
                }
            });
        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvComprobante, tvCliente, tvFecha, tvTotal;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvComprobante = itemView.findViewById(R.id.tvComprobante);
                tvCliente = itemView.findViewById(R.id.tvCliente);
                tvFecha = itemView.findViewById(R.id.tvFecha);
                tvTotal = itemView.findViewById(R.id.tvTotal);
            }
        }
    }
}
