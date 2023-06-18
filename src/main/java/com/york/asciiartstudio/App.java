package com.york.asciiartstudio;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class App extends Application {

    public static final String TITLE = "Ascii Art Studio 1.0.0-alpha";

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage mainStage) throws IOException {
        Parent root = new FXMLLoader(getClass().getResource("fxml/imageFileMode.fxml")).load();
        Scene imageFileScene = new Scene(root);

        URL icon = Objects.requireNonNull(getClass().getResource("icons/appIcon.png"));
        mainStage.getIcons().add(new Image(icon.toString()));

        mainStage.setScene(imageFileScene);
        mainStage.setTitle(TITLE);
        mainStage.show();
    }
}
