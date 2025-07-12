package com.project.minimercadofx.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.minimercadofx.models.bussines.DetallesVenta;
import com.project.minimercadofx.models.bussines.DetallesVentaRequest;
import com.project.minimercadofx.models.bussines.VentaRequest;

import com.project.minimercadofx.services.http.Session;
import com.project.minimercadofx.services.http.User;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;


public class VentaService {
    private static final String BASE_URL = "http://localhost:3050/api/ventas";
    private final HttpClient httpClientHelper;
    private final ObjectMapper objectMapper;
    DetallesVentaRequest detallesVentaRequest;
    VentaRequest ventaRequest= new VentaRequest();
    public VentaService() {
        this.httpClientHelper = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public String realizarVentaEfectivo(List<DetallesVentaRequest> detallesVenta) throws IOException, InterruptedException {
        URI urlefectivo= URI.create(BASE_URL + "/efectivo");

        ventaRequest.setIdUsuario(User.getId());
        ventaRequest.setDetalleVentas(detallesVenta);

        String jsonVentaEfectivo = objectMapper.writeValueAsString(ventaRequest);

        System.out.println("JSON de venta: " + jsonVentaEfectivo);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(urlefectivo)
                .header("Content-Type", "application/json")
                .header("Authorization" , "Bearer " + Session.getToken())
                .POST(HttpRequest.BodyPublishers.ofString(jsonVentaEfectivo))
                .build();
        HttpResponse<String> response = httpClientHelper.send(request, HttpResponse.BodyHandlers.ofString());
        if (!(response.statusCode() == 200)) {
           System.out.println("Error al realizar la venta: " + response.body());
            return "error";
        }


      return "success";
    }
    public String realizarVentaTarjeta(List<DetallesVentaRequest> detallesVenta) throws IOException, InterruptedException {
        URI urltarjeta = URI.create(BASE_URL + "/tarjeta");
        ventaRequest.setIdUsuario(User.getId());
        ventaRequest.setDetalleVentas(detallesVenta);
        String jsonVentaTarjeta = objectMapper.writeValueAsString(ventaRequest);
        System.out.println("JSON de venta: " + jsonVentaTarjeta);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(urltarjeta)
                .header("Content-Type", "application/json")
                .header("Authorization" , "Bearer " + Session.getToken())
                .POST(HttpRequest.BodyPublishers.ofString(jsonVentaTarjeta))
                .build();
        HttpResponse<String> response = httpClientHelper.send(request, HttpResponse.BodyHandlers.ofString());
        if (!(response.statusCode() == 200)) {
            System.out.println("Error al realizar la venta: " + response.body());
            return "error";
        }


        return "success";
    }
   public void callback(String transactionId) {
       String url = "http://localhost:3050/api/payments/callback" + transactionId;
       HttpRequest request = HttpRequest.newBuilder()
               .uri(URI.create(url))
               .header("Content-Type", "application/json")
               .header("Authorization", "Bearer " + Session.getToken())
               .GET()
               .build();
       try {
           HttpResponse<String> response = httpClientHelper.send(request, HttpResponse.BodyHandlers.ofString());
           if (response.statusCode() == 200) {
               System.out.println("Callback procesado correctamente: " + response.body());
           } else {
               System.out.println("Error en el callback: " + response.body());
           }
       } catch (IOException | InterruptedException e) {
           e.printStackTrace();
       }
       String s = "Callback realizado con éxito";

           }
       }

