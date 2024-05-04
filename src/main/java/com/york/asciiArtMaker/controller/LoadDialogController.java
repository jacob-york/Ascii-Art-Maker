package com.york.asciiArtMaker.controller;

import com.york.asciiArtMaker.adapters.VideoSource;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

public class LoadDialogController implements Observer {

    @FXML
    public Label info;
    @FXML
    public ProgressBar progressBar;
    @FXML
    public Button cancelBtn;
    private boolean userInterrupt;
    private int totalFrames;
    private Controller observer;
    private String text;

    @FXML
    public void initialize() {
        totalFrames = 0;
        text = "Loading";
    }

    @FXML
    public void onCancelBtn() {
        userInterrupt = true;
        ((Stage) info.getScene().getWindow()).close();
    }

    public boolean isCancelled() {
        return userInterrupt;
    }

    public void setTotalFrames(int totalFrames) {
        this.totalFrames = totalFrames;
    }

    public void setObserver(Controller controller) {
        observer = controller;
    }

    public void setDisplayText(String text) {
        this.text = text;
    }

    public void finish(VideoSource videoSource) {
        ((Stage) info.getScene().getWindow()).close();
        observer.setArt(videoSource);
    }

    public void update(int curFrame) {
        info.setText(String.format("%s...(%d/%d)", text, curFrame, totalFrames));
        ((Stage) info.getScene().getWindow()).setTitle(String.format("Progress Dialog - %d/%d",
                curFrame, totalFrames));
        progressBar.setProgress(((double) curFrame) / ((double) totalFrames));
    }
}
