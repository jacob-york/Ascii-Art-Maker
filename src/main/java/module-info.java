module com.york.asciiartstudio {
    requires java.desktop;
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;

    opens com.york.asciiartstudio to javafx.fxml;
    exports com.york.asciiartstudio;

    opens com.york.asciiartstudio.presenter to javafx.fxml;
    exports com.york.asciiartstudio.presenter;

    opens com.york.asciiartstudio.model.asciiArt to javafx.fxml;
    exports com.york.asciiartstudio.model.asciiArt;

    opens com.york.asciiartstudio.model.adapters to javafx.fxml;
    exports com.york.asciiartstudio.model.adapters;

}