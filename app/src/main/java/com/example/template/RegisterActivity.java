package com.example.template;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.template.network.ApiClient;
import com.example.template.network.ApiService;
import com.example.template.network.models.AuthResponse;
import com.example.template.network.models.RegisterRequest;
import com.example.template.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private EditText etName, etDomain, etEmail, etPassword;
    private Button btnRegister;
    private TextView tvLogin;
    private SessionManager sessionManager;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sessionManager = new SessionManager(this);
        apiService = ApiClient.getClient(this).create(ApiService.class);

        etName = findViewById(R.id.etName);
        etDomain = findViewById(R.id.etDomain);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        btnRegister.setOnClickListener(v -> handleRegister());
        tvLogin.setOnClickListener(v -> finish());
    }

    private void handleRegister() {
        String name = etName.getText().toString().trim();
        String domain = etDomain.getText().toString().trim().toLowerCase().replaceAll("[^a-z0-9]", "");
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.isEmpty() || domain.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        btnRegister.setEnabled(false);
        btnRegister.setText("Creando tienda...");

        RegisterRequest request = new RegisterRequest(name, domain, email, password);
        apiService.register(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                btnRegister.setEnabled(true);
                btnRegister.setText("Comenzar a usar OmniMall");

                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse auth = response.body();
                    // Auto login
                    sessionManager.createSession(auth.getTenant_id(), name);
                    Toast.makeText(RegisterActivity.this, "Cuenta creada exitosamente", Toast.LENGTH_SHORT).show();
                    
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    // Clear whole stack
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Error: dominio o correo podrían ya existir", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                btnRegister.setEnabled(true);
                btnRegister.setText("Comenzar a usar OmniMall");
                Toast.makeText(RegisterActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
