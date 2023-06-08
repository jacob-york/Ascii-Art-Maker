module com.example.demo {
    requires java.desktop;
    requires javafx.controls;
    requires javafx.fxml;
    requires opencv;

    opens com.york to javafx.fxml;
    exports com.york;

    opens com.york.controller to javafx.fxml;
    exports com.york.controller;

    opens com.york.model.asciiArt to javafx.fxml;
    exports com.york.model.asciiArt;

    opens com.york.model.adapters to javafx.fxml;
    exports com.york.model.adapters;
}