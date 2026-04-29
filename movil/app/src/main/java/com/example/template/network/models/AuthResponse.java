package com.example.template.network.models;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("user")
    private UserData user;

    @SerializedName("tenant_id")
    private String rootTenantId;

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

    public String getRole() {
        return user != null ? user.role : null;
    }

    public static class UserData {
        public String id;
        public String name;
        public String email;
        public String role;
        public String tenant_id;
        public String tenant_name;
    }
}
