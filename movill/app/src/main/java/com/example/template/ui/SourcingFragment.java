package com.example.template.ui;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.template.network.models.LoteIngreso;
import com.example.template.network.models.Producto;
import com.example.template.network.models.Proveedor;
import com.example.template.ui.adapters.LoteAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SourcingFragment extends Fragment {

    private Button btnToggleForm, btnGuardar;
    private CardView cardForm;
    private EditText etVolumen, etPrecio;
    private TextView tvInversionTotal;
    private Spinner spinnerProducto, spinnerProveedor;
    private RecyclerView recyclerView;
    
    private LoteAdapter adapter;
    private ApiService apiService;
    private boolean isFormVisible = false;

    private List<Producto> productosList = new ArrayList<>();
    private List<Proveedor> proveedoresList = new ArrayList<>();
    
    private ArrayAdapter<Proveedor> provAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sourcing, container, false);

        btnToggleForm = view.findViewById(R.id.btnToggleForm);
        btnGuardar = view.findViewById(R.id.btnGuardar);
        cardForm = view.findViewById(R.id.cardForm);
        etVolumen = view.findViewById(R.id.etVolumen);
        etPrecio = view.findViewById(R.id.etPrecio);
        tvInversionTotal = view.findViewById(R.id.tvInversionTotal);
        spinnerProducto = view.findViewById(R.id.spinnerProducto);
        spinnerProveedor = view.findViewById(R.id.spinnerProveedor);
        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new LoteAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        apiService = ApiClient.getClient(getContext()).create(ApiService.class);

        btnToggleForm.setOnClickListener(v -> toggleForm());
        btnGuardar.setOnClickListener(v -> saveLote());

        setupAutoCalculations();
        setupSpinnerLink();

        loadLotes();
        loadSpinnersData();

        return view;
    }

    private void toggleForm() {
        isFormVisible = !isFormVisible;
        if (isFormVisible) {
            cardForm.setVisibility(View.VISIBLE);
            btnToggleForm.setText("X Cancelar");
            btnToggleForm.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#64748b")));
        } else {
            cardForm.setVisibility(View.GONE);
            btnToggleForm.setText("Nuevo Ingreso");
            btnToggleForm.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2b3b55")));
        }
    }

    private void setupAutoCalculations() {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateTotal();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        };
        etVolumen.addTextChangedListener(watcher);
        etPrecio.addTextChangedListener(watcher);
    }

    private void calculateTotal() {
        try {
            String volStr = etVolumen.getText().toString();
            String preStr = etPrecio.getText().toString();
            if(!volStr.isEmpty() && !preStr.isEmpty()) {
                double vol = Double.parseDouble(volStr);
                double pre = Double.parseDouble(preStr);
                tvInversionTotal.setText(String.format(Locale.US, "Bs %.2f", (vol * pre)));
            } else {
                tvInversionTotal.setText("Bs 0.00");
            }
        } catch (Exception e) {
            tvInversionTotal.setText("Bs 0.00");
        }
    }

    private void setupSpinnerLink() {
        spinnerProducto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Producto prod = productosList.get(position);
                String provTarget = prod.getProveedorId();
                if(provTarget != null && proveedoresList != null && provAdapter != null) {
                    for (int i = 0; i < proveedoresList.size(); i++) {
                        if (proveedoresList.get(i).getId().equals(provTarget)) {
                            spinnerProveedor.setSelection(i);
                            break;
                        }
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadLotes() {
        apiService.getLotes().enqueue(new Callback<List<LoteIngreso>>() {
            @Override
            public void onResponse(Call<List<LoteIngreso>> call, Response<List<LoteIngreso>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.updateData(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<LoteIngreso>> call, Throwable t) {}
        });
    }

    private void loadSpinnersData() {
        // Load Productos
        apiService.getProductos().enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productosList.clear();
                    productosList.addAll(response.body());
                    if (getContext() != null) {
                        ArrayAdapter<Producto> prodAdapter = new ArrayAdapter<>(
                            getContext(), android.R.layout.simple_spinner_item, productosList
                        );
                        prodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerProducto.setAdapter(prodAdapter);
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {}
        });

        // Load Proveedores
        apiService.getProveedores().enqueue(new Callback<List<Proveedor>>() {
            @Override
            public void onResponse(Call<List<Proveedor>> call, Response<List<Proveedor>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    proveedoresList.clear();
                    proveedoresList.addAll(response.body());
                    if (getContext() != null) {
                        provAdapter = new ArrayAdapter<>(
                            getContext(), android.R.layout.simple_spinner_item, proveedoresList
                        );
                        provAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerProveedor.setAdapter(provAdapter);
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Proveedor>> call, Throwable t) {}
        });
    }

    private void saveLote() {
        String volStr = etVolumen.getText().toString().trim();
        String preStr = etPrecio.getText().toString().trim();
        Producto selectedProd = (Producto) spinnerProducto.getSelectedItem();
        Proveedor selectedProv = (Proveedor) spinnerProveedor.getSelectedItem();

        if (volStr.isEmpty() || preStr.isEmpty() || selectedProd == null || selectedProv == null) {
            Toast.makeText(getContext(), "Campos requeridos vacíos", Toast.LENGTH_SHORT).show();
            return;
        }

        int vol = Integer.parseInt(volStr);
        double pre = Double.parseDouble(preStr);

        LoteIngreso request = new LoteIngreso(selectedProd.getId(), selectedProv.getId(), vol, pre);
        
        apiService.createLote(request).enqueue(new Callback<LoteIngreso>() {
            @Override
            public void onResponse(Call<LoteIngreso> call, Response<LoteIngreso> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Lote de Sourcing Confirmado", Toast.LENGTH_SHORT).show();
                    etVolumen.setText(""); etPrecio.setText("");tvInversionTotal.setText("Bs 0.00");
                    toggleForm();
                    loadLotes(); 
                } else {
                    Toast.makeText(getContext(), "Error en Sourcing", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<LoteIngreso> call, Throwable t) {}
        });
    }
}
