package com.york;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    public static final String TITLE = "AsciiArtMaker (Pre-Alpha)";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage mainStage) throws IOException {
        nu.pattern.OpenCV.loadLocally();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/main.fxml"));
        Scene controlScene = new Scene(fxmlLoader.load());

        mainStage.setScene(controlScene);
        /*
        // TODO
        URL icon = Objects.requireNonNull(getClass().getResource("fxml/icon.png"));
        mainStage.getIcons().add(new Image(icon.toString()));
        */
        mainStage.setTitle(TITLE);
        mainStage.show();
    }
}
