package com.project.minimercadofx;

import com.project.minimercadofx.services.AuthService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MinimercadoApplication extends Application {
    private final AuthService authService = new AuthService();
    public static void main(String[] args) {

        launch();

    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MinimercadoApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles/main.css")).toExternalForm());
        stage.setTitle("MiniMercado FX");
        stage.setOnCloseRequest(event -> {
            event.consume();
            closeApplication(stage);

        });
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }
    private void closeApplication(Stage stage) {
        System.out.println("Closing application...");
        authService.logout();
        stage = (Stage) Stage.getWindows().get(0);
        if (stage != null) {
            stage.close();
        }
    }
}

