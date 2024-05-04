package com.york.asciiArtMaker;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public final class AppUtil {
    private AppUtil() {}

    // why tf do I have to write these myself, java?
    public static <T> Set<T> union(Set<T> coll1, Set<T> coll2) {
        Set<T> returnVal = new HashSet<>(coll1);
        returnVal.addAll(coll2);
        return returnVal;
    }

    public static <T> Set<T> intersection(Set<T> coll1, Set<T> coll2) {
        return coll1.stream()
                .filter(item -> !coll2.contains(item))
                .collect(Collectors.toSet());
    }


    public static java.awt.Color JFXColorToJavaColor(javafx.scene.paint.Color fx) {
        return new java.awt.Color((float) fx.getRed(),
                (float) fx.getGreen(),
                (float) fx.getBlue(),
                (float) fx.getOpacity());
    }

    /**
     * @author Javanaut
     * (<a href="https://answers.opencv.org/question/28348/converting-bufferedimage-to-mat-in-java/">...</a>)
     */
    public static Mat matify(BufferedImage sourceImg) {

        DataBuffer dataBuffer = sourceImg.getRaster().getDataBuffer();
        byte[] imgPixels = null;
        Mat imgMat = null;

        int width = sourceImg.getWidth();
        int height = sourceImg.getHeight();

        if(dataBuffer instanceof DataBufferByte) {
            imgPixels = ((DataBufferByte)dataBuffer).getData();
        }

        if(dataBuffer instanceof DataBufferInt) {

            int byteSize = width * height;
            imgPixels = new byte[byteSize*3];

            int[] imgIntegerPixels = ((DataBufferInt)dataBuffer).getData();

            for(int p = 0; p < byteSize; p++) {
                imgPixels[p*3 + 0] = (byte) ((imgIntegerPixels[p] & 0x00FF0000) >> 16);
                imgPixels[p*3 + 1] = (byte) ((imgIntegerPixels[p] & 0x0000FF00) >> 8);
                imgPixels[p*3 + 2] = (byte) (imgIntegerPixels[p] & 0x000000FF);
            }
        }

        if(imgPixels != null) {
            imgMat = new Mat(height, width, CvType.CV_8UC3);
            imgMat.put(0, 0, imgPixels);
        }

        return imgMat;
    }

    public static Mat bufferedImageToMat(BufferedImage image) {
        byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        Mat mat = new Mat(image.getWidth(), image.getHeight(), CvType.CV_8UC3);
        mat.put(0, 0, pixels);
        return mat;
    }
}
