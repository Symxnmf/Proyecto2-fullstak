package com.example.springboot.backend.fullstack_backend.dto;

import java.util.Map;

public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private Map<String, Object> user;

    public AuthResponse() {}

    public AuthResponse(String accessToken, String refreshToken, Map<String, Object> user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.user = user;
    }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public Map<String, Object> getUser() { return user; }
    public void setUser(Map<String, Object> user) { this.user = user; }
}
