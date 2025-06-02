package com.project.minimercadofx.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.scene.Node;
import com.project.minimercadofx.services.AuthService;
import com.project.minimercadofx.models.LoginResponse;
import javafx.application.Platform;
import javafx.concurrent.Task;

public class LoginController {
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Button loginButton;
    
    @FXML
    private VBox formContainer;

    @FXML
    private Label errorLabel;

    @FXML
    private Label successLabel;
    
    private final AuthService authService;
    
    public LoginController() {
        this.authService = new AuthService();
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
                        // TODO: Guardar el token y redirigir a la página principal
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