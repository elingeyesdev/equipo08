package com.example.template.network;

import com.example.template.network.models.AuthResponse;
import com.example.template.network.models.LoginRequest;
import com.example.template.network.models.RegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<AuthResponse> register(@Body RegisterRequest request);

    @retrofit2.http.GET("sucursales")
    Call<java.util.List<com.example.template.network.models.Sucursal>> getSucursales();

    @POST("sucursales")
    Call<com.example.template.network.models.Sucursal> createSucursal(@Body com.example.template.network.models.Sucursal sucursal);

    @retrofit2.http.PUT("sucursales/{id}")
    Call<com.example.template.network.models.Sucursal> updateSucursal(@retrofit2.http.Path("id") String id, @Body com.example.template.network.models.Sucursal sucursal);

    @retrofit2.http.DELETE("sucursales/{id}")
    Call<Void> deleteSucursal(@retrofit2.http.Path("id") String id);

    @retrofit2.http.GET("proveedores")
    Call<java.util.List<com.example.template.network.models.Proveedor>> getProveedores();

    @POST("proveedores")
    Call<com.example.template.network.models.Proveedor> createProveedor(@Body com.example.template.network.models.Proveedor proveedor);

    @retrofit2.http.DELETE("proveedores/{id}")
    Call<Void> deleteProveedor(@retrofit2.http.Path("id") String id);

    @retrofit2.http.GET("productos")
    Call<java.util.List<com.example.template.network.models.Producto>> getProductos();

    @POST("productos")
    Call<com.example.template.network.models.Producto> createProducto(@Body com.example.template.network.models.Producto producto);

    @retrofit2.http.PUT("productos/{id}")
    Call<com.example.template.network.models.Producto> updateProducto(@retrofit2.http.Path("id") String id, @Body com.example.template.network.models.Producto producto);

    @retrofit2.http.DELETE("productos/{id}")
    Call<Void> deleteProducto(@retrofit2.http.Path("id") String id);

    @retrofit2.http.GET("sourcing")
    Call<java.util.List<com.example.template.network.models.LoteIngreso>> getLotes();

    @POST("sourcing")
    Call<com.example.template.network.models.LoteIngreso> createLote(@Body com.example.template.network.models.LoteIngreso lotePeticion);

    @retrofit2.http.DELETE("sourcing/{id}")
    Call<Void> deleteLote(@retrofit2.http.Path("id") String id);

    @retrofit2.http.GET("proveedores/global/{nit}")
    Call<com.example.template.network.models.Proveedor> getGlobalProveedor(@retrofit2.http.Path("nit") String nit);


    @retrofit2.http.GET("stock")
    Call<java.util.List<com.example.template.network.models.Stock>> getStock();

    @POST("ajustes")
    Call<Void> createAjuste(@Body com.example.template.network.models.AjusteRequest request);

    @retrofit2.http.GET("ajustes")
    Call<java.util.List<com.example.template.network.models.Ajuste>> getAjustes();

    // EMPLEADOS (Users)
    @retrofit2.http.GET("users")
    Call<java.util.List<com.example.template.network.models.Empleado>> getEmpleados();

    @POST("users")
    Call<com.example.template.network.models.Empleado> createEmpleado(@Body com.example.template.network.models.Empleado empleado);

    @retrofit2.http.PUT("users/{id}")
    Call<com.example.template.network.models.Empleado> updateEmpleado(@retrofit2.http.Path("id") String id, @Body com.example.template.network.models.Empleado empleado);

    @retrofit2.http.DELETE("users/{id}")
    Call<Void> deleteEmpleado(@retrofit2.http.Path("id") String id);

    // PERMISOS
    @retrofit2.http.GET("users/permissions")
    Call<java.util.List<com.example.template.network.models.PermisosRoles>> getPermisos();

    @retrofit2.http.PUT("users/permissions")
    Call<com.example.template.network.models.PermisosRoles> updatePermisos(@Body com.example.template.network.models.PermisosRoles permisos);
}
