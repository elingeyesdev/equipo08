package com.example.template;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.template.utils.SessionManager;

public class LandingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_landing);

        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(LandingActivity.this, LoginActivity.class));
        });

        btnRegister.setOnClickListener(v -> {
            startActivity(new Intent(LandingActivity.this, RegisterActivity.class));
        });

        //Hola
    }
}
