package com.project.minimercadofx.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URI;

public class ChatController {

    @FXML
    private TextField mensajeInput;
    @FXML
    private TextArea mensajesArea;

    private org.java_websocket.client.WebSocketClient webSocketClient;

    @FXML
    public void initialize() {
        // Agregar listener para Enter en el campo de texto
        mensajeInput.setOnAction(event -> enviarMensaje());

        // Conectar al WebSocket
        conectarWebSocket();
    }

    private void conectarWebSocket() {
        try {
            URI serverUri = new URI("ws://localhost:3050/chat");


            Platform.runLater(() -> {
                mensajesArea.appendText("Conectando al servidor...\n");
            });

            webSocketClient = new org.java_websocket.client.WebSocketClient(serverUri) {
                @Override
                public void onOpen(org.java_websocket.handshake.ServerHandshake handshake) {
                    Platform.runLater(() -> {
                        mensajesArea.appendText("✓ Conectado al servidor exitosamente\n");
                    });
                }

                @Override
                public void onMessage(String message) {
                    Platform.runLater(() -> {
                        mensajesArea.appendText("Servidor: " + message + "\n");
                    });
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Platform.runLater(() -> {
                        mensajesArea.appendText("✗ Conexión cerrada: " + reason + "\n");
                    });
                }

                @Override
                public void onError(Exception ex) {
                    Platform.runLater(() -> {
                        mensajesArea.appendText("✗ Error: " + ex.getMessage() + "\n");
                    });
                }
            };

            webSocketClient.connect();

        } catch (Exception e) {
            Platform.runLater(() -> {
                mensajesArea.appendText("✗ Error de conexión: " + e.getMessage() + "\n");
            });
        }
    }

    @FXML
    public void enviarMensaje() {
        String mensaje = mensajeInput.getText().trim();

        if (mensaje.isEmpty()) {
            return;
        }

        if (webSocketClient == null || !webSocketClient.isOpen()) {
            Platform.runLater(() -> {
                mensajesArea.appendText("✗ No hay conexión al servidor\n");
            });
            return;
        }

        try {
            webSocketClient.send(mensaje);
            mensajeInput.clear();

            Platform.runLater(() -> {
                mensajesArea.appendText("Tú: " + mensaje + "\n");
            });

        } catch (Exception e) {
            Platform.runLater(() -> {
                mensajesArea.appendText("✗ Error al enviar mensaje: " + e.getMessage() + "\n");
            });
        }
    }

    // Método para limpiar recursos al cerrar
    public void desconectar() {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            webSocketClient.close();
        }
    }
}