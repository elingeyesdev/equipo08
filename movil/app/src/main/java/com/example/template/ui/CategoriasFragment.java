package com.example.template.ui;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.template.network.models.Categoria;
import com.example.template.network.models.PermisosRoles;
import com.example.template.ui.adapters.CategoriasAdapter;
import com.example.template.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoriasFragment extends Fragment {

    private FloatingActionButton btnToggleForm;
    private Button btnGuardar;
    private CardView cardForm;
    private TextView tvFormTitle;
    private EditText etNombre;
    private RecyclerView recyclerView;
    private CategoriasAdapter adapter;
    private ApiService apiService;
    private SessionManager sessionManager;
    
    
    private Button btnToggleFilter;
    private CardView cardFilter;
    private EditText etSearch;
    private boolean isFilterVisible = false;

    private boolean isFormVisible = false;
    private Categoria editingCategoria = null;
    private List<Categoria> allCategoriasList = new ArrayList<>();

    private boolean canCreate = true;
    private boolean canEdit = true;
    private boolean canDelete = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categorias, container, false);

        btnToggleForm = view.findViewById(R.id.btnToggleForm);
        btnGuardar = view.findViewById(R.id.btnGuardar);
        cardForm = view.findViewById(R.id.cardForm);
        tvFormTitle = view.findViewById(R.id.tvFormTitle);
        etNombre = view.findViewById(R.id.etNombre);
        recyclerView = view.findViewById(R.id.recyclerView);

        
        btnToggleFilter = view.findViewById(R.id.btnToggleFilter);
        cardFilter = view.findViewById(R.id.cardFilter);
        etSearch = view.findViewById(R.id.etSearch);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CategoriasAdapter(new ArrayList<>(), new CategoriasAdapter.OnActionClickListener() {
            @Override
            public void onDeleteClick(Categoria categoria) {
                if (canDelete) {
                    confirmDelete(categoria);
                } else {
                    Toast.makeText(getContext(), "No tienes permiso para eliminar categorías", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onEditClick(Categoria categoria) {
                if (canEdit) {
                    editCategoria(categoria);
                } else {
                    Toast.makeText(getContext(), "No tienes permiso para editar categorías", Toast.LENGTH_SHORT).show();
                }
            }
        });
        recyclerView.setAdapter(adapter);

        apiService = ApiClient.getClient(getContext()).create(ApiService.class);
        sessionManager = new SessionManager(getContext());

        btnToggleForm.setOnClickListener(v -> toggleForm(false));
        btnGuardar.setOnClickListener(v -> saveCategoria());

        
        btnToggleFilter.setOnClickListener(v -> {
            isFilterVisible = !isFilterVisible;
            if (isFilterVisible) {
                cardFilter.setVisibility(View.VISIBLE);
                btnToggleFilter.setText("Ocultar filtros");
            } else {
                cardFilter.setVisibility(View.GONE);
                btnToggleFilter.setText("Filtrar");
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCategorias();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadPermissions();
        loadCategorias();

        return view;
    }

    private void loadPermissions() {
        String role = sessionManager.getRole();
        if ("OWNER".equalsIgnoreCase(role)) {
            setManagementPermissions(true, true, true);
            return;
        }

        apiService.getPermisos().enqueue(new Callback<List<PermisosRoles>>() {
            @Override
            public void onResponse(Call<List<PermisosRoles>> call, Response<List<PermisosRoles>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (PermisosRoles pr : response.body()) {
                        if (pr.getRole().equalsIgnoreCase(role)) {
                            setManagementPermissions(pr.isCatalogoCrear(), pr.isCatalogoEditar(), pr.isCatalogoEliminar());
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<PermisosRoles>> call, Throwable t) {
                setManagementPermissions(false, false, false);
            }
        });
    }

    private void setManagementPermissions(boolean create, boolean edit, boolean delete) {
        this.canCreate = create;
        this.canEdit = edit;
        this.canDelete = delete;

        btnToggleForm.setVisibility(create ? View.VISIBLE : View.GONE);
        adapter.setCanManage(edit || delete);
    }

    private void toggleForm(boolean fromEdit) {
        if (!fromEdit) {
            editingCategoria = null;
            etNombre.setText("");
            tvFormTitle.setText("Nueva categoría");
            btnGuardar.setText("Registrar categoría");
        }
        isFormVisible = !isFormVisible || fromEdit;
        if (isFormVisible) {
            cardForm.setVisibility(View.VISIBLE);
            btnToggleForm.setImageResource(R.drawable.ic_close);
            btnToggleForm.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0d9488")));
        } else {
            cardForm.setVisibility(View.GONE);
            btnToggleForm.setImageResource(R.drawable.ic_add);
            btnToggleForm.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0f172a")));
        }
    }

    private void editCategoria(Categoria categoria) {
        editingCategoria = categoria;
        etNombre.setText(categoria.getNombre());
        tvFormTitle.setText("Editar categoría");
        btnGuardar.setText("Actualizar categoría");
        if (!isFormVisible) {
            toggleForm(true);
        }
    }

    private void loadCategorias() {
        apiService.getCategorias().enqueue(new Callback<List<Categoria>>() {
            @Override
            public void onResponse(Call<List<Categoria>> call, Response<List<Categoria>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allCategoriasList = response.body();
                    filterCategorias();
                }
            }

            @Override
            public void onFailure(Call<List<Categoria>> call, Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error al cargar categorías: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void filterCategorias() {
        String query = etSearch.getText().toString().trim().toLowerCase();
        if (query.isEmpty()) {
            adapter.updateData(allCategoriasList);
            return;
        }

        List<Categoria> filteredList = new ArrayList<>();
        for (Categoria c : allCategoriasList) {
            if (c.getNombre() != null && c.getNombre().toLowerCase().contains(query)) {
                filteredList.add(c);
            }
        }
        adapter.updateData(filteredList);
    }

    private void saveCategoria() {
        String nombre = etNombre.getText().toString().trim();

        if (nombre.isEmpty()) {
            Toast.makeText(getContext(), "El campo Nombre es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        Categoria request = new Categoria(nombre);
        if (editingCategoria != null) {
            request.setId(editingCategoria.getId());
            apiService.updateCategoria(editingCategoria.getId(), request).enqueue(new Callback<Categoria>() {
                @Override
                public void onResponse(Call<Categoria> call, Response<Categoria> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Categoría actualizada con éxito", Toast.LENGTH_SHORT).show();
                        editingCategoria = null;
                        toggleForm(false);
                        loadCategorias();
                    } else {
                        Toast.makeText(getContext(), "Error al actualizar la categoría", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Categoria> call, Throwable t) {
                    Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            apiService.createCategoria(request).enqueue(new Callback<Categoria>() {
                @Override
                public void onResponse(Call<Categoria> call, Response<Categoria> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Categoría creada con éxito", Toast.LENGTH_SHORT).show();
                        editingCategoria = null;
                        toggleForm(false);
                        loadCategorias();
                    } else {
                        Toast.makeText(getContext(), "Error al registrar la categoría", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Categoria> call, Throwable t) {
                    Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void confirmDelete(Categoria categoria) {
        com.example.template.utils.DialogHelper.showConfirmDialog(
            getContext(),
            "Eliminar Categoría",
            "¿Estás seguro de que deseas eliminar la categoría \"" + categoria.getNombre() + "\"?",
            "Eliminar",
            () -> deleteCategoria(categoria)
        );
    }

    private void deleteCategoria(Categoria categoria) {
        apiService.deleteCategoria(categoria.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Categoría eliminada", Toast.LENGTH_SHORT).show();
                    loadCategorias();
                } else {
                    Toast.makeText(getContext(), "Error al eliminar la categoría", Toast.LENGTH_SHORT).show();
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
