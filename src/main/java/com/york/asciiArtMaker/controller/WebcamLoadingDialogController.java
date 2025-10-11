package com.york.asciiArtMaker.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;

public class WebcamLoadingDialogController {

    @FXML
    public Stage stage;
    @FXML
    public Label animationLabel;
    private Timeline timeline;
    private final double TICKS_PER_SECOND = 3;

    @FXML
    public void initialize() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1.0 / TICKS_PER_SECOND), (ActionEvent event) -> {
            int curNumPeriods = animationLabel.getText().length();
            int nextNumPeriods = (curNumPeriods + 1) % 4;
            animationLabel.setText(".".repeat(nextNumPeriods));
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        stage.setOnCloseRequest(Event::consume);
    }
}
