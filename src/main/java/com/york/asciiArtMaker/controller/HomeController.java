package com.york.asciiArtMaker.controller;

import com.york.asciiArtMaker.AsciiArtMaker;
import com.york.asciiArtMaker.model.adapters.*;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuButton;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;


public class HomeController implements ReturnLocation<VideoSource> {

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
            File selectedFile = maybeFile.get();

            ((Stage) asciiImageMenuButton.getScene().getWindow()).close();
            AsciiArtMaker.launchImageEditor(new ImageFileAdapter(selectedFile));
        }
    }

    @FXML
    public void asciiVideoFromFileChosen() {
        Optional<File> maybeFile = AsciiArtMaker.getFileManager().selectVideoFile();
        if (maybeFile.isPresent()) {
            File selectedFile = maybeFile.get();

            AsciiArtMaker.launchVideoFileConnectionService(selectedFile, (Stage) asciiImageMenuButton.getScene().getWindow());
        }
    }

    @FXML
    public void liveRenderFromCameraChosen() {
        AsciiArtMaker.launchWebcamConnectionService((Stage) asciiImageMenuButton.getScene().getWindow());
    }

    @FXML
    public void liveRenderFromScreenCaptureChosen() {
        try {
            Robot robot = new Robot();
            ((Stage) asciiImageMenuButton.getScene().getWindow()).close();
            AsciiArtMaker.launchLiveEditor(new ScreenCaptureAdapter(robot));

        } catch (AWTException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        } catch (SecurityException e) {
            new Alert(Alert.AlertType.ERROR, "Permission to capture screen was denied by your security manager:\n" + e.getMessage()).showAndWait();
        }
    }

    @Override
    public void acceptResult(VideoSource result) {
        ((Stage) asciiImageMenuButton.getScene().getWindow()).close();
        AsciiArtMaker.launchVideoEditor(result);
    }
}
