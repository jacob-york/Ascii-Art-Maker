package com.york.asciiartstudio.model.adapters;

public class WebcamAdapter implements LiveStreamSource {

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public int getFPS() {
        return 0;
    }

    @Override
    public ImageSource getCurrentFrame() {
        return null;
    }
}
