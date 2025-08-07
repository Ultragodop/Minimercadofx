package com.project.minimercadofx.controllers;

import com.project.minimercadofx.models.bussines.CategoriaDTO;
import com.project.minimercadofx.services.CategoriaService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.Date;

public class CategoriaController {
    @FXML private TextField nombreField;
    @FXML private TextField descripcionField;
    @FXML private CheckBox activoCheck;
    
    @FXML private TableView<CategoriaDTO> categoriasTable;
    @FXML private TableColumn<CategoriaDTO, Integer> idColumn;
    @FXML private TableColumn<CategoriaDTO, String> nombreColumn;
    @FXML private TableColumn<CategoriaDTO, String> descripcionColumn;
    @FXML private TableColumn<CategoriaDTO, Boolean> activoColumn;
    @FXML private TableColumn<CategoriaDTO, Date> fechaCreacionColumn;

    private CategoriaService categoriaService;
    private ObservableList<CategoriaDTO> categorias;

    @FXML
    public void initialize() {
        categoriaService = new CategoriaService();
        categorias = FXCollections.observableArrayList();
        
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        descripcionColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        activoColumn.setCellValueFactory(new PropertyValueFactory<>("activo"));
        fechaCreacionColumn.setCellValueFactory(new PropertyValueFactory<>("fechaCreacion"));
        
        categoriasTable.setItems(categorias);
        cargarCategoriasInicial();
    }

    private void cargarCategoriasInicial() {
        try {
            CategoriaDTO[] categoriasArray = categoriaService.getAllCategorias();
            categorias.addAll(categoriasArray);
        } catch (IOException | InterruptedException e) {
            mostrarError("Error al cargar categorías", e.getMessage());
        }
    }

    @FXML
    private void onAnadirCategoria() {
        if (!validarCampos()) return;

        try {
            CategoriaDTO categoria = new CategoriaDTO();
            categoria.setNombre(nombreField.getText());
            categoria.setDescripcion(descripcionField.getText());
            categoria.setActivo(activoCheck.isSelected());
            categoria.setFechaCreacion(new Date());
            
            CategoriaDTO categoriaCreada = categoriaService.anadirCategoria(categoria);
            categorias.add(categoriaCreada);
            onLimpiarFormulario();
            mostrarExito("Categoría añadida exitosamente");
        } catch (Exception e) {
            mostrarError("Error al añadir categoría", e.getMessage());
        }
    }

    private boolean validarCampos() {
        if (nombreField.getText().trim().isEmpty()) {
            mostrarError("Error de validación", "El nombre es obligatorio");
            return false;
        }
        if (descripcionField.getText().trim().isEmpty()) {
            mostrarError("Error de validación", "La descripción es obligatoria");
            return false;
        }
        return true;
    }

    private void mostrarExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void onLimpiarFormulario() {
        nombreField.clear();
        descripcionField.clear();
        activoCheck.setSelected(false);
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
