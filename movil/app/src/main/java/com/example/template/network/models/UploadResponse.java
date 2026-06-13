package com.example.template.network.models;

import com.google.gson.annotations.SerializedName;

public class UploadResponse {
    @SerializedName("url")
    private String url;

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}
