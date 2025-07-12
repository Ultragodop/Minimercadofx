package com.project.minimercadofx.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.minimercadofx.models.bussines.Proveedor;
import com.project.minimercadofx.services.http.HttpClientHelper;

import java.io.IOException;

public class ProveedorService {
    private static final String BASE_URL = "http://localhost:3050/api/proveedores";
    private final HttpClientHelper httpClientHelper;
    private final ObjectMapper objectMapper;
    public ProveedorService() {
        this.httpClientHelper = new HttpClientHelper();
        this.objectMapper = new ObjectMapper();
    }
    public String  agregarProveedor(Proveedor proveedor) throws IOException {
        String json = objectMapper.writeValueAsString(proveedor);
        System.out.println(json);
        String response = httpClientHelper.sendRequest(BASE_URL + "/create", "POST", json);
        return response;



    }
    public Proveedor[] getAllProveedores() throws IOException {
        String response = httpClientHelper.sendRequest(BASE_URL + "/listar", "GET", null);
        System.out.println("Lista de proveedores" + response);

        try {

            return objectMapper.readValue(response, Proveedor[].class);
        } catch (IOException e) {
            throw new RuntimeException("Error al deserializar la respuesta: " + e.getMessage());
        }
    }

}
