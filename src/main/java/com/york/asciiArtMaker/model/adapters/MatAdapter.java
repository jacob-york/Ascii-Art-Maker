package com.york.asciiArtMaker.model.adapters;

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
    public int getPixelLuminance(int col, int row) {
        double[] rgb = mat.get(row, col);
        return ImageSource.desaturateARGB(1, (int) rgb[2], (int) rgb[1], (int) rgb[0]);
    }

}
