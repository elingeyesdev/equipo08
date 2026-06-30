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
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import com.example.template.network.models.Sucursal;
import com.example.template.ui.adapters.SucursalAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SucursalesFragment extends Fragment {

    private FloatingActionButton btnToggleForm;
    private Button btnGuardar;
    private CardView cardForm;
    private EditText etName, etAddress;
    private AutoCompleteTextView etPhone;
    private Spinner spinnerStatus;
    private RecyclerView recyclerView;
    private SucursalAdapter adapter;
    private ApiService apiService;
    private boolean isFormVisible = false;
    private Sucursal editingSucursal = null;

    
    private LinearLayout llHorariosContainer;
    private Button btnAddHorario;
    private final String[] DAYS_OF_WEEK = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
    private final List<HorarioBlock> horarioBlocks = new ArrayList<>();

    private static class HorarioBlock {
        List<String> days = new ArrayList<>();
        String start = "08:00";
        String end = "18:00";
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sucursales, container, false);

        btnToggleForm = view.findViewById(R.id.btnToggleForm);
        btnGuardar = view.findViewById(R.id.btnGuardar);
        cardForm = view.findViewById(R.id.cardForm);
        etName = view.findViewById(R.id.etName);
        etAddress = view.findViewById(R.id.etAddress);
        etPhone = view.findViewById(R.id.etPhone);

        llHorariosContainer = view.findViewById(R.id.llHorariosContainer);
        btnAddHorario = view.findViewById(R.id.btnAddHorario);

        btnAddHorario.setOnClickListener(v -> addHorarioBlock(null));

        spinnerStatus = view.findViewById(R.id.spinnerStatus);
        recyclerView = view.findViewById(R.id.recyclerView);

        
        String[] statusOptions = new String[]{"Activa y Operando", "Cerrada / Inactiva"};
        if (getContext() != null) {
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, statusOptions
            );
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerStatus.setAdapter(spinnerAdapter);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SucursalAdapter(new ArrayList<>(), new SucursalAdapter.OnActionClickListener() {
            @Override
            public void onDeleteClick(Sucursal sucursal) {
                confirmDelete(sucursal);
            }

            @Override
            public void onEditClick(Sucursal sucursal) {
                editSucursal(sucursal);
            }
        });
        recyclerView.setAdapter(adapter);

        apiService = ApiClient.getClient(getContext()).create(ApiService.class);

        btnToggleForm.setOnClickListener(v -> toggleForm(false));
        btnGuardar.setOnClickListener(v -> saveSucursal());

        
        btnToggleForm.setVisibility(View.GONE);
        adapter.setCanManage(false);

        loadPermissions();
        loadSucursales();
        return view;
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
                            boolean canManage = pr.isSucursalesGestionar();
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

    private void toggleForm(boolean fromEdit) {
        if (!fromEdit) {
            editingSucursal = null;
            etName.setText(""); etAddress.setText(""); etPhone.setText("");
            horarioBlocks.clear();
            llHorariosContainer.removeAllViews();
            addHorarioBlock(null); 
            spinnerStatus.setSelection(0);
            btnGuardar.setText("Crear sucursal físicamente");
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

    private void addHorarioBlock(@Nullable HorarioBlock existingBlock) {
        if (getContext() == null) return;

        HorarioBlock block;
        if (existingBlock != null) {
            block = existingBlock;
        } else {
            block = new HorarioBlock();
            
            block.days.add("Lunes");
            block.days.add("Martes");
            block.days.add("Miércoles");
            block.days.add("Jueves");
            block.days.add("Viernes");
        }
        horarioBlocks.add(block);

        View blockView = LayoutInflater.from(getContext()).inflate(R.layout.item_horario_block, llHorariosContainer, false);
        TextView tvSelectedDaysLabel = blockView.findViewById(R.id.tvSelectedDaysLabel);
        Button btnSelectDays = blockView.findViewById(R.id.btnSelectDays);
        Button btnStartTime = blockView.findViewById(R.id.btnStartTime);
        Button btnEndTime = blockView.findViewById(R.id.btnEndTime);
        ImageButton btnDeleteBlock = blockView.findViewById(R.id.btnDeleteBlock);

        
        updateDaysLabel(block, tvSelectedDaysLabel);
        btnStartTime.setText(block.start);
        btnEndTime.setText(block.end);

        
        btnSelectDays.setOnClickListener(v -> showDaysPickerDialog(block, tvSelectedDaysLabel));
        btnStartTime.setOnClickListener(v -> showTimePickerDialog(block, btnStartTime, true));
        btnEndTime.setOnClickListener(v -> showTimePickerDialog(block, btnEndTime, false));

        btnDeleteBlock.setOnClickListener(v -> {
            horarioBlocks.remove(block);
            llHorariosContainer.removeView(blockView);
        });

        llHorariosContainer.addView(blockView);
    }

    private void showDaysPickerDialog(HorarioBlock block, TextView tvLabel) {
        if (getContext() == null) return;
        boolean[] checkedItems = new boolean[DAYS_OF_WEEK.length];
        for (int i = 0; i < DAYS_OF_WEEK.length; i++) {
            checkedItems[i] = block.days.contains(DAYS_OF_WEEK[i]);
        }

        new androidx.appcompat.app.AlertDialog.Builder(getContext())
            .setTitle("Seleccionar Días")
            .setMultiChoiceItems(DAYS_OF_WEEK, checkedItems, (dialog, which, isChecked) -> {
                if (isChecked) {
                    if (!block.days.contains(DAYS_OF_WEEK[which])) {
                        block.days.add(DAYS_OF_WEEK[which]);
                    }
                } else {
                    block.days.remove(DAYS_OF_WEEK[which]);
                }
            })
            .setPositiveButton("Aceptar", (dialog, which) -> updateDaysLabel(block, tvLabel))
            .setNegativeButton("Cancelar", null)
            .show();
    }

    private void updateDaysLabel(HorarioBlock block, TextView tvLabel) {
        List<String> sortedSelected = new ArrayList<>();
        for (String day : DAYS_OF_WEEK) {
            if (block.days.contains(day)) {
                sortedSelected.add(day);
            }
        }
        block.days.clear();
        block.days.addAll(sortedSelected);

        if (block.days.isEmpty()) {
            tvLabel.setText("Días: Ninguno seleccionado");
        } else if (block.days.size() == 7) {
            tvLabel.setText("Días: Todos los días");
        } else {
            StringBuilder sb = new StringBuilder("Días: ");
            for (int i = 0; i < block.days.size(); i++) {
                sb.append(block.days.get(i));
                if (i < block.days.size() - 1) sb.append(", ");
            }
            tvLabel.setText(sb.toString());
        }
    }

    private void showTimePickerDialog(HorarioBlock block, Button btn, boolean isStart) {
        if (getContext() == null) return;
        String currentTime = isStart ? block.start : block.end;
        int hour = 8;
        int minute = 0;
        try {
            String[] parts = currentTime.split(":");
            hour = Integer.parseInt(parts[0]);
            minute = Integer.parseInt(parts[1]);
        } catch (Exception e) {}

        new android.app.TimePickerDialog(getContext(), (view, hourOfDay, min) -> {
            String formattedTime = String.format(java.util.Locale.US, "%02d:%02d", hourOfDay, min);
            if (isStart) {
                block.start = formattedTime;
            } else {
                block.end = formattedTime;
            }
            btn.setText(formattedTime);
        }, hour, minute, true).show();
    }

    private void editSucursal(Sucursal sucursal) {
        editingSucursal = sucursal;
        etName.setText(sucursal.getName());
        etAddress.setText(sucursal.getAddress() != null ? sucursal.getAddress() : "");
        etPhone.setText(sucursal.getPhone() != null ? sucursal.getPhone() : "");
        
        horarioBlocks.clear();
        llHorariosContainer.removeAllViews();

        String json = sucursal.getHorarios();
        if (json != null && !json.isEmpty()) {
            try {
                com.google.gson.JsonArray array = new com.google.gson.JsonParser().parse(json).getAsJsonArray();
                for (int i = 0; i < array.size(); i++) {
                    com.google.gson.JsonObject obj = array.get(i).getAsJsonObject();
                    HorarioBlock block = new HorarioBlock();
                    com.google.gson.JsonArray days = obj.getAsJsonArray("days");
                    for (int d = 0; d < days.size(); d++) {
                        block.days.add(days.get(d).getAsString());
                    }
                    block.start = obj.get("start").getAsString();
                    block.end = obj.get("end").getAsString();
                    addHorarioBlock(block);
                }
            } catch (Exception e) {
                addHorarioBlock(null);
            }
        } else {
            addHorarioBlock(null);
        }

        spinnerStatus.setSelection(sucursal.isActive() ? 0 : 1);
        btnGuardar.setText("Actualizar sucursal");
        
        if (!isFormVisible) {
            toggleForm(true);
        }
    }

    private void loadSucursales() {
        apiService.getSucursales().enqueue(new Callback<List<Sucursal>>() {
            @Override
            public void onResponse(Call<List<Sucursal>> call, Response<List<Sucursal>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Sucursal> list = response.body();
                    adapter.updateData(list);
                    
                    
                    List<String> phones = new ArrayList<>();
                    for (Sucursal s : list) {
                        if (s.getPhone() != null && !s.getPhone().isEmpty() && !phones.contains(s.getPhone())) {
                            phones.add(s.getPhone());
                        }
                    }
                    if (getContext() != null) {
                        ArrayAdapter<String> phoneAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, phones);
                        etPhone.setAdapter(phoneAdapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Sucursal>> call, Throwable t) {
                if(getContext() != null) Toast.makeText(getContext(), "Error al cargar sucursales", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveSucursal() {
        String name = etName.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        boolean isActive = spinnerStatus.getSelectedItemPosition() == 0; 

        if (name.isEmpty()) {
            Toast.makeText(getContext(), "El campo Nombre es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!phone.isEmpty() && !phone.matches("^[0-9]{8}$")) {
            Toast.makeText(getContext(), "El campo Teléfono debe tener exactamente 8 dígitos", Toast.LENGTH_SHORT).show();
            return;
        }

        String horariosJson = null;
        if (!horarioBlocks.isEmpty()) {
            com.google.gson.JsonArray scheduleArray = new com.google.gson.JsonArray();
            for (HorarioBlock block : horarioBlocks) {
                if (!block.days.isEmpty()) {
                    com.google.gson.JsonArray daysArray = new com.google.gson.JsonArray();
                    for (String day : block.days) {
                        daysArray.add(day);
                    }
                    com.google.gson.JsonObject blockObj = new com.google.gson.JsonObject();
                    blockObj.add("days", daysArray);
                    blockObj.addProperty("start", block.start);
                    blockObj.addProperty("end", block.end);
                    scheduleArray.add(blockObj);
                }
            }
            horariosJson = scheduleArray.toString();
        }

        Sucursal request = new Sucursal(
                name,
                address.isEmpty() ? null : address,
                phone.isEmpty() ? null : phone,
                isActive,
                horariosJson
        );

        if (editingSucursal != null) {
            apiService.updateSucursal(editingSucursal.getId(), request).enqueue(new Callback<Sucursal>() {
                @Override
                public void onResponse(Call<Sucursal> call, Response<Sucursal> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Sucursal actualizada", Toast.LENGTH_SHORT).show();
                        toggleForm(false);
                        loadSucursales(); 
                    } else {
                        Toast.makeText(getContext(), "Error al actualizar", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<Sucursal> call, Throwable t) {
                    Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            apiService.createSucursal(request).enqueue(new Callback<Sucursal>() {
                @Override
                public void onResponse(Call<Sucursal> call, Response<Sucursal> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Sucursal creada", Toast.LENGTH_SHORT).show();
                        toggleForm(false);
                        loadSucursales(); 
                    } else {
                        Toast.makeText(getContext(), "Error al crear", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<Sucursal> call, Throwable t) {
                    Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void confirmDelete(Sucursal sucursal) {
        com.example.template.utils.DialogHelper.showConfirmDialog(
            getContext(),
            "Eliminar Sucursal",
            "¿Estás seguro de que deseas eliminar la sucursal \"" + sucursal.getName() + "\"?\n\nEsta acción no se puede deshacer.",
            "Eliminar",
            () -> deleteSucursal(sucursal)
        );
    }

    private void deleteSucursal(Sucursal sucursal) {
        apiService.deleteSucursal(sucursal.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Sucursal eliminada", Toast.LENGTH_SHORT).show();
                    loadSucursales();
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
