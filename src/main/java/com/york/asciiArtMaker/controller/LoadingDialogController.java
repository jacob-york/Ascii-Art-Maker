package com.york.asciiArtMaker.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LoadingDialogController {

    @FXML
    public Stage stage;
    @FXML
    public Label label;
    private Timeline timeline;
    private final double TICKS_PER_SECOND = 3;

    @FXML
    public void initialize() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1.0 / TICKS_PER_SECOND), (ActionEvent event) -> {
            int numPeriods = label.getText().length() - 7;
            label.setText((numPeriods == 3) ? "Loading" : (label.getText() + "."));
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        stage.setOnCloseRequest(Event::consume);
    }
}
