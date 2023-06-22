package com.york.asciiartstudio;

import com.york.asciiartstudio.view.ImageFileView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;

public class App extends Application {

    public static final String TITLE = "Ascii Art Studio 1.1.2-alpha";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage mainStage) {
        Scene imageFileScene = new Scene(new ImageFileView());

        URL icon = Objects.requireNonNull(getClass().getResource("icons/appIcon.png"));
        mainStage.getIcons().add(new Image(icon.toString()));

        mainStage.setScene(imageFileScene);
        mainStage.setTitle(TITLE);
        mainStage.show();
    }
}
