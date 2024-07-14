package com.york.asciiArtMaker.model.adapters;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class ImageFileAdapter implements ImageSource {

    private final BufferedImage bufferedImage;
    private final String name;

    public ImageFileAdapter(File file) {
        try {
            this.bufferedImage = ImageIO.read(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.name = file.getName();
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
        return Optional.of(name);
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