package com.example.template.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.template.R;
import com.example.template.utils.SessionManager;

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        SessionManager sessionManager = new SessionManager(getContext());
        
        TextView tvWelcomeTitle = view.findViewById(R.id.tvWelcomeTitle);
        TextView tvWelcomeSub = view.findViewById(R.id.tvWelcomeSub);
        
        if (tvWelcomeTitle != null) {
            tvWelcomeTitle.setText("Bienvenido a " + sessionManager.getTenantName());
        }
        
        if (tvWelcomeSub != null) {
            tvWelcomeSub.setText("Usuario: " + sessionManager.getUserName() + "\n\nBienvenido al sistema de administración de tu tienda. Utiliza el menú inferior para gestionar proveedores, inventario y más.");
        }
        
        return view;
    }
}
