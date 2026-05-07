package com.example.template.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "OmniMallSession";
    private static final String KEY_TENANT_ID = "tenant_id";
    private static final String KEY_TENANT_NAME = "tenant_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_TOKEN = "access_token";
    private static final String KEY_ROLE = "role";
    private static final String KEY_USER_NAME = "user_name";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void createSession(String tenantId, String tenantName, String email, String token, String role, String userName) {
        editor.putString(KEY_TENANT_ID, tenantId);
        editor.putString(KEY_TENANT_NAME, tenantName);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_ROLE, role);
        editor.putString(KEY_USER_NAME, userName);
        editor.apply();
    }

    public String getTenantId() {
        return prefs.getString(KEY_TENANT_ID, null);
    }

    public String getTenantName() {
        return prefs.getString(KEY_TENANT_NAME, "Mi Tienda");
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, "admin@mitienda.com");
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, "Usuario");
    }

    public String getRole() {
        return prefs.getString(KEY_ROLE, "VENDEDOR");
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }

    public boolean isLoggedIn() {
        return getTenantId() != null && getToken() != null;
    }
}
