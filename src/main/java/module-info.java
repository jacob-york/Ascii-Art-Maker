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

    exports com.york.asciiArtMaker;
    opens com.york.asciiArtMaker to javafx.fxml;
    exports com.york.asciiArtMaker.model.adapters;
    opens com.york.asciiArtMaker.model.adapters to javafx.fxml;
    exports com.york.asciiArtMaker.model.asciiArt;
    opens com.york.asciiArtMaker.model.asciiArt to javafx.fxml;
    exports com.york.asciiArtMaker.controller;
    opens com.york.asciiArtMaker.controller to javafx.fxml;
    exports com.york.asciiArtMaker.model.models;
    opens com.york.asciiArtMaker.model.models to javafx.fxml;
    exports com.york.asciiArtMaker.view;
    opens com.york.asciiArtMaker.view to javafx.fxml;
}