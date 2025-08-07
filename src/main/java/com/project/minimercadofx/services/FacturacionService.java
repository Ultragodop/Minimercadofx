package com.project.minimercadofx.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.minimercadofx.services.http.Session;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class FacturacionService {
    private static final String BASE_URL = "http://localhost:3050/api/facturacion/";
    private final HttpClient httpClientHelper;
    private final ObjectMapper objectMapper;

    public FacturacionService() {
        this.httpClientHelper = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }
    public void generarFacturaTarjeta(String transactionId) {
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "ticketTarjeta/" + transactionId))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + Session.getToken())
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
            String response = httpClientHelper.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
            if (response == null || response.isEmpty()) {
                System.out.println("No se pudo generar la factura para la transacción " + transactionId);
                return;
            }
            if (response.contains("Error")) {
                System.out.println("Error al generar la factura: " + response);
                return;
            }
            System.out.println("Factura generada con éxito para la transacción " + transactionId);
        } catch (Exception e) {
            System.out.println("Error al generar la factura: " + e.getMessage());
        }
    }
    public void generarFactura(Integer idVenta) {
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "ticket/" + idVenta))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + Session.getToken())
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
            String response = httpClientHelper.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();
            if (response == null || response.isEmpty()) {
                System.out.println("No se pudo generar la factura para la venta " + idVenta);
                return;
            }
            if (response.contains("Error")) {
                System.out.println("Error al generar la factura: " + response);
                return;
            }
            System.out.println("Factura generada con éxito para la venta " + idVenta);
        } catch (Exception e) {
            System.out.println("Error al generar la factura: " + e.getMessage());
        }
    }
}
