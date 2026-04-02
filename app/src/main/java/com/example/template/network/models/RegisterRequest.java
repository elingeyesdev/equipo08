package com.example.template.network.models;

public class RegisterRequest {
    private String name;
    private String domain;
    private String email;
    private String password;

    public RegisterRequest(String name, String domain, String email, String password) {
        this.name = name;
        this.domain = domain;
        this.email = email;
        this.password = password;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
