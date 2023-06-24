package com.york.asciiartstudio.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * A Stage with an FXMLLoader.
 */
public class View extends Stage {

    public View(FXMLLoader fxmlLoader, String title, URL icon) {
        super();
        Parent root;
        try {
            root = fxmlLoader.load();  // IOException anticipated.
        }
        catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Unexpected error: Stack Trace printed.").showAndWait();
            throw new RuntimeException();
        }

        Scene scene = new Scene(root);

        setScene(scene);
        getIcons().add(new Image(icon.toString()));
        setTitle(title);
        show();
    }

    private static FileChooser initFileChooser(String title, File initDir, FileChooser.ExtensionFilter[] filters) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.setInitialDirectory(initDir);
        fileChooser.getExtensionFilters().addAll(filters);
        return fileChooser;
    }

    /**
     * uses a FileChooser to select a file.
     * @param title FileChooser TITLE property.
     * @param initDir FileChooser initialDirectory property.
     * @param filters An array of extension filters for fileChooser.
     * @return the user-selected file (null if cancelled).
     */
    public static File selectFile(String title, File initDir, FileChooser.ExtensionFilter[] filters) {
        return initFileChooser(title, initDir, filters).showOpenDialog(new Stage());
    }

    public static File saveFile(String title, File initDir, FileChooser.ExtensionFilter[] filters, String initFileName) {
        FileChooser fileChooser = initFileChooser(title, initDir, filters);
        fileChooser.setInitialFileName(initFileName);
        return fileChooser.showSaveDialog(new Stage());
    }

}
