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
        adapter = new StockAdapter(new ArrayList<>());
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
}
