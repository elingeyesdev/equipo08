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

import com.example.template.network.models.Sucursal;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SourcingFragment extends Fragment {

    private Button btnToggleForm, btnGuardar;
    private CardView cardForm;
    private EditText etVolumen;
    private Spinner spinnerSucursal, spinnerProducto, spinnerProveedor;
    private RecyclerView recyclerView;
    
    private LoteAdapter adapter;
    private ApiService apiService;
    private boolean isFormVisible = false;

    private List<Producto> productosList = new ArrayList<>();
    private List<Proveedor> proveedoresList = new ArrayList<>();
    private List<Sucursal> sucursalesList = new ArrayList<>();
    
    private ArrayAdapter<Proveedor> provAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sourcing, container, false);

        btnToggleForm = view.findViewById(R.id.btnToggleForm);
        btnGuardar = view.findViewById(R.id.btnGuardar);
        cardForm = view.findViewById(R.id.cardForm);
        etVolumen = view.findViewById(R.id.etVolumen);
        spinnerSucursal = view.findViewById(R.id.spinnerSucursal);
        spinnerProducto = view.findViewById(R.id.spinnerProducto);
        spinnerProveedor = view.findViewById(R.id.spinnerProveedor);
        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new LoteAdapter(new ArrayList<>(), this::confirmDelete);
        recyclerView.setAdapter(adapter);

        apiService = ApiClient.getClient(getContext()).create(ApiService.class);

        btnToggleForm.setOnClickListener(v -> toggleForm());
        btnGuardar.setOnClickListener(v -> saveLote());

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
        // Load Sucursales
        apiService.getSucursales().enqueue(new Callback<List<Sucursal>>() {
            @Override
            public void onResponse(Call<List<Sucursal>> call, Response<List<Sucursal>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    sucursalesList.clear();
                    sucursalesList.addAll(response.body());
                    if (getContext() != null) {
                        ArrayAdapter<Sucursal> sucAdapter = new ArrayAdapter<>(
                            getContext(), android.R.layout.simple_spinner_item, sucursalesList
                        );
                        sucAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerSucursal.setAdapter(sucAdapter);
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Sucursal>> call, Throwable t) {}
        });

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
        Sucursal selectedSucursal = (Sucursal) spinnerSucursal.getSelectedItem();
        Producto selectedProd = (Producto) spinnerProducto.getSelectedItem();
        Proveedor selectedProv = (Proveedor) spinnerProveedor.getSelectedItem();

        if (volStr.isEmpty() || selectedSucursal == null || selectedProd == null || selectedProv == null) {
            Toast.makeText(getContext(), "Campos requeridos vacíos", Toast.LENGTH_SHORT).show();
            return;
        }

        int vol = Integer.parseInt(volStr);

        LoteIngreso request = new LoteIngreso(selectedSucursal.getId(), selectedProd.getId(), selectedProv.getId(), vol);
        
        apiService.createLote(request).enqueue(new Callback<LoteIngreso>() {
            @Override
            public void onResponse(Call<LoteIngreso> call, Response<LoteIngreso> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Lote de Sourcing Confirmado", Toast.LENGTH_SHORT).show();
                    etVolumen.setText("");
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

    private void confirmDelete(LoteIngreso lote) {
        if (getContext() == null) return;
        new androidx.appcompat.app.AlertDialog.Builder(getContext())
            .setTitle("Eliminar Ingreso")
            .setMessage("¿Estás seguro de que quieres eliminar este lote de ingreso?")
            .setPositiveButton("Eliminar", (dialog, which) -> deleteLote(lote))
            .setNegativeButton("Cancelar", null)
            .show();
    }

    private void deleteLote(LoteIngreso lote) {
        apiService.deleteLote(lote.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Lote eliminado", Toast.LENGTH_SHORT).show();
                    loadLotes();
                } else {
                    Toast.makeText(getContext(), "Error al eliminar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
