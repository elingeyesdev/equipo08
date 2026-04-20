package com.example.template.ui;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.template.network.models.Proveedor;
import com.example.template.ui.adapters.ProveedorAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProvidersFragment extends Fragment {

    private Button btnToggleForm, btnGuardar, btnBuscarNit;
    private CardView cardForm;
    private EditText etRazonSocial, etNit, etEmail;
    private RecyclerView recyclerView;
    private ProveedorAdapter adapter;
    private ApiService apiService;
    private boolean isFormVisible = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_providers, container, false);

        btnToggleForm = view.findViewById(R.id.btnToggleForm);
        btnGuardar = view.findViewById(R.id.btnGuardar);
        cardForm = view.findViewById(R.id.cardForm);
        etRazonSocial = view.findViewById(R.id.etRazonSocial);
        etNit = view.findViewById(R.id.etNit);
        etEmail = view.findViewById(R.id.etEmail);
        recyclerView = view.findViewById(R.id.recyclerView);

        btnBuscarNit = view.findViewById(R.id.btnBuscarNit);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ProveedorAdapter(new ArrayList<>(), this::confirmDelete);
        recyclerView.setAdapter(adapter);

        apiService = ApiClient.getClient(getContext()).create(ApiService.class);

        btnToggleForm.setOnClickListener(v -> toggleForm());
        btnGuardar.setOnClickListener(v -> saveProveedor());
        
        btnBuscarNit.setOnClickListener(v -> {
            String nit = etNit.getText().toString().trim();
            if (nit.isEmpty()) {
                etNit.setError("Ingrese un NIT");
                return;
            }
            apiService.getGlobalProveedor(nit).enqueue(new Callback<Proveedor>() {
                @Override
                public void onResponse(Call<Proveedor> call, Response<Proveedor> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        etRazonSocial.setText(response.body().getName());
                        etEmail.setText(response.body().getContactEmail() != null ? response.body().getContactEmail() : "Sin email");
                        Toast.makeText(getContext(), "Proveedor encontrado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "NIT no encontrado en la BD global", Toast.LENGTH_SHORT).show();
                        etRazonSocial.setText("");
                        etEmail.setText("");
                    }
                }
                @Override
                public void onFailure(Call<Proveedor> call, Throwable t) {
                    Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        loadProveedores();
        return view;
    }

    private void toggleForm() {
        isFormVisible = !isFormVisible;
        if (isFormVisible) {
            cardForm.setVisibility(View.VISIBLE);
            btnToggleForm.setText("X Cancelar");
            btnToggleForm.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#64748b"))); // Gris
        } else {
            cardForm.setVisibility(View.GONE);
            btnToggleForm.setText("Nuevo Proveedor");
            btnToggleForm.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2b3b55"))); // Azul oscuro primario
        }
    }

    private void loadProveedores() {
        apiService.getProveedores().enqueue(new Callback<List<Proveedor>>() {
            @Override
            public void onResponse(Call<List<Proveedor>> call, Response<List<Proveedor>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.updateData(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Proveedor>> call, Throwable t) {
                if(getContext() != null) Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProveedor() {
        String name = etRazonSocial.getText().toString().trim();
        String nit = etNit.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (nit.isEmpty()) {
            etNit.setError("Primero busque el NIT");
            return;
        }

        if (name.isEmpty()) {
            Toast.makeText(getContext(), "Asegúrese de buscar un proveedor válido", Toast.LENGTH_SHORT).show();
            return;
        }

        Proveedor request = new Proveedor(name, email.equals("Sin email") || email.isEmpty() ? null : email, nit);
        apiService.createProveedor(request).enqueue(new Callback<Proveedor>() {
            @Override
            public void onResponse(Call<Proveedor> call, Response<Proveedor> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Proveedor guardado", Toast.LENGTH_SHORT).show();
                    etRazonSocial.setText(""); etNit.setText(""); etEmail.setText("");
                    toggleForm();
                    loadProveedores(); // refresh
                } else {
                    Toast.makeText(getContext(), "Error al guardar (Verifique NIT)", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Proveedor> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmDelete(Proveedor proveedor) {
        if (getContext() == null) return;
        new androidx.appcompat.app.AlertDialog.Builder(getContext())
            .setTitle("Eliminar Proveedor")
            .setMessage("¿Estás seguro de que quieres eliminar a " + proveedor.getName() + "?")
            .setPositiveButton("Eliminar", (dialog, which) -> deleteProveedor(proveedor))
            .setNegativeButton("Cancelar", null)
            .show();
    }

    private void deleteProveedor(Proveedor proveedor) {
        apiService.deleteProveedor(proveedor.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Proveedor eliminado", Toast.LENGTH_SHORT).show();
                    loadProveedores();
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
