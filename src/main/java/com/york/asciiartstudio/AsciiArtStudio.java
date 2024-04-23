package com.york.asciiartstudio;

import com.york.asciiartstudio.controller.FileManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public final class AsciiArtStudio extends Application {

    private static Stage stage;
    private static FileManager fileManager;

    public static Stage getStage() {
        return stage;
    }

    public static FileManager getFileManager() {
        if (fileManager == null) {
            fileManager = new FileManager();
        }

        return fileManager;
    }

    public static final String TITLE = "Ascii Art Studio";

    public static String getResourcePath(String relativePath) {
        return Objects.requireNonNull(AsciiArtStudio.class.getResource(relativePath)).toExternalForm();
    }

    public static void copyToClipboard(String string) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        clipboard.clear();
        ClipboardContent content = new ClipboardContent();
        content.putString(string);
        clipboard.setContent(content);
    }

    static {nu.pattern.OpenCV.loadShared();}

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage mainStage) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/home.fxml"));
        URL icon = getClass().getResource("icons/appIcon.png");

        stage = new Stage();
        Parent root;
        try {
            root = loader.load();  // IOException anticipated.
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.getIcons().add(new Image(icon.toExternalForm()));
            stage.setTitle(TITLE);
        }
        catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
            e.printStackTrace();
            System.exit(-1);
        }

        stage.show();
    }

}
