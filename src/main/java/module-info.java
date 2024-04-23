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

    opens com.york.asciiartstudio to javafx.fxml;
    exports com.york.asciiartstudio;
    exports com.york.asciiartstudio.controller;
    opens com.york.asciiartstudio.controller to javafx.fxml;
    exports com.york.asciiartstudio.models;
    opens com.york.asciiartstudio.models to javafx.fxml;
}