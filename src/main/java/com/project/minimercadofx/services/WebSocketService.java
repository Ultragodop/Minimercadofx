package com.project.minimercadofx.services;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.minimercadofx.models.chat.ChatMessage;
import com.project.minimercadofx.models.chat.CrearSalaRequest;
import com.project.minimercadofx.services.http.Session;
import com.project.minimercadofx.services.http.User;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.Map;
import java.util.function.BiConsumer;

public class WebSocketService {

    private WebSocketClient client;
    private final ObjectMapper mapper = new ObjectMapper();
    private BiConsumer<String, String> onMensajeRecibido;
    private ChatMessage chatMessage;


    public WebSocketService() {
    }

    public void conectar(String sala, BiConsumer<String, String> onMensajeRecibidoCallback) {
        this.onMensajeRecibido = onMensajeRecibidoCallback;
        String token = Session.getToken();
        String uri = "ws://localhost:3040/minimercado/chat/" + sala;
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);
        headers.put("User-Agent", "JavaFx-WebSocket-Client");

        try {
            client = new WebSocketClient(new URI(uri) , new HashMap<>(headers)) {

                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    System.out.println("✓ Conectado a la sala: " + sala);

                }
                @Override
                public void onMessage(String message) {
                    try {
                        chatMessage = mapper.readValue(message, ChatMessage.class);
                        if (onMensajeRecibido != null) {
                            onMensajeRecibido.accept(chatMessage.getUsuario(), chatMessage.getMensaje());
                        }
                    } catch (Exception e) {
                        System.err.println("✗ Error al leer mensaje: " + e.getMessage());
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("✗ Conexión cerrada (" + sala + "): " + reason);
                }

                @Override
                public void onError(Exception ex) {
                    System.err.println("✗ Error en sala (" + sala + "): " + ex.getMessage());
                }
            };

            client.connect();

        } catch (Exception e) {
            System.err.println("✗ Error al conectar en sala (" + sala + "): " + e.getMessage());
        }
    }

    public void enviarMensaje(String contenido, String sala) {
        if (client != null && client.isOpen()) {
            try {
                ChatMessage mensaje = new ChatMessage(User.getNombre(), contenido, sala);
                String json = mapper.writeValueAsString(mensaje);
                client.send(json);
            } catch (Exception e) {
                System.err.println("✗ Error al enviar mensaje: " + e.getMessage());
            }
        } else {
            assert client != null;
            System.err.println("✗ No hay conexión al servidor en sala: " + client.getURI());
        }
    }

    public void cerrarConexion() {
        if (client != null)
        {
            try {
                client.close(1000, "Cierre de conexión solicitado");
                System.out.println("✓ Conexión cerrada correctamente.");
            } catch (Exception e) {
                System.err.println("✗ Error al cerrar conexión: " + e.getMessage());
            }
        }
    }
    public List<String> obtenerSalas(){
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:3040/minimercado/api/salas/todas-por-permiso/" + User.getId()))
                    .header("Authorization", "Bearer " + Session.getToken())
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Respuesta del servidor: " + response.body());
            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), new TypeReference<>() {
                });
            } else {
                System.err.println("Respuesta HTTP inesperada: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    public String crearSala(String nombre, List<Long> usuariosAutorizados) throws IOException, InterruptedException, URISyntaxException {
        CrearSalaRequest requestBody = new CrearSalaRequest();
        requestBody.setNombre(nombre);
        requestBody.setCreadorId(User.getId());

        String token = Session.getToken();
        requestBody.setUsuariosAutorizadosIds(usuariosAutorizados);


        String json = new ObjectMapper().writeValueAsString(requestBody);
        System.out.println(json);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:3040/minimercado/api/salas/crear"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        if( response.statusCode() == 200) {;
            String salaId = response.body();
            System.out.println("Sala creada con éxito: " + salaId);
            return "success";
        } else {
            System.err.println("Error al crear sala: " + response.body());
            return "error"+ response.body();
        }
    }

}
