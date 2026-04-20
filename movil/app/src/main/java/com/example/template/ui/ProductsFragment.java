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
import com.example.template.network.models.Producto;
import com.example.template.network.models.Proveedor;
import com.example.template.ui.adapters.ProductoAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductsFragment extends Fragment {

    private Button btnToggleForm, btnGuardar;
    private CardView cardForm;
    private EditText etName, etSku, etPrecioCoste, etPrecioVenta;
    private TextView tvMargen;
    private Spinner spinnerProveedor, spinnerCategoria;
    private RecyclerView recyclerView;
    private ProductoAdapter adapter;
    private ApiService apiService;
    private boolean isFormVisible = false;

    private List<Proveedor> proveedoresList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);

        btnToggleForm = view.findViewById(R.id.btnToggleForm);
        btnGuardar = view.findViewById(R.id.btnGuardar);
        cardForm = view.findViewById(R.id.cardForm);
        etName = view.findViewById(R.id.etName);
        etSku = view.findViewById(R.id.etSku);
        etPrecioCoste = view.findViewById(R.id.etPrecioCoste);
        etPrecioVenta = view.findViewById(R.id.etPrecioVenta);
        tvMargen = view.findViewById(R.id.tvMargen);
        spinnerProveedor = view.findViewById(R.id.spinnerProveedor);
        spinnerCategoria = view.findViewById(R.id.spinnerCategoria);
        recyclerView = view.findViewById(R.id.recyclerView);

        // Setup Categories
        String[] categorias = {"Otros", "Electrónica", "Ropa", "Alimentos", "Hogar"};
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categorias);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(catAdapter);

        // Setup Margen Calculation
        android.text.TextWatcher textWatcher = new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { calculateMargin(); }
            @Override public void afterTextChanged(android.text.Editable s) {}
        };
        etPrecioCoste.addTextChangedListener(textWatcher);
        etPrecioVenta.addTextChangedListener(textWatcher);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ProductoAdapter(new ArrayList<>(), this::confirmDelete);
        recyclerView.setAdapter(adapter);

        apiService = ApiClient.getClient(getContext()).create(ApiService.class);

        btnToggleForm.setOnClickListener(v -> toggleForm());
        btnGuardar.setOnClickListener(v -> saveProducto());

        loadProductos();
        loadProveedoresToSpinner();
        
        return view;
    }

    private void calculateMargin() {
        try {
            String costeStr = etPrecioCoste.getText().toString();
            String ventaStr = etPrecioVenta.getText().toString();
            if (!costeStr.isEmpty() && !ventaStr.isEmpty()) {
                double coste = Double.parseDouble(costeStr);
                double venta = Double.parseDouble(ventaStr);
                if (coste > 0) {
                    double margin = ((venta - coste) / coste) * 100;
                    tvMargen.setText(String.format(java.util.Locale.US, "%.0f%%", margin));
                } else {
                    tvMargen.setText("0%");
                }
            } else {
                tvMargen.setText("0%");
            }
        } catch (Exception e) {
            tvMargen.setText("0%");
        }
    }

    private void toggleForm() {
        isFormVisible = !isFormVisible;
        if (isFormVisible) {
            cardForm.setVisibility(View.VISIBLE);
            btnToggleForm.setText("X Cancelar");
            btnToggleForm.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#64748b")));
        } else {
            cardForm.setVisibility(View.GONE);
            btnToggleForm.setText("Nuevo Artículo");
            btnToggleForm.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2b3b55")));
        }
    }

    private void loadProductos() {
        apiService.getProductos().enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.updateData(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) { }
        });
    }

    private void loadProveedoresToSpinner() {
        apiService.getProveedores().enqueue(new Callback<List<Proveedor>>() {
            @Override
            public void onResponse(Call<List<Proveedor>> call, Response<List<Proveedor>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    proveedoresList.clear();
                    proveedoresList.addAll(response.body());
                    if (getContext() != null) {
                        ArrayAdapter<Proveedor> spinnerAdapter = new ArrayAdapter<>(
                            getContext(), android.R.layout.simple_spinner_item, proveedoresList
                        );
                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerProveedor.setAdapter(spinnerAdapter);
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Proveedor>> call, Throwable t) { }
        });
    }

    private void saveProducto() {
        String name = etName.getText().toString().trim();
        String sku = etSku.getText().toString().trim();
        String cat = spinnerCategoria.getSelectedItem() != null ? spinnerCategoria.getSelectedItem().toString() : "Otros";
        String costeStr = etPrecioCoste.getText().toString().trim();
        String ventaStr = etPrecioVenta.getText().toString().trim();
        
        Proveedor selectedProv = (Proveedor) spinnerProveedor.getSelectedItem();

        if (name.isEmpty() || sku.isEmpty() || costeStr.isEmpty() || ventaStr.isEmpty() || selectedProv == null) {
            Toast.makeText(getContext(), "Por favor, completa todos los campos requeridos", Toast.LENGTH_SHORT).show();
            return;
        }

        double coste = Double.parseDouble(costeStr);
        double venta = Double.parseDouble(ventaStr);

        Producto request = new Producto(name, sku, cat, coste, venta, selectedProv.getId());
        apiService.createProducto(request).enqueue(new Callback<Producto>() {
            @Override
            public void onResponse(Call<Producto> call, Response<Producto> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Producto guardado", Toast.LENGTH_SHORT).show();
                    etName.setText(""); etSku.setText(""); etPrecioCoste.setText(""); etPrecioVenta.setText("");
                    toggleForm();
                    loadProductos(); 
                } else {
                    Toast.makeText(getContext(), "Error al guardar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Producto> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void confirmDelete(Producto producto) {
        if (getContext() == null) return;
        new androidx.appcompat.app.AlertDialog.Builder(getContext())
            .setTitle("Eliminar Producto")
            .setMessage("¿Estás seguro de que quieres eliminar " + producto.getName() + "?")
            .setPositiveButton("Eliminar", (dialog, which) -> deleteProducto(producto))
            .setNegativeButton("Cancelar", null)
            .show();
    }

    private void deleteProducto(Producto producto) {
        apiService.deleteProducto(producto.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Producto eliminado", Toast.LENGTH_SHORT).show();
                    loadProductos();
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
