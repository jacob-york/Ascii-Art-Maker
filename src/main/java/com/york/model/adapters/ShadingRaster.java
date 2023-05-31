package com.york.model.adapters;

public interface ShadingRaster {

    int getWidth();
    int getHeight();
    int getShadingAt(int x, int y);

    /**
     * algorithm for desaturating a color.
     * @return an int value from 0 to 255 that represents a pixel's shade of grey.
     */
    static int desaturate(int color) {

        int a = (color & 0xff000000) >> 24;
        int r = (color & 0xff0000) >> 16;
        int g = (color & 0xff00) >> 8;
        int b = color & 0xff;

        if (a == 0) {  // TODO: quick-fix for alpha values. Make it more sophisticated.
            return 255;
        }
        else {
            if (r > g) {
                if (r > b) {
                    // r is bigger than both, so it's the primary color.
                    while (r > g && r > b) {
                        // desaturate the pixel by lowering primaries and
                        // raising secondaries until the secondaries pass the primaries.
                        r --;
                        g ++;
                        b ++;
                    }
                }
                else if (r < b) {
                    while (b > r && b > g) {
                        r ++;
                        g ++;
                        b --;
                    }
                }
                else if (r == b) {
                    while (r > g) {
                        r --;
                        g ++;
                        b --;
                    }
                }
            }
            else if (g > r) {
                if (g > b) {
                    while (g > r && g > b) {
                        r ++;
                        g --;
                        b ++;
                    }
                }
                else if (g < b) {
                    while (b > r && b > g) {
                        r ++;
                        g ++;
                        b --;
                    }

                }
                else if (g == b) {
                    while (g > r) {
                        r ++;
                        g --;
                        b --;
                    }
                }
            }
            else if (r == g) {
                if (r > b) {
                    while (r > b) {
                        r --;
                        g --;
                        b ++;
                    }
                }
                else if (r < b) {
                    while (b > r && b > g) {
                        r ++;
                        g ++;
                        b --;
                    }
                }
                else if (g == b) {
                    return r;  // arriving here means the pixel is already grey, so just return any color value
                }
            }

            // afterwards, average the three
            return (r + g + b) / 3;
        }
    }
}
