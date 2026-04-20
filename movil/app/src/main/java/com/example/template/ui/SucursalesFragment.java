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
import com.example.template.network.models.Sucursal;
import com.example.template.ui.adapters.SucursalAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SucursalesFragment extends Fragment {

    private Button btnToggleForm, btnGuardar;
    private CardView cardForm;
    private EditText etName, etAddress, etPhone;
    private Spinner spinnerStatus;
    private RecyclerView recyclerView;
    private SucursalAdapter adapter;
    private ApiService apiService;
    private boolean isFormVisible = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sucursales, container, false);

        btnToggleForm = view.findViewById(R.id.btnToggleForm);
        btnGuardar = view.findViewById(R.id.btnGuardar);
        cardForm = view.findViewById(R.id.cardForm);
        etName = view.findViewById(R.id.etName);
        etAddress = view.findViewById(R.id.etAddress);
        etPhone = view.findViewById(R.id.etPhone);
        spinnerStatus = view.findViewById(R.id.spinnerStatus);
        recyclerView = view.findViewById(R.id.recyclerView);

        // Setup Spinner
        String[] statusOptions = new String[]{"Activa y Operando", "Cerrada / Inactiva"};
        if (getContext() != null) {
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, statusOptions
            );
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerStatus.setAdapter(spinnerAdapter);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SucursalAdapter(new ArrayList<>(), this::confirmDelete);
        recyclerView.setAdapter(adapter);

        apiService = ApiClient.getClient(getContext()).create(ApiService.class);

        btnToggleForm.setOnClickListener(v -> toggleForm());
        btnGuardar.setOnClickListener(v -> saveSucursal());

        loadSucursales();
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
            btnToggleForm.setText("Nueva");
            btnToggleForm.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2b3b55")));
        }
    }

    private void loadSucursales() {
        apiService.getSucursales().enqueue(new Callback<List<Sucursal>>() {
            @Override
            public void onResponse(Call<List<Sucursal>> call, Response<List<Sucursal>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.updateData(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Sucursal>> call, Throwable t) {
                if(getContext() != null) Toast.makeText(getContext(), "Error al cargar sucursales", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveSucursal() {
        String name = etName.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        boolean isActive = spinnerStatus.getSelectedItemPosition() == 0; // 0 is "Activa y Operando"

        if (name.isEmpty()) {
            etName.setError("Requerido");
            return;
        }

        Sucursal request = new Sucursal(
                name,
                address.isEmpty() ? null : address,
                phone.isEmpty() ? null : phone,
                isActive
        );

        apiService.createSucursal(request).enqueue(new Callback<Sucursal>() {
            @Override
            public void onResponse(Call<Sucursal> call, Response<Sucursal> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Sucursal creada", Toast.LENGTH_SHORT).show();
                    etName.setText(""); etAddress.setText(""); etPhone.setText("");
                    spinnerStatus.setSelection(0);
                    toggleForm();
                    loadSucursales(); // refresh
                } else {
                    Toast.makeText(getContext(), "Error al crear", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Sucursal> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmDelete(Sucursal sucursal) {
        if (getContext() == null) return;
        new androidx.appcompat.app.AlertDialog.Builder(getContext())
            .setTitle("Eliminar Sucursal")
            .setMessage("¿Estás seguro de que quieres eliminar la sucursal " + sucursal.getName() + "?")
            .setPositiveButton("Eliminar", (dialog, which) -> deleteSucursal(sucursal))
            .setNegativeButton("Cancelar", null)
            .show();
    }

    private void deleteSucursal(Sucursal sucursal) {
        apiService.deleteSucursal(sucursal.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Sucursal eliminada", Toast.LENGTH_SHORT).show();
                    loadSucursales();
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
