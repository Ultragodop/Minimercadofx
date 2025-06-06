package com.project.minimercadofx.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.minimercadofx.models.bussines.Producto;
import com.project.minimercadofx.services.http.HttpClientHelper;

import java.io.IOException;
import java.util.List;




public class ProductService {

    private static final String BASE_URL = "http://192.168.0.45:3050/api/inventario";
    private final HttpClientHelper httpClientHelper;
    private final ObjectMapper objectMapper;

    public ProductService() {
        this.httpClientHelper = new HttpClientHelper();
        this.objectMapper = new ObjectMapper();

    }

public List<Producto> getAllProducts() throws IOException {
        String response = httpClientHelper.sendRequest(BASE_URL + "/todos-productos", "GET", null);
        return objectMapper.readValue(response, new TypeReference<>() {
        });
}
public Producto createProduct(Producto producto) throws IOException {
        String json = objectMapper.writeValueAsString(producto);
        String response = httpClientHelper.sendRequest(BASE_URL + "/productos/create", "POST", json);
        return objectMapper.readValue(response, Producto.class);
    }
}


