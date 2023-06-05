package com.york.model.adapters;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

// TODO: kinda bad

public final class ImagePathAdapter implements ImageSource {

    public static final int NULL_PATH = 3;

    public static final int FILE_NOT_FOUND = 2;

    public static final int FILE_NOT_ACCEPTED = 1;

    public static final int SUCCESS = 0;

    private final BufferedImage bufferedImage;

    private final String path;

    public ImagePathAdapter(String path) throws IOException {
        bufferedImage = ImageIO.read(new File(path));
        this.path = path;
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

    /**
     * Returns the name of the original file (not including file extension).
     * @return The name of the original file (not including file extension).
     */
    public String getFileName() {
        return path.substring(path.lastIndexOf('\\') + 1, path.lastIndexOf('.'));
    }

    public String getFileExtension() {
        if (path == null) return null;

        int fileExtensionStart = path.lastIndexOf('.') + 1;
        return path.substring(fileExtensionStart);
    }

    public static String[] getAcceptedFormats() {
        return new String[] {"jpg", "jpeg", "png"};
    }

    public static boolean isAcceptedFormat(String path) {
        for (String format : getAcceptedFormats()) {
            if (path.toLowerCase().endsWith("." + format)) return true;
        }
        return false;
    }

    public static int testPath(String path) {
        if (path == null) return NULL_PATH;
        if (!new File(path).exists()) return FILE_NOT_FOUND;
        if (!isAcceptedFormat(path)) return FILE_NOT_ACCEPTED;
        return SUCCESS;
    }

}
