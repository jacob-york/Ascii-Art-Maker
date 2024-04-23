package com.york.asciiartstudio.adapters;

import java.awt.image.BufferedImage;
import java.util.Optional;

public class BufferedImageAdapter implements ImageSource {

    private final BufferedImage bufferedImage;

    public BufferedImageAdapter(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    @Override
    public int getWidth() {
        return bufferedImage.getWidth();
    }

    @Override
    public int getHeight() {
        return bufferedImage.getHeight();
    }

    @Override
    public Optional<String> getName() {
        return Optional.empty();
    }

    @Override
    public int getDesaturatedPixel(int x, int y) {
        int rgb = bufferedImage.getRGB(x, y);
        int a = (rgb & 0xFF000000) >>> 24;
        int r = (rgb & 0x00FF0000) >>> 16;
        int g = (rgb & 0x0000FF00) >>> 8;
        int b = (rgb & 0x000000FF);

        return ImageSource.desaturate(a, r, g, b);
    }
}
