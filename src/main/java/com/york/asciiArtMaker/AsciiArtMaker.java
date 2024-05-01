package com.york.asciiArtMaker;

import com.york.asciiArtMaker.controller.FileManager;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;
import java.util.Optional;

public final class AsciiArtMaker extends Application {

    private static Stage stage;
    private static FileManager fileManager;

    public static Stage getMainStage() {
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
        return Objects.requireNonNull(AsciiArtMaker.class.getResource(relativePath)).toExternalForm();
    }

    public static void copyToClipboard(String string) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        clipboard.clear();
        ClipboardContent content = new ClipboardContent();
        content.putString(string);
        clipboard.setContent(content);
    }

    static {nu.pattern.OpenCV.loadLocally();}

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage mainStage) {
        URL icon = getClass().getResource("icons/appIcon.png");

        Optional<Parent> maybe = getFileManager().safeLoadFXML("home.fxml");
        if (maybe.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Unexpected error: There was an issue loading home.fxml.").showAndWait();
            System.exit(-1);
        }

        Parent root = maybe.get();

        stage = new Stage();
        stage.setScene(new Scene(root));
        assert icon != null;
        stage.getIcons().add(new Image(icon.toExternalForm()));
        stage.setTitle(TITLE);
        stage.show();
    }

}
