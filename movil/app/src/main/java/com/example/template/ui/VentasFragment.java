package com.example.template.ui;

import android.app.AlertDialog;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.template.R;
import com.example.template.network.ApiClient;
import com.example.template.network.ApiService;
import com.example.template.network.models.DetalleItem;
import com.example.template.network.models.Sucursal;
import com.example.template.network.models.Venta;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VentasFragment extends Fragment {

    // History Views
    private RecyclerView rvSalesHistory;
    private SalesHistoryAdapter historyAdapter;

    // SwipeRefreshLayout
    private androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeRefreshHistory;

    // API
    private ApiService apiService;

    // State Variables
    private List<Sucursal> sucursalesList = new ArrayList<>();
    private List<Venta> salesHistoryList = new ArrayList<>();
    private List<Venta> allSalesHistoryList = new ArrayList<>();
    
    // History Filters
    private Button btnToggleHistoryFilters;
    private CardView cardHistoryFilters;
    private EditText etSearchHistory;
    private Button btnDateFromHistory, btnDateToHistory, btnClearHistoryFilters;
    private Spinner spinnerBranchHistory, spinnerPaymentHistory;
    private java.util.Calendar historyCalFrom = null, historyCalTo = null;
    private java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.US);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ventas, container, false);

        apiService = ApiClient.getClient(getContext()).create(ApiService.class);

        // Bind History
        rvSalesHistory = view.findViewById(R.id.rvSalesHistory);
        btnToggleHistoryFilters = view.findViewById(R.id.btnToggleHistoryFilters);
        cardHistoryFilters = view.findViewById(R.id.cardHistoryFilters);
        etSearchHistory = view.findViewById(R.id.etSearchHistory);
        btnDateFromHistory = view.findViewById(R.id.btnDateFromHistory);
        btnDateToHistory = view.findViewById(R.id.btnDateToHistory);
        btnClearHistoryFilters = view.findViewById(R.id.btnClearHistoryFilters);
        spinnerBranchHistory = view.findViewById(R.id.spinnerBranchHistory);
        spinnerPaymentHistory = view.findViewById(R.id.spinnerPaymentHistory);

        // Bind SwipeRefreshLayout
        swipeRefreshHistory = view.findViewById(R.id.swipeRefreshHistory);

        // Setup SwipeRefreshLayout
        swipeRefreshHistory.setOnRefreshListener(() -> loadSalesHistory());

        setupRecyclerViews();
        setupSearchAndFilters();

        loadSucursales();
        loadSalesHistory();

        return view;
    }

    private void setupRecyclerViews() {
        // History items: Linear layout
        rvSalesHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        historyAdapter = new SalesHistoryAdapter(salesHistoryList, sucursalesList, venta -> showComprobanteDialog(venta));
        rvSalesHistory.setAdapter(historyAdapter);
    }

    private void setupSearchAndFilters() {
        etSearchHistory.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { filterSalesHistory(); }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnToggleHistoryFilters.setOnClickListener(v -> {
            if (cardHistoryFilters.getVisibility() == View.VISIBLE) {
                cardHistoryFilters.setVisibility(View.GONE);
            } else {
                cardHistoryFilters.setVisibility(View.VISIBLE);
            }
        });
        btnToggleHistoryFilters.setText("Filtrar ventas");

        btnDateFromHistory.setOnClickListener(v -> showHistoryDatePicker(true));
        btnDateToHistory.setOnClickListener(v -> showHistoryDatePicker(false));
        btnClearHistoryFilters.setOnClickListener(v -> clearHistoryFilters());

        AdapterView.OnItemSelectedListener historyFilterListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { filterSalesHistory(); }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };
        spinnerBranchHistory.setOnItemSelectedListener(historyFilterListener);
        spinnerPaymentHistory.setOnItemSelectedListener(historyFilterListener);
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

        List<String> historyBranchOptions = new ArrayList<>();
        historyBranchOptions.add("Cualquier sucursal");
        historyBranchOptions.addAll(options);
        ArrayAdapter<String> historyBranchAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, historyBranchOptions);
        historyBranchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBranchHistory.setAdapter(historyBranchAdapter);

        List<String> paymentOptions = new ArrayList<>();
        paymentOptions.add("Cualquier método");
        paymentOptions.add("Efectivo");
        paymentOptions.add("QR");
        paymentOptions.add("Transferencia");
        paymentOptions.add("Tarjeta");
        ArrayAdapter<String> paymentAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, paymentOptions);
        paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPaymentHistory.setAdapter(paymentAdapter);
    }

    private void loadSalesHistory() {
        apiService.getVentas().enqueue(new Callback<List<Venta>>() {
            @Override
            public void onResponse(Call<List<Venta>> call, Response<List<Venta>> response) {
                if (swipeRefreshHistory != null) swipeRefreshHistory.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    allSalesHistoryList.clear();
                    allSalesHistoryList.addAll(response.body());
                    filterSalesHistory();
                }
            }

            @Override
            public void onFailure(Call<List<Venta>> call, Throwable t) {
                if (swipeRefreshHistory != null) swipeRefreshHistory.setRefreshing(false);
                if(getContext() != null) Toast.makeText(getContext(), "Error al cargar historial", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearHistoryFilters() {
        historyCalFrom = null;
        historyCalTo = null;
        btnDateFromHistory.setText("Desde");
        btnDateToHistory.setText("Hasta");
        spinnerBranchHistory.setSelection(0);
        spinnerPaymentHistory.setSelection(0);
        etSearchHistory.setText("");
        filterSalesHistory();
    }

    private void showHistoryDatePicker(boolean isFrom) {
        java.util.Calendar c = java.util.Calendar.getInstance();
        if (isFrom && historyCalFrom != null) c = historyCalFrom;
        else if (!isFrom && historyCalTo != null) c = historyCalTo;

        android.app.DatePickerDialog dpd = new android.app.DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            java.util.Calendar selected = java.util.Calendar.getInstance();
            selected.set(year, month, dayOfMonth, 0, 0, 0);
            if (isFrom) {
                historyCalFrom = selected;
                btnDateFromHistory.setText(dateFormat.format(historyCalFrom.getTime()));
            } else {
                historyCalTo = selected;
                historyCalTo.set(java.util.Calendar.HOUR_OF_DAY, 23);
                historyCalTo.set(java.util.Calendar.MINUTE, 59);
                historyCalTo.set(java.util.Calendar.SECOND, 59);
                btnDateToHistory.setText(dateFormat.format(historyCalTo.getTime()));
            }
            filterSalesHistory();
        }, c.get(java.util.Calendar.YEAR), c.get(java.util.Calendar.MONTH), c.get(java.util.Calendar.DAY_OF_MONTH));
        dpd.show();
    }

    private void filterSalesHistory() {
        salesHistoryList.clear();
        String query = etSearchHistory.getText().toString().toLowerCase().trim();
        String selBranch = spinnerBranchHistory.getSelectedItem() != null ? spinnerBranchHistory.getSelectedItem().toString() : "Cualquier sucursal";
        String selPayment = spinnerPaymentHistory.getSelectedItem() != null ? spinnerPaymentHistory.getSelectedItem().toString() : "Cualquier método";

        java.text.SimpleDateFormat utcFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US);
        utcFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));

        for (Venta v : allSalesHistoryList) {
            boolean matches = true;

            if (!query.isEmpty()) {
                String cliente = v.getClienteNombre() != null ? v.getClienteNombre().toLowerCase() : "";
                String doc = v.getClienteDocumento() != null ? v.getClienteDocumento().toLowerCase() : "";
                if (!cliente.contains(query) && !doc.contains(query)) {
                    matches = false;
                }
            }

            if (!"Cualquier sucursal".equals(selBranch)) {
                String branchName = v.getSucursal() != null ? v.getSucursal().getName() : "";
                if (!selBranch.equals(branchName)) matches = false;
            }

            if (!"Cualquier método".equals(selPayment)) {
                if (v.getMetodoPago() == null || !v.getMetodoPago().equalsIgnoreCase(selPayment)) {
                    matches = false;
                }
            }

            if (historyCalFrom != null || historyCalTo != null) {
                if (v.getFecha() != null) {
                    try {
                        java.util.Date d = utcFormat.parse(v.getFecha());
                        if (historyCalFrom != null && d.before(historyCalFrom.getTime())) matches = false;
                        if (historyCalTo != null && d.after(historyCalTo.getTime())) matches = false;
                    } catch (Exception e) {}
                }
            }

            if (matches) {
                salesHistoryList.add(v);
            }
        }
        historyAdapter.notifyDataSetChanged();
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

        // Fill headers
        tvNro.setText("Comprobante Nro: " + (venta.getNumeroComprobante() != null ? venta.getNumeroComprobante() : "N/A"));
        
        // Date formatting: Parse UTC to local timezone beautifully (AM/PM format)
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

        // Branch name lookup fallback if relation is not populated by POST response
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

        // Add dynamic items lines
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

                // Cant. (width: 40dp)
                TextView tvCant = new TextView(getContext());
                LinearLayout.LayoutParams lpCant = new LinearLayout.LayoutParams((int)(40 * density), ViewGroup.LayoutParams.WRAP_CONTENT);
                tvCant.setLayoutParams(lpCant);
                tvCant.setText(String.valueOf(item.getCantidad()));
                tvCant.setTextColor(Color.parseColor("#475569"));
                tvCant.setTextSize(11f);

                // Descripción (width: 0dp, weight: 1)
                TextView tvDesc = new TextView(getContext());
                LinearLayout.LayoutParams lpDesc = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
                tvDesc.setLayoutParams(lpDesc);
                tvDesc.setText("[" + item.getSku() + "] " + item.getName());
                tvDesc.setTextColor(Color.parseColor("#475569"));
                tvDesc.setTextSize(11f);

                // P. Unit (width: 70dp)
                TextView tvUnit = new TextView(getContext());
                LinearLayout.LayoutParams lpUnit = new LinearLayout.LayoutParams((int)(70 * density), ViewGroup.LayoutParams.WRAP_CONTENT);
                tvUnit.setLayoutParams(lpUnit);
                tvUnit.setText(String.format(java.util.Locale.US, "%.2f", item.getPrecioUnitario()));
                tvUnit.setTextColor(Color.parseColor("#475569"));
                tvUnit.setTextSize(11f);
                tvUnit.setGravity(android.view.Gravity.END);

                // Subtotal (width: 80dp)
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

                // Add token headers
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

    // --- RECENT SALES HISTORY RECYCLER ADAPTER ---
    private static class SalesHistoryAdapter extends RecyclerView.Adapter<SalesHistoryAdapter.ViewHolder> {
        private List<Venta> list;
        private List<Sucursal> sucursalesList;
        private OnSaleClickListener listener;

        public interface OnSaleClickListener {
            void onSaleClick(Venta venta);
        }

        public SalesHistoryAdapter(List<Venta> list, List<Sucursal> sucursalesList, OnSaleClickListener listener) {
            this.list = list;
            this.sucursalesList = sucursalesList;
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
            String sucursalName = null;
            if (v.getSucursal() != null && v.getSucursal().getName() != null) {
                sucursalName = v.getSucursal().getName();
            } else if (v.getSucursalId() != null && sucursalesList != null) {
                for (Sucursal s : sucursalesList) {
                    if (v.getSucursalId().equals(s.getId())) {
                        sucursalName = s.getName();
                        break;
                    }
                }
            }
            if (sucursalName != null) {
                label += " (" + sucursalName + ")";
            }
            holder.tvCliente.setText(label);
            
            String formattedDate = "N/A";
            String dateStr = v.getFecha();
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
            holder.tvFecha.setText(formattedDate);
            
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
