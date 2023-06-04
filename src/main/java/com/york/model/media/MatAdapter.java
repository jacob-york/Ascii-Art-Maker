package com.york.model.media;

import org.opencv.core.Mat;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

public class MatAdapter implements ShadingRaster {

    private final BufferedImage bufferedImage;

    private final Mat mat;

    public MatAdapter(Mat mat) {
        bufferedImage = matToBufferedImage(mat);
        this.mat = mat;
    }

    // TODO: bypass the need for buffered image conversion by converting mat directly to an array.
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
    public int getWidthPixels() {
        return mat.width();
    }

    @Override
    public int getHeightPixels() {
        return mat.height();
    }

    @Override
    public int getShadingAt(int x, int y) {
        return ShadingRaster.desaturate(bufferedImage.getRGB(x, y));
    }
}
