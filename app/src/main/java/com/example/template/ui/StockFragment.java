package com.example.template.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.template.R;
import com.example.template.network.ApiClient;
import com.example.template.network.ApiService;
import com.example.template.network.models.Stock;
import com.example.template.ui.adapters.StockAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StockFragment extends Fragment {

    private RecyclerView recyclerView;
    private StockAdapter adapter;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stock, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new StockAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        apiService = ApiClient.getClient(getContext()).create(ApiService.class);

        loadStock();

        return view;
    }

    private void loadStock() {
        apiService.getStock().enqueue(new Callback<List<Stock>>() {
            @Override
            public void onResponse(Call<List<Stock>> call, Response<List<Stock>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.updateData(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Stock>> call, Throwable t) {
                if(getContext() != null) Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
