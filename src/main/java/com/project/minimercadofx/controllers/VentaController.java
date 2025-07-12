package com.project.minimercadofx.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.minimercadofx.MinimercadoApplication;
import com.project.minimercadofx.models.bussines.DetallesVenta;
import com.project.minimercadofx.models.bussines.DetallesVentaRequest;
import com.project.minimercadofx.models.bussines.ProductoDTO;
import com.project.minimercadofx.services.AuthService;
import com.project.minimercadofx.services.ProductService;
import com.project.minimercadofx.services.VentaService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class VentaController {
    private ProductService productService;
    private VentaService ventaService;
    private AuthService authService;
    @FXML
    Button close;
    @FXML
    Button tarjeta;
    @FXML
    Label monto;
    @FXML
    TextField parseID;
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
    List<DetallesVentaRequest> detallesVentaList;
    DetallesVentaRequest detalleRequest = new DetallesVentaRequest();

    double total = 0;
    @FXML
    private void initialize() {

        productos.setEditable(true);
        parseID.setPromptText("Ingrese el ID del producto");
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

           if(!validarCampos()) {
               verify.setText("Ingrese un ID valido y una cantidad valida");

           }


           ProductoDTO producto = productService.getProductoById(Integer.parseInt(parseID.getText()));
               if(producto == null) {
                   parseID.setStyle("-fx-border-color: red;");
                   verify.setText("Producto no encontrado");
               }
               boolean exists = productos.getItems().stream()
                   .anyMatch(p -> {
                       assert producto != null;
                       return Objects.equals(p.getId(), producto.getId());
                   });
                   if (exists) {
                       verify.setText("Producto ya agregado");
                        throw new IllegalArgumentException("Producto ya agregado");
                   }
                       if(parseCantidad.getText()==null || parseCantidad.getText().isEmpty() || Integer.parseInt(parseCantidad.getText()) <= 0) {
                            parseCantidad.setStyle("-fx-border-color: red;");
                            verify.setText("Ingrese una cantidad valida");
                            throw new IllegalArgumentException("Cantidad invalida");
                       }
                       DetallesVenta venta= new DetallesVenta();
                        assert producto != null;
                       venta.setId(producto.getId());
                       venta.setNombreProducto(producto.getNombre());
                       venta.setPrecioUnitario(producto.getPrecioVenta());
                       venta.setCantidad(Integer.parseInt(parseCantidad.getText()));



                       productos.getItems().add(venta);
                          total=total+(venta.getPrecio() * venta.getCantidad());
                          monto.setText(String.valueOf(total));
                       parseCantidad.clear();
                       parseID.clear();
                       parseID.setStyle("-fx-border-color: green;");
                       verify.setText(" ");
                   } catch (Exception e) {
           throw new RuntimeException(e);
       }
    }
    private void handlePagarEfectivo() throws IOException, InterruptedException {
        if (productos.getItems().isEmpty()) {
            verify.setText("No hay productos para pagar");
        }
        detallesVentaList = productos.getItems().stream().map(detalle -> {
            DetallesVentaRequest detalleRequest = new DetallesVentaRequest();
            detalleRequest.setIdProducto(detalle.getId());
            detalleRequest.setCantidad(detalle.getCantidad());
            return detalleRequest;
        }).toList();

        String ventaefectivo= (ventaService.realizarVentaEfectivo(detallesVentaList));
        if(ventaefectivo.equals("success")) {
            total = 0;
            monto.setText(String.valueOf(total));
            productos.getItems().clear();
            parseID.clear();
            parseCantidad.clear();
        }
        else{
            verify.setText("Error al realizar la venta");

        }
    }
    private void handlePagarTarjeta() throws IOException, InterruptedException {
        if (productos.getItems().isEmpty()) {
            verify.setText("No hay productos para pagar");
        }

        detallesVentaList= productos.getItems().stream().map(detalle -> {;
            DetallesVentaRequest detalleRequest = new DetallesVentaRequest();
            detalleRequest.setIdProducto(detalle.getId());
            detalleRequest.setCantidad(detalle.getCantidad());
            return detalleRequest;
        }).toList();

        String ventaefectivo= (ventaService.realizarVentaTarjeta(detallesVentaList));
        if(ventaefectivo.equals("success")) {
            total = 0;
            monto.setText(String.valueOf(total));
            productos.getItems().clear();
            parseID.clear();
            parseCantidad.clear();
        }
        else{
            verify.setText("Error al realizar la venta");

        }
    }


private Boolean validarCampos() {

        if (parseID.getText().isEmpty()|| !parseID.getText().matches("\\d+")  ) {
            parseID.setStyle("-fx-border-color: red;");
            verify.setText("Ingrese un Id Valido");

            return false;
        } else {
            parseID.setStyle("-fx-border-color: green;");
            return true;
        }
    }


}
