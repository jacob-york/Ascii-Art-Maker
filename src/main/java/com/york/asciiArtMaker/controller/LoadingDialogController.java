package com.york.asciiArtMaker.controller;

import com.york.asciiArtMaker.adapters.VideoFileConnectionService;
import com.york.asciiArtMaker.adapters.VFCSObserver;
import com.york.asciiArtMaker.adapters.VideoSource;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class LoadingDialogController implements VFCSObserver {

    @FXML
    public Label info;
    @FXML
    public ProgressBar progressBar;
    @FXML
    public Button cancelBtn;

    private int curFrame;
    private int totalFrames;
    private String text;
    private final List<LoadingDialogObserver> observers;

    public LoadingDialogController() {
        observers = new ArrayList<>();
    }

    @FXML
    public void initialize() {
        curFrame = 0;
        totalFrames = 0;
        text = "Loading";
    }

    @FXML
    public void onCancelBtn() {
        observers.forEach(LoadingDialogObserver::onUserCancel);
    }

    public void setDisplayText(String text) {
        this.text = text;
    }

    public void addObserver(LoadingDialogObserver observer) {
        observers.add(observer);
    }

    public void close() {
        ((Stage) info.getScene().getWindow()).close();
    }

    public void updateDisplay() {
        info.setText(String.format("%s...(%d/%d)", text, curFrame, totalFrames));
        String newTitle = String.format("Progress Dialog - %d/%d",
                curFrame, totalFrames);

        ((Stage) info.getScene().getWindow()).setTitle(newTitle);
        progressBar.setProgress(((double) curFrame) / ((double) totalFrames));
    }

    @Override
    public void setTotalFrames(int totalFrames) {
        this.totalFrames = totalFrames;
        updateDisplay();
    }

    @Override
    public void userCancel() {
        close();
    }

    @Override
    public void setCurFrame(int curFrame) {
        this.curFrame = curFrame;
        updateDisplay();
    }

    @Override
    public void success(VideoSource videoSource) {
        close();
    }
}
