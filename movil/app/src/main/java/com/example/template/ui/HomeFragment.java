package com.example.template.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
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
import com.example.template.network.models.Ajuste;
import com.example.template.network.models.DashboardKpis;
import com.example.template.network.models.Stock;
import com.example.template.network.models.Venta;
import com.example.template.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private ProgressBar progressBarHome;
    private ScrollView scrollDashboardContent;

    // Header Views
    private TextView tvWelcomeTitle;

    // Metric Views
    private TextView tvMetricSales;
    private TextView tvMetricRevenue;
    private TextView tvMetricProfit;
    private TextView tvMetricStock;
    private TextView tvMetricStockValue;
    private TextView tvMetricLosses;

    // Card containers (to hide/show based on user role)
    private CardView cvMetricRevenue;
    private CardView cvMetricProfit;
    private CardView cvMetricLosses;

    // Recent Sales
    private RecyclerView rvRecentSales;
    private TextView tvEmptyRecentSales;
    private RecentSalesAdapter recentSalesAdapter;
    private List<Venta> recentSalesList = new ArrayList<>();

    // API & Session
    private ApiService apiService;
    private SessionManager sessionManager;

    // Call tracking
    private int completedCalls = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        sessionManager = new SessionManager(getContext());
        apiService = ApiClient.getClient(getContext()).create(ApiService.class);

        // Bind layouts
        progressBarHome = view.findViewById(R.id.progressBarHome);
        scrollDashboardContent = view.findViewById(R.id.scrollDashboardContent);

        tvWelcomeTitle = view.findViewById(R.id.tvWelcomeTitle);

        tvMetricSales = view.findViewById(R.id.tvMetricSales);
        tvMetricRevenue = view.findViewById(R.id.tvMetricRevenue);
        tvMetricProfit = view.findViewById(R.id.tvMetricProfit);
        tvMetricStock = view.findViewById(R.id.tvMetricStock);
        tvMetricStockValue = view.findViewById(R.id.tvMetricStockValue);
        tvMetricLosses = view.findViewById(R.id.tvMetricLosses);

        cvMetricRevenue = view.findViewById(R.id.cvMetricRevenue);
        cvMetricProfit = view.findViewById(R.id.cvMetricProfit);
        cvMetricLosses = view.findViewById(R.id.cvMetricLosses);

        rvRecentSales = view.findViewById(R.id.rvRecentSales);
        tvEmptyRecentSales = view.findViewById(R.id.tvEmptyRecentSales);

        // Setup RecyclerView
        rvRecentSales.setLayoutManager(new LinearLayoutManager(getContext()));
        recentSalesAdapter = new RecentSalesAdapter(recentSalesList, venta -> showComprobanteDialog(venta));
        rvRecentSales.setAdapter(recentSalesAdapter);

        // Welcome Texts
        tvWelcomeTitle.setText("Resumen de Tienda");

        // Apply role security
        applyRoleSecurity();

        // Load data in parallel
        loadDashboardData();

        return view;
    }

    private void applyRoleSecurity() {
        String role = sessionManager.getRole();
        if ("VENDEDOR".equalsIgnoreCase(role)) {
            // Hide sensitive financial cards for salesperson privacy
            cvMetricRevenue.setVisibility(View.GONE);
            cvMetricProfit.setVisibility(View.GONE);
            cvMetricLosses.setVisibility(View.GONE);
            tvMetricStockValue.setVisibility(View.GONE);
        } else {
            cvMetricRevenue.setVisibility(View.VISIBLE);
            cvMetricProfit.setVisibility(View.VISIBLE);
            cvMetricLosses.setVisibility(View.VISIBLE);
            tvMetricStockValue.setVisibility(View.VISIBLE);
        }
    }

    private void loadDashboardData() {
        completedCalls = 0;
        progressBarHome.setVisibility(View.VISIBLE);
        scrollDashboardContent.setVisibility(View.GONE);

        // Call 1: Dashboard KPIs and Recent Sales
        apiService.getDashboardKpis().enqueue(new Callback<DashboardKpis>() {
            @Override
            public void onResponse(Call<DashboardKpis> call, Response<DashboardKpis> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DashboardKpis kpis = response.body();
                    tvMetricSales.setText(kpis.getTotalVentas() + " ventas");
                    tvMetricRevenue.setText(String.format(java.util.Locale.US, "Bs %.2f", kpis.getIngresosTotales()));
                    tvMetricProfit.setText(String.format(java.util.Locale.US, "Bs %.2f", kpis.getUtilidadTotal()));

                    recentSalesList.clear();
                    if (kpis.getRecentSales() != null && !kpis.getRecentSales().isEmpty()) {
                        recentSalesList.addAll(kpis.getRecentSales());
                        recentSalesAdapter.notifyDataSetChanged();
                        rvRecentSales.setVisibility(View.VISIBLE);
                        tvEmptyRecentSales.setVisibility(View.GONE);
                    } else {
                        rvRecentSales.setVisibility(View.GONE);
                        tvEmptyRecentSales.setVisibility(View.VISIBLE);
                    }
                } else {
                    tvMetricSales.setText("Error");
                    tvMetricRevenue.setText("Error");
                    tvMetricProfit.setText("Error");
                }
                checkIfAllLoadingFinished();
            }

            @Override
            public void onFailure(Call<DashboardKpis> call, Throwable t) {
                tvMetricSales.setText("Error de red");
                tvMetricRevenue.setText("Error de red");
                tvMetricProfit.setText("Error de red");
                checkIfAllLoadingFinished();
            }
        });

        // Call 2: Stock Units and Value
        apiService.getStock().enqueue(new Callback<List<Stock>>() {
            @Override
            public void onResponse(Call<List<Stock>> call, Response<List<Stock>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Stock> stocks = response.body();
                    int totalUnits = 0;
                    double totalValue = 0.0;

                    for (Stock s : stocks) {
                        totalUnits += s.getCantidadTotal();
                        if (s.getProducto() != null) {
                            totalValue += s.getCantidadTotal() * s.getProducto().getPrecioVenta();
                        }
                    }

                    tvMetricStock.setText(totalUnits + " unidades");
                    tvMetricStockValue.setText(String.format(java.util.Locale.US, "Valor estimado: Bs %.2f", totalValue));
                } else {
                    tvMetricStock.setText("Error");
                    tvMetricStockValue.setText("Error");
                }
                checkIfAllLoadingFinished();
            }

            @Override
            public void onFailure(Call<List<Stock>> call, Throwable t) {
                tvMetricStock.setText("Error de red");
                tvMetricStockValue.setText("Error de red");
                checkIfAllLoadingFinished();
            }
        });

        // Call 3: Audit Losses (Ajustes)
        apiService.getAjustes().enqueue(new Callback<List<Ajuste>>() {
            @Override
            public void onResponse(Call<List<Ajuste>> call, Response<List<Ajuste>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Ajuste> ajustes = response.body();
                    double totalLossValue = 0.0;

                    for (Ajuste aj : ajustes) {
                        if (aj.getValorPerdido() != null) {
                            try {
                                totalLossValue += Double.parseDouble(aj.getValorPerdido());
                            } catch (NumberFormatException e) {
                                // ignore parse errors
                            }
                        }
                    }

                    tvMetricLosses.setText(String.format(java.util.Locale.US, "Bs %.2f", totalLossValue));
                } else {
                    tvMetricLosses.setText("Error");
                }
                checkIfAllLoadingFinished();
            }

            @Override
            public void onFailure(Call<List<Ajuste>> call, Throwable t) {
                tvMetricLosses.setText("Error de red");
                checkIfAllLoadingFinished();
            }
        });
    }

    private void checkIfAllLoadingFinished() {
        completedCalls++;
        if (completedCalls >= 3) {
            progressBarHome.setVisibility(View.GONE);
            scrollDashboardContent.setVisibility(View.VISIBLE);
        }
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
                
                java.text.SimpleDateFormat localFormat = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm a", java.util.Locale.US);
                localFormat.setTimeZone(java.util.TimeZone.getDefault());
                formattedDate = localFormat.format(date);
            } catch (Exception e) {
                if (dateStr.contains("T")) {
                    formattedDate = dateStr.replace("T", " ");
                    if (formattedDate.length() > 19) {
                        formattedDate = formattedDate.substring(0, 19);
                    }
                } else {
                    formattedDate = dateStr;
                }
            }
        }
        tvFecha.setText("Fecha: " + formattedDate);

        // Branch name
        String sucursalName = "Principal";
        if (venta.getSucursal() != null && venta.getSucursal().getName() != null) {
            sucursalName = venta.getSucursal().getName();
        }
        tvSuc.setText("Sucursal: " + sucursalName);
        tvCli.setText("Nombre/Razón Social: " + (venta.getClienteNombre() != null ? venta.getClienteNombre() : "Cliente Casual"));
        tvDoc.setText("NIT/CI: " + (venta.getClienteDocumento() != null ? venta.getClienteDocumento() : "N/A"));
        tvTotal.setText(String.format("Bs %.2f", venta.getTotal()));

        // Add dynamic items lines
        if (venta.getDetalle() != null) {
            llDetalleContainer.removeAllViews();
            float density = getResources().getDisplayMetrics().density;
            for (com.example.template.network.models.DetalleItem item : venta.getDetalle()) {
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
                tvCant.setTextColor(android.graphics.Color.parseColor("#475569"));
                tvCant.setTextSize(11f);

                // Descripción (width: 0dp, weight: 1)
                TextView tvDesc = new TextView(getContext());
                LinearLayout.LayoutParams lpDesc = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
                tvDesc.setLayoutParams(lpDesc);
                tvDesc.setText("[" + (item.getSku() != null ? item.getSku() : "N/A") + "] " + (item.getName() != null ? item.getName() : "Producto"));
                tvDesc.setTextColor(android.graphics.Color.parseColor("#475569"));
                tvDesc.setTextSize(11f);

                // P. Unit (width: 70dp)
                TextView tvUnit = new TextView(getContext());
                LinearLayout.LayoutParams lpUnit = new LinearLayout.LayoutParams((int)(70 * density), ViewGroup.LayoutParams.WRAP_CONTENT);
                tvUnit.setLayoutParams(lpUnit);
                tvUnit.setText(String.format(java.util.Locale.US, "%.2f", item.getPrecioUnitario()));
                tvUnit.setTextColor(android.graphics.Color.parseColor("#475569"));
                tvUnit.setTextSize(11f);
                tvUnit.setGravity(android.view.Gravity.END);

                // Subtotal (width: 80dp)
                TextView tvSub = new TextView(getContext());
                LinearLayout.LayoutParams lpSub = new LinearLayout.LayoutParams((int)(80 * density), ViewGroup.LayoutParams.WRAP_CONTENT);
                tvSub.setLayoutParams(lpSub);
                tvSub.setText(String.format(java.util.Locale.US, "%.2f", item.getSubtotal()));
                tvSub.setTextColor(android.graphics.Color.parseColor("#475569"));
                tvSub.setTextSize(11f);
                tvSub.setGravity(android.view.Gravity.END);

                row.addView(tvCant);
                row.addView(tvDesc);
                row.addView(tvUnit);
                row.addView(tvSub);

                llDetalleContainer.addView(row);
            }
        }

        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
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

    // --- RECENT SALES LIST RECYCLER ADAPTER ---
    private static class RecentSalesAdapter extends RecyclerView.Adapter<RecentSalesAdapter.ViewHolder> {
        private List<Venta> list;
        private OnSaleClickListener listener;

        public interface OnSaleClickListener {
            void onSaleClick(Venta venta);
        }

        public RecentSalesAdapter(List<Venta> list, OnSaleClickListener listener) {
            this.list = list;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recent_sale, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Venta v = list.get(position);
            holder.tvSaleTitle.setText("Venta #" + (v.getNumeroComprobante() != null ? v.getNumeroComprobante() : v.getId().substring(0, Math.min(v.getId().length(), 8))));
            holder.tvSaleTotal.setText(String.format(java.util.Locale.US, "Bs %.2f", v.getTotal()));

            // Date format helper
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

                    java.text.SimpleDateFormat localFormat = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm a", java.util.Locale.US);
                    localFormat.setTimeZone(java.util.TimeZone.getDefault());
                    formattedDate = localFormat.format(date);
                } catch (Exception e) {
                    if (dateStr.contains("T")) {
                        formattedDate = dateStr.replace("T", " ");
                        if (formattedDate.length() > 16) {
                            formattedDate = formattedDate.substring(0, 16);
                        }
                    } else {
                        formattedDate = dateStr;
                    }
                }
            }
            holder.tvSaleDate.setText(formattedDate);
            
            holder.itemView.setOnClickListener(view -> {
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
            TextView tvSaleTitle, tvSaleDate, tvSaleTotal;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvSaleTitle = itemView.findViewById(R.id.tvSaleTitle);
                tvSaleDate = itemView.findViewById(R.id.tvSaleDate);
                tvSaleTotal = itemView.findViewById(R.id.tvSaleTotal);
            }
        }
    }
}
