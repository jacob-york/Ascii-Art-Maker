package com.york.model.adapters;

/**
 * Interface for reading necessary image data.
 */
public interface ImageSource {

    /**
     * @return the width in pixels of the video stream.
     */
    int getWidth();

    /**
     * @return the height in pixels of the video stream.
     */
    int getHeight();

    /**
     * @param x the desired pixel's column (read like a cartesian plane)
     * @param y the desired pixel's row (read like a cartesian plane)
     * @return an int value from 0 to 255 that represents the black-and-white color value at pixel (x, y)
     */
    int getBWValue(int x, int y);
}
