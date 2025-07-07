package com.project.minimercadofx.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.minimercadofx.models.chat.CrearSalaRequest;
import com.project.minimercadofx.models.chat.User;
import com.project.minimercadofx.services.WebSocketService;
import com.project.minimercadofx.services.chat.EncryptionUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ChatController {

    String token= "Sexo";
    @FXML private ComboBox<String> salaComboBox;
    @FXML private ScrollPane scrollPane;
    @FXML private VBox mensajesContainer;
    @FXML private TextArea TextArea;

    private WebSocketService webSocketService;
    private final Map<String, Color> usuarioColores = new HashMap<>();
    private final List<Color> coloresDisponibles = Arrays.asList(
            Color.BLUE, Color.GREEN, Color.ORANGE, Color.PURPLE,
            Color.BROWN, Color.DARKCYAN, Color.MAGENTA
    );
    private int colorIndex = 0;
    private String salaActual;

    private Color obtenerColorUsuario(String usuario) {
        return usuarioColores.computeIfAbsent(usuario, u -> {
            Color color = coloresDisponibles.get(colorIndex % coloresDisponibles.size());
            colorIndex++;
            return color;
        });
    }

    @FXML
    public void initialize() {
        // 1) Antes de cualquier otra cosa, llamamos al backend para listar salas
        new Thread(() -> {
            try {
               String token= "Sexo";
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI("http://localhost:3050/api/salas/todas"))
                        .header("Authorization", "Bearer " + token)
                        .GET()
                        .build();

                HttpResponse<String> response =
                        client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    // Parseamos JSON Array de strings: ["general","programadores",...]
                    ObjectMapper mapper = new ObjectMapper();
                    List<String> listaSalas = mapper.readValue(
                            response.body(), new TypeReference<>() {
                            }
                    );

                    Platform.runLater(() -> {
                        salaComboBox.getItems().clear();
                        salaComboBox.getItems().addAll(listaSalas);

                        // Si existe "general", lo seleccionamos; si no, tomamos el primero
                        if (listaSalas.contains("general")) {
                            salaComboBox.setValue("general");
                        } else {
                            salaComboBox.setValue(listaSalas.get(0));
                        }
                        // Guardamos cuál es la sala actual
                        salaActual = salaComboBox.getValue();
                        conectarASala(salaActual);
                    });
                } else {
                    // Si no devuelve 200, mostramos alerta y ponemos "general" como fallback
                    Platform.runLater(() -> {
                        mostrarError("Error al obtener salas", "Código HTTP: " + response.statusCode());
                        // opción de fallback: crear “general” localmente
                        salaComboBox.getItems().clear();
                        salaComboBox.getItems().add("general");
                        salaComboBox.setValue("general");
                        salaActual = "general";
                        conectarASala(salaActual);
                    });
                }

            } catch (Exception e) {
                Platform.runLater(() -> {
                    mostrarError("Excepción al leer salas", e.getMessage());
                    salaComboBox.getItems().clear();
                    salaComboBox.getItems().add("general");
                    salaComboBox.setValue("general");
                    salaActual = "general";
                    conectarASala(salaActual);
                });
            }
        }).start();

        // 2) Listener para cambio de sala
        salaComboBox.setOnAction(event -> {
            String nuevaSala = salaComboBox.getValue();
            if (nuevaSala != null && !nuevaSala.equals(salaActual)) {
                cambiarSala(nuevaSala);
            }
        });

        // 3) Texto + ENTER para enviar
        TextArea.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && !event.isShiftDown()) {
                event.consume();
                enviarMensaje();
            }
        });

        // 4) Auto-scroll
        mensajesContainer.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (scrollPane.getVvalue() == 1.0 || scrollPane.getVvalue() == scrollPane.getVmax()) {
                Platform.runLater(() -> scrollPane.setVvalue(1.0));
            }
        });

        // 5) Al cerrar la ventana, cerramos el WebSocket
        Platform.runLater(() -> {
            Stage stage = (Stage) scrollPane.getScene().getWindow();
            stage.setOnCloseRequest(evt -> cerrarChat());
        });
    }

    private void conectarASala(String sala) {
        webSocketService = new WebSocketService(User.getNombre(), sala);
        String uri = "ws://localhost:3050/chat/" + sala + "?token=" + token;

        webSocketService.conectar(uri, (usuario, mensaje) -> {
            if (usuario.equals(User.getNombre())) return;
            Platform.runLater(() -> {
                Color color = obtenerColorUsuario(usuario);
                String msjdecrypt = EncryptionUtils.decrypt(mensaje);

                Text nombre = new Text(usuario + ": ");
                nombre.setFill(color);
                nombre.setStyle("-fx-font-weight: bold;");

                Text texto = new Text(msjdecrypt);
                texto.setFill(Color.BLACK);

                TextFlow bubbleFlow = new TextFlow(nombre, texto);
                bubbleFlow.getStyleClass().addAll("bubble", "received");
                bubbleFlow.setMaxWidth(250);

                HBox contenedor = new HBox(bubbleFlow);
                contenedor.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                contenedor.setPadding(new Insets(2, 50, 2, 0));

                mensajesContainer.getChildren().add(contenedor);
                scrollToBottom();
            });
        });
    }

    private void cambiarSala(String nuevaSala) {
        cerrarChat();
        mensajesContainer.getChildren().clear();
        usuarioColores.clear();
        colorIndex = 0;
        salaActual = nuevaSala;
        conectarASala(salaActual);
    }

    @FXML
    public void enviarMensaje() {
        String texto = TextArea.getText().trim();
        if (!texto.isEmpty()) {
            Text contenido = new Text(texto);
            contenido.setFill(Color.BLACK);

            TextFlow bubbleFlow = new TextFlow(contenido);
            bubbleFlow.getStyleClass().addAll("bubble", "sent");
            bubbleFlow.setMaxWidth(250);

            HBox contenedor = new HBox(bubbleFlow);
            contenedor.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
            contenedor.setPadding(new Insets(2, 0, 2, 50));

            mensajesContainer.getChildren().add(contenedor);

            String cifrado = EncryptionUtils.encrypt(texto);
            webSocketService.enviarMensaje(cifrado);

            TextArea.clear();
            Platform.runLater(() -> scrollPane.setVvalue(2.0));
        }
    }

    private void scrollToBottom() {
        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }

    public void cerrarChat() {
        if (webSocketService != null) {
            webSocketService.cerrarConexion();
            webSocketService = null;
        }
    }

    @FXML
    public void crearNuevaSala() {
        TextInputDialog dialogNombre = new TextInputDialog();
        dialogNombre.setTitle("Crear nueva sala");
        dialogNombre.setHeaderText("Ingresa el nombre de la nueva sala:");
        dialogNombre.setContentText("Nombre de sala:");

        Optional<String> resultadoNombre = dialogNombre.showAndWait();
        resultadoNombre.ifPresent(rawNombre -> {
            String nombre = rawNombre.trim();
            if (!nombre.isEmpty() && !salaComboBox.getItems().contains(nombre)) {

                TextInputDialog dialogUsuarios = new TextInputDialog();
                dialogUsuarios.setTitle("Usuarios autorizados");
                dialogUsuarios.setHeaderText("Ingresa los IDs de usuarios autorizados separados por coma:");
                dialogUsuarios.setContentText("Ejemplo: 2,3,4");

                Optional<String> resultadoUsuarios = dialogUsuarios.showAndWait();
                resultadoUsuarios.ifPresent(inputUsuarios -> {

                    new Thread(() -> {
                        try {
                            List<Long> usuariosAutorizados = new ArrayList<>();
                            for (String parte : inputUsuarios.split(",")) {
                                try {
                                    Long id = Long.parseLong(parte.trim());
                                    usuariosAutorizados.add(id);
                                } catch (NumberFormatException ignored) {
                                }
                            }

                            String user =(User.getNombre());
                            System.out.println(user);
                            Long v= obtenerIdPorNombre(user);
                            System.out.println(v);

                            if (!usuariosAutorizados.contains(v)) {
                                usuariosAutorizados.add(v);
                            }


                            CrearSalaRequest requestBody = new CrearSalaRequest();
                            requestBody.setNombre(nombre);
                            requestBody.setCreadorId(v);

                            requestBody.setUsuariosAutorizadosIds(usuariosAutorizados);


                            String json = new ObjectMapper().writeValueAsString(requestBody);
                            System.out.println(json);
                         
                            HttpClient client = HttpClient.newHttpClient();
                            HttpRequest request = HttpRequest.newBuilder()
                                    .uri(new URI("http://localhost:3050/api/salas/crear"))
                                    .header("Content-Type", "application/json")
                                    .header("Authorization", "Bearer " + token)
                                    .POST(HttpRequest.BodyPublishers.ofString(json))
                                    .build();

                            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                            if (response.statusCode() == 200) {
                                Platform.runLater(() -> {
                                    salaComboBox.getItems().add(nombre);
                                    salaComboBox.setValue(nombre);
                                });
                            } else {
                                Platform.runLater(() -> {
                                    mostrarError("No se pudo crear la sala en el servidor.",
                                            "Código HTTP: " + response.statusCode());
                                });
                            }

                        } catch (Exception e) {
                            Platform.runLater(() -> {
                                mostrarError("Excepción al crear sala", e.getMessage());
                            });
                        }
                    }).start();

                });
            }
        });
    }
    public Long obtenerIdPorNombre(String nombreUsuario) {
        try {
            String url = "http://localhost:3050/api/salas/userid?nombre=" + URLEncoder.encode(nombreUsuario, StandardCharsets.UTF_8);
            System.out.println(url);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(request);
            System.out.println(response.body());

            if (response.statusCode() == 200) {
                String body = response.body();
                if (body == null || body.trim().equals("null")) {
                    return null; // No se encontró el usuario
                }

                return Long.parseLong(body.trim());
            } else if (response.statusCode() == 404) {
                return null;
            } else {
                System.err.println("Error al obtener ID de usuario: " + response.statusCode());
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



    private void mostrarError(String header, String content) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText(header);
        alerta.setContentText(content);
        alerta.showAndWait();
    }
}

