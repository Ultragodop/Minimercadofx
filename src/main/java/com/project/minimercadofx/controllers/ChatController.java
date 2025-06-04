package com.project.minimercadofx.controllers;

import com.project.minimercadofx.models.chat.User;
import com.project.minimercadofx.services.WebSocketService;
import com.project.minimercadofx.services.chat.EncryptionUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatController {

    @FXML
    private TextFlow mensajesFlow;

    @FXML
    private TextArea TextArea;

    private WebSocketService webSocketService;

    private final Map<String, Color> usuarioColores = new HashMap<>();
    private final List<Color> coloresDisponibles = Arrays.asList(
            Color.BLUE, Color.GREEN, Color.ORANGE, Color.PURPLE, Color.BROWN, Color.DARKCYAN, Color.MAGENTA
    );
    private int colorIndex = 0;

    private Color obtenerColorUsuario(String usuario) {
        return usuarioColores.computeIfAbsent(usuario, u -> {
            Color color = coloresDisponibles.get(colorIndex % coloresDisponibles.size());
            colorIndex++;
            return color;
        });
    }

    @FXML
    public void initialize() {
        webSocketService = new WebSocketService(User.getNombre());

        webSocketService.conectar("ws://localhost:3050/chat", (usuario, mensaje) -> {
            Platform.runLater(() -> {
                Color color = obtenerColorUsuario(usuario);
                String msjdecrypt = EncryptionUtils.decrypt(mensaje);

                // Nombre en negrita y color asignado
                Text nombre = new Text(usuario + ": ");
                nombre.setFill(color);
                nombre.setStyle("-fx-font-weight: bold;");

                // Contenido del mensaje
                Text contenido = new Text(msjdecrypt);
                contenido.setFill(Color.BLACK);

                // Crear un TextFlow que contenga nombre + contenido
                TextFlow bubbleFlow = new TextFlow(nombre, contenido);
                bubbleFlow.getStyleClass().addAll("bubble", "received");
                bubbleFlow.setMaxWidth(250); // máximo ancho para que el texto se ajuste en varias líneas

                // Envolver en HBox alineado a la izquierda
                HBox contenedor = new HBox(bubbleFlow);
                contenedor.setAlignment(Pos.CENTER_LEFT);
                contenedor.setPadding(new Insets(2, 0, 2, 50)); // margen derecho

                mensajesFlow.getChildren().add(contenedor);
            });
        });
    }

    @FXML
    public void enviarMensaje() {
        String texto = TextArea.getText().trim();
        if (!texto.isEmpty()) {
            // Mostrar nuestro propio mensaje como burbuja “sent”
            Text contenido = new Text(texto);
            contenido.setFill(Color.BLACK);

            TextFlow bubbleFlow = new TextFlow(contenido);
            bubbleFlow.getStyleClass().addAll("bubble", "sent");
            bubbleFlow.setMaxWidth(250);

            HBox contenedor = new HBox(bubbleFlow);
            contenedor.setAlignment(Pos.CENTER_RIGHT);
            contenedor.setPadding(new Insets(2, 50, 2, 0)); // margen izquierdo

            mensajesFlow.getChildren().add(contenedor);

            // Enviar el texto cifrado al servidor
            String cifrado = EncryptionUtils.encrypt(texto);
            webSocketService.enviarMensaje(cifrado);

            TextArea.clear(); // Limpiar el campo luego de enviar
        }
    }

    public void cerrarChat() {
        webSocketService.cerrarConexion();
    }
}
