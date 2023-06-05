module com.example.demo {
    requires java.desktop;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires opencv;

    opens com.york to javafx.fxml;
    exports com.york;
    exports com.york.model;
    opens com.york.model to javafx.fxml;
    exports com.york.util;
    opens com.york.util to javafx.fxml;
    exports com.york.model.adapters;
    opens com.york.model.adapters to javafx.fxml;
    exports com.york.model.console;
    opens com.york.model.console to javafx.fxml;
    exports com.york.model.asciiArt;
    opens com.york.model.asciiArt to javafx.fxml;
}