package com.york.model.adapters;

import org.opencv.core.Mat;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

public class MatAdapter implements ShadingRaster {

    int[][] raster;

    public MatAdapter(Mat mat) {
        BufferedImage image = matToBufferedImage(mat);

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

    // temp.
    private static BufferedImage matToBufferedImage(Mat mat) {
        BufferedImage returnImage;
        int type = 0;
        if (mat.channels() == 1) {
            type = BufferedImage.TYPE_BYTE_GRAY;
        } else if (mat.channels() == 3) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        returnImage = new BufferedImage(mat.width(), mat.height(), type);
        WritableRaster raster = returnImage.getRaster();
        DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
        byte[] data = dataBuffer.getData();
        mat.get(0, 0, data);

        return returnImage;
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
