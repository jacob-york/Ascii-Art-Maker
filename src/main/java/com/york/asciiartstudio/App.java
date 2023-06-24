package com.york.asciiartstudio;

import com.york.asciiartstudio.view.View;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.net.URL;

public class App extends Application {

    public static final String TITLE = "Ascii Art Studio 1.2.0-alpha";

    public static void main(String[] args) {
        launch(args);
    }

    private URL toURL(String path) {
        return getClass().getResource(path);
    }

    @Override
    public void start(Stage mainStage) {
        new View(new FXMLLoader(toURL("fxml/home.fxml")), TITLE, toURL("icons/appIcon.ico"));
    }
}
