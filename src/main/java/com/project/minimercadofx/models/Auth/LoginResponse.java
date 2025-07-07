package com.project.minimercadofx.models.Auth;

public class LoginResponse {
    private String status;
    private String message;
    private String token;
    private Long id;
    public LoginResponse() {
    }



    public LoginResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }



    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
} 