package com.york.asciiArtMaker.controller;

import com.york.asciiArtMaker.AsciiArtMaker;
import com.york.asciiArtMaker.model.adapters.ImageFileAdapter;
import com.york.asciiArtMaker.model.adapters.DefaultCameraAdapter;
import com.york.asciiArtMaker.model.adapters.VideoSource;
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;


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

            AsciiArtMaker.launchVideoFileConnectionService(selectedFile, this);
        }
    }

    @FXML
    public void liveRenderFromCameraChosen() {
        ((Stage) asciiImageMenuButton.getScene().getWindow()).close();
        AsciiArtMaker.launchLiveEditor(new DefaultCameraAdapter());
    }

    @Override
    public void acceptResult(VideoSource result) {
        ((Stage) asciiImageMenuButton.getScene().getWindow()).close();
        AsciiArtMaker.launchVideoEditor(result);
    }
}
