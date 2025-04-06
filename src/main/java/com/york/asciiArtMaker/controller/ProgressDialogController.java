package com.york.asciiArtMaker.controller;

import com.york.asciiArtMaker.AsciiArtMaker;
import com.york.asciiArtMaker.model.adapters.VideoSource;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

public class ProgressDialogController implements ProgressMonitor, ReturnLocation<VideoSource> {

    @FXML
    public Label info;
    @FXML
    public Stage stage;
    @FXML
    public ProgressBar progressBar;
    @FXML
    public Button cancelBtn;

    private double progress;
    private String text;
    private Cancellable cancellable;

    @FXML
    public void initialize() {
        progress = 0.0;
        text = "Gathering Frame Data...(0.0%)";
    }

    @FXML
    public void onCancel() {
        cancel();
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setCancellable(Cancellable process) {
        this.cancellable = process;
    }

    public void updateDisplay() {
        info.setText(String.format("%s...(%f%%)", text, progress * 100));
        progressBar.setProgress(progress);
    }

    @Override
    public boolean cancel() {
        if (cancellable != null) {
            cancellable.cancel();
        }
        stage.close();
        return true;
    }

    @Override
    public void setProgress(double progress) {
        this.progress = progress;
        updateDisplay();
    }

    @Override
    public void finish() {
        stage.close();
    }

    @Override
    public void acceptResult(VideoSource result) {
        AsciiArtMaker.launchVideoEditor(result);
    }
}
