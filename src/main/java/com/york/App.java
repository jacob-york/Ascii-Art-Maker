package com.york;

import com.york.controller.IFMController;
import javafx.application.Application;
import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class App extends Application {

    public static final String TITLE = "Ascii Art Studio (Pre-Alpha)";

    public static void main(String[] args) {
        // nu.pattern.OpenCV.loadLocally();
        launch(args);
    }

    @Override
    public void start(Stage mainStage) throws IOException {
        Parent root = new FXMLLoader(getClass().getResource("fxml/imageFileMode.fxml")).load();
        Scene imageFileScene = new Scene(root);

        URL icon = Objects.requireNonNull(getClass().getResource("icons/appIcon.png"));
        mainStage.getIcons().add(new Image(icon.toString()));

        // String css = Objects.requireNonNull(getClass().getResource("css/application.css")).toExternalForm();
        // imageFileScene.getStylesheets().add(css);

        mainStage.setScene(imageFileScene);
        mainStage.setTitle(TITLE);
        mainStage.show();
    }
}
