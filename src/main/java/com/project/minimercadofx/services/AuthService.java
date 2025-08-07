package com.project.minimercadofx.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.minimercadofx.models.Auth.LoginRequest;
import com.project.minimercadofx.models.Auth.LoginResponse;
import com.project.minimercadofx.models.Auth.RegisterRequest;
import com.project.minimercadofx.models.Auth.RegisterResponse;
import com.project.minimercadofx.services.http.User;
import com.project.minimercadofx.services.http.Session;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.Executor;

public class AuthService {

    private static final String BASE_URL = "http://localhost:3040/minimercado/api/auth";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public AuthService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();

    }

    public LoginResponse login(String username, String password) {
        try {
            long startTime = System.currentTimeMillis();
            LoginRequest request = new LoginRequest(username, password);
            String requestBody = objectMapper.writeValueAsString(request);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            long endTime = System.currentTimeMillis();
            HttpHeaders headers = response.headers();
            System.out.println("[AuthService] Headers: " + headers.map());
            System.out.println("[AuthService] Respuesta recibida en " + (endTime - startTime) + " ms");
            LoginResponse loginResponse = objectMapper.readValue(response.body(), LoginResponse.class);
            Session.setToken(loginResponse.getToken());
            User.setId(loginResponse.getId());
            User.setNombre(loginResponse.getUsername());
            return loginResponse;

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
    public String logout(){
        try {
            long startTime = System.currentTimeMillis();
            String token = Session.getToken();
            HttpRequest httpRequest = HttpRequest.newBuilder()

                    .uri(URI.create(BASE_URL + "/logout"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .POST(HttpRequest.BodyPublishers.ofString(token))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            long endTime = System.currentTimeMillis();
            System.out.println("[AuthService] Respuesta recibida en " + (endTime - startTime) + " ms");
            if (response.statusCode() == 200) {
                Session.setToken(null); // Limpiar el token de la sesión
                return "Success";
            } else {
                return "Error al hacer logout: " + response.body();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al conectar con el servidor";
        }
    }
} 
