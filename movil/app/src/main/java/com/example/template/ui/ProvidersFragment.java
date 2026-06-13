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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProvidersFragment extends Fragment {

    private FloatingActionButton btnToggleForm;
    private Button btnGuardar, btnBuscarNit;
    private CardView cardForm;
    private EditText etRazonSocial, etNit, etEmail;
    private RecyclerView recyclerView;
    private ProveedorAdapter adapter;
    private ApiService apiService;
    private boolean isFormVisible = false;
    private Proveedor editingProveedor = null;

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
        adapter = new ProveedorAdapter(new ArrayList<>(), new ProveedorAdapter.OnActionClickListener() {
            @Override
            public void onDeleteClick(Proveedor proveedor) {
                confirmDelete(proveedor);
            }

            @Override
            public void onEditClick(Proveedor proveedor) {
                editProveedor(proveedor);
            }
        });
        recyclerView.setAdapter(adapter);

        apiService = ApiClient.getClient(getContext()).create(ApiService.class);

        btnToggleForm.setOnClickListener(v -> toggleForm(false));
        btnGuardar.setOnClickListener(v -> saveProveedor());
        
        btnBuscarNit.setOnClickListener(v -> {
            String nit = etNit.getText().toString().trim();
            if (nit.isEmpty()) {
                etNit.setError("Ingrese un NIT");
                return;
            }
            apiService.getGlobalProveedor(nit).enqueue(new retrofit2.Callback<okhttp3.ResponseBody>() {
                @Override
                public void onResponse(retrofit2.Call<okhttp3.ResponseBody> call, retrofit2.Response<okhttp3.ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            String json = response.body().string();
                            if (json == null || json.trim().isEmpty() || json.trim().equals("null")) {
                                Toast.makeText(getContext(), "NIT no registrado globalmente. Puedes ingresar los datos manualmente para registrarlo.", Toast.LENGTH_LONG).show();
                                return;
                            }
                            com.example.template.network.models.Proveedor proveedor = new com.google.gson.Gson().fromJson(json, com.example.template.network.models.Proveedor.class);
                            if (proveedor != null && proveedor.getName() != null) {
                                etRazonSocial.setText(proveedor.getName());
                                etEmail.setText(proveedor.getContactEmail() != null ? proveedor.getContactEmail() : "");
                                Toast.makeText(getContext(), "Proveedor Maestro encontrado y autocompletado.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "NIT no registrado globalmente. Puedes ingresar los datos manualmente para registrarlo.", Toast.LENGTH_LONG).show();
                            }
                        } catch (java.io.IOException e) {
                            Toast.makeText(getContext(), "Error al leer respuesta: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        } catch (com.google.gson.JsonSyntaxException e) {
                            Toast.makeText(getContext(), "Error de formato de datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "NIT no registrado globalmente. Puedes ingresar los datos manualmente para registrarlo.", Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onFailure(retrofit2.Call<okhttp3.ResponseBody> call, Throwable t) {
                    Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        loadProveedores();
        return view;
    }

    private void toggleForm(boolean fromEdit) {
        if (!fromEdit) {
            editingProveedor = null;
            etRazonSocial.setText("");
            etNit.setText("");
            etEmail.setText("");
            btnGuardar.setText("Anexar proveedor a mi tienda");
        }
        isFormVisible = !isFormVisible || fromEdit;
        if (isFormVisible) {
            cardForm.setVisibility(View.VISIBLE);
            btnToggleForm.setImageResource(R.drawable.ic_close);
            btnToggleForm.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0d9488"))); // Gris/Verde
        } else {
            cardForm.setVisibility(View.GONE);
            btnToggleForm.setImageResource(R.drawable.ic_add);
            btnToggleForm.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0f172a"))); // Azul oscuro primario
        }
    }

    private void editProveedor(Proveedor proveedor) {
        editingProveedor = proveedor;
        etRazonSocial.setText(proveedor.getName());
        etNit.setText(proveedor.getTaxId());
        etEmail.setText(proveedor.getContactEmail() != null ? proveedor.getContactEmail() : "");
        btnGuardar.setText("Actualizar proveedor");
        if (!isFormVisible) {
            toggleForm(true);
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
            etNit.setError("Ingrese un NIT");
            return;
        }

        if (name.isEmpty()) {
            etRazonSocial.setError("Ingrese o busque una razón social válida");
            return;
        }

        Proveedor request = new Proveedor(name, email.isEmpty() ? null : email, nit);
        if (editingProveedor != null) {
            request.setId(editingProveedor.getId());
            apiService.updateProveedor(editingProveedor.getId(), request).enqueue(new Callback<Proveedor>() {
                @Override
                public void onResponse(Call<Proveedor> call, Response<Proveedor> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Proveedor actualizado", Toast.LENGTH_SHORT).show();
                        editingProveedor = null;
                        toggleForm(false);
                        loadProveedores();
                    } else {
                        Toast.makeText(getContext(), "Error al actualizar", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Proveedor> call, Throwable t) {
                    Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            apiService.createProveedor(request).enqueue(new Callback<Proveedor>() {
                @Override
                public void onResponse(Call<Proveedor> call, Response<Proveedor> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Proveedor guardado", Toast.LENGTH_SHORT).show();
                        editingProveedor = null;
                        toggleForm(false);
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

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            getActivity().getWindow().setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }
}
