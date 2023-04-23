module com.example.demo {
    requires java.desktop;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    //requires opencv.java460;

    opens com.york to javafx.fxml;
    exports com.york;
    exports com.york.model;
    opens com.york.model to javafx.fxml;
    exports com.york.util;
    opens com.york.util to javafx.fxml;
}