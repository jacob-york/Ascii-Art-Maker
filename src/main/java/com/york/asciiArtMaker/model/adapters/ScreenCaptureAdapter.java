package com.york.asciiArtMaker.model.adapters;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Optional;

public class ScreenCaptureAdapter implements LiveSource {

    private final Robot robot;
    private final Rectangle rectangle;
    private BufferedImage lastBufferedImage;
    private boolean isPaused;

    public ScreenCaptureAdapter(Robot robot) {
        isPaused = false;
        this.robot = robot;
        this.rectangle = new Rectangle(0, 0, 1920, 1080);
    }

    @Override
    public int getWidth() {
        return rectangle.width;
    }

    @Override
    public int getHeight() {
        return rectangle.height;
    }

    @Override
    public Optional<String> getName() {
        return Optional.of("screen capture");
    }

    @Override
    public double getFPS() {
        return 30;
    }

    @Override
    public ImageSource getCurrentImageSource() {
        if (!isPaused) {
            lastBufferedImage = robot.createScreenCapture(rectangle);
        }
        return new BufferedImageAdapter(lastBufferedImage);
    }

    @Override
    public void pause() {
        isPaused = true;
    }

    @Override
    public void unpause() {
        isPaused = false;
    }

    @Override
    public void close() {
        // N/A
    }
}
