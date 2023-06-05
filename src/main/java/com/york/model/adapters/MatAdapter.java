package com.york.model.adapters;

import org.opencv.core.Mat;

public final class MatAdapter implements ImageSource {

    private final Mat mat;

    public MatAdapter(Mat mat) {
        this.mat = mat;
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
    public int getBWValue(int x, int y) {
        // TODO: implement this
        int pixelColor = 0;
        return ImageSource.desaturate(pixelColor);
    }
}
