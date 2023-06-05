package com.york.model.adapters;

public interface ImageSource {

    /**
     *
     * @return the width in pixels of the original image.
     */
    int getWidthPixels();

    /**
     *
     * @return the height in pixels of the original image.
     */
    int getHeightPixels();

    /**
     * @param x the desired pixel's column (read like a cartesian plane)
     * @param y the desired pixel's row (read like a cartesian plane)
     * @return an int value from 0 to 255 that represents the black-and-white color value at pixel (x, y)
     */
    int getBWValue(int x, int y);

    /**
     * Algorithm for desaturating a color.
     * @return an int value from 0 to 255 that represents a pixel's black-and-white color value.
     */
    static int desaturate(int color) {
        return adobePhotoshopDesaturate(color);
        // return openCVDesaturate(color);
    }

    /**
     * This same algorithm is used by Adobe Photoshop.
     */
    static int adobePhotoshopDesaturate(int color) {
        int a = (color & 0xff000000) >> 24;
        int r = (color & 0xff0000) >> 16;
        int g = (color & 0xff00) >> 8;
        int b = color & 0xff;

        // TODO: quick-fix for alpha values. Make it more sophisticated.
        if (a == 0) return 255;
        return (int) ((Math.min(Math.min(r, g), b) + Math.max(Math.max(r, g), b)) * .5);
    }

    /**
     * A similar algorithm is used by OpenCV.
     */
    static int openCVDesaturate(int color) {
        int a = (color & 0xff000000) >> 24;
        int r = (color & 0xff0000) >> 16;
        int g = (color & 0xff00) >> 8;
        int b = color & 0xff;

        if (a == 0) return 255;
        return (int) (g*.59 + r*.3 + b*.11);
    }

}
