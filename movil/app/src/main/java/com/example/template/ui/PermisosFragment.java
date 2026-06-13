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
            "Punto de Venta (POS)",
            "Catálogo Central (Productos)",
            "Gestión de Proveedores",
            "Sourcing (Recepción de Stock)",
            "Ajustes de Inventario",
            "Recursos Humanos",
            "Sucursales Físicas"
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

        // Inicializar datos EXACTAMENTE como en el frontend
        permisosPorCategoria.put("Punto de Venta (POS)", new String[]{
                "Acceder a la Terminal de POS / Ventas",
                "Procesar y Emitir Ventas",
                "Modificar Registros de Ventas",
                "Anular / Eliminar Transacciones"
        });
        permisosPorCategoria.put("Catálogo Central (Productos)", new String[]{
                "Visualización Global de Productos",
                "Añadir Nuevos Productos",
                "Modificar Precios y Artículos",
                "Eliminar Artículos del Catálogo"
        });
        permisosPorCategoria.put("Gestión de Proveedores", new String[]{
                "Consultar Directorio de Proveedores",
                "Registrar Nuevos Proveedores",
                "Editar Datos de Proveedores",
                "Dar de Baja Proveedores"
        });
        permisosPorCategoria.put("Sourcing (Recepción de Stock)", new String[]{
                "Inspeccionar Lotes de Compra",
                "Registrar Nuevos Ingresos",
                "Editar Lotes Existentes",
                "Anular Lotes Ingresados"
        });
        permisosPorCategoria.put("Ajustes de Inventario", new String[]{
                "Visualización de Auditorías",
                "Registrar Actas de Ajuste",
                "Modificar Incidencias",
                "Eliminar Historial de Pérdidas"
        });
        permisosPorCategoria.put("Recursos Humanos", new String[]{
                "Consultar Organigrama Interno",
                "Contratar Personal",
                "Editar Datos de Personal",
                "Desvincular Personal"
        });
        permisosPorCategoria.put("Sucursales Físicas", new String[]{
                "Consultar Directorio Geográfico",
                "Registrar Nuevas Sucursales",
                "Editar Datos de Sucursales",
                "Dar de Baja Sucursales"
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
            // Container for this category
            LinearLayout catContainer = new LinearLayout(getContext());
            catContainer.setOrientation(LinearLayout.VERTICAL);
            catContainer.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            catContainer.setPadding(0, 8, 0, 8);
            catContainer.setBackgroundResource(android.R.drawable.list_selector_background);

            // Header for Category
            LinearLayout headerLayout = new LinearLayout(getContext());
            headerLayout.setOrientation(LinearLayout.HORIZONTAL);
            headerLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            headerLayout.setPadding(16, 24, 16, 24);
            headerLayout.setGravity(android.view.Gravity.CENTER_VERTICAL);
            
            // Categoria Title (only first letter capitalized, keep proper acronyms)
            TextView tvCat = new TextView(getContext());
            String catDisplay = cat;
            if (catDisplay != null && catDisplay.length() > 0) {
                catDisplay = catDisplay.substring(0, 1).toUpperCase() + catDisplay.substring(1).toLowerCase();
                catDisplay = catDisplay.replace("(pos)", "(POS)");
            }
            tvCat.setText(catDisplay);
            tvCat.setTypeface(null, android.graphics.Typeface.BOLD);
            tvCat.setTextColor(android.graphics.Color.parseColor("#0f172a"));
            tvCat.setTextSize(14f);
            LinearLayout.LayoutParams tvCatParams = new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f
            );
            tvCat.setLayoutParams(tvCatParams);
            headerLayout.addView(tvCat);

            // Toggle Icon (Modern Chevron)
            android.widget.ImageView iconChevron = new android.widget.ImageView(getContext());
            iconChevron.setImageResource(R.drawable.ic_chevron_down);
            iconChevron.setColorFilter(android.graphics.Color.parseColor("#0f172a"));
            // Set size ofchevron to be neat
            int iconSize = (int) (20 * getResources().getDisplayMetrics().density);
            iconChevron.setLayoutParams(new LinearLayout.LayoutParams(iconSize, iconSize));
            headerLayout.addView(iconChevron);

            catContainer.addView(headerLayout);

            // Permisos List Container (Initially Hidden)
            LinearLayout listContainer = new LinearLayout(getContext());
            listContainer.setOrientation(LinearLayout.VERTICAL);
            listContainer.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            listContainer.setPadding(16, 0, 16, 16);
            listContainer.setVisibility(View.GONE);

            // Toggle logic using rotation
            headerLayout.setOnClickListener(v -> {
                if (listContainer.getVisibility() == View.VISIBLE) {
                    listContainer.setVisibility(View.GONE);
                    iconChevron.animate().rotation(0).setDuration(200).start();
                } else {
                    listContainer.setVisibility(View.VISIBLE);
                    iconChevron.animate().rotation(180).setDuration(200).start();
                }
            });

            // Permisos
            String[] perms = permisosPorCategoria.get(cat);
            if (perms != null) {
                for (int i = 0; i < perms.length; i++) {
                    String p = perms[i];
                    LinearLayout row = new LinearLayout(getContext());
                    row.setOrientation(LinearLayout.HORIZONTAL);
                    row.setLayoutParams(new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    ));
                    row.setPadding(0, 16, 0, 16);

                    // Permission Title (only first letter capitalized, keep proper acronyms)
                    TextView tvPermiso = new TextView(getContext());
                    String pDisplay = p;
                    if (pDisplay != null && pDisplay.length() > 0) {
                        pDisplay = pDisplay.substring(0, 1).toUpperCase() + pDisplay.substring(1).toLowerCase();
                        pDisplay = pDisplay.replace("pos", "POS");
                    }
                    tvPermiso.setText(pDisplay);
                    tvPermiso.setTextColor(android.graphics.Color.parseColor("#0f172a"));
                    tvPermiso.setTextSize(12f);
                    LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(
                            0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f
                    );
                    tvPermiso.setLayoutParams(tvParams);
                    row.addView(tvPermiso);

                    Switch sw = new Switch(getContext());
                    if (switchesMap == supervisorSwitches) {
                        int activeColor = android.graphics.Color.parseColor("#5981DF");
                        int inactiveColor = android.graphics.Color.parseColor("#94a3b8");
                        
                        int[][] states = new int[][] {
                            new int[] { android.R.attr.state_checked },
                            new int[] { -android.R.attr.state_checked }
                        };
                        
                        int[] thumbColors = new int[] {
                            activeColor,
                            inactiveColor
                        };
                        
                        int[] trackColors = new int[] {
                            android.graphics.Color.argb(76, android.graphics.Color.red(activeColor), android.graphics.Color.green(activeColor), android.graphics.Color.blue(activeColor)),
                            android.graphics.Color.argb(76, android.graphics.Color.red(inactiveColor), android.graphics.Color.green(inactiveColor), android.graphics.Color.blue(inactiveColor))
                        };
                        
                        sw.setThumbTintList(new android.content.res.ColorStateList(states, thumbColors));
                        sw.setTrackTintList(new android.content.res.ColorStateList(states, trackColors));
                    }
                    switchesMap.put(p, sw);
                    row.addView(sw);

                    listContainer.addView(row);
                    
                    // Add divider unless it's the last item
                    if (i < perms.length - 1) {
                        View divider = new View(getContext());
                        divider.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, 1
                        ));
                        divider.setBackgroundColor(android.graphics.Color.parseColor("#f1f5f9"));
                        listContainer.addView(divider);
                    }
                }
            }

            catContainer.addView(listContainer);
            
            // Add outer divider
            parent.addView(catContainer);
            View outerDivider = new View(getContext());
            outerDivider.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 2
            ));
            outerDivider.setBackgroundColor(android.graphics.Color.parseColor("#f1f5f9"));
            parent.addView(outerDivider);
        }
    }

    private void setSwitchSafe(Map<String, Switch> map, String key, boolean value) {
        Switch sw = map.get(key);
        if (sw != null) sw.setChecked(value);
    }
    
    private boolean getSwitchSafe(Map<String, Switch> map, String key) {
        Switch sw = map.get(key);
        return sw != null && sw.isChecked();
    }

    private void loadPermisos() {
        apiService.getPermisos().enqueue(new Callback<List<PermisosRoles>>() {
            @Override
            public void onResponse(Call<List<PermisosRoles>> call, Response<List<PermisosRoles>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (PermisosRoles pr : response.body()) {
                        Map<String, Switch> targetSwitches = null;
                        if ("SUPERVISOR".equalsIgnoreCase(pr.getRole())) {
                            targetSwitches = supervisorSwitches;
                        } else if ("VENDEDOR".equalsIgnoreCase(pr.getRole())) {
                            targetSwitches = vendedorSwitches;
                        }

                        if (targetSwitches != null) {
                            setSwitchSafe(targetSwitches, "Consultar Directorio Geográfico", pr.isSucursalesVer());
                            setSwitchSafe(targetSwitches, "Registrar Nuevas Sucursales", pr.isSucursalesCrear());
                            setSwitchSafe(targetSwitches, "Editar Datos de Sucursales", pr.isSucursalesEditar());
                            setSwitchSafe(targetSwitches, "Dar de Baja Sucursales", pr.isSucursalesEliminar());
                            
                            setSwitchSafe(targetSwitches, "Visualización Global de Productos", pr.isCatalogoVer());
                            setSwitchSafe(targetSwitches, "Añadir Nuevos Productos", pr.isCatalogoCrear());
                            setSwitchSafe(targetSwitches, "Modificar Precios y Artículos", pr.isCatalogoEditar());
                            setSwitchSafe(targetSwitches, "Eliminar Artículos del Catálogo", pr.isCatalogoEliminar());
                            
                            setSwitchSafe(targetSwitches, "Consultar Directorio de Proveedores", pr.isProveedoresVer());
                            setSwitchSafe(targetSwitches, "Registrar Nuevos Proveedores", pr.isProveedoresCrear());
                            setSwitchSafe(targetSwitches, "Editar Datos de Proveedores", pr.isProveedoresEditar());
                            setSwitchSafe(targetSwitches, "Dar de Baja Proveedores", pr.isProveedoresEliminar());
                            
                            setSwitchSafe(targetSwitches, "Inspeccionar Lotes de Compra", pr.isSourcingVer());
                            setSwitchSafe(targetSwitches, "Registrar Nuevos Ingresos", pr.isSourcingCrear());
                            setSwitchSafe(targetSwitches, "Editar Lotes Existentes", pr.isSourcingEditar());
                            setSwitchSafe(targetSwitches, "Anular Lotes Ingresados", pr.isSourcingEliminar());
                            
                            setSwitchSafe(targetSwitches, "Visualización de Auditorías", pr.isInventarioVer());
                            setSwitchSafe(targetSwitches, "Registrar Actas de Ajuste", pr.isInventarioCrear());
                            setSwitchSafe(targetSwitches, "Modificar Incidencias", pr.isInventarioEditar());
                            setSwitchSafe(targetSwitches, "Eliminar Historial de Pérdidas", pr.isInventarioEliminar());
                            
                            setSwitchSafe(targetSwitches, "Consultar Organigrama Interno", pr.isUsuariosVer());
                            setSwitchSafe(targetSwitches, "Contratar Personal", pr.isUsuariosCrear());
                            setSwitchSafe(targetSwitches, "Editar Datos de Personal", pr.isUsuariosEditar());
                            setSwitchSafe(targetSwitches, "Desvincular Personal", pr.isUsuariosEliminar());

                            setSwitchSafe(targetSwitches, "Acceder a la Terminal de POS / Ventas", pr.isVentasVer());
                            setSwitchSafe(targetSwitches, "Procesar y Emitir Ventas", pr.isVentasCrear());
                            setSwitchSafe(targetSwitches, "Modificar Registros de Ventas", pr.isVentasEditar());
                            setSwitchSafe(targetSwitches, "Anular / Eliminar Transacciones", pr.isVentasEliminar());
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
        PermisosRoles req = new PermisosRoles();
        req.setRole(rol);
        
        req.setSucursalesVer(getSwitchSafe(switchesMap, "Consultar Directorio Geográfico"));
        req.setSucursalesCrear(getSwitchSafe(switchesMap, "Registrar Nuevas Sucursales"));
        req.setSucursalesEditar(getSwitchSafe(switchesMap, "Editar Datos de Sucursales"));
        req.setSucursalesEliminar(getSwitchSafe(switchesMap, "Dar de Baja Sucursales"));
        
        req.setCatalogoVer(getSwitchSafe(switchesMap, "Visualización Global de Productos"));
        req.setCatalogoCrear(getSwitchSafe(switchesMap, "Añadir Nuevos Productos"));
        req.setCatalogoEditar(getSwitchSafe(switchesMap, "Modificar Precios y Artículos"));
        req.setCatalogoEliminar(getSwitchSafe(switchesMap, "Eliminar Artículos del Catálogo"));
        
        req.setProveedoresVer(getSwitchSafe(switchesMap, "Consultar Directorio de Proveedores"));
        req.setProveedoresCrear(getSwitchSafe(switchesMap, "Registrar Nuevos Proveedores"));
        req.setProveedoresEditar(getSwitchSafe(switchesMap, "Editar Datos de Proveedores"));
        req.setProveedoresEliminar(getSwitchSafe(switchesMap, "Dar de Baja Proveedores"));
        
        req.setSourcingVer(getSwitchSafe(switchesMap, "Inspeccionar Lotes de Compra"));
        req.setSourcingCrear(getSwitchSafe(switchesMap, "Registrar Nuevos Ingresos"));
        req.setSourcingEditar(getSwitchSafe(switchesMap, "Editar Lotes Existentes"));
        req.setSourcingEliminar(getSwitchSafe(switchesMap, "Anular Lotes Ingresados"));
        
        req.setInventarioVer(getSwitchSafe(switchesMap, "Visualización de Auditorías"));
        req.setInventarioCrear(getSwitchSafe(switchesMap, "Registrar Actas de Ajuste"));
        req.setInventarioEditar(getSwitchSafe(switchesMap, "Modificar Incidencias"));
        req.setInventarioEliminar(getSwitchSafe(switchesMap, "Eliminar Historial de Pérdidas"));
        
        req.setUsuariosVer(getSwitchSafe(switchesMap, "Consultar Organigrama Interno"));
        req.setUsuariosCrear(getSwitchSafe(switchesMap, "Contratar Personal"));
        req.setUsuariosEditar(getSwitchSafe(switchesMap, "Editar Datos de Personal"));
        req.setUsuariosEliminar(getSwitchSafe(switchesMap, "Desvincular Personal"));

        req.setVentasVer(getSwitchSafe(switchesMap, "Acceder a la Terminal de POS / Ventas"));
        req.setVentasCrear(getSwitchSafe(switchesMap, "Procesar y Emitir Ventas"));
        req.setVentasEditar(getSwitchSafe(switchesMap, "Modificar Registros de Ventas"));
        req.setVentasEliminar(getSwitchSafe(switchesMap, "Anular / Eliminar Transacciones"));

        apiService.updatePermisos(req).enqueue(new Callback<PermisosRoles>() {
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
