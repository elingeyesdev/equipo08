package com.example.template.network.models;

import com.google.gson.annotations.SerializedName;

public class TenantProfile {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("domain")
    private String domain;

    @SerializedName("logoUrl")
    private String logoUrl;

    @SerializedName("brandColor")
    private String brandColor;

    @SerializedName("phone")
    private String phone;

    @SerializedName("status")
    private String status;

    @SerializedName("isActive")
    private boolean isActive;

    public TenantProfile(String name, String phone, String logoUrl, String brandColor) {
        this.name = name;
        this.phone = phone;
        this.logoUrl = logoUrl;
        this.brandColor = brandColor;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public String getBrandColor() { return brandColor; }
    public void setBrandColor(String brandColor) { this.brandColor = brandColor; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
