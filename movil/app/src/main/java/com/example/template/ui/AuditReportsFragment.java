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
    private Spinner spinnerOperator, spinnerCategory;
    private boolean isFilterVisible = false;
    private Calendar calFrom = null;
    private Calendar calTo = null;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

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
        spinnerCategory = view.findViewById(R.id.spinnerCategory);

        btnToggleFilter.setOnClickListener(v -> {
            isFilterVisible = !isFilterVisible;
            if (isFilterVisible) {
                cardFilters.setVisibility(View.VISIBLE);
                btnToggleFilter.setText("Ocultar Filtros");
            } else {
                cardFilters.setVisibility(View.GONE);
                btnToggleFilter.setText("Filtrar Reporte");
            }
        });

        btnDateFrom.setOnClickListener(v -> showDatePicker(true));
        btnDateTo.setOnClickListener(v -> showDatePicker(false));

        btnClearFilters.setOnClickListener(v -> {
            calFrom = null;
            calTo = null;
            btnDateFrom.setText("dd/mm/yyyy");
            btnDateTo.setText("dd/mm/yyyy");
            spinnerOperator.setSelection(0);
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
        spinnerCategory.setOnItemSelectedListener(filterListener);

        loadAudits();

        return view;
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
        
        tvTotalLoss.setText(String.format("Bs. %.2f", totalLoss));
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
        operators.add("Cualquier Operador");
        List<String> motives = new ArrayList<>();
        motives.add("Cualquier Motivo");
        motives.add("Error de Registro");
        motives.add("Artículo Dañado / Extraviado");
        motives.add("Robo / No Habido");
        motives.add("Vencido");

        for (Ajuste a : allAjustes) {
            String op = a.getUsuario() != null ? a.getUsuario().getNombreCompleto() : "Operador";
            if (!operators.contains(op)) operators.add(op);
        }

        ArrayAdapter<String> opAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, operators);
        opAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOperator.setAdapter(opAdapter);

        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, motives);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(catAdapter);
    }

    private void applyFilters() {
        if (allAjustes == null || allAjustes.isEmpty()) return;
        
        List<Ajuste> filtered = new ArrayList<>();
        String selOp = spinnerOperator.getSelectedItem() != null ? spinnerOperator.getSelectedItem().toString() : "Cualquier Operador";
        String selMot = spinnerCategory.getSelectedItem() != null ? spinnerCategory.getSelectedItem().toString() : "Cualquier Motivo";

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

            if (!"Cualquier Operador".equals(selOp)) {
                String op = a.getUsuario() != null ? a.getUsuario().getNombreCompleto() : "Operador";
                if (!selOp.equals(op)) matches = false;
            }

            if (!"Cualquier Motivo".equals(selMot)) {
                String m = a.getMotivo();
                String desc = m != null ? m : "";
                if ("ERROR_REGISTRO".equals(m)) desc = "Error de Registro";
                else if ("DANO_MERMA".equals(m)) desc = "Artículo Dañado / Extraviado";
                else if ("ROBO_O_PERDIDA".equals(m)) desc = "Robo / No Habido";
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
}
