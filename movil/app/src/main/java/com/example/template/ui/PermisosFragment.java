package com.example.template.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.template.R;
import com.example.template.network.ApiClient;
import com.example.template.network.ApiService;
import com.example.template.network.models.PermisosRoles;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PermisosFragment extends Fragment {

    private LinearLayout llSupervisorPermisos, llVendedorPermisos;
    private Button btnAplicarSupervisor, btnAplicarVendedor;
    private ApiService apiService;
    
    private Map<String, Switch> supervisorSwitches = new HashMap<>();
    private Map<String, Switch> vendedorSwitches = new HashMap<>();

    private String[] categorias = {
            "SUCURSALES FÍSICAS",
            "CATÁLOGO CENTRAL",
            "AUDITORÍA SOURCING",
            "NIVELES DE INVENTARIO",
            "RECURSOS HUMANOS"
    };

    private Map<String, String[]> permisosPorCategoria = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_permisos, container, false);

        llSupervisorPermisos = view.findViewById(R.id.llSupervisorPermisos);
        llVendedorPermisos = view.findViewById(R.id.llVendedorPermisos);
        btnAplicarSupervisor = view.findViewById(R.id.btnAplicarSupervisor);
        btnAplicarVendedor = view.findViewById(R.id.btnAplicarVendedor);

        apiService = ApiClient.getClient(getContext()).create(ApiService.class);

        // Inicializar datos
        permisosPorCategoria.put("SUCURSALES FÍSICAS", new String[]{
                "Consultar Directorio Geográfico",
                "Control de Alta, Edición y Cierre"
        });
        permisosPorCategoria.put("CATÁLOGO CENTRAL", new String[]{
                "Visualización Global de Productos",
                "Creación y Alteración de Precios"
        });
        permisosPorCategoria.put("AUDITORÍA SOURCING", new String[]{
                "Inspeccionar Historial de Accesos",
                "Registrar Nuevos Lotes y Costos"
        });
        permisosPorCategoria.put("NIVELES DE INVENTARIO", new String[]{
                "Visualización de Cantidades por Sucursal"
        });
        permisosPorCategoria.put("RECURSOS HUMANOS", new String[]{
                "Consultar Organigrama Interno",
                "Contratar y Desvincular Personal"
        });

        buildUI(llSupervisorPermisos, supervisorSwitches);
        buildUI(llVendedorPermisos, vendedorSwitches);

        btnAplicarSupervisor.setOnClickListener(v -> savePermisos("SUPERVISOR", supervisorSwitches));
        btnAplicarVendedor.setOnClickListener(v -> savePermisos("VENDEDOR", vendedorSwitches));

        loadPermisos();
        return view;
    }

    private void buildUI(LinearLayout parent, Map<String, Switch> switchesMap) {
        for (String cat : categorias) {
            // Categoria Title
            TextView tvCat = new TextView(getContext());
            tvCat.setText(cat);
            tvCat.setTypeface(null, android.graphics.Typeface.BOLD);
            tvCat.setTextColor(android.graphics.Color.parseColor("#2b3b55"));
            tvCat.setPadding(0, 16, 0, 8);
            parent.addView(tvCat);

            // Permisos
            String[] perms = permisosPorCategoria.get(cat);
            if (perms != null) {
                for (String p : perms) {
                    LinearLayout row = new LinearLayout(getContext());
                    row.setOrientation(LinearLayout.HORIZONTAL);
                    row.setLayoutParams(new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    ));
                    row.setPadding(0, 8, 0, 8);

                    TextView tvPermiso = new TextView(getContext());
                    tvPermiso.setText(p);
                    tvPermiso.setTextColor(android.graphics.Color.parseColor("#5a6a85"));
                    LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(
                            0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f
                    );
                    tvPermiso.setLayoutParams(tvParams);
                    row.addView(tvPermiso);

                    Switch sw = new Switch(getContext());
                    switchesMap.put(p, sw);
                    row.addView(sw);

                    parent.addView(row);
                }
            }
        }
    }

    private void loadPermisos() {
        apiService.getPermisos().enqueue(new Callback<List<PermisosRoles>>() {
            @Override
            public void onResponse(Call<List<PermisosRoles>> call, Response<List<PermisosRoles>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (PermisosRoles pr : response.body()) {
                        Map<String, Switch> targetSwitches = null;
                        if ("SUPERVISOR".equalsIgnoreCase(pr.getRol())) {
                            targetSwitches = supervisorSwitches;
                        } else if ("VENDEDOR".equalsIgnoreCase(pr.getRol())) {
                            targetSwitches = vendedorSwitches;
                        }

                        if (targetSwitches != null && pr.getPermisos() != null) {
                            for (Map.Entry<String, Boolean> entry : pr.getPermisos().entrySet()) {
                                Switch sw = targetSwitches.get(entry.getKey());
                                if (sw != null) {
                                    sw.setChecked(entry.getValue());
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<PermisosRoles>> call, Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error al cargar permisos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void savePermisos(String rol, Map<String, Switch> switchesMap) {
        Map<String, Boolean> permisos = new HashMap<>();
        for (Map.Entry<String, Switch> entry : switchesMap.entrySet()) {
            permisos.put(entry.getKey(), entry.getValue().isChecked());
        }

        PermisosRoles req = new PermisosRoles(rol, permisos);
        apiService.updatePermisos(rol, req).enqueue(new Callback<PermisosRoles>() {
            @Override
            public void onResponse(Call<PermisosRoles> call, Response<PermisosRoles> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Permisos actualizados para " + rol, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Error al actualizar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PermisosRoles> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
