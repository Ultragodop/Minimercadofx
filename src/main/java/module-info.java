module com.project.minimercadofx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.antdesignicons;
    requires org.kordamp.bootstrapfx.core;
    requires static org.kordamp.jipsy.annotations;
    requires static lombok;

    opens com.project.minimercadofx to javafx.fxml;
    opens com.project.minimercadofx.controllers to javafx.fxml;
    opens com.project.minimercadofx.models.bussines to javafx.fxml;
    exports com.project.minimercadofx.models.bussines to com.fasterxml.jackson.databind;
    exports com.project.minimercadofx;
    exports com.project.minimercadofx.controllers;

    exports com.project.minimercadofx.services;
    exports com.project.minimercadofx.models.Auth;
    opens com.project.minimercadofx.models.Auth;
}