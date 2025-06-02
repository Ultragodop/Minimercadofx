package com.project.minimercadofx.services;
import com.project.minimercadofx.models.bussines.Producto;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ProductService {
    private static final String BASE_URL = "http://localhost:3050/api/auth";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    public ProductService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();

    }
    public Producto FindByCodigoBarra(Integer id){
        try {
            Producto producto = new Producto(id);
            String requestbody= objectMapper.writeValueAsString(producto);
            HttpRequest httpRequest= HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestbody))
                    .build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return objectMapper.readValue(response.body(),
                    Producto.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        }
    }

