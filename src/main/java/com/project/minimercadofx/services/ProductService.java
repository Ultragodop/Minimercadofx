package com.project.minimercadofx.services;
import com.fasterxml.jackson.core.type.TypeReference;
import com.project.minimercadofx.models.bussines.Producto;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ProductService {
    private static final String BASE_URL = "http://localhost:3050/api/inventario";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final Producto producto;
    public ProductService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.producto = new Producto();
    }
    public List<Producto> findAll() {
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/todos-productos"))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            // Deserializa directamente como lista
            return objectMapper.readValue(response.body(), new TypeReference<List<Producto>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList(); // mejor que null
        }
    }

}


