package com.york.asciiArtMaker.view;

import javafx.scene.control.Label;
import javafx.scene.text.Font;

public class FPSTracker extends Label {
    private long prevTimeMillis;
    private double avgSecsBetFrames;
    private long runningSumMillis;
    private int measurements;

    public FPSTracker() {
        setFont(new Font("System", 15));
        setStyle("-fx-background-color: #ffffff;");
    }

    public void tick() {
        long curTimeMillis = System.currentTimeMillis();
        if (prevTimeMillis != 0) {
            double millisBetFrames = curTimeMillis - prevTimeMillis;
            runningSumMillis += millisBetFrames;
            avgSecsBetFrames = (runningSumMillis/(double) measurements) / 1000;
        }
        prevTimeMillis = curTimeMillis;
        if (measurements % 5 == 0) {
            setText(String.format("%.3f", 1.0 / avgSecsBetFrames));
        }
        measurements++;
    }
}
