package com.project.minimercadofx.services;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.project.minimercadofx.models.bussines.ProductoDTO;
import com.project.minimercadofx.models.bussines.ProductoRequest;
import com.project.minimercadofx.services.http.Session;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;



public class ProductService {

    private static final String BASE_URL = "http://localhost:3040/minimercado/api/inventario";
    private final HttpClient httpClientHelper;
    private final ObjectMapper objectMapper;

    public ProductService() {
        this.httpClientHelper = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();

    }
    public ProductoDTO[] getAllProducts() throws IOException, InterruptedException {
    HttpRequest httpRequest = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/todos-productos"))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + Session.getToken())
            .GET()
            .build();

    String response = httpClientHelper.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
    ProductoDTO[] productos = objectMapper.readValue(response, ProductoDTO[].class);
    for (ProductoDTO producto : productos) {
        System.out.println("Producto: " + producto.getNombre() + ", Precio: " + producto.getPrecioVenta());

    }

      return productos ;
}
public ProductoDTO getProductoById(Integer id) throws IOException, InterruptedException{
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/producto/"+id))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + Session.getToken())
                .GET()
                .build();
        String response = httpClientHelper.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
        if(response == null || response.isEmpty()) {
            System.out.println("No se encontro el producto " + id);
            return null;

        }

        if(response.contains("Error")) {
            System.out.println("Error al obtener el producto: " + response);
            return null;
        }
        System.out.println("Response: " + response);
    return objectMapper.readValue(response, ProductoDTO.class);
}

public ProductoDTO createProduct(ProductoRequest producto) throws IOException, InterruptedException {
        String jsonProduct = objectMapper.writeValueAsString(producto);
        
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/producto"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + Session.getToken())
                .POST(HttpRequest.BodyPublishers.ofString(jsonProduct))
                .build();

        String response = httpClientHelper.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
        System.out.println(jsonProduct);
        if(response == null || response.isEmpty()) {
            throw new IOException("No se pudo crear el producto");
        }

        if(response.contains("Error")) {
            throw new IOException("Error al crear el producto: " + response);
        }

        return objectMapper.readValue(response, ProductoDTO.class);
    }
}

