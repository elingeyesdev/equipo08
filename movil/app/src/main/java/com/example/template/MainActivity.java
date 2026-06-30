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
import androidx.core.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;


import com.example.template.ui.HomeFragment;
import com.example.template.ui.ProductsFragment;
import com.example.template.ui.ProvidersFragment;
import com.example.template.ui.SourcingFragment;
import com.example.template.ui.StockFragment;
import com.example.template.ui.AuditReportsFragment;
import com.example.template.ui.SucursalesFragment;
import com.example.template.ui.EmpleadosFragment;
import com.example.template.ui.PermisosFragment;
import com.example.template.ui.TerminalPOSFragment;
import com.example.template.ui.VentasFragment;
import com.example.template.ui.SettingsFragment;
import com.example.template.utils.SessionManager;
import com.example.template.network.models.TenantProfile;
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
            startActivity(new Intent(this, LandingActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Inicio");
        
        // Force the system status bar color to match the header color (slate #0f172a)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.primary_color));
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.primary_color));
        }

        toolbar.inflateMenu(R.menu.toolbar_menu);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_logout) {
                sessionManager.logout();
                startActivity(new Intent(this, LandingActivity.class));
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

        // Update nav header
        android.view.View headerView = navigationView.getHeaderView(0);
        if (headerView == null) {
            headerView = navigationView.inflateHeaderView(R.layout.nav_header);
        }
        android.widget.TextView navTenantName = headerView.findViewById(R.id.nav_header_tenant_name);
        android.widget.TextView navUsername = headerView.findViewById(R.id.nav_header_name);
        android.widget.TextView navRole = headerView.findViewById(R.id.nav_header_role);
        android.widget.ImageView navLogo = headerView.findViewById(R.id.nav_header_logo);
        if (navTenantName != null) navTenantName.setText(sessionManager.getTenantName());
        if (navUsername != null) navUsername.setText(sessionManager.getUserName());
        if (navRole != null) {
            String roleText = sessionManager.getRole();
            if ("OWNER".equalsIgnoreCase(roleText)) {
                roleText = "Administrador";
            }
            navRole.setText(roleText);
        }
        if (navLogo != null) {
            String logoUrl = sessionManager.getLogoUrl();
            if (logoUrl != null && !logoUrl.trim().isEmpty()) {
                navLogo.setImageTintList(null);
                com.example.template.utils.ImageLoader.loadCircularImage(logoUrl, navLogo);
            } else {
                navLogo.setImageResource(R.drawable.ic_logo_vector);
                navLogo.setImageTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.WHITE));
            }
        }

        // Fetch latest tenant profile to sync logo & tenant name
        ApiClient.getClient(this).create(ApiService.class).getTenantProfile().enqueue(new Callback<TenantProfile>() {
            @Override
            public void onResponse(Call<TenantProfile> call, Response<TenantProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TenantProfile profile = response.body();
                    sessionManager.updateTenantName(profile.getName());
                    sessionManager.updateLogoUrl(profile.getLogoUrl());
                    updateNavHeaderLogo(profile.getLogoUrl());
                    updateNavHeaderTenantName(profile.getName());
                }
            }

            @Override
            public void onFailure(Call<TenantProfile> call, Throwable t) {
                // Network error, fallback to cached settings
            }
        });

        // initial load
        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.nav_home);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        int[] groupIds = {R.id.group_navigation, R.id.group_cruds, R.id.group_sales, R.id.nav_admin_group, R.id.group_session};
        for (int id : groupIds) {
            MenuItem groupItem = navigationView.getMenu().findItem(id);
            if (groupItem != null && groupItem.getTitle() != null) {
                SpannableString s = new SpannableString(groupItem.getTitle());
                // Color gris claro para que resalte sutilmente en el fondo oscuro
                s.setSpan(new ForegroundColorSpan(android.graphics.Color.parseColor("#94A3B8")), 0, s.length(), 0);
                // Texto un poco más pequeño
                s.setSpan(new android.text.style.RelativeSizeSpan(0.8f), 0, s.length(), 0);
                groupItem.setTitle(s);
            }
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
        menu.findItem(R.id.nav_settings).setVisible(false);

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
                            menu.findItem(R.id.nav_pos).setVisible(pr.isVentasVer());
                            menu.findItem(R.id.nav_sales_history).setVisible(pr.isVentasVer());
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
        } else if (itemId == R.id.nav_pos) {
            selectedFragment = new TerminalPOSFragment();
        } else if (itemId == R.id.nav_sales_history) {
            selectedFragment = new VentasFragment();
        } else if (itemId == R.id.nav_audit_reports) {
            selectedFragment = new AuditReportsFragment();
        } else if (itemId == R.id.nav_sucursales) {
            selectedFragment = new SucursalesFragment();
        } else if (itemId == R.id.nav_empleados) {
            selectedFragment = new EmpleadosFragment();
        } else if (itemId == R.id.nav_permisos) {
            selectedFragment = new PermisosFragment();
        } else if (itemId == R.id.nav_settings) {
            selectedFragment = new SettingsFragment();
        } else if (itemId == R.id.nav_logout) {
            sessionManager.logout();
            startActivity(new Intent(this, LandingActivity.class));
            finish();
            return true;
        }

        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
            uncheckAllMenuItems(navigationView.getMenu());
            item.setChecked(true);
            
            // Set toolbar title dynamically to match screen name
            String title = item.getTitle().toString();
            if (itemId == R.id.nav_home) {
                title = "Inicio";
            }
            updateToolbarTitle(title);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(androidx.core.view.GravityCompat.START)) {
            drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START);
        } else {
            androidx.fragment.app.Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (currentFragment != null && !(currentFragment instanceof HomeFragment)) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new HomeFragment())
                        .commit();
                uncheckAllMenuItems(navigationView.getMenu());
                navigationView.setCheckedItem(R.id.nav_home);
                updateToolbarTitle("Inicio");
            } else {
                super.onBackPressed();
            }
        }
    }

    public void updateToolbarTitle(String title) {
        if (toolbar != null) {
            toolbar.setTitle(title);
        }
    }

    public void updateNavHeaderLogo(String logoUrl) {
        android.view.View headerView = navigationView.getHeaderView(0);
        if (headerView != null) {
            android.widget.ImageView navLogo = headerView.findViewById(R.id.nav_header_logo);
            if (navLogo != null) {
                if (logoUrl != null && !logoUrl.trim().isEmpty()) {
                    navLogo.setImageTintList(null);
                    com.example.template.utils.ImageLoader.loadCircularImage(logoUrl, navLogo);
                } else {
                    navLogo.setImageResource(R.drawable.ic_logo_vector);
                    navLogo.setImageTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.WHITE));
                }
            }
        }
    }

    public void updateNavHeaderTenantName(String tenantName) {
        android.view.View headerView = navigationView.getHeaderView(0);
        if (headerView != null) {
            android.widget.TextView navTenantName = headerView.findViewById(R.id.nav_header_tenant_name);
            if (navTenantName != null) {
                navTenantName.setText(tenantName);
            }
        }
    }

    public void navigateToSalesHistory() {
        uncheckAllMenuItems(navigationView.getMenu());
        MenuItem salesItem = navigationView.getMenu().findItem(R.id.nav_sales_history);
        if (salesItem != null) {
            salesItem.setChecked(true);
            updateToolbarTitle(salesItem.getTitle().toString());
        } else {
            navigationView.setCheckedItem(R.id.nav_sales_history);
            updateToolbarTitle("Ventas");
        }
        VentasFragment historyFragment = new VentasFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, historyFragment)
                .commit();
    }

    private void uncheckAllMenuItems(android.view.Menu menu) {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            item.setChecked(false);
            if (item.hasSubMenu()) {
                uncheckAllMenuItems(item.getSubMenu());
            }
        }
    }
}