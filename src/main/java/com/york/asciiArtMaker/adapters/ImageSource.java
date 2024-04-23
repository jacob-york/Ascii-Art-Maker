package com.york.asciiArtMaker.adapters;

import java.util.Optional;

public interface ImageSource {

    /**
     * @return the width in pixels of the image.
     */
    int getWidth();

    /**
     * @return the height in pixels of the image.
     */
    int getHeight();

    /**
     * @return The name of the image if it has one
     * (i.e., object instances like BufferedImage are not files, meaning they have no name).
     */
    Optional<String> getName();

    /**
     * @param x the desired pixel's column (read like a cartesian plane)
     * @param y the desired pixel's row (read like a cartesian plane)
     * @return an int value from 0 to 255 that represents the black-and-white color value at the pixel (x, y),
     * OR -1 if the pixel is completely transparent.
     */
    int getDesaturatedPixel(int x, int y);

    /**
     * Stock desaturation algorithm provided by ImageSource (for optional use in a getBWValue() implementation).
     * @param a alpha value
     * @param r red value
     * @param g green value
     * @param b blue value
     * @return an int value from 0 to 255 that represents the black-and-white color value at the pixel (x, y),
     * OR -1 if the pixel is completely transparent.
     */
    static int desaturate(int a, int r, int g, int b) {
        if (a == 0) {
            return -1;
        } else {
            int min = Math.min(r, Math.min(g, b));
            int max = Math.max(r, Math.max(g, b));
            return (int) ((min + max) * 0.5);
        }
    }
}
