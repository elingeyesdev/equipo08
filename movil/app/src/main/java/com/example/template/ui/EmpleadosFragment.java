package com.example.template.ui;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.example.template.network.models.Empleado;
import com.example.template.network.models.Sucursal;
import com.example.template.ui.adapters.EmpleadosAdapter;
import com.example.template.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmpleadosFragment extends Fragment {

    private Button btnToggleForm, btnGuardar;
    private CardView cardForm;
    private EditText etNombre, etEmail, etPassword;
    private Spinner spinnerRol, spinnerSucursal, spinnerFilterRol, spinnerFilterSucursal;
    private RecyclerView recyclerView;
    private EmpleadosAdapter adapter;
    private ApiService apiService;
    private SessionManager sessionManager;
    private boolean isFormVisible = false;
    
    private List<Empleado> allEmpleados = new ArrayList<>();
    private List<Sucursal> sucursalesList = new ArrayList<>();
    private Empleado editingEmpleado = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_empleados, container, false);

        btnToggleForm = view.findViewById(R.id.btnToggleForm);
        btnGuardar = view.findViewById(R.id.btnGuardar);
        cardForm = view.findViewById(R.id.cardForm);
        etNombre = view.findViewById(R.id.etNombre);
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        spinnerRol = view.findViewById(R.id.spinnerRol);
        spinnerSucursal = view.findViewById(R.id.spinnerSucursal);
        
        spinnerFilterRol = view.findViewById(R.id.spinnerFilterRol);
        spinnerFilterSucursal = view.findViewById(R.id.spinnerFilterSucursal);
        
        recyclerView = view.findViewById(R.id.recyclerView);

        // Setup Role Spinner
        String[] roleOptions = new String[]{"VENDEDOR", "SUPERVISOR"};
        if (getContext() != null) {
            ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, roleOptions
            );
            roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerRol.setAdapter(roleAdapter);
            
            // Filters
            String[] filterRoleOptions = new String[]{"Todos los roles", "VENDEDOR", "SUPERVISOR", "OWNER"};
            ArrayAdapter<String> filterRoleAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, filterRoleOptions
            );
            filterRoleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerFilterRol.setAdapter(filterRoleAdapter);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EmpleadosAdapter(new ArrayList<>(), new EmpleadosAdapter.OnActionClickListener() {
            @Override
            public void onDeleteClick(Empleado empleado) {
                confirmDelete(empleado);
            }

            @Override
            public void onEditClick(Empleado empleado) {
                editEmpleado(empleado);
            }
        });
        recyclerView.setAdapter(adapter);

        apiService = ApiClient.getClient(getContext()).create(ApiService.class);
        sessionManager = new SessionManager(getContext());

        btnToggleForm.setOnClickListener(v -> toggleForm(false));
        btnGuardar.setOnClickListener(v -> saveEmpleado());

        loadSucursales();
        loadEmpleados();
        return view;
    }

    private void toggleForm(boolean fromEdit) {
        if (!fromEdit) {
            editingEmpleado = null; // Clear if manual toggle
            etNombre.setText("");
            etEmail.setText("");
            etPassword.setText("");
            btnGuardar.setText("Confirmar Alta de Personal");
        }
        
        isFormVisible = !isFormVisible || fromEdit;
        if (isFormVisible) {
            cardForm.setVisibility(View.VISIBLE);
            btnToggleForm.setText("X Cancelar");
            btnToggleForm.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#64748b")));
        } else {
            cardForm.setVisibility(View.GONE);
            btnToggleForm.setText("Registrar Nuevo");
            btnToggleForm.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1d4ed8")));
        }
    }

    private void editEmpleado(Empleado empleado) {
        editingEmpleado = empleado;
        etNombre.setText(empleado.getNombreCompleto());
        etEmail.setText(empleado.getCorreo());
        etPassword.setText(""); // Keep empty for security
        btnGuardar.setText("Actualizar Empleado");
        
        // Select Role
        if (empleado.getRol() != null) {
            if (empleado.getRol().equalsIgnoreCase("VENDEDOR")) spinnerRol.setSelection(0);
            else if (empleado.getRol().equalsIgnoreCase("SUPERVISOR")) spinnerRol.setSelection(1);
        }
        
        // Select Sucursal
        if (empleado.getSucursalId() != null) {
            for (int i = 0; i < sucursalesList.size(); i++) {
                if (sucursalesList.get(i).getId().equals(empleado.getSucursalId())) {
                    spinnerSucursal.setSelection(i);
                    break;
                }
            }
        }
        
        if (!isFormVisible) {
            toggleForm(true);
        }
    }

    private void loadSucursales() {
        apiService.getSucursales().enqueue(new Callback<List<Sucursal>>() {
            @Override
            public void onResponse(Call<List<Sucursal>> call, Response<List<Sucursal>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    sucursalesList = new ArrayList<>();
                    for (Sucursal s : response.body()) {
                        if (s.isActive()) {
                            sucursalesList.add(s);
                        }
                    }
                    
                    List<String> sucNames = new ArrayList<>();
                    List<String> filterSucNames = new ArrayList<>();
                    filterSucNames.add("Todas las sucursales");
                    
                    for (Sucursal s : sucursalesList) {
                        sucNames.add(s.getName());
                        filterSucNames.add(s.getName());
                    }
                    
                    if (getContext() != null) {
                        ArrayAdapter<String> sucAdapter = new ArrayAdapter<>(
                            getContext(), android.R.layout.simple_spinner_item, sucNames
                        );
                        sucAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerSucursal.setAdapter(sucAdapter);
                        
                        ArrayAdapter<String> filterSucAdapter = new ArrayAdapter<>(
                            getContext(), android.R.layout.simple_spinner_item, filterSucNames
                        );
                        filterSucAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerFilterSucursal.setAdapter(filterSucAdapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Sucursal>> call, Throwable t) { }
        });
    }

    private void loadEmpleados() {
        apiService.getEmpleados().enqueue(new Callback<List<Empleado>>() {
            @Override
            public void onResponse(Call<List<Empleado>> call, Response<List<Empleado>> response) {
                allEmpleados = new ArrayList<>();
                
                // Add the Owner manually
                Empleado owner = new Empleado();
                owner.setId("owner_id");
                String tenantName = sessionManager != null ? sessionManager.getTenantName() : "Mi Tienda";
                String ownerEmail = sessionManager != null ? sessionManager.getEmail() : "admin@mitienda.com";
                owner.setNombreCompleto(tenantName);
                owner.setCorreo(ownerEmail);
                owner.setRol("OWNER");
                owner.setSucursalNombre("Administración Global");
                owner.setEstado("Activo");
                allEmpleados.add(owner);
                
                if (response.isSuccessful() && response.body() != null) {
                    allEmpleados.addAll(response.body());
                }
                applyFilters();
            }

            @Override
            public void onFailure(Call<List<Empleado>> call, Throwable t) {
                if(getContext() != null) Toast.makeText(getContext(), "Error al cargar empleados", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void applyFilters() {
        String roleFilter = spinnerFilterRol.getSelectedItem() != null ? spinnerFilterRol.getSelectedItem().toString() : "Todos los roles";
        String sucFilter = spinnerFilterSucursal.getSelectedItem() != null ? spinnerFilterSucursal.getSelectedItem().toString() : "Todas las sucursales";
        
        List<Empleado> filtered = new ArrayList<>();
        for (Empleado e : allEmpleados) {
            boolean matchRole = roleFilter.equals("Todos los roles") || roleFilter.equalsIgnoreCase(e.getRol());
            boolean matchSuc = sucFilter.equals("Todas las sucursales") || sucFilter.equals(e.getSucursalNombre());
            
            if (matchRole && matchSuc) {
                filtered.add(e);
            }
        }
        
        adapter.updateData(filtered);
    }

    private void saveEmpleado() {
        String nombre = etNombre.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String rol = spinnerRol.getSelectedItem() != null ? spinnerRol.getSelectedItem().toString() : "VENDEDOR";
        
        int sucIndex = spinnerSucursal.getSelectedItemPosition();
        String sucursalId = null;
        String sucursalNombre = null;
        if (sucIndex >= 0 && sucIndex < sucursalesList.size()) {
            sucursalId = sucursalesList.get(sucIndex).getId();
            sucursalNombre = sucursalesList.get(sucIndex).getName();
        }

        if (nombre.isEmpty()) { etNombre.setError("Requerido"); return; }
        if (email.isEmpty()) { etEmail.setError("Requerido"); return; }
        if (editingEmpleado == null && password.isEmpty()) { etPassword.setError("Requerido"); return; }

        if (password.isEmpty()) {
            password = null;
        }

        Empleado request = new Empleado(nombre, email, password, rol, sucursalId);
        
        if (editingEmpleado != null) {
            request.setId(editingEmpleado.getId());
            apiService.updateEmpleado(editingEmpleado.getId(), request).enqueue(new Callback<Empleado>() {
                @Override
                public void onResponse(Call<Empleado> call, Response<Empleado> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Empleado actualizado", Toast.LENGTH_SHORT).show();
                        editingEmpleado = null;
                        toggleForm(false);
                        loadEmpleados();
                    } else {
                        Toast.makeText(getContext(), "Error al actualizar", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<Empleado> call, Throwable t) {
                    Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            apiService.createEmpleado(request).enqueue(new Callback<Empleado>() {
                @Override
                public void onResponse(Call<Empleado> call, Response<Empleado> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Empleado registrado", Toast.LENGTH_SHORT).show();
                        toggleForm(false);
                        loadEmpleados();
                    } else {
                        Toast.makeText(getContext(), "Error al crear", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Empleado> call, Throwable t) {
                    Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void confirmDelete(Empleado empleado) {
        if (getContext() == null) return;
        new androidx.appcompat.app.AlertDialog.Builder(getContext())
            .setTitle("Eliminar Empleado")
            .setMessage("¿Estás seguro de que quieres eliminar a " + empleado.getNombreCompleto() + "?")
            .setPositiveButton("Eliminar", (dialog, which) -> deleteEmpleado(empleado))
            .setNegativeButton("Cancelar", null)
            .show();
    }

    private void deleteEmpleado(Empleado empleado) {
        apiService.deleteEmpleado(empleado.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Empleado eliminado", Toast.LENGTH_SHORT).show();
                    loadEmpleados();
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
