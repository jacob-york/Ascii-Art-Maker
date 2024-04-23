package com.york.asciiArtMaker.adapters;

import org.opencv.core.Mat;

import java.util.Optional;

public class MatAdapter implements ImageSource {

    private final Mat mat;

    public MatAdapter(Mat mat) {
        this.mat = mat;
    }

    @Override
    public int getWidth() {
        return mat.width();
    }

    @Override
    public int getHeight() {
        return mat.height();
    }

    @Override
    public Optional<String> getName() {
        return Optional.empty();
    }

    @Override
    public int getDesaturatedPixel(int x, int y) {
        double[] rgb = mat.get(y, x);
        return ImageSource.desaturate(1, (int) rgb[2], (int) rgb[1], (int) rgb[0]);
    }

}
