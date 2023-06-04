package com.york.model.media;

import java.awt.image.BufferedImage;

public class BufferedImageAdapter implements ShadingRaster {

    private final BufferedImage bufferedImage;

    public BufferedImageAdapter(BufferedImage image) {
        this.bufferedImage = image;
    }

    @Override
    public int getWidthPixels() {
        return bufferedImage.getWidth();
    }

    @Override
    public int getHeightPixels() {
        return bufferedImage.getHeight();
    }

    @Override
    public int getShadingAt(int x, int y) {
        int pixelColor = bufferedImage.getRGB(x, y);
        return ShadingRaster.desaturate(pixelColor);
    }

}
