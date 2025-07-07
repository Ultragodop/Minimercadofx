package com.project.minimercadofx.controllers;

import com.project.minimercadofx.MinimercadoApplication;
import com.project.minimercadofx.models.Auth.LoginResponse;

import com.project.minimercadofx.models.bussines.ProductoDTO;
import com.project.minimercadofx.services.AuthService;
import com.project.minimercadofx.services.ProductService;

import com.project.minimercadofx.services.http.Session;
import javafx.animation.FadeTransition;
import com.project.minimercadofx.models.chat.User;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;


public class LoginController {
    @FXML
    public Button registerButton;
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;
    @FXML
    private Button productButton;
    @FXML
    private VBox formContainer;

    @FXML
    private Label errorLabel;

    @FXML
    private Label successLabel;
    private final AuthService authService;
    private final ProductService productService;
    public LoginController() {
        this.authService = new AuthService();
        this.productService = new ProductService();
    }

    @FXML
    private void initialize() {
        // Aplicar animación de entrada
        fadeInNode(formContainer, 1.0);

        // Configurar listeners para validación
        usernameField.textProperty().addListener((obs, oldVal, newVal) -> {
            validateField(usernameField);
            hideMessages();
        });

        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            validateField(passwordField);
            hideMessages();
        });

        
        loginButton.setOnAction(event -> handleLogin());
        productButton.setOnAction(event -> handleGetProducts());
        registerButton.setOnAction(event -> handleRegister());
    }

    private void handleLogin() {
        if (validateForm()) {
            loginButton.setDisable(true);
            hideMessages();

            Task<LoginResponse> loginTask = new Task<>() {
                @Override
                protected LoginResponse call() throws Exception {
                    return authService.login(usernameField.getText(), passwordField.getText());
                }
            };

            loginTask.setOnSucceeded(e -> {
                LoginResponse response = loginTask.getValue();

                Platform.runLater(() -> {
                    if ("success".equals(response.getStatus())) {
                        showSuccess("¡Usted se ha logueado correctamente!");
                        String username= usernameField.getText();

                        User.setNombre(username);



                        try {
                            FXMLLoader fxmlLoader = new FXMLLoader(MinimercadoApplication.class.getResource("proveedor.fxml"));
                            Scene scene = new Scene(fxmlLoader.load());
                            Stage stage = (Stage) loginButton.getScene().getWindow();
                            stage.setScene(scene);
                            System.out.println("Usuario logueado: " + username + " con token: " + Session.getToken());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            showError(usernameField, "Error al cargar la pantalla principal");
                        }

                    } else {
                        showError(usernameField, response.getMessage());
                    }
                    loginButton.setDisable(false);
                });
            });

            loginTask.setOnFailed(e -> {
                Platform.runLater(() -> {
                    showError(usernameField, "Error al conectar con el servidor");
                    loginButton.setDisable(false);
                });
            });

            new Thread(loginTask).start();
        }
    }
    private void handleGetProducts() {
        Task<Void> productTask = new Task<>() {
            @Override
            protected Void call() throws Exception {


             ProductoDTO[] p =productService.getAllProducts();
             for (ProductoDTO producto : p) {
                 System.out.println("Producto: " + producto.getNombre() + ", Precio: " + producto.getPrecioVenta());

             }

                return null;
            }
        };

        productTask.setOnSucceeded(e -> {

            System.out.println("Productos obtenidos correctamente");

        });

        productTask.setOnFailed(e -> {
            // Aquí puedes manejar el error al obtener productos
            System.err.println("Error al obtener productos: " + productTask.getException().getMessage());
        });

        new Thread(productTask).start();

    }

    private void handleRegister() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MinimercadoApplication.class.getResource("register.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) registerButton.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showError(registerButton, "Error al cargar la pantalla de registro");
        }
    }

    private boolean validateForm() {
        boolean isValid = true;

        if (usernameField.getText().trim().isEmpty()) {
            showError(usernameField, "Por favor ingrese su usuario");
            isValid = false;
        }

        if (passwordField.getText().trim().isEmpty()) {
            showError(passwordField, "Por favor ingrese su contraseña");
            isValid = false;
        }

        return isValid;
    }

    private void validateField(TextField field) {
        if (!field.getText().trim().isEmpty()) {
            field.setStyle("-fx-border-color: #2193b0; -fx-border-width: 0 0 2 0;");
        } else {
            field.setStyle("-fx-border-color: transparent;");
        }
    }

    private void hideMessages() {
        errorLabel.setVisible(false);
        successLabel.setVisible(false);
    }

    private void showError(Control field, String message) {
        field.setStyle("-fx-border-color: #ff0000; -fx-border-width: 0 0 2 0;");
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        successLabel.setVisible(false);

        // Animación de shake para el campo con error
        shakeNode(field);
    }

    private void showSuccess(String message) {
        successLabel.setText(message);
        successLabel.setVisible(true);
        errorLabel.setVisible(false);
        usernameField.setStyle("-fx-border-color: #2ecc71; -fx-border-width: 0 0 2 0;");
        passwordField.setStyle("-fx-border-color: #2ecc71; -fx-border-width: 0 0 2 0;");
    }

    private void fadeInNode(Node node, double duration) {
        node.setOpacity(0);
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(duration), node);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        fadeTransition.play();
    }

    private void shakeNode(Node node) {
        double originalX = node.getTranslateX();
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(Duration.millis(0), new javafx.animation.KeyValue(node.translateXProperty(), originalX)),
            new javafx.animation.KeyFrame(Duration.millis(100), new javafx.animation.KeyValue(node.translateXProperty(), originalX + 10)),
            new javafx.animation.KeyFrame(Duration.millis(200), new javafx.animation.KeyValue(node.translateXProperty(), originalX - 10)),
            new javafx.animation.KeyFrame(Duration.millis(300), new javafx.animation.KeyValue(node.translateXProperty(), originalX + 5)),
            new javafx.animation.KeyFrame(Duration.millis(400), new javafx.animation.KeyValue(node.translateXProperty(), originalX))
        );
        timeline.play();
    }
}
