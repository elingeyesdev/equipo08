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
import androidx.cardview.widget.CardView;


import com.example.template.R;
import com.example.template.network.ApiClient;
import com.example.template.network.ApiService;
import com.example.template.network.models.AjusteRequest;

import com.example.template.network.models.Stock;
import com.example.template.network.models.Sucursal;
import com.example.template.network.models.TrasladoRequest;
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
    private EditText etSearch;
    private TextView tvTotalValuation, tvTotalDeficit;
    private LinearLayout llAlertBanner;
    private TextView tvAlertTitle;
    private Button btnToggleFilter;
    private CardView cardFilter;
    private boolean isFilterVisible = false;

    private List<Stock> allStockList = new ArrayList<>();
    private List<com.example.template.network.models.Ajuste> allAjustesList = new ArrayList<>();
    private List<Sucursal> sucursalesList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stock, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        spinnerSucursal = view.findViewById(R.id.spinnerSucursal);
        etSearch = view.findViewById(R.id.etSearch);
        tvTotalValuation = view.findViewById(R.id.tvTotalValuation);
        tvTotalDeficit = view.findViewById(R.id.tvTotalDeficit);
        llAlertBanner = view.findViewById(R.id.llAlertBanner);
        tvAlertTitle = view.findViewById(R.id.tvAlertTitle);


        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { filterStock(); }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new StockAdapter(new ArrayList<>(), new StockAdapter.OnTrasladoClickListener() {
            @Override
            public void onTrasladoClick(Stock stock) {
                showTrasladoDialog(stock);
            }
        });
        recyclerView.setAdapter(adapter);

        apiService = ApiClient.getClient(getContext()).create(ApiService.class);



        btnToggleFilter = view.findViewById(R.id.btnToggleFilter);
        cardFilter = view.findViewById(R.id.cardFilter);
        btnToggleFilter.setOnClickListener(v -> {
            isFilterVisible = !isFilterVisible;
            if (isFilterVisible) {
                cardFilter.setVisibility(View.VISIBLE);
                btnToggleFilter.setText("Ocultar filtros");
                btnToggleFilter.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#0d9488")));
            } else {
                cardFilter.setVisibility(View.GONE);
                btnToggleFilter.setText("Filtrar");
                btnToggleFilter.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#0d9488")));
            }
        });

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
        spinnerOptions.add("Consolidado total (todas las sucursales)");
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
        
        apiService.getAjustes().enqueue(new Callback<List<com.example.template.network.models.Ajuste>>() {
            @Override
            public void onResponse(Call<List<com.example.template.network.models.Ajuste>> call, Response<List<com.example.template.network.models.Ajuste>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allAjustesList = response.body();
                    filterStock();
                }
            }
            @Override
            public void onFailure(Call<List<com.example.template.network.models.Ajuste>> call, Throwable t) {}
        });
    }

    private void filterStock() {
        String query = "";
        if (etSearch != null && etSearch.getText() != null) {
            query = etSearch.getText().toString().toLowerCase();
        }

        List<Stock> baseList;
        Sucursal selectedBranch = null;

        if (spinnerSucursal.getSelectedItemPosition() > 0) {
            selectedBranch = sucursalesList.get(spinnerSucursal.getSelectedItemPosition() - 1);
            baseList = new ArrayList<>();
            for (Stock s : allStockList) {
                if (s.getSucursalId() != null && s.getSucursalId().equals(selectedBranch.getId())) {
                    baseList.add(s);
                }
            }
        } else {
            baseList = allStockList;
        }

        List<Stock> filteredList = new ArrayList<>();
        for (Stock s : baseList) {
            boolean matches = true;
            if (!query.isEmpty() && s.getProducto() != null) {
                String name = s.getProducto().getName() != null ? s.getProducto().getName().toLowerCase() : "";
                String sku = s.getProducto().getSku() != null ? s.getProducto().getSku().toLowerCase() : "";
                if (!name.contains(query) && !sku.contains(query)) {
                    matches = false;
                }
            }
            if (matches) {
                filteredList.add(s);
            }
        }

        List<com.example.template.network.models.Ajuste> filteredAjustes = new ArrayList<>();
        for (com.example.template.network.models.Ajuste a : allAjustesList) {
            boolean sucursalMatch = selectedBranch == null || (a.getSucursal() != null && selectedBranch.getId().equals(a.getSucursal().getId()));
            
            boolean queryMatch = true;
            if (!query.isEmpty() && a.getProducto() != null) {
                String name = a.getProducto().getName() != null ? a.getProducto().getName().toLowerCase() : "";
                String sku = a.getProducto().getSku() != null ? a.getProducto().getSku().toLowerCase() : "";
                if (!name.contains(query) && !sku.contains(query)) {
                    queryMatch = false;
                }
            }

            if (sucursalMatch && queryMatch) {
                filteredAjustes.add(a);
            }
        }

        adapter.updateData(filteredList);
        calculateTotal(filteredList, filteredAjustes);

        // Calculate alerts reactively based on the filtered list
        int alertCount = 0;
        for (Stock s : filteredList) {
            int minStock = s.getProducto() != null ? s.getProducto().getStockMinimo() : 10;
            if (s.getCantidadTotal() < minStock) {
                alertCount++;
            }
        }

        if (llAlertBanner != null && tvAlertTitle != null) {
            if (alertCount > 0) {
                llAlertBanner.setVisibility(View.VISIBLE);
                tvAlertTitle.setText("Alertas de inventario bajo (" + alertCount + ")");
            } else {
                llAlertBanner.setVisibility(View.GONE);
            }
        }
    }

    private void calculateTotal(List<Stock> list, List<com.example.template.network.models.Ajuste> ajustes) {
        double total = 0;
        for (Stock s : list) {
            double costoFijo = s.getProducto() != null ? s.getProducto().getPrecioCosto() : 0.0;
            total += s.getCantidadTotal() * costoFijo;
        }
        tvTotalValuation.setText(String.format("Bs. %.2f", total));
        
        double deficit = 0;
        if (ajustes != null) {
            for (com.example.template.network.models.Ajuste a : ajustes) {
                if (a.getValorPerdido() != null && !a.getValorPerdido().isEmpty()) {
                    try {
                        deficit += Double.parseDouble(a.getValorPerdido());
                    } catch (Exception ignored) {}
                }
            }
        }
        if (deficit > 0) {
            tvTotalDeficit.setText(String.format("- Bs. %.2f", deficit));
        } else {
            tvTotalDeficit.setText(String.format("Bs. %.2f", deficit));
        }
    }

    private void showTrasladoDialog(Stock stock) {
        if (getContext() == null) return;

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_traslado, null);
        EditText etCantidad = dialogView.findViewById(R.id.etCantidad);
        Spinner spinnerSucursalDestino = dialogView.findViewById(R.id.spinnerSucursalDestino);
        TextView tvTrasladoInfo = dialogView.findViewById(R.id.tvTrasladoInfo);
        LinearLayout llWarningBox = dialogView.findViewById(R.id.llWarningBox);
        TextView tvWarningText = dialogView.findViewById(R.id.tvWarningText);
        Button btnCancelar = dialogView.findViewById(R.id.btnCancelar);
        Button btnProcesar = dialogView.findViewById(R.id.btnProcesar);
        ImageButton btnClose = dialogView.findViewById(R.id.btnClose);

        // Filter out current branch
        List<Sucursal> validDestinations = new ArrayList<>();
        List<String> validDestinationsNames = new ArrayList<>();
        for (Sucursal s : sucursalesList) {
            if (!s.getId().equals(stock.getSucursalId())) {
                validDestinations.add(s);
                validDestinationsNames.add(s.getName());
            }
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, validDestinationsNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSucursalDestino.setAdapter(spinnerAdapter);

        String prodName = stock.getProducto() != null ? stock.getProducto().getName() : "Producto";
        String sucName = stock.getSucursal() != null ? stock.getSucursal().getName() : "Sucursal";
        String infoText = "Moverás " + prodName + " desde la sucursal origen " + sucName + ". Unidades disponibles para transferir: " + stock.getCantidadTotal() + ".";
        tvTrasladoInfo.setText(infoText);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
            .setView(dialogView)
            .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        etCantidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    llWarningBox.setVisibility(View.GONE);
                    btnProcesar.setEnabled(false);
                    btnProcesar.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#0d9488")));
                    return;
                }
                
                int cantidad = Integer.parseInt(s.toString());
                
                if (cantidad > stock.getCantidadTotal() || cantidad < 1) {
                    llWarningBox.setVisibility(View.VISIBLE);
                    btnProcesar.setEnabled(false);
                    btnProcesar.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#0d9488")));
                } else {
                    llWarningBox.setVisibility(View.GONE);
                    btnProcesar.setEnabled(true);
                    btnProcesar.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#0d9488")));
                }
            }
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());
        btnClose.setOnClickListener(v -> dialog.dismiss());
        
        btnProcesar.setOnClickListener(v -> {
            String cantStr = etCantidad.getText().toString().trim();
            if (cantStr.isEmpty()) return;

            int cantidad = Integer.parseInt(cantStr);
            if (cantidad > stock.getCantidadTotal() || cantidad < 1) return;
            
            if (validDestinations.isEmpty() || spinnerSucursalDestino.getSelectedItemPosition() < 0) {
                Toast.makeText(getContext(), "No hay sucursal destino válida", Toast.LENGTH_SHORT).show();
                return;
            }

            Sucursal toSucursal = validDestinations.get(spinnerSucursalDestino.getSelectedItemPosition());
            submitTraslado(stock, toSucursal.getId(), cantidad);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void submitTraslado(Stock stock, String toSucursalId, int cantidad) {
        TrasladoRequest request = new TrasladoRequest(
            stock.getSucursalId(),
            toSucursalId,
            stock.getProductoId(),
            cantidad
        );

        apiService.transferStock(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Traslado exitoso", Toast.LENGTH_SHORT).show();
                    loadStock(); // refresh inventory
                } else {
                    Toast.makeText(getContext(), "Error al trasladar inventario", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            getActivity().getWindow().setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }
}
