package com.york.asciiArtMaker.controller;

import com.york.asciiArtMaker.AsciiArtMaker;
import com.york.asciiArtMaker.model.adapters.*;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuButton;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.util.Optional;


public class HomeController implements Closable {

    @FXML
    public MenuButton asciiImageMenuButton;
    @FXML
    public MenuButton asciiVideoMenuButton;
    @FXML
    public MenuButton liveRenderMenuButton;

    @FXML
    public void asciiImageFromFileChosen() {
        Optional<File> maybeFile = AsciiArtMaker.getFileManager().selectImageFile();
        if (maybeFile.isPresent()) {
            close();

            File selectedFile = maybeFile.get();
            AsciiArtMaker.launchImageEditor(new ImageFileAdapter(selectedFile));
        }
    }

    @FXML
    public void asciiVideoFromFileChosen() {
        Optional<File> maybeFile = AsciiArtMaker.getFileManager().selectVideoFile();
        if (maybeFile.isPresent()) {
            File selectedFile = maybeFile.get();
            AsciiArtMaker.launchVideoFileConnectionTask(selectedFile, this);
        }
    }

    @FXML
    public void liveRenderFromCameraChosen() {
        AsciiArtMaker.launchWebcamConnectionTask(this);
    }

    @FXML
    public void liveRenderFromScreenCaptureChosen() {
        Robot robot = null;

        try {
            robot = new Robot();
        } catch (AWTException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        } catch (SecurityException e) {
            new Alert(Alert.AlertType.ERROR, "Permission to capture screen was denied by your security manager:\n" + e.getMessage()).showAndWait();
        }

        if (robot != null) {
            close();
            AsciiArtMaker.launchLiveEditor(new ScreenCaptureAdapter(robot));
        }
    }

    @Override
    public void close() {
        Stage stage = (Stage) asciiImageMenuButton.getScene().getWindow();
        stage.close();
    }
}
