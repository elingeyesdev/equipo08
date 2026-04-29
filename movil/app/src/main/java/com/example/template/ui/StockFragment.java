package com.example.template.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.ImageButton;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.template.R;
import com.example.template.network.ApiClient;
import com.example.template.network.ApiService;
import com.example.template.network.models.Stock;
import com.example.template.network.models.Sucursal;
import com.example.template.network.models.AjusteRequest;
import com.example.template.ui.adapters.StockAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StockFragment extends Fragment {

    private RecyclerView recyclerView;
    private StockAdapter adapter;
    private ApiService apiService;
    private Spinner spinnerSucursal;
    private TextView tvTotalValuation;

    private List<Stock> allStockList = new ArrayList<>();
    private List<Sucursal> sucursalesList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stock, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        spinnerSucursal = view.findViewById(R.id.spinnerSucursal);
        tvTotalValuation = view.findViewById(R.id.tvTotalValuation);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new StockAdapter(new ArrayList<>(), new StockAdapter.OnIncidenciaClickListener() {
            @Override
            public void onIncidenciaClick(Stock stock) {
                showIncidenciaDialog(stock);
            }
        });
        recyclerView.setAdapter(adapter);

        apiService = ApiClient.getClient(getContext()).create(ApiService.class);

        loadSucursales();
        loadStock();

        return view;
    }

    private void loadSucursales() {
        apiService.getSucursales().enqueue(new Callback<List<Sucursal>>() {
            @Override
            public void onResponse(Call<List<Sucursal>> call, Response<List<Sucursal>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    sucursalesList = response.body();
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
        if (getContext() == null) return;

        List<String> spinnerOptions = new ArrayList<>();
        spinnerOptions.add("Consolidado Total (Todas las Sucursales)");
        for (Sucursal s : sucursalesList) {
            spinnerOptions.add("Sucursal: " + s.getName());
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, spinnerOptions
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSucursal.setAdapter(spinnerAdapter);

        spinnerSucursal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterStock();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void loadStock() {
        apiService.getStock().enqueue(new Callback<List<Stock>>() {
            @Override
            public void onResponse(Call<List<Stock>> call, Response<List<Stock>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allStockList = response.body();
                    filterStock(); // Apply current filter
                }
            }

            @Override
            public void onFailure(Call<List<Stock>> call, Throwable t) {
                if(getContext() != null) Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterStock() {
        if (spinnerSucursal.getSelectedItemPosition() <= 0) {
            // "ALL" selected
            adapter.updateData(allStockList);
            calculateTotal(allStockList);
            return;
        }

        // -1 because the first option is "ALL"
        Sucursal selectedBranch = sucursalesList.get(spinnerSucursal.getSelectedItemPosition() - 1);
        List<Stock> filteredList = new ArrayList<>();
        
        for (Stock s : allStockList) {
            if (s.getSucursalId() != null && s.getSucursalId().equals(selectedBranch.getId())) {
                filteredList.add(s);
            }
        }
        
        adapter.updateData(filteredList);
        calculateTotal(filteredList);
    }

    private void calculateTotal(List<Stock> list) {
        double total = 0;
        for (Stock s : list) {
            double costoFijo = s.getProducto() != null ? s.getProducto().getPrecioCosto() : 0.0;
            total += s.getCantidadTotal() * costoFijo;
        }
        tvTotalValuation.setText(String.format("Bs. %.2f", total));
    }

    private void showIncidenciaDialog(Stock stock) {
        if (getContext() == null) return;

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_incidencia, null);
        EditText etCantidadFisica = dialogView.findViewById(R.id.etCantidadFisica);
        Spinner spinnerMotivo = dialogView.findViewById(R.id.spinnerMotivo);
        EditText etObservaciones = dialogView.findViewById(R.id.etObservaciones);
        TextView tvAuditInfo = dialogView.findViewById(R.id.tvAuditInfo);
        LinearLayout llWarningBox = dialogView.findViewById(R.id.llWarningBox);
        TextView tvWarningText = dialogView.findViewById(R.id.tvWarningText);
        Button btnCancelar = dialogView.findViewById(R.id.btnCancelar);
        Button btnProcesar = dialogView.findViewById(R.id.btnProcesar);
        ImageButton btnClose = dialogView.findViewById(R.id.btnClose);

        String[] motivos = {"ERROR_REGISTRO", "DANO_MERMA", "ROBO_O_PERDIDA", "CADUCIDAD"};
        String[] motivosLabels = {"Error de Registro Numérico", "Artículo Dañado / Extraviado", "Robo / No Habido", "Caducidad / Vencimiento"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, motivosLabels);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMotivo.setAdapter(spinnerAdapter);

        String prodName = stock.getProducto() != null ? stock.getProducto().getName() : "Producto";
        String sucName = stock.getSucursal() != null ? stock.getSucursal().getName() : "Sucursal";
        String infoText = "Estás auditando <b>" + prodName + "</b> en <b>" + sucName + "</b>. Actualmente el sistema registra <b>" + stock.getCantidadTotal() + "</b> unidades.";
        tvAuditInfo.setText(Html.fromHtml(infoText, Html.FROM_HTML_MODE_LEGACY));

        // Pre-fill
        etCantidadFisica.setText(String.valueOf(stock.getCantidadTotal()));

        AlertDialog dialog = new AlertDialog.Builder(getContext())
            .setView(dialogView)
            .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        etCantidadFisica.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    llWarningBox.setVisibility(View.GONE);
                    btnProcesar.setEnabled(false);
                    btnProcesar.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#d1d5db")));
                    return;
                }
                
                int cantidadFisica = Integer.parseInt(s.toString());
                int delta = cantidadFisica - stock.getCantidadTotal();
                
                if (delta < 0) {
                    llWarningBox.setVisibility(View.VISIBLE);
                    double costoFijo = stock.getProducto() != null ? stock.getProducto().getPrecioCosto() : 0.0;
                    double lostValue = Math.abs(delta) * costoFijo;
                    String warningMsg = "<b>⚠ Impacto Financiero Directo:</b> La diferencia de " + Math.abs(delta) + " unidades resultará en una pérdida de valuación estimada de <b>Bs. " + String.format("%.2f", lostValue) + "</b> para esta sucursal.";
                    tvWarningText.setText(Html.fromHtml(warningMsg, Html.FROM_HTML_MODE_LEGACY));
                    
                    btnProcesar.setEnabled(true);
                    btnProcesar.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#d97706")));
                } else if (delta > 0) {
                    llWarningBox.setVisibility(View.VISIBLE);
                    String warningMsg = "<b>❌ Excedente Anómalo Detectado:</b> No puedes declarar una cantidad física (" + cantidadFisica + ") mayor a la registrada en sistema (" + stock.getCantidadTotal() + ").<br/>Si ingresó mercancía nueva, debes hacerlo formalmente mediante Lotes / Sourcing.";
                    tvWarningText.setText(Html.fromHtml(warningMsg, Html.FROM_HTML_MODE_LEGACY));
                    
                    btnProcesar.setEnabled(false);
                    btnProcesar.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#d1d5db")));
                } else {
                    llWarningBox.setVisibility(View.GONE);
                    btnProcesar.setEnabled(true);
                    btnProcesar.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#d97706")));
                }
            }
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());
        btnClose.setOnClickListener(v -> dialog.dismiss());
        
        btnProcesar.setOnClickListener(v -> {
            String cantStr = etCantidadFisica.getText().toString().trim();
            String obs = etObservaciones.getText().toString().trim();
            String motivo = motivos[spinnerMotivo.getSelectedItemPosition()];

            if (cantStr.isEmpty()) {
                Toast.makeText(getContext(), "La cantidad física es requerida", Toast.LENGTH_SHORT).show();
                return;
            }

            int cantidadFisica = Integer.parseInt(cantStr);
            if (cantidadFisica > stock.getCantidadTotal()) {
                Toast.makeText(getContext(), "No puedes declarar una cantidad física mayor a la del sistema", Toast.LENGTH_LONG).show();
                return;
            }

            submitIncidencia(stock, cantidadFisica, motivo, obs);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void submitIncidencia(Stock stock, int cantidadFisica, String motivo, String observaciones) {
        AjusteRequest request = new AjusteRequest(
            stock.getSucursalId(),
            stock.getProductoId(),
            stock.getCantidadTotal(),
            cantidadFisica,
            motivo,
            observaciones.isEmpty() ? null : observaciones
        );

        apiService.createAjuste(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Ajuste registrado", Toast.LENGTH_SHORT).show();
                    loadStock(); // refresh inventory
                } else {
                    Toast.makeText(getContext(), "Error al registrar ajuste", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
