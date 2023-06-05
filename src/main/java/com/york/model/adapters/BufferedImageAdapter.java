package com.york.model.adapters;

import java.awt.image.BufferedImage;

public final class BufferedImageAdapter implements ImageSource {

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
    public int getBWValue(int x, int y) {
        int pixelColor = bufferedImage.getRGB(x, y);
        return ImageSource.desaturate(pixelColor);
    }

}
