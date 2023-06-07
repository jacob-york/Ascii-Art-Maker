package com.york;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage cpStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/controlPanel.fxml"));
        Scene cpScene = new Scene(fxmlLoader.load());

        cpStage.setScene(cpScene);
        cpStage.setTitle("AsciiArtMaker (Pre-Alpha)");
        cpStage.setResizable(false);
        cpStage.show();
    }
}
