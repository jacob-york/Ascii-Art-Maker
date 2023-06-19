package com.york.asciiartstudio.model.adapters;

import java.io.File;
import java.io.IOException;

// TODO
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
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public String getName() {
        return file.getName();
    }

    @Override
    public int getFPS() {
        return 0;
    }

    @Override
    public int getFrameCount() {
        return 0;
    }

    @Override
    public ImageSource[] getImageSourceArray() {
        return new ImageSource[0];
    }
}
