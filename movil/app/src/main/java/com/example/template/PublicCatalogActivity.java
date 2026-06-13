package com.example.template;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.template.network.models.CatalogCartItem;
import com.example.template.network.models.CatalogProducto;
import com.example.template.ui.adapters.CatalogCartAdapter;
import com.example.template.ui.adapters.CatalogProductAdapter;
import com.example.template.utils.ImageLoader;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PublicCatalogActivity extends AppCompatActivity {

    private Toolbar toolbarCatalog;
    private TextView tvToolbarStoreTitle;
    private TextView tvStoreInitial, tvStoreName, tvStoreDesc;
    private ImageView ivStoreLogo;
    private EditText etSearchCatalog;
    private ChipGroup cgCatalogCategories;
    private RecyclerView rvCatalogProducts;
    private ExtendedFloatingActionButton fabCart;
    private View flLoading, llEmptyState;

    // Data lists
    private List<CatalogProducto> allProducts = new ArrayList<>();
    private List<CatalogProducto> filteredProducts = new ArrayList<>();
    private List<CatalogCartItem> cartItems = new ArrayList<>();

    // UI Adapters
    private CatalogProductAdapter productAdapter;

    // Store Info Mock
    private String storeName = "Nike Store";
    private String storePhone = "77777777"; // Número de WhatsApp de prueba
    private String storeDesc = "Ropa y calzado deportivo de la mejor calidad.";

    private String selectedCategory = "TODOS";
    private String searchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_catalog);

        // Extract domain from intent or deep link
        String domain = null;
        Intent intent = getIntent();
        if (intent != null) {
            Uri data = intent.getData();
            if (data != null) {
                // deep link format: omnimall://tienda/{domain} o https://omnimall.com/tienda/{domain}
                java.util.List<String> pathSegments = data.getPathSegments();
                if (pathSegments != null && !pathSegments.isEmpty()) {
                    domain = pathSegments.get(pathSegments.size() - 1);
                }
            }
            if (domain == null || domain.trim().isEmpty()) {
                domain = intent.getStringExtra("STORE_DOMAIN");
            }
        }

        if (domain == null || domain.trim().isEmpty()) {
            domain = "nike"; // fallback
        }

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupSearch();

        // Cargar datos reales desde el backend
        loadCatalogData(domain);
    }

    private void initViews() {
        toolbarCatalog = findViewById(R.id.toolbarCatalog);
        tvToolbarStoreTitle = findViewById(R.id.tvToolbarStoreTitle);
        tvStoreInitial = findViewById(R.id.tvStoreInitial);
        tvStoreName = findViewById(R.id.tvStoreName);
        tvStoreDesc = findViewById(R.id.tvStoreDesc);
        ivStoreLogo = findViewById(R.id.ivStoreLogo);
        etSearchCatalog = findViewById(R.id.etSearchCatalog);
        cgCatalogCategories = findViewById(R.id.cgCatalogCategories);
        rvCatalogProducts = findViewById(R.id.rvCatalogProducts);
        fabCart = findViewById(R.id.fabCart);
        flLoading = findViewById(R.id.flLoading);
        llEmptyState = findViewById(R.id.llEmptyState);

        fabCart.setOnClickListener(v -> showCartDialog());
    }

    private void setupToolbar() {
        setSupportActionBar(toolbarCatalog);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbarCatalog.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        // Calcular columnas dinámicamente para diseño responsive
        int spanCount = 2; // Por defecto para celulares en vertical
        android.util.DisplayMetrics displayMetrics = new android.util.DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float widthDp = displayMetrics.widthPixels / displayMetrics.density;

        if (widthDp >= 900) {
            spanCount = 4; // Tabletas grandes u orientación horizontal en pantallas grandes
        } else if (widthDp >= 600) {
            spanCount = 3; // Tabletas pequeñas u orientación horizontal en celulares
        }

        rvCatalogProducts.setLayoutManager(new GridLayoutManager(this, spanCount));
        productAdapter = new CatalogProductAdapter(filteredProducts, this::showProductDetailDialog);
        rvCatalogProducts.setAdapter(productAdapter);
    }

    private void setupSearch() {
        etSearchCatalog.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString().trim().toLowerCase();
                filterProducts();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadCatalogData(String domain) {
        flLoading.setVisibility(View.VISIBLE);
        llEmptyState.setVisibility(View.GONE);
        rvCatalogProducts.setVisibility(View.GONE);

        com.example.template.network.ApiService apiService = 
                com.example.template.network.ApiClient.getClient(this).create(com.example.template.network.ApiService.class);

        apiService.getCatalogByDomain(domain).enqueue(new retrofit2.Callback<com.example.template.network.models.CatalogResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.template.network.models.CatalogResponse> call, 
                                   retrofit2.Response<com.example.template.network.models.CatalogResponse> response) {
                flLoading.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    com.example.template.network.models.CatalogResponse catalog = response.body();
                    
                    if (catalog.getTienda() != null) {
                        storeName = catalog.getTienda().getName();
                        storePhone = catalog.getTienda().getPhone();
                        storeDesc = "Dominio: " + catalog.getTienda().getDomain();
                        if (catalog.getTienda().getPhone() != null && !catalog.getTienda().getPhone().isEmpty()) {
                            storeDesc += "\nTel: " + catalog.getTienda().getPhone();
                        }
                        
                        tvStoreName.setText(storeName);
                        tvStoreDesc.setText(storeDesc);
                        tvToolbarStoreTitle.setText(storeName);
                        
                        if (storeName != null && !storeName.isEmpty()) {
                            tvStoreInitial.setText(storeName.substring(0, 1).toUpperCase());
                        }

                        // Load store logo if present
                        if (catalog.getTienda().getLogoUrl() != null && !catalog.getTienda().getLogoUrl().isEmpty()) {
                            ivStoreLogo.setVisibility(View.VISIBLE);
                            tvStoreInitial.setVisibility(View.GONE);
                            ImageLoader.loadCircularImage(catalog.getTienda().getLogoUrl(), ivStoreLogo);
                        } else {
                            ivStoreLogo.setVisibility(View.GONE);
                            tvStoreInitial.setVisibility(View.VISIBLE);
                        }
                    }

                    allProducts.clear();
                    if (catalog.getProductos() != null) {
                        allProducts.addAll(catalog.getProductos());
                    }

                    setupCategoryChips();
                    filterProducts();
                } else {
                    showErrorState("Tienda no encontrada o no disponible.");
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.template.network.models.CatalogResponse> call, Throwable t) {
                flLoading.setVisibility(View.GONE);
                showErrorState("Error de red: " + t.getMessage());
            }
        });
    }

    private void showErrorState(String message) {
        tvStoreName.setText("Error");
        tvStoreDesc.setText(message);
        tvToolbarStoreTitle.setText("Error");
        tvStoreInitial.setText("!");
        ivStoreLogo.setVisibility(View.GONE);
        tvStoreInitial.setVisibility(View.VISIBLE);
        
        allProducts.clear();
        filterProducts();
        
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void setupCategoryChips() {
        cgCatalogCategories.removeAllViews();

        // Extraer categorías únicas
        Set<String> categories = new HashSet<>();
        categories.add("TODOS");
        for (CatalogProducto p : allProducts) {
            if (p.getCategory() != null && !p.getCategory().trim().isEmpty()) {
                categories.add(p.getCategory());
            }
        }

        // Crear Chips
        for (String cat : categories) {
            Chip chip = new Chip(this);
            chip.setText(cat);
            chip.setCheckable(true);
            chip.setCheckedIconVisible(false);
            chip.setChipStrokeColorResource(R.color.border_color);
            chip.setChipStrokeWidth(1f);

            if ("TODOS".equals(cat)) {
                chip.setChecked(true);
                chip.setChipBackgroundColorResource(R.color.primary_color);
                chip.setTextColor(androidx.core.content.ContextCompat.getColorStateList(this, R.color.white));
                chip.setChipStrokeColorResource(R.color.primary_color);
            } else {
                chip.setChecked(false);
                chip.setChipBackgroundColorResource(R.color.white);
                chip.setTextColor(androidx.core.content.ContextCompat.getColorStateList(this, R.color.text_secondary));
            }

            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    // Cambiar fondos visuales
                    for (int i = 0; i < cgCatalogCategories.getChildCount(); i++) {
                        Chip c = (Chip) cgCatalogCategories.getChildAt(i);
                        if (c != chip) {
                            c.setChecked(false);
                            c.setChipBackgroundColorResource(R.color.white);
                            c.setTextColor(androidx.core.content.ContextCompat.getColorStateList(this, R.color.text_secondary));
                            c.setChipStrokeColorResource(R.color.border_color);
                        }
                    }
                    chip.setChipBackgroundColorResource(R.color.primary_color);
                    chip.setTextColor(androidx.core.content.ContextCompat.getColorStateList(this, R.color.white));
                    chip.setChipStrokeColorResource(R.color.primary_color);
                    selectedCategory = cat;
                    filterProducts();
                }
            });

            cgCatalogCategories.addView(chip);
        }
    }

    private void filterProducts() {
        filteredProducts.clear();
        for (CatalogProducto p : allProducts) {
            boolean matchCategory = selectedCategory.equals("TODOS") || selectedCategory.equalsIgnoreCase(p.getCategory());
            boolean matchSearch = searchQuery.isEmpty() ||
                    p.getName().toLowerCase().contains(searchQuery) ||
                    p.getSku().toLowerCase().contains(searchQuery);

            if (matchCategory && matchSearch) {
                filteredProducts.add(p);
            }
        }

        productAdapter.updateData(filteredProducts);

        if (filteredProducts.isEmpty()) {
            llEmptyState.setVisibility(View.VISIBLE);
            rvCatalogProducts.setVisibility(View.GONE);
        } else {
            llEmptyState.setVisibility(View.GONE);
            rvCatalogProducts.setVisibility(View.VISIBLE);
        }
    }

    private void showProductDetailDialog(CatalogProducto product) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(R.layout.dialog_catalog_product_detail);

        ImageView ivDetailImage = dialog.findViewById(R.id.ivDetailImage);
        TextView tvDetailCategory = dialog.findViewById(R.id.tvDetailCategory);
        TextView tvDetailName = dialog.findViewById(R.id.tvDetailName);
        TextView tvDetailSku = dialog.findViewById(R.id.tvDetailSku);
        TextView tvDetailDescription = dialog.findViewById(R.id.tvDetailDescription);
        TextView tvDetailPrice = dialog.findViewById(R.id.tvDetailPrice);
        Button btnDetailAddToCart = dialog.findViewById(R.id.btnDetailAddToCart);

        if (tvDetailCategory != null) tvDetailCategory.setText(product.getCategory().toUpperCase());
        if (tvDetailName != null) tvDetailName.setText(product.getName());
        if (tvDetailSku != null) tvDetailSku.setVisibility(View.GONE);
        if (tvDetailDescription != null) {
            String desc = product.getDescription();
            if (product.getAttributes() != null && !product.getAttributes().isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (java.util.Map.Entry<String, String> entry : product.getAttributes().entrySet()) {
                    if (entry.getValue() != null && !entry.getValue().trim().isEmpty()) {
                        if (sb.length() > 0) sb.append("\n");
                        sb.append(entry.getKey()).append(": ").append(entry.getValue());
                    }
                }
                if (sb.length() > 0) {
                    if (desc != null && !desc.trim().isEmpty()) {
                        desc = desc + "\n\nEspecificaciones:\n" + sb.toString();
                    } else {
                        desc = sb.toString();
                    }
                }
            }
            tvDetailDescription.setText(desc != null ? desc : "");
        }
        if (tvDetailPrice != null) tvDetailPrice.setText(String.format("Bs %.2f", product.getPrecioVenta()));

        if (product.getImagenUrl() != null && !product.getImagenUrl().trim().isEmpty()) {
            if (ivDetailImage != null) {
                ivDetailImage.setPadding(0, 0, 0, 0);
                ivDetailImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                ImageLoader.loadImage(product.getImagenUrl(), ivDetailImage);
            }
        } else {
            if (ivDetailImage != null) {
                int padding = (int) (32 * ivDetailImage.getContext().getResources().getDisplayMetrics().density);
                ivDetailImage.setPadding(padding, padding, padding, padding);
                ivDetailImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                ivDetailImage.setImageResource(R.drawable.ic_product_placeholder);
            }
        }

        if (btnDetailAddToCart != null) {
            btnDetailAddToCart.setOnClickListener(v -> {
                addToCart(product);
                dialog.dismiss();
            });
        }

        dialog.show();
    }

    private void addToCart(CatalogProducto product) {
        boolean exists = false;
        for (CatalogCartItem item : cartItems) {
            if (item.getProduct().getId().equals(product.getId())) {
                item.setQuantity(item.getQuantity() + 1);
                exists = true;
                break;
            }
        }

        if (!exists) {
            cartItems.add(new CatalogCartItem(product, 1));
        }

        updateCartFab();
        Toast.makeText(this, product.getName() + " añadido al pedido", Toast.LENGTH_SHORT).show();
    }

    private void updateCartFab() {
        if (cartItems.isEmpty()) {
            fabCart.setVisibility(View.GONE);
            return;
        }

        double total = 0;
        int count = 0;
        for (CatalogCartItem item : cartItems) {
            total += item.getProduct().getPrecioVenta() * item.getQuantity();
            count += item.getQuantity();
        }

        fabCart.setText(String.format("Bs %.2f (%d)", total, count));
        fabCart.setVisibility(View.VISIBLE);
    }

    private void showCartDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(R.layout.dialog_catalog_cart);

        RecyclerView rvCart = dialog.findViewById(R.id.rvCatalogCartItems);
        TextView tvTotal = dialog.findViewById(R.id.tvCatalogCartTotal);
        TextView tvEmpty = dialog.findViewById(R.id.tvCatalogCartEmpty);
        View llCheckout = dialog.findViewById(R.id.llCatalogCartCheckout);
        Button btnConfirm = dialog.findViewById(R.id.btnCatalogCartConfirm);
        View btnClose = dialog.findViewById(R.id.btnCatalogCartClose);

        if (btnClose != null) {
            btnClose.setOnClickListener(v -> dialog.dismiss());
        }

        if (rvCart != null) {
            rvCart.setLayoutManager(new LinearLayoutManager(this));
            CatalogCartAdapter cartAdapter = new CatalogCartAdapter(cartItems, new CatalogCartAdapter.OnCartActionListener() {
                @Override
                public void onQuantityChange(CatalogCartItem item, int newQty) {
                    item.setQuantity(newQty);
                    updateCartTotals(tvTotal, tvEmpty, llCheckout);
                    rvCart.getAdapter().notifyDataSetChanged();
                    updateCartFab();
                }

                @Override
                public void onDelete(CatalogCartItem item) {
                    cartItems.remove(item);
                    updateCartTotals(tvTotal, tvEmpty, llCheckout);
                    rvCart.getAdapter().notifyDataSetChanged();
                    updateCartFab();
                    if (cartItems.isEmpty()) {
                        dialog.dismiss();
                    }
                }
            });
            rvCart.setAdapter(cartAdapter);
        }

        updateCartTotals(tvTotal, tvEmpty, llCheckout);

        if (btnConfirm != null) {
            btnConfirm.setOnClickListener(v -> {
                sendWhatsAppOrder();
                dialog.dismiss();
            });
        }

        dialog.show();
    }

    private void updateCartTotals(TextView tvTotal, TextView tvEmpty, View llCheckout) {
        if (cartItems.isEmpty()) {
            if (tvEmpty != null) tvEmpty.setVisibility(View.VISIBLE);
            if (llCheckout != null) llCheckout.setVisibility(View.GONE);
            return;
        }

        if (tvEmpty != null) tvEmpty.setVisibility(View.GONE);
        if (llCheckout != null) llCheckout.setVisibility(View.VISIBLE);

        double total = 0;
        for (CatalogCartItem item : cartItems) {
            total += item.getProduct().getPrecioVenta() * item.getQuantity();
        }

        if (tvTotal != null) {
            tvTotal.setText(String.format("Bs %.2f", total));
        }
    }

    private void sendWhatsAppOrder() {
        if (cartItems.isEmpty()) return;

        StringBuilder sb = new StringBuilder();
        String displayStoreName = storeName;
        if (displayStoreName != null && !displayStoreName.trim().toLowerCase().startsWith("tienda")) {
            displayStoreName = "Tienda " + displayStoreName;
        }
        sb.append("Hola! Quisiera hacer un pedido de ").append(displayStoreName).append(":\n\n");

        double total = 0;
        for (CatalogCartItem item : cartItems) {
            double subtotal = item.getProduct().getPrecioVenta() * item.getQuantity();
            sb.append("- ").append(item.getQuantity()).append("x ")
              .append(item.getProduct().getName())
              .append(" (Bs ").append(String.format("%.2f", item.getProduct().getPrecioVenta())).append(")\n");
            total += subtotal;
        }

        sb.append("\nTotal estimado: Bs ").append(String.format("%.2f", total));

        String phone = storePhone.replaceAll("\\D", "");
        if (phone.length() == 8) {
            phone = "591" + phone; // Prefijo boliviano
        }

        try {
            String url = "https://wa.me/" + phone + "?text=" + Uri.encode(sb.toString());
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "No se pudo abrir WhatsApp", Toast.LENGTH_SHORT).show();
        }
    }
}
