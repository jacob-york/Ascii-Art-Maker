package com.york.model.media;

public interface ShadingRaster {

    /**
     *
     * @return
     */
    int getWidthPixels();

    /**
     *
     * @return
     */
    int getHeightPixels();

    /**
     * @param x the desired pixel's column (read like a cartesian plane)
     * @param y the desired pixel's row (read like a cartesian plane)
     * @return an int value from 0 to 255 that represents the shade of grey at pixel (x, y)
     */
    int getShadingAt(int x, int y);

    /**
     * algorithm for desaturating a color.
     * @return an int value from 0 to 255 that represents a pixel's shade of grey.
     *
     * This same algorithm is used by Adobe Photoshop.
     */
    static int desaturate(int color) {
        int a = (color & 0xff000000) >> 24;
        int r = (color & 0xff0000) >> 16;
        int g = (color & 0xff00) >> 8;
        int b = color & 0xff;

        // TODO: quick-fix for alpha values. Make it more sophisticated.
        if (a == 0) return 255;
        return (int) ((Math.min(Math.min(r, g), b) + Math.max(Math.max(r, g), b)) * .5);
    }


    /*
     * algorithm for desaturating a color.
     * @return an int value from 0 to 255 that represents a pixel's shade of grey.
     *
     * A similar algorithm is used by OpenCV.
     *
    static int desaturate(int color) {
        int a = (color & 0xff000000) >> 24;
        int r = (color & 0xff0000) >> 16;
        int g = (color & 0xff00) >> 8;
        int b = color & 0xff;

        if (a == 0) return 255;
        return (int) (g*.59 + r*.3 + b*.11);
    }
    */

}
