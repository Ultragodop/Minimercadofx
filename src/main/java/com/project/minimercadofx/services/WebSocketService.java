package com.project.minimercadofx.services;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.minimercadofx.models.chat.ChatMessage;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.function.BiConsumer;

public class WebSocketService {

    private WebSocketClient client;
    private final ObjectMapper mapper = new ObjectMapper();
    private final String usuario;
    private final String sala;
    private BiConsumer<String, String> onMensajeRecibido;
    private ChatMessage chatMessage;

    public WebSocketService(String usuario, String sala) {
        this.usuario = usuario;
        this.sala = sala;
    }

    public void conectar(String uriConsala, BiConsumer<String, String> onMensajeRecibidoCallback) {
        this.onMensajeRecibido = onMensajeRecibidoCallback;

        try {
            client = new WebSocketClient(new URI(uriConsala)) {
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

    public void enviarMensaje(String contenido) {
        if (client != null && client.isOpen()) {
            try {
                ChatMessage mensaje = new ChatMessage(usuario, contenido);
                String json = mapper.writeValueAsString(mensaje);
                client.send(json);
            } catch (Exception e) {
                System.err.println("✗ Error al enviar mensaje: " + e.getMessage());
            }
        } else {
            System.err.println("✗ No hay conexión al servidor en sala: " + sala);
        }
    }

    public void cerrarConexion() {
        if (client != null) {
            client.close();
        }
    }
}
