package com.example.template.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.template.R;
import com.example.template.network.ApiClient;
import com.example.template.network.ApiService;
import com.example.template.network.models.Ajuste;
import com.example.template.ui.adapters.AuditAdapter;

import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Button;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.widget.Spinner;
import android.app.DatePickerDialog;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.widget.EditText;
import android.widget.LinearLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.graphics.Color;

import com.example.template.network.models.Sucursal;
import com.example.template.network.models.Stock;
import com.example.template.network.models.AjusteRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuditReportsFragment extends Fragment {

    private RecyclerView recyclerView;
    private AuditAdapter adapter;
    private ApiService apiService;
    private TextView tvTotalLoss, tvTotalIncidents;
    
    private List<Ajuste> allAjustes = new ArrayList<>();
    private Button btnToggleFilter, btnDateFrom, btnDateTo, btnClearFilters;
    private androidx.cardview.widget.CardView cardFilters;
    private Spinner spinnerOperator, spinnerCategory, spinnerRole;
    private boolean isFilterVisible = false;
    private Calendar calFrom = null;
    private Calendar calTo = null;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

    // Audit Form Variables
    private FloatingActionButton btnToggleAuditForm;
    private Button btnSubmitAudit;
    private androidx.cardview.widget.CardView cardAuditForm;
    private Spinner spinnerSucursalAudit, spinnerProductoAudit, spinnerMotivoAudit;
    private EditText etUnidadesPerdidas, etObservacionesAudit;
    private LinearLayout llWarningAudit;
    private TextView tvWarningAudit;
    private boolean isAuditFormVisible = false;

    private List<Sucursal> sucursalesList = new ArrayList<>();
    private List<Stock> stockList = new ArrayList<>();
    private List<Stock> filteredStockList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_audit_reports, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        tvTotalLoss = view.findViewById(R.id.tvTotalLoss);
        tvTotalIncidents = view.findViewById(R.id.tvTotalIncidents);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AuditAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        apiService = ApiClient.getClient(getContext()).create(ApiService.class);

        btnToggleFilter = view.findViewById(R.id.btnToggleFilter);
        cardFilters = view.findViewById(R.id.cardFilters);
        btnDateFrom = view.findViewById(R.id.btnDateFrom);
        btnDateTo = view.findViewById(R.id.btnDateTo);
        btnClearFilters = view.findViewById(R.id.btnClearFilters);
        spinnerOperator = view.findViewById(R.id.spinnerOperator);
        spinnerRole = view.findViewById(R.id.spinnerRole);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);

        btnToggleFilter.setOnClickListener(v -> {
            isFilterVisible = !isFilterVisible;
            if (isFilterVisible) {
                cardFilters.setVisibility(View.VISIBLE);
                btnToggleFilter.setText("Ocultar filtros");
            } else {
                cardFilters.setVisibility(View.GONE);
                btnToggleFilter.setText("Filtrar reporte");
            }
        });

        // Bind Audit Form
        btnToggleAuditForm = view.findViewById(R.id.btnToggleAuditForm);
        cardAuditForm = view.findViewById(R.id.cardAuditForm);
        spinnerSucursalAudit = view.findViewById(R.id.spinnerSucursalAudit);
        spinnerProductoAudit = view.findViewById(R.id.spinnerProductoAudit);
        spinnerMotivoAudit = view.findViewById(R.id.spinnerMotivoAudit);
        etUnidadesPerdidas = view.findViewById(R.id.etUnidadesPerdidas);
        etObservacionesAudit = view.findViewById(R.id.etObservacionesAudit);
        llWarningAudit = view.findViewById(R.id.llWarningAudit);
        tvWarningAudit = view.findViewById(R.id.tvWarningAudit);
        btnSubmitAudit = view.findViewById(R.id.btnSubmitAudit);

        btnToggleAuditForm.setOnClickListener(v -> {
            isAuditFormVisible = !isAuditFormVisible;
            if (isAuditFormVisible) {
                cardAuditForm.setVisibility(View.VISIBLE);
                btnToggleAuditForm.setImageResource(R.drawable.ic_close);
            } else {
                cardAuditForm.setVisibility(View.GONE);
                btnToggleAuditForm.setImageResource(R.drawable.ic_add);
            }
        });

        String[] motivos = {"ERROR_REGISTRO", "DANO_MERMA", "ROBO_O_PERDIDA", "CADUCIDAD"};
        String[] motivosLabels = {"Error de registro", "Dañado / defectuoso", "Pérdida / robo", "Producto vencido"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, motivosLabels);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMotivoAudit.setAdapter(spinnerAdapter);

        setupAuditFormLogic(motivos);

        btnDateFrom.setOnClickListener(v -> showDatePicker(true));
        btnDateTo.setOnClickListener(v -> showDatePicker(false));

        btnClearFilters.setOnClickListener(v -> {
            calFrom = null;
            calTo = null;
            btnDateFrom.setText("dd/mm/yyyy");
            btnDateTo.setText("dd/mm/yyyy");
            spinnerOperator.setSelection(0);
            spinnerRole.setSelection(0);
            spinnerCategory.setSelection(0);
            applyFilters();
        });

        AdapterView.OnItemSelectedListener filterListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };
        spinnerOperator.setOnItemSelectedListener(filterListener);
        spinnerRole.setOnItemSelectedListener(filterListener);
        spinnerCategory.setOnItemSelectedListener(filterListener);

        loadAudits();
        loadSucursales();
        loadStock();

        return view;
    }

    private void loadSucursales() {
        apiService.getSucursales().enqueue(new Callback<List<Sucursal>>() {
            @Override
            public void onResponse(Call<List<Sucursal>> call, Response<List<Sucursal>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    sucursalesList = response.body();
                    List<String> branchNames = new ArrayList<>();
                    for (Sucursal s : sucursalesList) {
                        branchNames.add(s.getName());
                    }
                    if (getContext() != null) {
                        ArrayAdapter<String> branchAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, branchNames);
                        branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerSucursalAudit.setAdapter(branchAdapter);
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Sucursal>> call, Throwable t) {}
        });
    }

    private void loadStock() {
        apiService.getStock().enqueue(new Callback<List<Stock>>() {
            @Override
            public void onResponse(Call<List<Stock>> call, Response<List<Stock>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    stockList = response.body();
                    updateProductSpinner();
                }
            }
            @Override
            public void onFailure(Call<List<Stock>> call, Throwable t) {}
        });
    }

    private void updateProductSpinner() {
        if (getContext() == null || sucursalesList.isEmpty() || spinnerSucursalAudit.getSelectedItemPosition() < 0) return;
        
        Sucursal selectedSucursal = sucursalesList.get(spinnerSucursalAudit.getSelectedItemPosition());
        filteredStockList.clear();
        List<String> productNames = new ArrayList<>();
        
        for (Stock s : stockList) {
            if (s.getSucursalId() != null && s.getSucursalId().equals(selectedSucursal.getId()) && s.getProducto() != null) {
                filteredStockList.add(s);
                String sku = s.getProducto().getSku() != null ? s.getProducto().getSku() : "N/A";
                productNames.add(s.getProducto().getName() + " (" + sku + ")");
            }
        }
        
        ArrayAdapter<String> productAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, productNames);
        productAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProductoAudit.setAdapter(productAdapter);
        validateAuditForm();
    }

    private void setupAuditFormLogic(String[] motivos) {
        spinnerSucursalAudit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateProductSpinner();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerProductoAudit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                validateAuditForm();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        etUnidadesPerdidas.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { validateAuditForm(); }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnSubmitAudit.setOnClickListener(v -> {
            if (filteredStockList.isEmpty() || spinnerProductoAudit.getSelectedItemPosition() < 0) return;
            Stock selectedStock = filteredStockList.get(spinnerProductoAudit.getSelectedItemPosition());
            
            String cantStr = etUnidadesPerdidas.getText().toString().trim();
            int perdidas = cantStr.isEmpty() ? 0 : Integer.parseInt(cantStr);
            int fisica = selectedStock.getCantidadTotal() - perdidas;
            
            String motivo = motivos[spinnerMotivoAudit.getSelectedItemPosition()];
            String obs = etObservacionesAudit.getText().toString().trim();
            
            if (("DANO_MERMA".equals(motivo) || "ROBO_O_PERDIDA".equals(motivo) || "CADUCIDAD".equals(motivo)) && obs.isEmpty()) {
                Toast.makeText(getContext(), "Observaciones requeridas para este motivo", Toast.LENGTH_SHORT).show();
                return;
            }
            
            AjusteRequest req = new AjusteRequest(
                selectedStock.getSucursalId(),
                selectedStock.getProductoId(),
                selectedStock.getCantidadTotal(),
                fisica,
                motivo,
                obs.isEmpty() ? null : obs
            );

            btnSubmitAudit.setEnabled(false);
            apiService.createAjuste(req).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    btnSubmitAudit.setEnabled(true);
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Auditoría registrada", Toast.LENGTH_SHORT).show();
                        etUnidadesPerdidas.setText("");
                        etObservacionesAudit.setText("");
                        cardAuditForm.setVisibility(View.GONE);
                        isAuditFormVisible = false;
                        btnToggleAuditForm.setImageResource(R.drawable.ic_add);
                        loadAudits();
                        loadStock();
                    } else {
                        Toast.makeText(getContext(), "Error al registrar", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    btnSubmitAudit.setEnabled(true);
                    Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void validateAuditForm() {
        String cantStr = etUnidadesPerdidas.getText().toString().trim();
        if (cantStr.isEmpty() || filteredStockList.isEmpty() || spinnerProductoAudit.getSelectedItemPosition() < 0) {
            btnSubmitAudit.setEnabled(false);
            btnSubmitAudit.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#0d9488")));
            llWarningAudit.setVisibility(View.GONE);
            return;
        }
        
        Stock selectedStock = filteredStockList.get(spinnerProductoAudit.getSelectedItemPosition());
        int perdidas = Integer.parseInt(cantStr);
        int sistema = selectedStock.getCantidadTotal();
        
        if (perdidas > sistema) {
            llWarningAudit.setVisibility(View.VISIBLE);
            tvWarningAudit.setText("No puedes perder más de las unidades disponibles en sistema (" + sistema + ")");
            btnSubmitAudit.setEnabled(false);
            btnSubmitAudit.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#0d9488")));
        } else {
            llWarningAudit.setVisibility(View.GONE);
            btnSubmitAudit.setEnabled(true);
            btnSubmitAudit.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#0f172a")));
        }
    }

    private void loadAudits() {
        apiService.getAjustes().enqueue(new Callback<List<Ajuste>>() {
            @Override
            public void onResponse(Call<List<Ajuste>> call, Response<List<Ajuste>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Ajuste> list = response.body();
                    
                    // reverse to show newest first
                    Collections.reverse(list);
                    
                    allAjustes = new ArrayList<>(list);
                    populateSpinners();
                    
                    adapter.updateData(list);
                    calculateKPIs(list);
                }
            }

            @Override
            public void onFailure(Call<List<Ajuste>> call, Throwable t) {
                if(getContext() != null) Toast.makeText(getContext(), "Error cargando auditorías", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculateKPIs(List<Ajuste> list) {
        double totalLoss = 0;
        for (Ajuste a : list) {
            if (a.getValorPerdido() != null && !a.getValorPerdido().isEmpty()) {
                try {
                    totalLoss += Double.parseDouble(a.getValorPerdido());
                } catch (Exception ignored) {}
            }
        }
        
        if (totalLoss > 0) {
            tvTotalLoss.setText(String.format("- Bs. %.2f", totalLoss));
        } else {
            tvTotalLoss.setText(String.format("Bs. %.2f", totalLoss));
        }
        tvTotalIncidents.setText(list.size() + " Registros");
    }

    private void showDatePicker(boolean isFrom) {
        Calendar c = Calendar.getInstance();
        if (isFrom && calFrom != null) c = calFrom;
        else if (!isFrom && calTo != null) c = calTo;
        
        DatePickerDialog dpd = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            Calendar selected = Calendar.getInstance();
            selected.set(year, month, dayOfMonth, 0, 0, 0);
            if (isFrom) {
                calFrom = selected;
                btnDateFrom.setText(dateFormat.format(calFrom.getTime()));
            } else {
                calTo = selected;
                calTo.set(Calendar.HOUR_OF_DAY, 23);
                calTo.set(Calendar.MINUTE, 59);
                calTo.set(Calendar.SECOND, 59);
                btnDateTo.setText(dateFormat.format(calTo.getTime()));
            }
            applyFilters();
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dpd.show();
    }

    private void populateSpinners() {
        if (getContext() == null) return;
        List<String> operators = new ArrayList<>();
        operators.add("Cualquier operador");
        List<String> roles = new ArrayList<>();
        roles.add("Cualquier rol");
        List<String> motives = new ArrayList<>();
        motives.add("Cualquier motivo");
        motives.add("Error de registro");
        motives.add("Artículo dañado / extraviado");
        motives.add("Robo / no habido");
        motives.add("Vencido");

        for (Ajuste a : allAjustes) {
            String op = a.getUsuario() != null ? a.getUsuario().getNombreCompleto() : "Operador";
            if (!operators.contains(op)) operators.add(op);
            
            String rol = a.getUsuario() != null ? a.getUsuario().getRol() : null;
            if (rol != null && !roles.contains(rol)) roles.add(rol);
        }

        ArrayAdapter<String> opAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, operators);
        opAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOperator.setAdapter(opAdapter);

        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, roles);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(roleAdapter);

        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, motives);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(catAdapter);
    }

    private void applyFilters() {
        if (allAjustes == null || allAjustes.isEmpty()) return;
        
        List<Ajuste> filtered = new ArrayList<>();
        String selOp = spinnerOperator.getSelectedItem() != null ? spinnerOperator.getSelectedItem().toString() : "Cualquier operador";
        String selMot = spinnerCategory.getSelectedItem() != null ? spinnerCategory.getSelectedItem().toString() : "Cualquier motivo";

        SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        for (Ajuste a : allAjustes) {
            boolean matches = true;

            if (calFrom != null || calTo != null) {
                if (a.getFecha() != null) {
                    try {
                        Date d = utcFormat.parse(a.getFecha());
                        if (calFrom != null && d.before(calFrom.getTime())) matches = false;
                        if (calTo != null && d.after(calTo.getTime())) matches = false;
                    } catch (Exception e) {}
                }
            }

            if (!"Cualquier operador".equals(selOp)) {
                String op = a.getUsuario() != null ? a.getUsuario().getNombreCompleto() : "Operador";
                if (!selOp.equals(op)) matches = false;
            }
            
            String selRol = spinnerRole.getSelectedItem() != null ? spinnerRole.getSelectedItem().toString() : "Cualquier rol";
            if (!"Cualquier rol".equals(selRol)) {
                String rol = a.getUsuario() != null ? a.getUsuario().getRol() : null;
                if (rol == null || !selRol.equals(rol)) matches = false;
            }

            if (!"Cualquier motivo".equals(selMot)) {
                String m = a.getMotivo();
                String desc = m != null ? m : "";
                if ("ERROR_REGISTRO".equals(m)) desc = "Error de registro";
                else if ("DANO_MERMA".equals(m)) desc = "Artículo dañado / extraviado";
                else if ("ROBO_O_PERDIDA".equals(m)) desc = "Robo / no habido";
                else if ("CADUCIDAD".equals(m)) desc = "Vencido";
                
                if (!selMot.equals(desc)) matches = false;
            }

            if (matches) {
                filtered.add(a);
            }
        }
        
        adapter.updateData(filtered);
        calculateKPIs(filtered);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            getActivity().getWindow().setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }
}
