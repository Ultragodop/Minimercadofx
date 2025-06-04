package com.project.minimercadofx.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.minimercadofx.models.bussines.Proveedor;
import com.project.minimercadofx.services.ProveedorService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;

public class ProveedorController {
    @FXML
    private TextField nombre, telefono, direccion, email, activo;
    @FXML
    private Button agregar;



    @FXML
    public void initialize() {
        nombre.textProperty().addListener((obs, oldVal, newVal) -> validateField(nombre));
        telefono.textProperty().addListener((obs, oldVal, newVal) -> validateField(telefono));
        direccion.textProperty().addListener((obs, oldVal, newVal) -> validateField(direccion));
        email.textProperty().addListener((obs, oldVal, newVal) -> validateField(email));
        activo.textProperty().addListener((obs, oldVal, newVal) -> validateField(activo));
        agregar.setOnAction(event -> handleAgregar());

    }

    private void handleAgregar() {
        ProveedorService proveedorService = new ProveedorService();
        if (validateField(nombre) && validateField(telefono) && validateField(direccion) && validateField(email) && validateField(activo)) {
            Proveedor proveedor = new Proveedor();
            proveedor.setNombre(nombre.getText());
            proveedor.setTelefono(telefono.getText());
            proveedor.setDireccion(direccion.getText());
            proveedor.setEmail(email.getText());
            proveedor.setActivo(Boolean.parseBoolean(activo.getText()));

            try {
                proveedorService.agregarProveedor(proveedor);
                // Clear fields after successful addition
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
        String text = field.getText();
        if (text == null || text.trim().isEmpty()) {
            field.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            return false;
        } else {
            field.setStyle("-fx-border-color: green; -fx-border-width: 2px;");
            return true;
        }
    }
}
