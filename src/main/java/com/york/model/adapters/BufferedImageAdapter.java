package com.york.model.adapters;

import java.awt.image.BufferedImage;

public class BufferedImageAdapter implements ShadingRaster {

    int[][] raster;

    public BufferedImageAdapter(BufferedImage image) {
        int sRWidth = image.getWidth();
        int sRHeight = image.getHeight();
        int[][] shadingRaster = new int[sRHeight][sRWidth];

        for (int y = 0; y < sRHeight; y++) {
            for(int x = 0; x < sRWidth; x++) {
                int pixelColor = image.getRGB(x, y);
                shadingRaster[y][x] = ShadingRaster.desaturate(pixelColor);
            }
        }
        raster = shadingRaster;
    }

    @Override
    public int getWidth() {
        return raster[0].length;
    }

    @Override
    public int getHeight() {
        return raster.length;
    }

    @Override
    public int getShadingAt(int x, int y) {
        return raster[y][x];
    }

}
