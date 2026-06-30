package com.example.template.ui;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.content.res.ColorStateList;
import android.os.Bundle;
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
import android.widget.ImageView;
import com.example.template.utils.ImageLoader;

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
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TerminalPOSFragment extends Fragment {

    
    private Spinner spinnerBranch;
    private RecyclerView rvProducts;
    private SalesProductAdapter productAdapter;

    
    private LinearLayout llCategoriesContainer;
    private String selectedCategory = "Todos";
    private final String[] categories = {
        "Todos", "Abarrotes y Alimentos", "Bebidas", "Ropa y Moda", "Zapatos y Calzado",
        "Belleza y Cuidado Personal", "Joyería y Relojes", "Juguetes y Niños",
        "Hogar y Decoración", "Electrónica y Tecnología", "Ferretería y Construcción",
        "Deportes y Aire Libre", "Entretenimiento y Ocio", "Otros"
    };

    
    private CardView cvCartPanel;
    private TextView tvCartCount, tvCartTotal;
    private Button btnToggleCart, btnCheckout, btnTicket;
    private LinearLayout llCartDetails;
    private RecyclerView rvCartItems;
    private CartAdapter cartAdapter;

    
    private Button btnCartTabNew, btnCartTabHistory;
    private LinearLayout llCartTabNewContent, llCartTabHistoryContent;
    private RecyclerView rvSavedTickets;
    private TextView tvEmptyTickets;
    private SavedTicketsAdapter savedTicketsAdapter;

    
    private androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeRefreshProducts;

    
    private ApiService apiService;

    
    private List<Sucursal> sucursalesList = new ArrayList<>();
    private List<Stock> allStockList = new ArrayList<>();
    private List<Stock> filteredStockList = new ArrayList<>();
    private com.example.template.utils.SessionManager sessionManager;
    
    
    private List<CartProduct> cartProducts = new ArrayList<>();
    private boolean isCartExpanded = false;

    
    private static List<SavedTicket> savedTicketsList = new ArrayList<>();
    private static int ticketCounter = 11; 

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_terminal_pos, container, false);

        apiService = ApiClient.getClient(getContext()).create(ApiService.class);
        sessionManager = new com.example.template.utils.SessionManager(getContext());

        
        spinnerBranch = view.findViewById(R.id.spinnerBranch);
        rvProducts = view.findViewById(R.id.rvProducts);

        
        cvCartPanel = view.findViewById(R.id.cvCartPanel);
        tvCartCount = view.findViewById(R.id.tvCartCount);
        tvCartTotal = view.findViewById(R.id.tvCartTotal);
        btnToggleCart = view.findViewById(R.id.btnToggleCart);
        llCartDetails = view.findViewById(R.id.llCartDetails);
        rvCartItems = view.findViewById(R.id.rvCartItems);
        btnCheckout = view.findViewById(R.id.btnCheckout);
        btnTicket = view.findViewById(R.id.btnTicket);

        
        btnCartTabNew = view.findViewById(R.id.btnCartTabNew);
        btnCartTabHistory = view.findViewById(R.id.btnCartTabHistory);
        llCartTabNewContent = view.findViewById(R.id.llCartTabNewContent);
        llCartTabHistoryContent = view.findViewById(R.id.llCartTabHistoryContent);
        rvSavedTickets = view.findViewById(R.id.rvSavedTickets);
        tvEmptyTickets = view.findViewById(R.id.tvEmptyTickets);

        
        swipeRefreshProducts = view.findViewById(R.id.swipeRefreshProducts);

        
        swipeRefreshProducts.setOnRefreshListener(() -> loadStock());

        loadTicketsFromPrefs();
        setupCartDetailsToggle();
        setupRecyclerViews();
        setupSearchAndFilters();
        setupCategoryFilters(view);
        setupSubtabs();

        loadSucursales();
        loadStock();

        return view;
    }

    private void setupCategoryFilters(View view) {
        llCategoriesContainer = view.findViewById(R.id.llCategoriesContainer);
        if (llCategoriesContainer == null) return;

        llCategoriesContainer.removeAllViews();
        for (String cat : categories) {
            TextView tv = new TextView(getContext());
            tv.setText(cat);
            tv.setTextSize(13f);
            tv.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
            tv.setGravity(android.view.Gravity.CENTER);
            
            
            float density = getResources().getDisplayMetrics().density;
            int hp = (int) (16 * density);
            int vp = (int) (10 * density);
            tv.setPadding(hp, vp, hp, vp);

            
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
            int m = (int) (4 * density);
            lp.setMargins(m, 0, m, 0);
            tv.setLayoutParams(lp);

            tv.setTag(cat);
            updateCategoryStyle(tv, cat.equals(selectedCategory));

            tv.setOnClickListener(v -> {
                selectedCategory = cat;
                for (int i = 0; i < llCategoriesContainer.getChildCount(); i++) {
                    View child = llCategoriesContainer.getChildAt(i);
                    if (child instanceof TextView) {
                        updateCategoryStyle((TextView) child, child.getTag().equals(selectedCategory));
                    }
                }
                filterCatalog();
            });

            llCategoriesContainer.addView(tv);
        }
    }

    private void updateCategoryStyle(TextView tv, boolean isSelected) {
        float density = getResources().getDisplayMetrics().density;
        android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
        gd.setCornerRadius(8 * density);
        
        if (isSelected) {
            gd.setColor(Color.parseColor("#0f172a")); 
            gd.setStroke((int) (1.5 * density), Color.parseColor("#0f172a"));
            tv.setTextColor(Color.WHITE);
        } else {
            gd.setColor(Color.WHITE);
            gd.setStroke((int) (1.5 * density), Color.parseColor("#cbd5e1")); 
            tv.setTextColor(Color.parseColor("#475569"));
        }
        tv.setBackground(gd);
    }

    private void saveTicketsToPrefs() {
        if (getContext() == null) return;
        android.content.SharedPreferences prefs = getContext().getSharedPreferences("pos_prefs", android.content.Context.MODE_PRIVATE);
        String json = new com.google.gson.Gson().toJson(savedTicketsList);
        prefs.edit().putString("saved_tickets", json).putInt("ticket_counter", ticketCounter).apply();
    }

    private void loadTicketsFromPrefs() {
        if (getContext() == null) return;
        android.content.SharedPreferences prefs = getContext().getSharedPreferences("pos_prefs", android.content.Context.MODE_PRIVATE);
        ticketCounter = prefs.getInt("ticket_counter", 11);
        String json = prefs.getString("saved_tickets", null);
        if (json != null) {
            try {
                java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<ArrayList<SavedTicket>>(){}.getType();
                List<SavedTicket> loaded = new com.google.gson.Gson().fromJson(json, type);
                if (loaded != null) {
                    savedTicketsList.clear();
                    savedTicketsList.addAll(loaded);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setupCartDetailsToggle() {
        btnToggleCart.setOnClickListener(v -> {
            isCartExpanded = !isCartExpanded;
            if (isCartExpanded) {
                llCartDetails.setVisibility(View.VISIBLE);
                btnToggleCart.setText("Ocultar");
            } else {
                llCartDetails.setVisibility(View.GONE);
                btnToggleCart.setText("Ver detalles");
            }
        });

        btnCheckout.setOnClickListener(v -> {
            if (cartProducts.isEmpty()) {
                Toast.makeText(getContext(), "El carrito está vacío", Toast.LENGTH_SHORT).show();
                return;
            }
            showConfirmarPagoDialog();
        });
        btnTicket.setOnClickListener(v -> handleSaveAsTicket());
    }

    private void setupSubtabs() {
        btnCartTabNew.setOnClickListener(v -> {
            llCartTabNewContent.setVisibility(View.VISIBLE);
            llCartTabHistoryContent.setVisibility(View.GONE);
            btnCartTabNew.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            btnCartTabNew.setTextColor(Color.WHITE);
            btnCartTabHistory.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            btnCartTabHistory.setTextColor(Color.parseColor("#94a3b8"));
        });

        btnCartTabHistory.setOnClickListener(v -> {
            llCartTabNewContent.setVisibility(View.GONE);
            llCartTabHistoryContent.setVisibility(View.VISIBLE);
            btnCartTabHistory.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            btnCartTabHistory.setTextColor(Color.WHITE);
            btnCartTabNew.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            btnCartTabNew.setTextColor(Color.parseColor("#94a3b8"));
            updateSavedTicketsListVisibility();
        });
    }

    private void updateSavedTicketsListVisibility() {
        if (savedTicketsList.isEmpty()) {
            rvSavedTickets.setVisibility(View.GONE);
            tvEmptyTickets.setVisibility(View.VISIBLE);
        } else {
            rvSavedTickets.setVisibility(View.VISIBLE);
            tvEmptyTickets.setVisibility(View.GONE);
        }
        savedTicketsAdapter.notifyDataSetChanged();
    }

    private void handleSaveAsTicket() {
        if (cartProducts.isEmpty()) {
            Toast.makeText(getContext(), "El carrito está vacío", Toast.LENGTH_SHORT).show();
            return;
        }

        String ticketId = "Orden #0000" + (ticketCounter++);
        String timeStr = new java.text.SimpleDateFormat("HH:mm", java.util.Locale.US).format(new java.util.Date());

        double total = 0;
        int itemsCount = 0;
        for (CartProduct cp : cartProducts) {
            total += cp.getCantidad() * cp.getPrecioUnitario();
            itemsCount += cp.getCantidad();
        }

        
        List<CartProduct> itemsCopy = new ArrayList<>();
        for (CartProduct cp : cartProducts) {
            itemsCopy.add(new CartProduct(
                cp.getProductoId(),
                cp.getName(),
                cp.getSku(),
                cp.getPrecioUnitario(),
                cp.getCantidad(),
                cp.getMaxStock()
            ));
        }

        SavedTicket ticket = new SavedTicket(ticketId, timeStr, itemsCopy, total, itemsCount);
        savedTicketsList.add(ticket);
        saveTicketsToPrefs();

        Toast.makeText(getContext(), "Pedido guardado como ticket: " + ticketId, Toast.LENGTH_LONG).show();

        
        cartProducts.clear();
        updateCartTotals();
        cartAdapter.notifyDataSetChanged();

        
        btnCartTabHistory.performClick();
    }

    private void showConfirmarPagoDialog() {
        if (getContext() == null) return;

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_confirmar_pago, null);
        EditText etDialogClienteNombre = dialogView.findViewById(R.id.etDialogClienteNombre);
        EditText etDialogClienteDoc = dialogView.findViewById(R.id.etDialogClienteDoc);
        Spinner spinnerDialogPaymentMethod = dialogView.findViewById(R.id.spinnerDialogPaymentMethod);
        EditText etDialogMontoRecibido = dialogView.findViewById(R.id.etDialogMontoRecibido);
        TextView tvDialogTotalPagar = dialogView.findViewById(R.id.tvDialogTotalPagar);
        Button btnDialogConfirmVenta = dialogView.findViewById(R.id.btnDialogConfirmVenta);
        ImageButton btnDialogClose = dialogView.findViewById(R.id.btnDialogClose);

        
        etDialogClienteNombre.setText("Cliente Casual");
        
        double total = 0;
        for (CartProduct cp : cartProducts) {
            total += cp.getCantidad() * cp.getPrecioUnitario();
        }
        final double finalTotal = total;
        String formattedTotal = String.format(java.util.Locale.US, "Bs %.2f", finalTotal);
        
        tvDialogTotalPagar.setText(formattedTotal);
        etDialogMontoRecibido.setText(String.format(java.util.Locale.US, "%.2f", finalTotal));
        btnDialogConfirmVenta.setText("Confirmar venta • " + formattedTotal);

        
        String[] paymentMethods = {"Efectivo", "QR", "Transferencia", "Tarjeta"};
        ArrayAdapter<String> pmAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, paymentMethods);
        pmAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDialogPaymentMethod.setAdapter(pmAdapter);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        btnDialogClose.setOnClickListener(v -> dialog.dismiss());

        btnDialogConfirmVenta.setOnClickListener(v -> {
            String clientName = etDialogClienteNombre.getText().toString().trim();
            String clientDoc = etDialogClienteDoc.getText().toString().trim();
            String paymentMethod = spinnerDialogPaymentMethod.getSelectedItem().toString();

            if (clientName.isEmpty()) {
                Toast.makeText(getContext(), "Por favor ingresa el nombre del cliente", Toast.LENGTH_SHORT).show();
                return;
            }

            btnDialogConfirmVenta.setEnabled(false);
            btnDialogConfirmVenta.setText("Procesando...");

            handleCheckoutFromDialog(clientName, clientDoc, paymentMethod.toUpperCase(), dialog, btnDialogConfirmVenta);
        });

        dialog.show();
    }

    private void handleCheckoutFromDialog(String clientName, String clientDoc, String paymentMethod, AlertDialog paymentDialog, Button confirmButton) {
        if (spinnerBranch.getSelectedItemPosition() < 0 || sucursalesList.isEmpty()) {
            Toast.makeText(getContext(), "Selecciona una sucursal", Toast.LENGTH_SHORT).show();
            confirmButton.setEnabled(true);
            return;
        }

        Sucursal selectedBranch = sucursalesList.get(spinnerBranch.getSelectedItemPosition());

        List<VentaItem> items = new ArrayList<>();
        for (CartProduct cp : cartProducts) {
            items.add(new VentaItem(cp.getProductoId(), cp.getCantidad()));
        }

        VentaRequest request = new VentaRequest(
            selectedBranch.getId(),
            clientName,
            clientDoc.isEmpty() ? null : clientDoc,
            paymentMethod,
            items
        );

        apiService.createVenta(request).enqueue(new Callback<Venta>() {
            @Override
            public void onResponse(Call<Venta> call, Response<Venta> response) {
                if (paymentDialog != null) paymentDialog.dismiss();

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), "¡Venta registrada exitosamente!", Toast.LENGTH_LONG).show();
                    
                    
                    Venta savedVenta = response.body();
                    showComprobanteDialog(savedVenta);

                    
                    cartProducts.clear();
                    updateCartTotals();
                    cartAdapter.notifyDataSetChanged();
                    
                    
                    isCartExpanded = false;
                    llCartDetails.setVisibility(View.GONE);
                    btnToggleCart.setText("Ver detalles");

                    
                    loadStock();
                } else {
                    Toast.makeText(getContext(), "Error al procesar la venta en el servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Venta> call, Throwable t) {
                confirmButton.setEnabled(true);
                confirmButton.setText("Confirmar venta");
                Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerViews() {
        
        rvProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        productAdapter = new SalesProductAdapter(filteredStockList, stockItem -> addToCart(stockItem));
        rvProducts.setAdapter(productAdapter);

        
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

        
        rvSavedTickets.setLayoutManager(new LinearLayoutManager(getContext()));
        savedTicketsAdapter = new SavedTicketsAdapter(savedTicketsList, new SavedTicketsAdapter.SavedTicketActionListener() {
            @Override
            public void onLoadTicket(SavedTicket ticket) {
                
                cartProducts.clear();
                for (CartProduct cp : ticket.getItems()) {
                    cartProducts.add(new CartProduct(
                        cp.getProductoId(),
                        cp.getName(),
                        cp.getSku(),
                        cp.getPrecioUnitario(),
                        cp.getCantidad(),
                        cp.getMaxStock()
                    ));
                }

                
                savedTicketsList.remove(ticket);
                saveTicketsToPrefs();

                
                updateCartTotals();
                cartAdapter.notifyDataSetChanged();
                btnCartTabNew.performClick();
                Toast.makeText(getContext(), "Ticket " + ticket.getId() + " cargado al carrito", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeleteTicket(SavedTicket ticket) {
                savedTicketsList.remove(ticket);
                saveTicketsToPrefs();
                updateCartTotals();
                updateSavedTicketsListVisibility();
                Toast.makeText(getContext(), "Ticket eliminado", Toast.LENGTH_SHORT).show();
            }
        });
        rvSavedTickets.setAdapter(savedTicketsAdapter);
    }

    private void setupSearchAndFilters() {
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
                    List<Sucursal> allBranches = response.body();
                    sucursalesList.clear();
                    
                    String userRole = sessionManager.getRole();
                    String userSucursalId = sessionManager.getSucursalId();
                    
                    if (!"OWNER".equalsIgnoreCase(userRole) && !"SUPER_ADMIN".equalsIgnoreCase(userRole) && userSucursalId != null && !userSucursalId.isEmpty()) {
                        for (Sucursal s : allBranches) {
                            if (s.isActive() && userSucursalId.equals(s.getId())) {
                                sucursalesList.add(s);
                            }
                        }
                    } else {
                        for (Sucursal s : allBranches) {
                            if (s.isActive()) {
                                sucursalesList.add(s);
                            }
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

        String userRole = sessionManager.getRole();
        String userSucursalId = sessionManager.getSucursalId();
        boolean isRestricted = !"OWNER".equalsIgnoreCase(userRole) && !"SUPER_ADMIN".equalsIgnoreCase(userRole) && userSucursalId != null && !userSucursalId.isEmpty();

        if (isRestricted) {
            spinnerBranch.setEnabled(false);
        } else {
            spinnerBranch.setEnabled(true);
        }
    }

    private void loadStock() {
        apiService.getStock().enqueue(new Callback<List<Stock>>() {
            @Override
            public void onResponse(Call<List<Stock>> call, Response<List<Stock>> response) {
                if (swipeRefreshProducts != null) swipeRefreshProducts.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    allStockList = response.body();
                    filterCatalog();
                }
            }

            @Override
            public void onFailure(Call<List<Stock>> call, Throwable t) {
                if (swipeRefreshProducts != null) swipeRefreshProducts.setRefreshing(false);
                if(getContext() != null) Toast.makeText(getContext(), "Error cargando inventario", Toast.LENGTH_SHORT).show();
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

        for (Stock s : allStockList) {
            if (s.getSucursalId() != null && s.getSucursalId().equals(selectedBranch.getId()) && s.getCantidadTotal() > 0) {
                boolean matches = true;
                if (!"Todos".equals(selectedCategory) && s.getProducto() != null) {
                    String category = s.getProducto().getCategory();
                    if (category == null || !category.equalsIgnoreCase(selectedCategory)) {
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
        } else {
            tvCartCount.setText(totalItems + " prod. en carrito");
        }
        cvCartPanel.setVisibility(View.VISIBLE);
        tvCartTotal.setText(String.format("Bs %.2f", total));

        
        btnCartTabNew.setText("Nueva orden (" + totalItems + ")");
        btnCartTabHistory.setText("Tickets (" + savedTicketsList.size() + ")");
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
        Button btnDescargar = dialogView.findViewById(R.id.btnDescargarComprobante);

        
        tvNro.setText("Comprobante Nro: " + (venta.getNumeroComprobante() != null ? venta.getNumeroComprobante() : "N/A"));
        
        String formattedDate = "N/A";
        String dateStr = venta.getFecha();
        if (dateStr != null) {
            try {
                java.text.SimpleDateFormat utcFormat;
                if (dateStr.contains(".")) {
                    utcFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US);
                } else {
                    utcFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US);
                }
                utcFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                java.util.Date date = utcFormat.parse(dateStr);
                
                java.text.SimpleDateFormat localFormat = new java.text.SimpleDateFormat("dd/MM/yyyy hh:mm a", java.util.Locale.US);
                localFormat.setTimeZone(java.util.TimeZone.getDefault());
                formattedDate = localFormat.format(date);
            } catch (Exception e) {
                try {
                    String clean = dateStr.replace("T", " ");
                    if (clean.length() >= 10) {
                        String datePart = clean.substring(0, 10);
                        String timePart = clean.substring(10);
                        if (timePart.contains(".")) {
                            timePart = timePart.substring(0, timePart.indexOf("."));
                        }
                        String[] parts = datePart.split("-");
                        if (parts.length == 3) {
                            formattedDate = parts[2] + "/" + parts[1] + "/" + parts[0] + timePart;
                        } else {
                            formattedDate = clean;
                        }
                    } else {
                        formattedDate = clean;
                    }
                } catch (Exception ex) {
                    formattedDate = dateStr;
                }
            }
        }
        tvFecha.setText("Fecha: " + formattedDate);

        String sucursalName = "Principal";
        if (venta.getSucursal() != null && venta.getSucursal().getName() != null) {
            sucursalName = venta.getSucursal().getName();
        } else if (venta.getSucursalId() != null) {
            for (com.example.template.network.models.Sucursal s : sucursalesList) {
                if (venta.getSucursalId().equals(s.getId())) {
                    sucursalName = s.getName();
                    break;
                }
            }
        }
        tvSuc.setText("Sucursal: " + sucursalName);
        tvCli.setText("Nombre/Razón Social: " + (venta.getClienteNombre() != null ? venta.getClienteNombre() : "Cliente Casual"));
        tvDoc.setText("NIT/CI: " + (venta.getClienteDocumento() != null ? venta.getClienteDocumento() : "N/A"));
        tvTotal.setText(String.format("Bs %.2f", venta.getTotal()));

        if (venta.getDetalle() != null) {
            llDetalleContainer.removeAllViews();
            float density = getResources().getDisplayMetrics().density;
            for (DetalleItem item : venta.getDetalle()) {
                LinearLayout row = new LinearLayout(getContext());
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                row.setPadding(0, (int)(8 * density), 0, (int)(8 * density));

                TextView tvCant = new TextView(getContext());
                LinearLayout.LayoutParams lpCant = new LinearLayout.LayoutParams((int)(40 * density), ViewGroup.LayoutParams.WRAP_CONTENT);
                tvCant.setLayoutParams(lpCant);
                tvCant.setText(String.valueOf(item.getCantidad()));
                tvCant.setTextColor(Color.parseColor("#475569"));
                tvCant.setTextSize(11f);

                TextView tvDesc = new TextView(getContext());
                LinearLayout.LayoutParams lpDesc = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
                tvDesc.setLayoutParams(lpDesc);
                tvDesc.setText("[" + item.getSku() + "] " + item.getName());
                tvDesc.setTextColor(Color.parseColor("#475569"));
                tvDesc.setTextSize(11f);

                TextView tvUnit = new TextView(getContext());
                LinearLayout.LayoutParams lpUnit = new LinearLayout.LayoutParams((int)(70 * density), ViewGroup.LayoutParams.WRAP_CONTENT);
                tvUnit.setLayoutParams(lpUnit);
                tvUnit.setText(String.format(java.util.Locale.US, "%.2f", item.getPrecioUnitario()));
                tvUnit.setTextColor(Color.parseColor("#475569"));
                tvUnit.setTextSize(11f);
                tvUnit.setGravity(android.view.Gravity.END);

                TextView tvSub = new TextView(getContext());
                LinearLayout.LayoutParams lpSub = new LinearLayout.LayoutParams((int)(80 * density), ViewGroup.LayoutParams.WRAP_CONTENT);
                tvSub.setLayoutParams(lpSub);
                tvSub.setText(String.format(java.util.Locale.US, "%.2f", item.getSubtotal()));
                tvSub.setTextColor(Color.parseColor("#475569"));
                tvSub.setTextSize(11f);
                tvSub.setGravity(android.view.Gravity.END);

                row.addView(tvCant);
                row.addView(tvDesc);
                row.addView(tvUnit);
                row.addView(tvSub);

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
        btnDescargar.setOnClickListener(v -> {
            try {
                String baseUrl = ApiClient.getClient(getContext()).baseUrl().toString();
                String url = baseUrl + "ventas/" + venta.getId() + "/pdf";

                android.app.DownloadManager.Request downloadRequest = new android.app.DownloadManager.Request(android.net.Uri.parse(url));
                downloadRequest.setTitle("Comprobante " + (venta.getNumeroComprobante() != null ? venta.getNumeroComprobante() : "Venta"));
                downloadRequest.setDescription("Descargando PDF de la factura...");
                downloadRequest.setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                
                String filename = (venta.getNumeroComprobante() != null ? venta.getNumeroComprobante() : "comprobante_" + venta.getId()) + ".pdf";
                downloadRequest.setDestinationInExternalPublicDir(android.os.Environment.DIRECTORY_DOWNLOADS, filename);

                com.example.template.utils.SessionManager sessionManager = new com.example.template.utils.SessionManager(getContext());
                String token = sessionManager.getToken();
                if (token != null) {
                    downloadRequest.addRequestHeader("Authorization", "Bearer " + token);
                }
                String tenantId = sessionManager.getTenantId();
                if (tenantId != null) {
                    downloadRequest.addRequestHeader("x-tenant-id", tenantId);
                }

                android.app.DownloadManager manager = (android.app.DownloadManager) getContext().getSystemService(android.content.Context.DOWNLOAD_SERVICE);
                if (manager != null) {
                    manager.enqueue(downloadRequest);
                    Toast.makeText(getContext(), "Descargando factura en la carpeta de Descargas...", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Error: Servicio de descargas no disponible", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), "Error al iniciar descarga: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    
    private static class SavedTicket {
        private String id;
        private String timestamp;
        private List<CartProduct> items;
        private double totalAmount;
        private int totalItemsCount;

        public SavedTicket(String id, String timestamp, List<CartProduct> items, double totalAmount, int totalItemsCount) {
            this.id = id;
            this.timestamp = timestamp;
            this.items = items;
            this.totalAmount = totalAmount;
            this.totalItemsCount = totalItemsCount;
        }

        public String getId() { return id; }
        public String getTimestamp() { return timestamp; }
        public List<CartProduct> getItems() { return items; }
        public double getTotalAmount() { return totalAmount; }
        public int getTotalItemsCount() { return totalItemsCount; }
    }

    
    private static class SavedTicketsAdapter extends RecyclerView.Adapter<SavedTicketsAdapter.ViewHolder> {
        private List<SavedTicket> list;
        private SavedTicketActionListener listener;

        public interface SavedTicketActionListener {
            void onLoadTicket(SavedTicket ticket);
            void onDeleteTicket(SavedTicket ticket);
        }

        public SavedTicketsAdapter(List<SavedTicket> list, SavedTicketActionListener listener) {
            this.list = list;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_saved_ticket, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            SavedTicket ticket = list.get(position);
            holder.tvTicketId.setText(ticket.getId());
            holder.tvTicketTime.setText(ticket.getTimestamp());
            holder.tvTicketSummary.setText(ticket.getTotalItemsCount() + " items • " + String.format(java.util.Locale.US, "Bs %.2f", ticket.getTotalAmount()));

            holder.btnLoadTicket.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onLoadTicket(ticket);
                }
            });

            holder.btnDeleteTicket.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteTicket(ticket);
                }
            });
        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTicketId, tvTicketTime, tvTicketSummary;
            Button btnLoadTicket;
            ImageButton btnDeleteTicket;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTicketId = itemView.findViewById(R.id.tvTicketId);
                tvTicketTime = itemView.findViewById(R.id.tvTicketTime);
                tvTicketSummary = itemView.findViewById(R.id.tvTicketSummary);
                btnLoadTicket = itemView.findViewById(R.id.btnLoadTicket);
                btnDeleteTicket = itemView.findViewById(R.id.btnDeleteTicket);
            }
        }
    }

    
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
            
            if (s.getProducto() != null && s.getProducto().getImagenUrl() != null && !s.getProducto().getImagenUrl().trim().isEmpty()) {
                holder.ivProductImage.setPadding(0, 0, 0, 0);
                holder.ivProductImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                ImageLoader.loadImage(s.getProducto().getImagenUrl(), holder.ivProductImage);
            } else {
                holder.ivProductImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                holder.ivProductImage.setImageResource(R.drawable.ic_product_placeholder);
                int pad = (int) (8 * holder.itemView.getResources().getDisplayMetrics().density);
                holder.ivProductImage.setPadding(pad, pad, pad, pad);
            }

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
            View cvProductImageContainer;
            ImageView ivProductImage;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvSku = itemView.findViewById(R.id.tvSku);
                tvNombre = itemView.findViewById(R.id.tvNombre);
                tvPrecio = itemView.findViewById(R.id.tvPrecio);
                tvStockBadge = itemView.findViewById(R.id.tvStockBadge);
                btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
                cvProductImageContainer = itemView.findViewById(R.id.cvProductImageContainer);
                ivProductImage = itemView.findViewById(R.id.ivProductImage);
            }
        }
    }

    
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

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            getActivity().getWindow().setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }
    }
}
