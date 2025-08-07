package com.project.minimercadofx.controllers;

import com.project.minimercadofx.MinimercadoApplication;
import com.project.minimercadofx.models.bussines.DetallesVenta;
import com.project.minimercadofx.models.bussines.DetallesVentaRequest;
import com.project.minimercadofx.models.bussines.ProductoDTO;
import com.project.minimercadofx.models.bussines.VentaDTO;
import com.project.minimercadofx.services.AuthService;
import com.project.minimercadofx.services.FacturacionService;
import com.project.minimercadofx.services.ProductService;
import com.project.minimercadofx.services.VentaService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
public class VentaController {
    private final Executor executor = Executors.newFixedThreadPool(5);
    private ProductService productService;
    private VentaService ventaService;
    private FacturacionService facturacionService;
    private AuthService authService;
    @FXML
    Button close;
    @FXML
    Button tarjeta;
    @FXML
    Label monto;

    @FXML
    TextField parseCantidad;
    @FXML
    Button agregar;
    @FXML
    Button efectivo;
    @FXML
    TableView<DetallesVenta> productos;
    @FXML
    TableColumn<DetallesVenta, String> nombre;
    @FXML
    TableColumn<DetallesVenta, Integer> idProducto;
    @FXML
    TableColumn<DetallesVenta, Double> precio;
    @FXML
    Label verify;
    @FXML
    TableColumn<DetallesVenta, Integer> cantidad;
    @FXML
    ComboBox<ProductoDTO> productoComboBox;
    List<DetallesVentaRequest> detallesVentaList;
    DetallesVentaRequest detalleRequest = new DetallesVentaRequest();

    double total = 0;
    @FXML
    private void initialize() {

        productos.setEditable(true);

        idProducto.setCellValueFactory(new PropertyValueFactory<>("id"));
        nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        precio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        close.setOnAction(event -> handleCerrarSesion());
        cantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        parseCantidad.setPromptText("Ingrese la cantidad");

        agregar.setOnAction(event -> handleAgregarProducto());
        efectivo.setOnAction(event -> {
            try {
                handlePagarEfectivo();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        tarjeta.setOnAction(event -> {
            try{
                handlePagarTarjeta();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        productService = new ProductService();
        ventaService = new VentaService();
        authService = new AuthService();
        facturacionService = new FacturacionService();

        // Cargar productos en el ComboBox
        executor.execute(() -> {
            try {
                ProductoDTO[] productos = productService.getAllProducts();
                Platform.runLater(() -> {
                    productoComboBox.getItems().addAll(productos);
                    productoComboBox.setConverter(new StringConverter<ProductoDTO>() {
                        @Override
                        public String toString(ProductoDTO producto) {
                            return producto != null ? producto.getNombre() : "";
                        }

                        @Override
                        public ProductoDTO fromString(String string) {
                            return null;
                        }
                    });
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void handleCerrarSesion() {
        try {
            String response = authService.logout();
            if (response.equals("Success")) {
                FXMLLoader fxmlLoader = new FXMLLoader(MinimercadoApplication.class.getResource("login.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                Stage stage = (Stage) close.getScene().getWindow();
                stage.setScene(scene);
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void handleAgregarProducto() {
        try {
            ProductoDTO productoSeleccionado = productoComboBox.getValue();
            if (productoSeleccionado == null) {
                verify.setText("Seleccione un producto");
                return;
            }

            if(parseCantidad.getText()==null || parseCantidad.getText().isEmpty() || Integer.parseInt(parseCantidad.getText()) <= 0) {
                parseCantidad.setStyle("-fx-border-color: red;");
                verify.setText("Ingrese una cantidad válida");
                return;
            }

            boolean exists = productos.getItems().stream()
                    .anyMatch(p -> Objects.equals(p.getId(), productoSeleccionado.getIdProducto()));
            if (exists) {
                verify.setText("Producto ya agregado");
                return;
            }

            DetallesVenta venta = new DetallesVenta();
            venta.setId(productoSeleccionado.getIdProducto());
            venta.setNombreProducto(productoSeleccionado.getNombre());
            venta.setPrecioUnitario(productoSeleccionado.getPrecioVenta());
            venta.setCantidad(Integer.parseInt(parseCantidad.getText()));

            productos.getItems().add(venta);
            total = total + (venta.getPrecio() * venta.getCantidad());
            monto.setText(String.valueOf(total));
            
            parseCantidad.clear();
            productoComboBox.setValue(null);
            verify.setText("Producto agregado con éxito");
            
        } catch (Exception e) {
            e.printStackTrace();
            verify.setText("Error al agregar el producto");
        }
    }
    private void handlePagarEfectivo() throws IOException, InterruptedException {
        if (productos.getItems().isEmpty()) {
            verify.setText("No hay productos para pagar");
            System.out.println("No hay productos para pagar");
            return;
        }
        detallesVentaList = productos.getItems().stream().map(detalle -> {
            DetallesVentaRequest detalleRequest = new DetallesVentaRequest();
            detalleRequest.setIdProducto(detalle.getId());
            detalleRequest.setCantidad(detalle.getCantidad());
            return detalleRequest;
        }).toList();

        VentaDTO ventaefectivo= (ventaService.realizarVentaEfectivo(detallesVentaList));

        if(ventaefectivo != null) {
            total = 0;
            monto.setText(String.valueOf(total));
            productos.getItems().clear();
            productoComboBox.setValue(null);
            parseCantidad.clear();
        verify.setText("Venta realizada con exito");
            System.out.println("Venta con id " + ventaefectivo.getIdVenta());
        facturacionService.generarFactura(ventaefectivo.getIdVenta());
        }


        else{
            verify.setText("Error al realizar la venta");

        }

    }
    private void handlePagarTarjeta() throws IOException, InterruptedException {
        if (productos.getItems().isEmpty()) {
            verify.setText("No hay productos para pagar");
            System.out.println("No hay productos para pagar");
            return;
        }

        detallesVentaList= productos.getItems().stream().map(detalle -> {;
            DetallesVentaRequest detalleRequest = new DetallesVentaRequest();
            detalleRequest.setIdProducto(detalle.getId());
            detalleRequest.setCantidad(detalle.getCantidad());
            return detalleRequest;
        }).toList();

        String transactionId= (ventaService.realizarVentaTarjeta(detallesVentaList));
        if(transactionId != null) {
            total = 0;
            monto.setText(String.valueOf(total));
            productos.getItems().clear();

            parseCantidad.clear();
            facturacionService.generarFacturaTarjeta(transactionId);
        }
        else{
            verify.setText("Error al realizar la venta");

        }
    }


private Boolean validarCampos() {

        if (productoComboBox.getItems().isEmpty()) {
            productoComboBox.setStyle("-fx-border-color: red;");
            verify.setText("Ingrese un Id Valido");

            return false;
        } else {
            productoComboBox.setStyle("-fx-border-color: green;");
            return true;
        }
    }


}
