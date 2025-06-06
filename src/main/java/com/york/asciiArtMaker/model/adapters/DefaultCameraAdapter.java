package com.york.asciiArtMaker.model.adapters;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.util.Optional;

public class DefaultCameraAdapter implements LiveSource {

    private final VideoCapture videoCapture;
    private final Mat frameBuffer;
    private final int width;
    private final int height;
    private boolean isPaused;

    public DefaultCameraAdapter() {
        videoCapture = new VideoCapture(0);
        frameBuffer = new Mat();

        assert videoCapture.isOpened();
        videoCapture.read(frameBuffer);

        this.width = frameBuffer.cols();
        this.height = frameBuffer.rows();

        isPaused = false;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public Optional<String> getName() {
        return Optional.of("default camera");
    }

    @Override
    public double getFPS() {
        return videoCapture.get(Videoio.CAP_PROP_FPS);
    }

    @Override
    public ImageSource getCurrentImageSource() {
        if (!isPaused) {
            videoCapture.read(frameBuffer);
        }
        return new MatAdapter(frameBuffer);
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
        videoCapture.release();
        frameBuffer.release();
    }
}
