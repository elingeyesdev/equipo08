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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ScrollView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputLayout;

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

    private FloatingActionButton btnToggleForm;
    private Button btnGuardar, btnToggleFilters;
    private CardView cardForm, cardFilter;
    private ScrollView scrollForm;
    private EditText etVolumen, etCostoUnitario, etFechaVencimiento, etFechaProduccion, etSearch, etFilterDateFrom, etFilterDateTo;
    private TextInputLayout tilFechaVencimiento, tilFechaProduccion;
    private Spinner spinnerSucursal, spinnerProducto, spinnerProveedor, spinnerFilterSucursal;
    private RecyclerView recyclerView;
    
    private LoteAdapter adapter;
    private ApiService apiService;
    private boolean isFormVisible = false;
    private boolean isFiltersVisible = false;

    private List<LoteIngreso> allLotesList = new ArrayList<>();
    private List<Producto> productosList = new ArrayList<>();
    private List<Proveedor> proveedoresList = new ArrayList<>();
    private List<Sucursal> sucursalesList = new ArrayList<>();
    private com.example.template.utils.SessionManager sessionManager;
    
    private ArrayAdapter<Proveedor> provAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sourcing, container, false);

        btnToggleForm = view.findViewById(R.id.btnToggleForm);
        btnGuardar = view.findViewById(R.id.btnGuardar);
        cardForm = view.findViewById(R.id.cardForm);
        cardFilter = view.findViewById(R.id.cardFilter);
        scrollForm = view.findViewById(R.id.scrollForm);
        etVolumen = view.findViewById(R.id.etVolumen);
        etCostoUnitario = view.findViewById(R.id.etCostoUnitario);
        spinnerSucursal = view.findViewById(R.id.spinnerSucursal);
        spinnerProducto = view.findViewById(R.id.spinnerProducto);
        spinnerProveedor = view.findViewById(R.id.spinnerProveedor);
        recyclerView = view.findViewById(R.id.recyclerView);
        etFechaVencimiento = view.findViewById(R.id.etFechaVencimiento);
        tilFechaVencimiento = view.findViewById(R.id.tilFechaVencimiento);
        etFechaProduccion = view.findViewById(R.id.etFechaProduccion);
        tilFechaProduccion = view.findViewById(R.id.tilFechaProduccion);
        etSearch = view.findViewById(R.id.etSearch);
        etFilterDateFrom = view.findViewById(R.id.etFilterDateFrom);
        etFilterDateTo = view.findViewById(R.id.etFilterDateTo);
        spinnerFilterSucursal = view.findViewById(R.id.spinnerFilterSucursal);

        etFechaVencimiento.setOnClickListener(v -> showDatePicker(etFechaVencimiento));
        etFechaProduccion.setOnClickListener(v -> showDatePicker(etFechaProduccion));
        etFilterDateFrom.setOnClickListener(v -> showFilterDatePicker(etFilterDateFrom));
        etFilterDateTo.setOnClickListener(v -> showFilterDatePicker(etFilterDateTo));

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { filterLotes(); }
            @Override public void afterTextChanged(Editable s) {}
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new LoteAdapter(new ArrayList<>(), this::confirmDelete);
        recyclerView.setAdapter(adapter);

        apiService = ApiClient.getClient(getContext()).create(ApiService.class);
        sessionManager = new com.example.template.utils.SessionManager(getContext());

        btnToggleForm.setOnClickListener(v -> toggleForm());
        btnGuardar.setOnClickListener(v -> saveLote());

        btnToggleFilters = view.findViewById(R.id.btnToggleFilters);
        btnToggleFilters.setOnClickListener(v -> {
            isFiltersVisible = !isFiltersVisible;
            if (isFiltersVisible) {
                cardFilter.setVisibility(View.VISIBLE);
                btnToggleFilters.setText("Ocultar filtros");
                btnToggleFilters.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0d9488")));
            } else {
                cardFilter.setVisibility(View.GONE);
                btnToggleFilters.setText("Filtros");
                btnToggleFilters.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0d9488")));
            }
        });

        setupSpinnerLink();

        loadLotes();
        loadSpinnersData();

        return view;
    }

    private void toggleForm() {
        isFormVisible = !isFormVisible;
        if (isFormVisible) {
            scrollForm.setVisibility(View.VISIBLE);
            cardFilter.setVisibility(View.GONE);
            btnToggleFilters.setVisibility(View.GONE);
            btnToggleForm.setImageResource(R.drawable.ic_close);
            btnToggleForm.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0d9488")));
        } else {
            scrollForm.setVisibility(View.GONE);
            cardFilter.setVisibility(isFiltersVisible ? View.VISIBLE : View.GONE);
            btnToggleFilters.setVisibility(View.VISIBLE);
            btnToggleForm.setImageResource(R.drawable.ic_add);
            btnToggleForm.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0f172a")));
            etVolumen.setText("");
            etCostoUnitario.setText("");
            etFechaVencimiento.setText("");
            etFechaProduccion.setText("");
        }
    }

    private void showDatePicker(EditText targetEditText) {
        if (getContext() == null) return;
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        int year = calendar.get(java.util.Calendar.YEAR);
        int month = calendar.get(java.util.Calendar.MONTH);
        int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);

        android.app.DatePickerDialog datePickerDialog = new android.app.DatePickerDialog(
                getContext(),
                (view, year1, month1, dayOfMonth) -> {
                    String selectedDate = String.format(Locale.US, "%04d-%02d-%02d", year1, month1 + 1, dayOfMonth);
                    targetEditText.setText(selectedDate);
                },
                year, month, day);
        datePickerDialog.show();
    }

    private void showFilterDatePicker(EditText targetEditText) {
        if (getContext() == null) return;
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        int year = calendar.get(java.util.Calendar.YEAR);
        int month = calendar.get(java.util.Calendar.MONTH);
        int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);

        android.app.DatePickerDialog datePickerDialog = new android.app.DatePickerDialog(
                getContext(),
                (view, year1, month1, dayOfMonth) -> {
                    String selectedDate = String.format(Locale.US, "%04d-%02d-%02d", year1, month1 + 1, dayOfMonth);
                    targetEditText.setText(selectedDate);
                    filterLotes();
                },
                year, month, day);
        datePickerDialog.setButton(android.content.DialogInterface.BUTTON_NEUTRAL, "Limpiar", (dialog, which) -> {
            targetEditText.setText("");
            filterLotes();
        });
        datePickerDialog.show();
    }

    private void setupSpinnerLink() {
        spinnerProducto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Producto prod = productosList.get(position);
                String provTarget = prod.getProveedorId();
                if (provTarget == null && prod.getProveedor() != null) {
                    provTarget = prod.getProveedor().getId();
                }
                if(provTarget != null && proveedoresList != null && provAdapter != null) {
                    for (int i = 0; i < proveedoresList.size(); i++) {
                        if (proveedoresList.get(i).getId().equals(provTarget)) {
                            spinnerProveedor.setSelection(i);
                            break;
                        }
                    }
                }
                spinnerProveedor.setEnabled(false);
                
                String cat = prod.getCategory();
                if (cat != null && (cat.equals("Abarrotes y Alimentos") || cat.equals("Bebidas"))) {
                    tilFechaProduccion.setVisibility(View.VISIBLE);
                    tilFechaVencimiento.setVisibility(View.VISIBLE);
                } else {
                    tilFechaProduccion.setVisibility(View.GONE);
                    etFechaProduccion.setText("");
                    tilFechaVencimiento.setVisibility(View.GONE);
                    etFechaVencimiento.setText("");
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
                    allLotesList = response.body();
                    filterLotes();
                }
            }
            @Override
            public void onFailure(Call<List<LoteIngreso>> call, Throwable t) {}
        });
    }

    private void loadSpinnersData() {
        
        apiService.getSucursales().enqueue(new Callback<List<Sucursal>>() {
            @Override
            public void onResponse(Call<List<Sucursal>> call, Response<List<Sucursal>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Sucursal> allBranches = response.body();
                    sucursalesList.clear();
                    
                    String userRole = sessionManager.getRole();
                    String userSucursalId = sessionManager.getSucursalId();
                    boolean isRestricted = !"OWNER".equalsIgnoreCase(userRole) && !"SUPER_ADMIN".equalsIgnoreCase(userRole) && userSucursalId != null && !userSucursalId.isEmpty();

                    if (isRestricted) {
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
                    
                    if (getContext() != null) {
                        ArrayAdapter<Sucursal> sucAdapter = new ArrayAdapter<>(
                            getContext(), android.R.layout.simple_spinner_item, sucursalesList
                        );
                        sucAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerSucursal.setAdapter(sucAdapter);

                        List<String> filterOptions = new ArrayList<>();
                        if (!isRestricted) {
                            filterOptions.add("Todas las sucursales");
                        }
                        for (Sucursal s : sucursalesList) {
                            filterOptions.add(s.getName());
                        }
                        
                        ArrayAdapter<String> filterSucAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, filterOptions);
                        filterSucAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerFilterSucursal.setAdapter(filterSucAdapter);
                        
                        if (isRestricted) {
                            spinnerFilterSucursal.setEnabled(false);
                        } else {
                            spinnerFilterSucursal.setEnabled(true);
                        }
                        
                        spinnerFilterSucursal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { filterLotes(); }
                            @Override public void onNothingSelected(AdapterView<?> parent) {}
                        });
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Sucursal>> call, Throwable t) {}
        });

        
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

    private void filterLotes() {
        if (allLotesList == null) return;
        
        String query = etSearch != null && etSearch.getText() != null ? etSearch.getText().toString().toLowerCase() : "";
        String dateFrom = etFilterDateFrom != null ? etFilterDateFrom.getText().toString() : "";
        String dateTo = etFilterDateTo != null ? etFilterDateTo.getText().toString() : "";
        
        String userRole = sessionManager.getRole();
        String userSucursalId = sessionManager.getSucursalId();
        boolean isRestricted = !"OWNER".equalsIgnoreCase(userRole) && !"SUPER_ADMIN".equalsIgnoreCase(userRole) && userSucursalId != null && !userSucursalId.isEmpty();

        Sucursal selectedBranch = null;
        String selectedOption = spinnerFilterSucursal != null && spinnerFilterSucursal.getSelectedItem() != null ? spinnerFilterSucursal.getSelectedItem().toString() : "";
        
        if (!selectedOption.equals("Todas las sucursales") && !selectedOption.isEmpty()) {
            for (Sucursal s : sucursalesList) {
                if (selectedOption.equals(s.getName())) {
                    selectedBranch = s;
                    break;
                }
            }
        }

        List<LoteIngreso> filteredList = new ArrayList<>();
        for (LoteIngreso lote : allLotesList) {
            boolean match = true;
            
            if (isRestricted) {
                if (lote.getSucursalId() == null || !lote.getSucursalId().equals(userSucursalId)) {
                    continue;
                }
            }
            
            
            if (!query.isEmpty() && lote.getProducto() != null) {
                String name = lote.getProducto().getName() != null ? lote.getProducto().getName().toLowerCase() : "";
                String sku = lote.getProducto().getSku() != null ? lote.getProducto().getSku().toLowerCase() : "";
                if (!name.contains(query) && !sku.contains(query)) {
                    match = false;
                }
            }
            
            
            if (match && selectedBranch != null) {
                if (lote.getSucursalId() == null || !lote.getSucursalId().equals(selectedBranch.getId())) {
                    match = false;
                }
            }
            
            
            if (match && (!dateFrom.isEmpty() || !dateTo.isEmpty())) {
                String venc = lote.getFechaVencimiento();
                if (venc == null || venc.isEmpty()) {
                    match = false;
                } else {
                    venc = venc.substring(0, Math.min(venc.length(), 10)); 
                    if (!dateFrom.isEmpty() && venc.compareTo(dateFrom) < 0) {
                        match = false;
                    }
                    if (!dateTo.isEmpty() && venc.compareTo(dateTo) > 0) {
                        match = false;
                    }
                }
            }
            
            if (match) {
                filteredList.add(lote);
            }
        }
        adapter.updateData(filteredList);
    }

    private void saveLote() {
        String volStr = etVolumen.getText().toString().trim();
        String fechaVencimiento = etFechaVencimiento.getText().toString().trim();
        if (fechaVencimiento.isEmpty()) fechaVencimiento = null;
        String fechaProduccion = etFechaProduccion.getText().toString().trim();
        if (fechaProduccion.isEmpty()) fechaProduccion = null;

        Sucursal selectedSucursal = (Sucursal) spinnerSucursal.getSelectedItem();
        Producto selectedProd = (Producto) spinnerProducto.getSelectedItem();
        Proveedor selectedProv = (Proveedor) spinnerProveedor.getSelectedItem();

        if (volStr.isEmpty() || selectedSucursal == null || selectedProd == null || selectedProv == null) {
            Toast.makeText(getContext(), "Campos requeridos vacíos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (tilFechaProduccion.getVisibility() == View.VISIBLE) {
            if (fechaProduccion == null || fechaVencimiento == null) {
                Toast.makeText(getContext(), "Las fechas de producción y vencimiento son obligatorias para este producto", Toast.LENGTH_SHORT).show();
                return;
            }
            if (fechaProduccion.compareTo(fechaVencimiento) >= 0) {
                Toast.makeText(getContext(), "La fecha de producción debe ser anterior a la de vencimiento", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        int vol = Integer.parseInt(volStr);

        LoteIngreso request = new LoteIngreso(selectedSucursal.getId(), selectedProd.getId(), selectedProv.getId(), vol, fechaVencimiento, fechaProduccion);
        
        String costoStr = etCostoUnitario.getText().toString().trim();
        if (!costoStr.isEmpty()) {
            try {
                request.setCostoUnitario(Double.parseDouble(costoStr));
            } catch (NumberFormatException e) {
                
            }
        }
        
        apiService.createLote(request).enqueue(new Callback<LoteIngreso>() {
            @Override
            public void onResponse(Call<LoteIngreso> call, Response<LoteIngreso> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Lote de Sourcing Confirmado", Toast.LENGTH_SHORT).show();
                    etVolumen.setText("");
                    etFechaProduccion.setText("");
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
        com.example.template.utils.DialogHelper.showConfirmDialog(
            getContext(),
            "Eliminar Ingreso",
            "¿Estás seguro de que deseas eliminar este lote de ingreso?\n\nEsta acción no se puede deshacer.",
            "Eliminar",
            () -> deleteLote(lote)
        );
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

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            getActivity().getWindow().setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }
}
