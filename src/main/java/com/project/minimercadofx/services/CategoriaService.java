package com.project.minimercadofx.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.minimercadofx.models.bussines.CategoriaDTO;
import com.project.minimercadofx.services.http.Session;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CategoriaService {
    private static final String BASE_URL = "http://localhost:3050/api/categorias";
    private final HttpClient httpClientHelper;
    private final ObjectMapper objectMapper;

    public CategoriaService() {
        this.httpClientHelper = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public CategoriaDTO[] getAllCategorias() throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/listar"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + Session.getToken())
                .GET()
                .build();

        String response = httpClientHelper.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
        return objectMapper.readValue(response, CategoriaDTO[].class);
    }

    public CategoriaDTO anadirCategoria(CategoriaDTO categoria) throws IOException, InterruptedException {
        String jsonCategoria = objectMapper.writeValueAsString(categoria);
        
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/create"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + Session.getToken())
                .POST(HttpRequest.BodyPublishers.ofString(jsonCategoria))
                .build();

        String response = httpClientHelper.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
        
        if(response == null || response.isEmpty()) {
            throw new IOException("No se pudo crear la categoría");
        }

        if(response.contains("Error")) {
            throw new IOException("Error al crear la categoría: " + response);
        }

        return objectMapper.readValue(response, CategoriaDTO.class);
    }
}
