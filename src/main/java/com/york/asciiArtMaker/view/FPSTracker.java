package com.york.asciiArtMaker.view;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class FPSTracker extends Label {
    private long prevTimeMillis;
    private double avgSecsBetweenFrames;
    private long runningSumMillis;
    private int measurements;

    public FPSTracker() {
        setFont(new Font("System", 15));
    }

    public void tick() {
        long curTimeMillis = System.currentTimeMillis();
        if (prevTimeMillis != 0) {
            double millisBetFrames = curTimeMillis - prevTimeMillis;
            runningSumMillis += millisBetFrames;
            avgSecsBetweenFrames = (runningSumMillis/(double) measurements) / 1000;
        }
        prevTimeMillis = curTimeMillis;
        if (measurements % 5 == 0) {
            setText(String.format("FPS: %.3f", 1.0 / avgSecsBetweenFrames));
        }
        measurements++;
    }
}
