package com.example.template;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.template.ui.HomeFragment;
import com.example.template.ui.ProductsFragment;
import com.example.template.ui.ProvidersFragment;
import com.example.template.ui.SourcingFragment;
import com.example.template.ui.StockFragment;
import com.example.template.ui.AuditReportsFragment;
import com.example.template.ui.SucursalesFragment;
import com.example.template.ui.EmpleadosFragment;
import com.example.template.ui.PermisosFragment;
import com.example.template.utils.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import android.view.Menu;
import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.template.network.ApiClient;
import com.example.template.network.ApiService;
import com.example.template.network.models.PermisosRoles;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private SessionManager sessionManager;
    private MaterialToolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        // Bypass login and use dummy session as requested
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("OmniMall - " + sessionManager.getTenantName());
        toolbar.inflateMenu(R.menu.toolbar_menu);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_logout) {
                sessionManager.logout();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;
            }
            return false;
        });

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        // initial load
        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.nav_home);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        applyPermissions();
    }

    private void applyPermissions() {
        String role = sessionManager.getRole();
        Menu menu = navigationView.getMenu();

        if ("OWNER".equalsIgnoreCase(role)) {
            // Owner sees everything, nothing to hide
            return;
        }

        // Default hide administration stuff for non-owners
        menu.findItem(R.id.nav_permisos).setVisible(false);

        ApiClient.getClient(this).create(ApiService.class).getPermisos().enqueue(new Callback<List<PermisosRoles>>() {
            @Override
            public void onResponse(Call<List<PermisosRoles>> call, Response<List<PermisosRoles>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (PermisosRoles pr : response.body()) {
                        if (pr.getRole().equalsIgnoreCase(role)) {
                            menu.findItem(R.id.nav_providers).setVisible(pr.isCatalogoVer());
                            menu.findItem(R.id.nav_products).setVisible(pr.isCatalogoVer());
                            menu.findItem(R.id.nav_sucursales).setVisible(pr.isSucursalesVer());
                            menu.findItem(R.id.nav_sourcing).setVisible(pr.isSourcingVer());
                            menu.findItem(R.id.nav_stock).setVisible(pr.isInventarioVer());
                            menu.findItem(R.id.nav_audit_reports).setVisible(pr.isInventarioVer());
                            menu.findItem(R.id.nav_empleados).setVisible(pr.isUsuariosVer());
                            break;
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<List<PermisosRoles>> call, Throwable t) {
                Log.e("MainActivity", "Error fetching permissions");
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;
        int itemId = item.getItemId();

        if (itemId == R.id.nav_home) {
            selectedFragment = new HomeFragment();
        } else if (itemId == R.id.nav_providers) {
            selectedFragment = new ProvidersFragment();
        } else if (itemId == R.id.nav_products) {
            selectedFragment = new ProductsFragment();
        } else if (itemId == R.id.nav_sourcing) {
            selectedFragment = new SourcingFragment();
        } else if (itemId == R.id.nav_stock) {
            selectedFragment = new StockFragment();
        } else if (itemId == R.id.nav_audit_reports) {
            selectedFragment = new AuditReportsFragment();
        } else if (itemId == R.id.nav_sucursales) {
            selectedFragment = new SucursalesFragment();
        } else if (itemId == R.id.nav_empleados) {
            selectedFragment = new EmpleadosFragment();
        } else if (itemId == R.id.nav_permisos) {
            selectedFragment = new PermisosFragment();
        } else if (itemId == R.id.nav_logout) {
            sessionManager.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }

        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}