package com.project.minimercadofx.controllers;

import com.project.minimercadofx.models.bussines.*;
import com.project.minimercadofx.services.CategoriaService;
import com.project.minimercadofx.services.ProductService;
import com.project.minimercadofx.services.ProveedorService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.time.format.DateTimeFormatter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;


public class InventarioController {
    @FXML private TextField nombreField;
    @FXML private TextField descripcionField;
    @FXML private TextField precioCompraField;
    @FXML private TextField precioVentaField;
    @FXML private TextField stockMinimoField;
    @FXML private TextField stockActualField;
    @FXML private ComboBox<CategoriaDTO> categoriaComboBox;
    @FXML private ComboBox<Proveedor> proveedorComboBox;
    @FXML private DatePicker fechaVencimientoField;
    
    @FXML private TableView<ProductoDTO> productosTable; 
    @FXML private TableColumn<ProductoDTO, Integer> cbarras;  // new column for idProducto
    @FXML private TableColumn<ProductoDTO, String> nombre;
    @FXML private TableColumn<ProductoDTO, String> descripcion;
    @FXML private TableColumn<ProductoDTO, Double> precioCompra;
    @FXML private TableColumn<ProductoDTO, Double> precioVenta;
    @FXML private TableColumn<ProductoDTO, Integer> stockMinimo;
    @FXML private TableColumn<ProductoDTO, Integer> stockActual;
    @FXML private TableColumn<ProductoDTO, String> categoria;
    @FXML private TableColumn<ProductoDTO, String> proveedor;
    @FXML private TableColumn<ProductoDTO, Date> fechavencimiento;

    private ProductService productService;
    private CategoriaService categoriaService;
    private ProveedorService proveedorService;
    private ObservableList<ProductoDTO> productos;

    @FXML
    public void initialize() {
        productService = new ProductService();
        categoriaService = new CategoriaService();
        proveedorService = new ProveedorService();
        productos = FXCollections.observableArrayList();
        
        // Configurar columnas
        cbarras.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        descripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        precioCompra.setCellValueFactory(new PropertyValueFactory<>("precioCompra"));
        precioVenta.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        stockMinimo.setCellValueFactory(new PropertyValueFactory<>("stockMinimo"));
        stockActual.setCellValueFactory(new PropertyValueFactory<>("stockActual"));
        categoria.setCellValueFactory(new PropertyValueFactory<>("categoriaNombre"));
        proveedor.setCellValueFactory(new PropertyValueFactory<>("proveedorNombre"));
        fechavencimiento.setCellValueFactory(new PropertyValueFactory<>("fechaVencimiento"));
        
        productosTable.setItems(productos);
        cargarProductos();
        cargarComboBoxes();
    }
    
    private void cargarComboBoxes() {
        try {
            CategoriaDTO[] categorias = categoriaService.getAllCategorias();
            Proveedor[] proveedores = proveedorService.getAllProveedores();
            categoriaComboBox.setItems(FXCollections.observableArrayList(categorias));
            proveedorComboBox.setItems(FXCollections.observableArrayList(proveedores));
            
            categoriaComboBox.setConverter(new StringConverter<>() {
                @Override
                public String toString(CategoriaDTO categoria) {
                    return categoria != null ? categoria.getNombre() : "";
                }

                @Override
                public CategoriaDTO fromString(String string) {
                    return null;
                }
            });
            
            proveedorComboBox.setConverter(new StringConverter<>() {
                @Override
                public String toString(Proveedor proveedor) {
                    return proveedor != null ? proveedor.getNombre() : "";
                }

                @Override
                public Proveedor fromString(String string) {
                    return null;
                }
            });
        } catch (IOException | InterruptedException e) {
            mostrarError("Error", "No se pudieron cargar categorías o proveedores: " + e.getMessage());
        }
    }

    private void cargarProductos() {
        try {
            ProductoDTO[] productosArray = productService.getAllProducts();
            productos.clear();
            productos.addAll(productosArray);
        } catch (IOException | InterruptedException e) {
            mostrarError("Error al cargar productos", e.getMessage());
        }
    }

    @FXML
    private void onAgregarProducto() {
        try { if(categoriaComboBox.getValue() == null || proveedorComboBox.getValue() == null) {
            mostrarError("Error", "Debe seleccionar una categoría y un proveedor.");
            return;
        }
            ProductoRequest productoRequest = new ProductoRequest();
            productoRequest.setNombre(nombreField.getText());
            productoRequest.setDescripcion(descripcionField.getText());
            productoRequest.setPrecioCompra(Double.parseDouble(precioCompraField.getText()));
            productoRequest.setPrecioVenta(Double.parseDouble(precioVentaField.getText()));
            productoRequest.setStockMinimo(Integer.parseInt(stockMinimoField.getText()));
            productoRequest.setStockActual(Integer.parseInt(stockActualField.getText()));
            
           idCategoria categoriaId= new idCategoria(categoriaComboBox.getValue().getId());
            idProveedor idProveedor= new idProveedor(proveedorComboBox.getValue().getIdProveedor());
            productoRequest.setIdCategoria(categoriaId);
            Proveedor proveedorId= (proveedorComboBox.getValue());
            System.out.println("proveedorId: " + proveedorId.getIdProveedor());
            productoRequest.setIdProveedor(idProveedor);
            LocalDate localDate = fechaVencimientoField.getValue();
            String fechaVencimiento = DateTimeFormatter.ISO_INSTANT.format(
                    localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            productoRequest.setFechaVencimiento(fechaVencimiento);
            
            productoRequest.setActivo(true);
            
            ProductoDTO productoCreado = productService.createProduct(productoRequest);
            productos.add(productoCreado);
            onLimpiarFormulario();
        } catch (Exception e) {
            mostrarError("Error al agregar producto", e.getMessage());
        }
    }

    @FXML
    private void onLimpiarFormulario() {
        nombreField.clear();
        descripcionField.clear();
        precioCompraField.clear();
        precioVentaField.clear();
        stockMinimoField.clear();
        stockActualField.clear();
        categoriaComboBox.setValue(null);
        proveedorComboBox.setValue(null);
        fechaVencimientoField.setValue(null);
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
