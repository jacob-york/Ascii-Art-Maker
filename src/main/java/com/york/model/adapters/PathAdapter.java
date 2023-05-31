package com.york.model.adapters;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/*
 * More of a decorator, really...
 */
public class PathAdapter implements ShadingRaster {

    public static final int NULL_PATH = 3;

    public static final int FILE_NOT_FOUND = 2;

    public static final int FILE_NOT_ACCEPTED = 1;

    public static final int SUCCESS = 0;

    private static final String[] ACCEPTED_FORMATS = {"jpg", "jpeg", "png"};

    int[][] raster;

    String path;

    public PathAdapter(String path) throws IOException {
        BufferedImage image = ImageIO.read(new File(path));

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
        this.path = path;
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

    /**
     * returns null if no path was used.
     * @return
     */
    public String getImageName() {
        return path.substring(path.lastIndexOf('\\') + 1, path.lastIndexOf('.'));
    }

    public String getFileExtension() {
        if (path == null) return null;

        int fileExtensionStart = path.lastIndexOf('.') + 1;
        return path.substring(fileExtensionStart);
    }

    public static String[] getAcceptedFormats() {
        return ACCEPTED_FORMATS.clone();
    }

    public static boolean isAcceptedFormat(String path) {
        for (String format : ACCEPTED_FORMATS) {
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
