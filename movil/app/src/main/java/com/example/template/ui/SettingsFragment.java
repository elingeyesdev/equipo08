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
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.template.MainActivity;
import com.example.template.R;
import com.example.template.network.ApiClient;
import com.example.template.network.ApiService;
import com.example.template.network.models.TenantProfile;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsFragment extends Fragment {

    private TextInputEditText etStoreName, etContactPhone, etBrandColor;
    private View viewColorPreview;
    private Button btnSave;
    private ProgressBar pbLoading;
    private ApiService apiService;

    private android.widget.ImageView ivLogoPreview;
    private Button btnSelectLogo;
    private android.widget.TextView tvLogoStatus, tvRemoveLogo;
    private String uploadedLogoUrl = null;
    private static final int PICK_IMAGE_REQUEST = 1002;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        initViews(view);
        setupColorPreviewListener();

        apiService = ApiClient.getClient(getContext()).create(ApiService.class);

        // Cargar los ajustes actuales
        loadTenantProfile();

        btnSave.setOnClickListener(v -> saveTenantProfile());

        btnSelectLogo.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        tvRemoveLogo.setOnClickListener(v -> {
            uploadedLogoUrl = null;
            ivLogoPreview.setVisibility(View.GONE);
            tvLogoStatus.setText("No se ha seleccionado logo");
            tvRemoveLogo.setVisibility(View.GONE);
        });

        return view;
    }

    private void initViews(View view) {
        etStoreName = view.findViewById(R.id.etSettingsStoreName);
        etContactPhone = view.findViewById(R.id.etSettingsContactPhone);
        etBrandColor = view.findViewById(R.id.etSettingsBrandColor);
        viewColorPreview = view.findViewById(R.id.viewSettingsColorPreview);
        btnSave = view.findViewById(R.id.btnSettingsSave);
        pbLoading = view.findViewById(R.id.pbSettingsLoading);
        ivLogoPreview = view.findViewById(R.id.ivSettingsLogoPreview);
        btnSelectLogo = view.findViewById(R.id.btnSelectSettingsLogo);
        tvLogoStatus = view.findViewById(R.id.tvSettingsLogoStatus);
        tvRemoveLogo = view.findViewById(R.id.tvRemoveSettingsLogo);
    }

    private void setupColorPreviewListener() {
        etBrandColor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String colorHex = s.toString().trim();
                if (colorHex.matches("^#[0-9a-fA-F]{6}$")) {
                    try {
                        int color = Color.parseColor(colorHex);
                        viewColorPreview.setBackgroundTintList(ColorStateList.valueOf(color));
                    } catch (IllegalArgumentException e) {
                        // Color inválido
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadTenantProfile() {
        pbLoading.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        apiService.getTenantProfile().enqueue(new Callback<TenantProfile>() {
            @Override
            public void onResponse(Call<TenantProfile> call, Response<TenantProfile> response) {
                if (isAdded()) {
                    pbLoading.setVisibility(View.GONE);
                    btnSave.setEnabled(true);

                    if (response.isSuccessful() && response.body() != null) {
                        TenantProfile profile = response.body();
                        etStoreName.setText(profile.getName());
                        etContactPhone.setText(profile.getPhone());
                        
                        uploadedLogoUrl = profile.getLogoUrl();
                        if (uploadedLogoUrl != null && !uploadedLogoUrl.trim().isEmpty()) {
                            ivLogoPreview.setVisibility(View.VISIBLE);
                            com.example.template.utils.ImageLoader.loadImage(uploadedLogoUrl, ivLogoPreview);
                            tvLogoStatus.setText("Logo cargado");
                            tvRemoveLogo.setVisibility(View.VISIBLE);
                        } else {
                            ivLogoPreview.setVisibility(View.GONE);
                            tvLogoStatus.setText("No se ha seleccionado logo");
                            tvRemoveLogo.setVisibility(View.GONE);
                        }

                        String brandColor = profile.getBrandColor();
                        if (brandColor == null || brandColor.trim().isEmpty()) {
                            brandColor = "#0d9488"; // Default color
                        }
                        etBrandColor.setText(brandColor);

                        try {
                            viewColorPreview.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(brandColor)));
                        } catch (Exception e) {
                            viewColorPreview.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0d9488")));
                        }
                    } else {
                        Toast.makeText(getContext(), "Error al cargar los ajustes", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<TenantProfile> call, Throwable t) {
                if (isAdded()) {
                    pbLoading.setVisibility(View.GONE);
                    btnSave.setEnabled(true);
                    Toast.makeText(getContext(), "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveTenantProfile() {
        String name = etStoreName.getText().toString().trim();
        String phone = etContactPhone.getText().toString().trim();
        String brandColor = etBrandColor.getText().toString().trim();

        if (name.isEmpty()) {
            etStoreName.setError("El nombre es requerido");
            return;
        }

        if (!brandColor.matches("^#[0-9a-fA-F]{6}$")) {
            etBrandColor.setError("Color HEX inválido (ej: #0d9488)");
            return;
        }

        pbLoading.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        TenantProfile updatedProfile = new TenantProfile(name, phone, uploadedLogoUrl, brandColor);

        apiService.updateTenantProfile(updatedProfile).enqueue(new Callback<TenantProfile>() {
            @Override
            public void onResponse(Call<TenantProfile> call, Response<TenantProfile> response) {
                if (isAdded()) {
                    pbLoading.setVisibility(View.GONE);
                    btnSave.setEnabled(true);

                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(getContext(), "Ajustes guardados correctamente", Toast.LENGTH_SHORT).show();
                        
                        // Actualizar caché de sesión y título del toolbar en tiempo real
                        com.example.template.utils.SessionManager session = new com.example.template.utils.SessionManager(getContext());
                        session.updateTenantName(name);
                        session.updateLogoUrl(uploadedLogoUrl);
                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).updateNavHeaderLogo(uploadedLogoUrl);
                        }

                        // Recargar para confirmar persistencia
                        loadTenantProfile();
                    } else {
                        Toast.makeText(getContext(), "Error al guardar los ajustes", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<TenantProfile> call, Throwable t) {
                if (isAdded()) {
                    pbLoading.setVisibility(View.GONE);
                    btnSave.setEnabled(true);
                    Toast.makeText(getContext(), "Fallo de conexión al guardar: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == android.app.Activity.RESULT_OK && data != null && data.getData() != null) {
            android.net.Uri imageUri = data.getData();
            uploadLogo(imageUri);
        }
    }

    private void uploadLogo(android.net.Uri imageUri) {
        if (getContext() == null) return;
        tvLogoStatus.setText("Subiendo logo...");
        btnSelectLogo.setEnabled(false);

        okhttp3.MultipartBody.Part body = com.example.template.utils.FileUtils.getMultipartBody(getContext(), imageUri, "file");
        if (body == null) {
            tvLogoStatus.setText("Error al procesar archivo");
            btnSelectLogo.setEnabled(true);
            return;
        }

        apiService.uploadFile(body).enqueue(new Callback<com.example.template.network.models.UploadResponse>() {
            @Override
            public void onResponse(Call<com.example.template.network.models.UploadResponse> call, Response<com.example.template.network.models.UploadResponse> response) {
                btnSelectLogo.setEnabled(true);
                if (isAdded()) {
                    if (response.isSuccessful() && response.body() != null) {
                        uploadedLogoUrl = response.body().getUrl();
                        tvLogoStatus.setText("Logo subido con éxito");
                        tvRemoveLogo.setVisibility(View.VISIBLE);
                        ivLogoPreview.setVisibility(View.VISIBLE);
                        com.example.template.utils.ImageLoader.loadImage(uploadedLogoUrl, ivLogoPreview);
                        
                        // Opcionalmente actualizar la UI
                        com.example.template.utils.SessionManager session = new com.example.template.utils.SessionManager(getContext());
                        session.updateLogoUrl(uploadedLogoUrl);
                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).updateNavHeaderLogo(uploadedLogoUrl);
                        }
                    } else {
                        tvLogoStatus.setText("Error al subir logo");
                        Toast.makeText(getContext(), "Error del servidor al cargar archivo", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<com.example.template.network.models.UploadResponse> call, Throwable t) {
                btnSelectLogo.setEnabled(true);
                if (isAdded()) {
                    tvLogoStatus.setText("Error de conexión");
                    Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
