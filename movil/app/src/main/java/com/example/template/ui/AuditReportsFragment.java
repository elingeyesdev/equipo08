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
}
