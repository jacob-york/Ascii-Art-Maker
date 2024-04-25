module com.york.asciiartstudio {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires opencv;
    requires java.desktop;

    opens com.york.asciiArtMaker to javafx.fxml;
    exports com.york.asciiArtMaker;
    exports com.york.asciiArtMaker.controller;
    opens com.york.asciiArtMaker.controller to javafx.fxml;
    exports com.york.asciiArtMaker.models;
    opens com.york.asciiArtMaker.models to javafx.fxml;
    exports com.york.asciiArtMaker.observer;
    opens com.york.asciiArtMaker.observer to javafx.fxml;
}