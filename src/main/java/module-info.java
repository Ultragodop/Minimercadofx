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

    opens com.project.minimercadofx to javafx.fxml;
    opens com.project.minimercadofx.controllers to javafx.fxml;
    opens com.project.minimercadofx.models;
    
    exports com.project.minimercadofx;
    exports com.project.minimercadofx.controllers;
    exports com.project.minimercadofx.models;
    exports com.project.minimercadofx.services;
}