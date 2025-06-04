package com.project.minimercadofx.controllers;

import com.project.minimercadofx.models.chat.User;
import com.project.minimercadofx.services.WebSocketService;
import com.project.minimercadofx.services.chat.EncryptionUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
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
                Text nombre = new Text(usuario + ": ");
                nombre.setFill(color);
                nombre.setStyle("-fx-font-weight: bold");

                Text contenido = new Text(msjdecrypt + "\n");
                contenido.setFill(Color.BLACK);

                mensajesFlow.getChildren().addAll(nombre, contenido);
            });
        });
    }

    @FXML
    public void enviarMensaje() {
        String texto = TextArea.getText().trim();
        if (!texto.isEmpty()) {
            String cifrado= EncryptionUtils.encrypt(texto);
            webSocketService.enviarMensaje(cifrado);
            TextArea.clear(); // Limpiar el campo luego de enviar
        }
    }

    public void cerrarChat() {
        webSocketService.cerrarConexion();
    }
}
