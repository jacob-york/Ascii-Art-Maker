package com.york.asciiartstudio.model.adapters;

import java.awt.image.BufferedImage;

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
    public int getBWValue(int x, int y) {
        int rgb = bufferedImage.getRGB(x, y);

        int a = (rgb & 0xff000000) >> 24;
        int r = (rgb & 0xff0000) >> 16;
        int g = (rgb & 0xff00) >> 8;
        int b = rgb & 0xff;

        if (a < 1) return -1;
        return (int) ((Math.min(r, Math.min(g, b)) + Math.max(r, Math.max(g, b))) * .5);
    }

    @Override
    public String getName() {
        return null;
    }
}
