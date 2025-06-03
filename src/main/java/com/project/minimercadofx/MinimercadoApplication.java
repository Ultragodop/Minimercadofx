package com.project.minimercadofx;

import com.project.minimercadofx.models.bussines.Producto;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.project.minimercadofx.services.ProductService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MinimercadoApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MinimercadoApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles/main.css")).toExternalForm());
        stage.setTitle("MiniMercado FX");
        stage.setMinWidth(1000);
        stage.setMinHeight(600);
        stage.setScene(scene);
        stage.show();
    }

    public static List<Producto> findAllProducts() {
        ProductService productService = new ProductService();
        List<Producto> productos = new ArrayList<>();
        try {
            productos = productService.findAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return productos;
    }

    public static void main(String[] args) {

        System.out.println(findAllProducts());
        launch();
    }
}
