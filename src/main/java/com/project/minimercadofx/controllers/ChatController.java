package com.project.minimercadofx.controllers;

import com.project.minimercadofx.models.chat.User;
import com.project.minimercadofx.services.WebSocketService;
import com.project.minimercadofx.services.chat.EncryptionUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextArea;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatController {

    @FXML
    private ScrollPane scrollPane;           // <-- Inyección del ScrollPane
    @FXML
    private VBox mensajesContainer;          // <-- Inyección del contenedor de mensajes (VBox)
    @FXML
    private TextArea TextArea;               // <-- Inyección del TextArea de entrada

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
        // Creamos la conexión WebSocket
        webSocketService = new WebSocketService(User.getNombre());

        webSocketService.conectar("ws://localhost:3050/chat", (usuario, mensaje) -> {
            Platform.runLater(() -> {
                Color color = obtenerColorUsuario(usuario);
                String msjdecrypt = EncryptionUtils.decrypt(mensaje);

                // Texto del nombre de usuario en negrita y color asignado
                Text nombre = new Text(usuario + ": ");
                nombre.setFill(color);
                nombre.setStyle("-fx-font-weight: bold;");

                // Texto del contenido del mensaje
                Text texto = new Text(msjdecrypt);
                texto.setFill(Color.BLACK);

                // Creamos el TextFlow que contendrá nombre + contenido
                TextFlow bubbleFlow = new TextFlow(nombre, texto);
                bubbleFlow.getStyleClass().addAll("bubble", "received");
                bubbleFlow.setMaxWidth(250); // ancho máximo para hacer wrap automático

                // Envolvemos en HBox alineado a la izquierda
                HBox contenedor = new HBox(bubbleFlow);
                contenedor.setAlignment(Pos.CENTER_LEFT);
                contenedor.setPadding(new Insets(2, 50, 2, 0)); // margen derecho

                mensajesContainer.getChildren().add(contenedor);

                // Forzamos el ScrollPane a bajar hasta el final
                scrollToBottom();
            });
        });
    }

    @FXML
    public void enviarMensaje() {
        String texto = TextArea.getText().trim();
        if (!texto.isEmpty()) {
            // Primero mostramos la burbuja “sent” en el propio cliente
            Text contenido = new Text(texto);
            contenido.setFill(Color.BLACK);

            TextFlow bubbleFlow = new TextFlow(contenido);
            bubbleFlow.getStyleClass().addAll("bubble", "sent");
            bubbleFlow.setMaxWidth(250);

            HBox contenedor = new HBox(bubbleFlow);
            contenedor.setAlignment(Pos.CENTER_RIGHT);
            contenedor.setPadding(new Insets(2, 0, 2, 50)); // margen izquierdo

            mensajesContainer.getChildren().add(contenedor);
            scrollToBottom();

            // Ahora enviamos el texto cifrado al servidor
            String cifrado = EncryptionUtils.encrypt(texto);
            webSocketService.enviarMensaje(cifrado);

            TextArea.clear();
        }
    }

    /**
     * Método auxiliar para forzar que el ScrollPane baje hasta el final
     * (Vvalue = 1.0 significa scroll completamente abajo).
     */
    private void scrollToBottom() {
        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }

    public void cerrarChat() {
        webSocketService.cerrarConexion();
    }
}
