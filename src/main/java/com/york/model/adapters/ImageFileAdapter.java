package com.york.model.adapters;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Represents a File object as an image source.
 */
public class ImageFileAdapter implements ImageSource {

    private final BufferedImage bufferedImage;
    private final File file;

    /**
     * @param file Image File to read data from.
     * @throws IOException When file does not exist.
     * @throws IllegalArgumentException When the file's format is not supported.
     */
    public ImageFileAdapter(File file) throws IOException, IllegalArgumentException {
        this.file = file;
        bufferedImage = ImageIO.read(file);
        if (bufferedImage == null) throw new IllegalArgumentException("Invalid file format.");
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

        if (a == 0) return 255;
        return (int) ((Math.min(r, Math.min(g, b)) + Math.max(r, Math.max(g, b))) * .5);
    }


    @Override
    public String getName() {
        return file.getName();
    }
}
