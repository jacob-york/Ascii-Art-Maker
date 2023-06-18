package com.york.asciiartstudio.model.adapters;

import java.io.File;
import java.io.IOException;

public class VideoFileAdapter implements VideoSource {

    File file;

    /**
     *
     * @param file Video file to read video data from.
     * @throws IOException When the file is not found.
     * @throws IllegalArgumentException When the file format is not accepted.
     */
    public VideoFileAdapter(File file) throws IOException, IllegalArgumentException {
        this.file = file;
        // TODO
    }

    @Override
    public int getWidth() {
        // TODO
        return 0;
    }

    @Override
    public int getHeight() {
        // TODO
        return 0;
    }

    @Override
    public String getName() {
        return file.getName();
    }

    @Override
    public int getFPS() {
        // TODO
        return 0;
    }

    @Override
    public int getFrameCount() {
        // TODO
        return 0;
    }

    @Override
    public ImageSource[] getImageSourceArray() {
        // TODO
        return new ImageSource[0];
    }
}
