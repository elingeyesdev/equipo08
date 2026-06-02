package com.example.template.network.models;

import com.google.gson.annotations.SerializedName;

public class Tienda {
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

    public Tienda(String id, String name, String domain, String logoUrl, String brandColor, String phone) {
        this.id = id;
        this.name = name;
        this.domain = domain;
        this.logoUrl = logoUrl;
        this.brandColor = brandColor;
        this.phone = phone;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDomain() { return domain; }
    public String getLogoUrl() { return logoUrl; }
    public String getBrandColor() { return brandColor; }
    public String getPhone() { return phone; }
}
