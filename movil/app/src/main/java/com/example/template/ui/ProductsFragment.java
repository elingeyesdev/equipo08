package com.example.template.ui;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
    private AutoCompleteTextView etName;
    private EditText etSku, etPrecioCoste, etPrecioVenta, etDescription, etStockMinimo, etImagenUrl;
    private TextView tvMargen;
    private Spinner spinnerProveedor, spinnerCategoria;
    private RecyclerView recyclerView;
    private ProductoAdapter adapter;
    private ApiService apiService;
    private boolean isFormVisible = false;
    private Producto editingProducto = null;

    private List<Proveedor> proveedoresList = new ArrayList<>();

    private com.google.android.material.textfield.TextInputLayout tilDescription;
    private android.widget.LinearLayout llAttributesContainer;

    private static final java.util.Map<String, String[][]> CATEGORY_ATTRIBUTES = new java.util.HashMap<>();
    static {
        CATEGORY_ATTRIBUTES.put("Bebidas", new String[][]{{"sabor", "Sabor"}, {"volumen_ml", "Volumen (ML)"}});
        CATEGORY_ATTRIBUTES.put("Ropa y Moda", new String[][]{{"talla", "Talla"}, {"color", "Color"}, {"genero", "Género"}});
        CATEGORY_ATTRIBUTES.put("Zapatos y Calzado", new String[][]{{"talla", "Talla"}, {"color", "Color"}});
        CATEGORY_ATTRIBUTES.put("Electrónica y Tecnología", new String[][]{{"marca", "Marca"}, {"modelo", "Modelo"}, {"garantia", "Garantía (Meses)"}});
        CATEGORY_ATTRIBUTES.put("Abarrotes y Alimentos", new String[][]{{"peso", "Peso/Gramaje"}, {"marca", "Marca"}});
        CATEGORY_ATTRIBUTES.put("Belleza y Cuidado Personal", new String[][]{{"volumen_ml", "Volumen (ML)"}, {"fragancia", "Fragancia / Tono"}});
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);

        btnToggleForm = view.findViewById(R.id.btnToggleForm);
        btnGuardar = view.findViewById(R.id.btnGuardar);
        cardForm = view.findViewById(R.id.cardForm);
        etName = view.findViewById(R.id.etName);
        etDescription = view.findViewById(R.id.etDescription);
        etSku = view.findViewById(R.id.etSku);
        etPrecioCoste = view.findViewById(R.id.etPrecioCoste);
        etPrecioVenta = view.findViewById(R.id.etPrecioVenta);
        tvMargen = view.findViewById(R.id.tvMargen);
        spinnerProveedor = view.findViewById(R.id.spinnerProveedor);
        spinnerCategoria = view.findViewById(R.id.spinnerCategoria);
        etStockMinimo = view.findViewById(R.id.etStockMinimo);
        etImagenUrl = view.findViewById(R.id.etImagenUrl);
        recyclerView = view.findViewById(R.id.recyclerView);

        tilDescription = view.findViewById(R.id.tilDescription);
        llAttributesContainer = view.findViewById(R.id.llAttributesContainer);

        spinnerCategoria.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                updateAttributeFields(editingProducto != null ? editingProducto.getAttributes() : null);
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        // Setup Categories
        String[] categorias = {
            "Abarrotes y Alimentos", "Bebidas", "Ropa y Moda", "Zapatos y Calzado",
            "Belleza y Cuidado Personal", "Joyería y Relojes", "Juguetes y Niños",
            "Hogar y Decoración", "Electrónica y Tecnología", "Ferretería y Construcción",
            "Deportes y Aire Libre", "Entretenimiento y Ocio", "Otros"
        };
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
        adapter = new ProductoAdapter(new ArrayList<>(), new ProductoAdapter.OnActionClickListener() {
            @Override
            public void onDeleteClick(Producto producto) {
                confirmDelete(producto);
            }

            @Override
            public void onEditClick(Producto producto) {
                editProducto(producto);
            }
        });
        recyclerView.setAdapter(adapter);

        apiService = ApiClient.getClient(getContext()).create(ApiService.class);

        btnToggleForm.setOnClickListener(v -> toggleForm(false));
        btnGuardar.setOnClickListener(v -> saveProducto());

        // Default hide
        btnToggleForm.setVisibility(View.GONE);
        adapter.setCanManage(false);

        loadPermissions();
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

    private void toggleForm(boolean fromEdit) {
        if (!fromEdit) {
            editingProducto = null;
            etName.setText("", false); etDescription.setText(""); etSku.setText(""); etPrecioCoste.setText(""); etPrecioVenta.setText(""); etStockMinimo.setText(""); etImagenUrl.setText("");
            btnGuardar.setText("Nuevo Artículo");
            if (llAttributesContainer != null) llAttributesContainer.removeAllViews();
            if (tilDescription != null) tilDescription.setVisibility(View.VISIBLE);
        }
        
        isFormVisible = !isFormVisible || fromEdit;
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

    private void editProducto(Producto producto) {
        editingProducto = producto;
        etName.setText(producto.getName(), false);
        etDescription.setText(producto.getDescription() != null ? producto.getDescription() : "");
        etSku.setText(producto.getSku());
        etPrecioCoste.setText(String.valueOf(producto.getPrecioCosto()));
        etPrecioVenta.setText(String.valueOf(producto.getPrecioVenta()));
        etStockMinimo.setText(String.valueOf(producto.getStockMinimo()));
        etImagenUrl.setText(producto.getImagenUrl() != null ? producto.getImagenUrl() : "");
        btnGuardar.setText("Actualizar Artículo");
        
        // Categoria
        if (producto.getCategory() != null) {
            ArrayAdapter<String> catAdapter = (ArrayAdapter<String>) spinnerCategoria.getAdapter();
            for (int i = 0; i < catAdapter.getCount(); i++) {
                if (catAdapter.getItem(i).equalsIgnoreCase(producto.getCategory())) {
                    spinnerCategoria.setSelection(i);
                    break;
                }
            }
        }
        
        // Force attributes generation even if category selection listener doesn't fire
        updateAttributeFields(producto.getAttributes());

        // Proveedor
        if (producto.getProveedorId() != null) {
            for (int i = 0; i < proveedoresList.size(); i++) {
                if (proveedoresList.get(i).getId().equals(producto.getProveedorId())) {
                    spinnerProveedor.setSelection(i);
                    break;
                }
            }
        }
        
        if (!isFormVisible) {
            toggleForm(true);
        }
    }

    private void loadPermissions() {
        String role = new com.example.template.utils.SessionManager(getContext()).getRole();
        if ("OWNER".equalsIgnoreCase(role)) {
            adapter.setCanManage(true);
            btnToggleForm.setVisibility(View.VISIBLE);
            return;
        }

        apiService.getPermisos().enqueue(new Callback<List<com.example.template.network.models.PermisosRoles>>() {
            @Override
            public void onResponse(Call<List<com.example.template.network.models.PermisosRoles>> call, Response<List<com.example.template.network.models.PermisosRoles>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (com.example.template.network.models.PermisosRoles pr : response.body()) {
                        if (pr.getRole().equalsIgnoreCase(role)) {
                            boolean canManage = pr.isCatalogoGestionar();
                            btnToggleForm.setVisibility(canManage ? View.VISIBLE : View.GONE);
                            adapter.setCanManage(canManage);
                            break;
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<List<com.example.template.network.models.PermisosRoles>> call, Throwable t) { }
        });
    }

    private void loadProductos() {
        apiService.getProductos().enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Producto> list = response.body();
                    adapter.updateData(list);

                    // Setup AutoComplete for product names
                    List<String> productNames = new ArrayList<>();
                    for (Producto p : list) {
                        if (p.getName() != null && !p.getName().isEmpty() && !productNames.contains(p.getName())) {
                            productNames.add(p.getName());
                        }
                    }
                    if (getContext() != null) {
                        ArrayAdapter<String> nameAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, productNames);
                        etName.setThreshold(1);
                        etName.setAdapter(nameAdapter);
                    }
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
        String desc = etDescription.getText().toString().trim();
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

        String stockMinimoStr = etStockMinimo.getText().toString().trim();
        int stockMinimo = stockMinimoStr.isEmpty() ? 10 : Integer.parseInt(stockMinimoStr);
        String imagenUrl = etImagenUrl.getText().toString().trim();

        Producto request = new Producto(name, sku, cat, coste, venta, selectedProv.getId(), desc.isEmpty() ? null : desc);
        request.setStockMinimo(stockMinimo);
        request.setImagenUrl(imagenUrl.isEmpty() ? null : imagenUrl);

        java.util.Map<String, String> attributes = new java.util.HashMap<>();
        String[][] attrs = CATEGORY_ATTRIBUTES.get(cat);
        if (attrs != null) {
            for (int i = 0; i < llAttributesContainer.getChildCount(); i++) {
                View child = llAttributesContainer.getChildAt(i);
                if (child instanceof com.google.android.material.textfield.TextInputLayout) {
                    com.google.android.material.textfield.TextInputLayout layout = (com.google.android.material.textfield.TextInputLayout) child;
                    EditText et = layout.getEditText();
                    if (et != null && et.getTag() != null) {
                        String key = et.getTag().toString();
                        String val = et.getText().toString().trim();
                        attributes.put(key, val);
                    }
                }
            }
            request.setDescription(null);
        }
        request.setAttributes(attributes);
        
        if (editingProducto != null) {
            apiService.updateProducto(editingProducto.getId(), request).enqueue(new Callback<Producto>() {
                @Override
                public void onResponse(Call<Producto> call, Response<Producto> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Producto actualizado", Toast.LENGTH_SHORT).show();
                        editingProducto = null;
                        toggleForm(false);
                        loadProductos(); 
                    } else {
                        Toast.makeText(getContext(), "Error al actualizar", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<Producto> call, Throwable t) {
                    Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            apiService.createProducto(request).enqueue(new Callback<Producto>() {
                @Override
                public void onResponse(Call<Producto> call, Response<Producto> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Producto guardado", Toast.LENGTH_SHORT).show();
                        toggleForm(false);
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

    private void updateAttributeFields(@Nullable java.util.Map<String, String> existingValues) {
        if (getContext() == null || llAttributesContainer == null) return;
        llAttributesContainer.removeAllViews();
        String selectedCategory = spinnerCategoria.getSelectedItem() != null ? spinnerCategoria.getSelectedItem().toString() : "";
        String[][] attrs = CATEGORY_ATTRIBUTES.get(selectedCategory);

        if (attrs != null) {
            if (tilDescription != null) tilDescription.setVisibility(View.GONE);
            llAttributesContainer.setVisibility(View.VISIBLE);

            for (String[] attr : attrs) {
                String key = attr[0];
                String label = attr[1];

                View inputView = LayoutInflater.from(getContext()).inflate(R.layout.item_attribute_input, llAttributesContainer, false);
                com.google.android.material.textfield.TextInputLayout layout = (com.google.android.material.textfield.TextInputLayout) inputView;
                EditText et = layout.findViewById(R.id.etAttributeInput);
                if (et != null) {
                    et.setHint(label);
                    et.setTag(key);
                    if (existingValues != null && existingValues.containsKey(key)) {
                        et.setText(existingValues.get(key));
                    }
                }
                llAttributesContainer.addView(inputView);
            }
        } else {
            if (tilDescription != null) tilDescription.setVisibility(View.VISIBLE);
            llAttributesContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            getActivity().getWindow().setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }
}
