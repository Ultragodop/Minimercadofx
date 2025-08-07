package com.project.minimercadofx.controllers;
import com.project.minimercadofx.MinimercadoApplication;
import com.project.minimercadofx.services.AuthService;
import com.project.minimercadofx.services.http.User;
import com.project.minimercadofx.services.WebSocketService;
import com.project.minimercadofx.services.chat.EncryptionUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;



import java.io.IOException;
import java.util.*;

public class ChatController {


    @FXML private ComboBox<String> salaComboBox;
    @FXML private ScrollPane scrollPane;
    @FXML private VBox mensajesContainer;
    @FXML private Button cerrarSesion;
    @FXML private TextArea TextArea;
    private List<String> listaSalas= new ArrayList<>();
    private final WebSocketService webSocketService= new WebSocketService();
    private final AuthService authService= new AuthService();
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
                new Thread(() -> {
                    listaSalas = webSocketService.obtenerSalas();

                    if (listaSalas == null || !listaSalas.isEmpty()) {
                        Platform.runLater(() -> {
                            salaComboBox.getItems().clear();
                            salaComboBox.getItems().addAll(listaSalas);


                            salaActual = listaSalas.get(0);
                            salaComboBox.setValue(salaActual);
                            conectarASala(salaActual);

                        });
                    }

                    else {
                        Platform.runLater(() -> {
                            mostrarError("Error al obtener salas", "No se encontraron salas disponibles.");

                            salaComboBox.getItems().clear();


                        });
                    }
                }).start();


                    cerrarSesion.setOnAction(event -> {
                        cerrarChat();
                        authService.logout();
                        FXMLLoader fxmlLoader = new FXMLLoader(MinimercadoApplication.class.getResource("login.fxml"));
                        Scene scene;
                        try {
                            scene = new Scene(fxmlLoader.load());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        Stage stage = (Stage) cerrarSesion.getScene().getWindow();
                        stage.setScene(scene);
                    });

                    salaComboBox.setOnAction(event -> {
                        String nuevaSala = salaComboBox.getValue();
                        if (nuevaSala != null && !nuevaSala.equals(salaActual)) {
                            cambiarSala(nuevaSala);
                        }
                    });
                    TextArea.setOnKeyPressed(event -> {
                        if (event.getCode() == KeyCode.ENTER && !event.isShiftDown()) {
                            event.consume();
                            enviarMensaje();
                        }
                    });
                    mensajesContainer.heightProperty().addListener((obs, oldVal, newVal) -> {
                        if (scrollPane.getVvalue() == 1.0 || scrollPane.getVvalue() == scrollPane.getVmax()) {
                            Platform.runLater(() -> scrollPane.setVvalue(1.0));
                        }
                    });
                    Platform.runLater(() -> {
                        Stage stage = (Stage) scrollPane.getScene().getWindow();
                        stage.setOnCloseRequest(evt -> cerrarChat());
                    });

    }
    private void conectarASala(String sala) {
        webSocketService.conectar(sala, (usuario, mensaje) -> {

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

        salaComboBox.setValue(nuevaSala);
        System.out.println("Cambiando a la sala: " + nuevaSala);
        salaActual = nuevaSala;
        System.out.println("Sala actual: " + salaActual);
        conectarASala(nuevaSala);
        System.out.println("Conectado a la sala: " + salaActual);
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
             webSocketService.enviarMensaje(cifrado,salaActual);
            TextArea.clear();
            Platform.runLater(() -> scrollPane.setVvalue(2.0));
        }
    }
    private void scrollToBottom() {
        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }
    public void cerrarChat() {
        Thread i = new Thread(() -> {
            try {
                webSocketService.cerrarConexion();

                System.out.println("Conexión cerrada y sesión finalizada correctamente.");
            } catch (Exception e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        });
        i.start();
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
                new Thread(() ->{ try{System.out.println("Si");} catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

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
                            if (usuariosAutorizados.isEmpty()) {
                                Platform.runLater(() -> {
                                    mostrarError("Error al crear sala", "Debe ingresar al menos un ID de usuario válido.");
                                });
                                return;
                            }
                            String response = webSocketService.crearSala(nombre, usuariosAutorizados);
                            if (response.equals("success")) {
                                Platform.runLater(() -> {
                                    salaComboBox.getItems().add(nombre);
                                    salaComboBox.setValue(nombre);
                                });
                            } else {
                                Platform.runLater(() -> {
                                    mostrarError("No se pudo crear la sala en el servidor.",
                                            "Código HTTP: " + response);
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




    private void mostrarError(String header, String content) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText(header);
        alerta.setContentText(content);
        alerta.showAndWait();
    }
}

