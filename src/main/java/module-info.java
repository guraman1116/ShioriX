module net.guraman.shiorix {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires javafx.web;
    requires com.fasterxml.jackson.databind;
    requires javafx.graphics;
    requires javafx.swing;
    requires jcef;
    requires jcefmaven;

    opens net.guraman.shiorix to javafx.fxml;
    exports net.guraman.shiorix;
}