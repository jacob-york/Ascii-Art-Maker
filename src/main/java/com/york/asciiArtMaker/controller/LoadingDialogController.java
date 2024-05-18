package com.york.asciiArtMaker.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

public class LoadingDialogController implements ProgressMonitor {

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
        text = "Loading...";
    }

    @FXML
    public void onCancel() {
        cancel();
    }

    public void setDisplayText(String text) {
        this.text = text;
    }

    public void setCancellable(Cancellable process) {
        this.cancellable = process;
    }

    public void updateDisplay() {
        info.setText(String.format("%s...(%f%%)", text, progress));

        String newTitle = String.format("Progress Dialog - %f%%", progress);

        stage.setTitle(newTitle);
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
}
