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
    private Button btnCerrarSesion;

    @FXML
    private void initialize() {
        fadeInNode(sidebar, 1.0);
        fadeInNode(content, 1.0);
        setupButtonListeners();
        hideMessages();
    }

    private void setupButtonListeners() {
        btnInventario.setOnAction(event -> loadModule("Inventario", "inventario.fxml"));
        btnVentas.setOnAction(event -> loadModule("Ventas", "venta.fxml"));
        btnCerrarSesion.setOnAction(event -> handleCerrarSesion());

        setupHoverEffect(btnInventario);
        setupHoverEffect(btnVentas);
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

    private void handleCerrarSesion() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MinimercadoApplication.class.getResource("login.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) btnCerrarSesion.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
            showError(null, "Error al cargar la pantalla de login");
        }
    }

    private void loadModule(String moduleName, String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(MinimercadoApplication.class.getResource(fxmlFile));
            Node moduleContent = loader.load();

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
        } catch (Exception e) {
            e.printStackTrace();
            showError(null, "Error al cargar el módulo " + moduleName);
        }
    }

    private void hideMessages() {
        errorLabel.setVisible(false);
        successLabel.setVisible(false);
    }

    private void showError(Control field, String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        successLabel.setVisible(false);
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
}