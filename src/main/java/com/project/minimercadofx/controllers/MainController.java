package com.project.minimercadofx.controllers;

import com.project.minimercadofx.MinimercadoApplication;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class MainController {
    @FXML
    private VBox content;
    
    @FXML
    private VBox sidebar;
    
    @FXML
    private StackPane contentPane;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    private Label successLabel;

    @FXML
    private Button btnInventario;
    
    @FXML
    private Button btnVentas;
    
    @FXML
    private Button btnCompras;
    
    @FXML
    private Button btnGastos;
    
    @FXML
    private Button btnEmpleados;
    
    @FXML
    private Button btnConfiguracion;
    
    @FXML
    private Button btnCerrarSesion;

    @FXML
    private void initialize() {
        // Aplicar animación de entrada
        fadeInNode(sidebar, 1.0);
        fadeInNode(content, 1.0);
        
        // Configurar listeners para los botones
        setupButtonListeners();
        
        // Ocultar mensajes al inicio
        hideMessages();
    }

    private void setupButtonListeners() {
        btnInventario.setOnAction(event -> handleInventario());
        btnVentas.setOnAction(event -> handleVentas());
        btnCompras.setOnAction(event -> handleCompras());
        btnGastos.setOnAction(event -> handleGastos());
        btnEmpleados.setOnAction(event -> handleEmpleados());
        btnConfiguracion.setOnAction(event -> handleConfiguracion());
        btnCerrarSesion.setOnAction(event -> handleCerrarSesion());
        
        // Configurar efectos hover para todos los botones
        setupHoverEffect(btnInventario);
        setupHoverEffect(btnVentas);
        setupHoverEffect(btnCompras);
        setupHoverEffect(btnGastos);
        setupHoverEffect(btnEmpleados);
        setupHoverEffect(btnConfiguracion);
        setupHoverEffect(btnCerrarSesion);
    }

    private void setupHoverEffect(Button button) {
        button.setOnMouseEntered(e -> {
            if (!button.getStyle().contains("-fx-background-color")) {
                button.setStyle(button.getStyle() + "-fx-background-color: rgba(33,147,176,0.1);");
            }
        });
        
        button.setOnMouseExited(e -> {
            button.setStyle(button.getStyle().replace("-fx-background-color: rgba(33,147,176,0.1);", ""));
        });
    }

    private void handleInventario() {
        loadModule("Inventario", "views/inventario.fxml");
    }

    private void handleVentas() {
        loadModule("Ventas", "views/ventas.fxml");
    }

    private void handleCompras() {
        loadModule("Compras", "views/compras.fxml");
    }

    private void handleGastos() {
        loadModule("Gastos", "views/gastos.fxml");
    }

    private void handleEmpleados() {
        loadModule("Empleados", "views/empleados.fxml");
    }

    private void handleConfiguracion() {
        loadModule("Configuración", "views/configuracion.fxml");
    }

    private void handleCerrarSesion() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MinimercadoApplication.class.getResource("login.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) btnCerrarSesion.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showError(null, "Error al cargar la pantalla de login");
        }
    }

    private void loadModule(String moduleName, String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(MinimercadoApplication.class.getResource(fxmlFile));
            Node moduleContent = loader.load();
            
            // Animación de transición
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.3), contentPane);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(e -> {
                contentPane.getChildren().setAll(moduleContent);
                
                FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.3), contentPane);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });
            fadeOut.play();
            
            showSuccess("Módulo " + moduleName + " cargado exitosamente");
        } catch (IOException e) {
            e.printStackTrace();
            showError(null, "Error al cargar el módulo " + moduleName);
        }
    }

    private void hideMessages() {
        errorLabel.setVisible(false);
        successLabel.setVisible(false);
    }

    private void showError(Control field, String message) {
        if (field != null) {
            field.setStyle("-fx-border-color: #ff0000; -fx-border-width: 0 0 2 0;");
        }
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        successLabel.setVisible(false);
        
        // Animación de shake para el campo con error si existe
        if (field != null) {
            shakeNode(field);
        }
    }

    private void showSuccess(String message) {
        successLabel.setText(message);
        successLabel.setVisible(true);
        errorLabel.setVisible(false);
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