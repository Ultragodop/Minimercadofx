package com.project.minimercadofx.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.minimercadofx.models.Auth.LoginRequest;
import com.project.minimercadofx.models.Auth.LoginResponse;
import com.project.minimercadofx.models.Auth.RegisterRequest;
import com.project.minimercadofx.models.Auth.RegisterResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AuthService {
    private static final String BASE_URL = "http://localhost:3050/api/auth";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public AuthService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public LoginResponse login(String username, String password) {
        try {
            LoginRequest request = new LoginRequest(username, password);
            String requestBody = objectMapper.writeValueAsString(request);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return objectMapper.readValue(response.body(), LoginResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new LoginResponse("error", "Error al conectar con el servidor");
        }
    }

    public RegisterResponse register(String username, String password, String rol) {
        try {
            RegisterRequest request = new RegisterRequest(username, password, rol);
            String requestBody = objectMapper.writeValueAsString(request);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/register"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return objectMapper.readValue(response.body(), RegisterResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new RegisterResponse("error", "Error al conectar con el servidor");
        }
    }
} 
