package com.project.minimercadofx.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.minimercadofx.MinimercadoApplication;
import com.project.minimercadofx.models.bussines.Proveedor;
import com.project.minimercadofx.services.AuthService;
import com.project.minimercadofx.services.ProveedorService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class ProveedorController {
    @FXML
    private Button cerrarsesion;
    @FXML
    private TextField nombre, telefono, direccion, email, activo;
    @FXML
    private Button agregar;
    public AuthService authService = new AuthService();
    public ProveedorService proveedorService = new ProveedorService();

    @FXML
    public void initialize() {
        nombre.textProperty().addListener((obs, oldVal, newVal) -> validateField(nombre));
        telefono.textProperty().addListener((obs, oldVal, newVal) -> validateField(telefono));
        direccion.textProperty().addListener((obs, oldVal, newVal) -> validateField(direccion));
        email.textProperty().addListener((obs, oldVal, newVal) -> validateField(email));
        activo.textProperty().addListener((obs, oldVal, newVal) -> validateField(activo));
        agregar.setOnAction(event -> handleAgregar());
        cerrarsesion.setOnAction(event -> handleCerrarSesion());


    }

    private void handleCerrarSesion() {
        try {
            String response = authService.logout();
            if (response.equals("Success")) {
                FXMLLoader fxmlLoader = new FXMLLoader(MinimercadoApplication.class.getResource("login.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                Stage stage = (Stage) cerrarsesion.getScene().getWindow();
                stage.setScene(scene);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleAgregar() {

        if (validateField(nombre) && validateField(telefono) && validateField(direccion) && validateField(email) && validateField(activo)) {
            Proveedor proveedor = new Proveedor();
            proveedor.setNombre(nombre.getText());
            proveedor.setTelefono(telefono.getText());
            proveedor.setDireccion(direccion.getText());
            proveedor.setEmail(email.getText());
            proveedor.setActivo(Boolean.parseBoolean(activo.getText()));


            try {
                proveedorService.agregarProveedor(proveedor);

                nombre.clear();
                telefono.clear();
                direccion.clear();
                email.clear();
                activo.clear();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean validateField(TextField field) {





        if (field.getText().isEmpty()) {
            field.setStyle("-fx-border-color: red;");
            return false;
        } else {
            field.setStyle("-fx-border-color: green;");
            return true;
        }


    }
}
