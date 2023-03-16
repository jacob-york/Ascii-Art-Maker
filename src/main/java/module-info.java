module com.example.demo {
    requires java.desktop;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires opencv.java460;


    opens com.example.demo to javafx.fxml;
}