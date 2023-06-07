package com.york.model.adapters;

import org.junit.jupiter.api.Test;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class MatAdapterTest {

     static {
        nu.pattern.OpenCV.loadLocally();
    }

    @Test
    void getWidthPixels() {
        Mat mat = Imgcodecs.imread("E:\\Media++\\Pictures++\\discord shit\\gottem.jpg");
        double[] channels = mat.get(0, 230);
        for (double db : channels) {
            System.out.println(db);
        }
    }

    @Test
    void getHeightPixels() {
        // TODO: implement this
    }

    @Test
    void getBWValue() {
        // TODO: implement this
    }
}
