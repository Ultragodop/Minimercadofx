package com.project.minimercadofx.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.project.minimercadofx.models.bussines.ProductoDTO;
import com.project.minimercadofx.services.http.Session;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;


public class ProductService {

    private static final String BASE_URL = "http://localhost:3050/api/inventario";
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
}


