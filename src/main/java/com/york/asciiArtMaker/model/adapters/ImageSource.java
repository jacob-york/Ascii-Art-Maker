package com.york.asciiArtMaker.model.adapters;

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
     * @return an Optional with the name of the image if it has one, otherwise it's empty.
     * (i.e., object instances like BufferedImage are not files, so they have no menuName).
     */
    Optional<String> getName();

    /**
     * @param col the desired pixel's column
     * @param row the desired pixel's row
     * @return an int value from 0 to 255 that represents the black-and-white color value at the pixel (col, row),
     * OR -1 if the pixel is completely transparent.
     */
    int getPixelLuminance(int col, int row);

    /**
     * Stock desaturation algorithm provided by ImageSource (for optional use in a getPixelLuminance() implementation).
     * @param a alpha value
     * @param r red value
     * @param g green value
     * @param b blue value
     * @return an int value from 0 to 255 that represents the black-and-white color value at the pixel (x, y),
     * OR -1 if the pixel is completely transparent.
     */
    static int desaturateARGB(int a, int r, int g, int b) {

        // todo: a more sophisticated way of handling semi-transparent pixels?
        if (a == 0) {
            return -1;
        } else {
            final int min = Math.min(r, Math.min(g, b));
            final int max = Math.max(r, Math.max(g, b));

            return (int) ((min + max) * 0.5);
        }
    }
}
