package com.example.template.network.models;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("user")
    private UserData user;

    @SerializedName("tenant_id")
    private String rootTenantId;

    @SerializedName("sucursal_id")
    private String sucursalId;

    @SerializedName("sucursal_name")
    private String sucursalName;

    public String getAccessToken() {
        return accessToken;
    }

    public String getTenant_id() { 
        if (rootTenantId != null) return rootTenantId;
        return user != null ? user.tenant_id : null; 
    }
    
    public String getName() { 
        return user != null ? user.tenant_name : null; 
    }

    public String getUserName() {
        return user != null ? user.name : null;
    }

    public String getRole() {
        return user != null ? user.role : null;
    }

    public String getSucursalId() {
        if (sucursalId != null) return sucursalId;
        return user != null ? user.sucursalId : null;
    }

    public String getSucursalName() {
        if (sucursalName != null) return sucursalName;
        return user != null ? user.sucursalName : null;
    }

    public static class UserData {
        public String id;
        public String name;
        public String email;
        public String role;
        public String tenant_id;
        public String tenant_name;

        @SerializedName("sucursal_id")
        public String sucursalId;

        @SerializedName("sucursal_name")
        public String sucursalName;
    }
}
