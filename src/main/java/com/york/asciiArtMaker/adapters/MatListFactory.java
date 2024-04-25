package com.york.asciiArtMaker.adapters;

import com.york.asciiArtMaker.observer.LoadingDialog;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.io.File;
import java.util.*;

public class MatListFactory {

    public static class VideoFileAdapter implements VideoSource {

        private final String name;
        private final double fps;
        private List<Mat> matrices;
        private final ImageSource[] imageSources;
        private final int width;
        private final int height;
        private final int frameCount;

        private VideoFileAdapter(String name, double fps, List<Mat> matrices) {
            this.name = name;
            this.fps = fps;
            this.matrices = matrices;

            this.width = matrices.get(0).cols();
            this.height = matrices.get(0).rows();
            this.frameCount = matrices.size();

            this.imageSources = new ImageSource[frameCount];
        }

        @Override
        public int getWidth() {
            return width;
        }

        @Override
        public int getHeight() {
            return height;
        }

        @Override
        public Optional<String> getName() {
            return Optional.of(name);
        }

        @Override
        public double getFps() {
            return fps;
        }

        @Override
        public int getFrameCount() {
            return frameCount;
        }

        @Override
        public ImageSource getImageSource(int i) {
            if (i < 0 || i >= getFrameCount()) return null;
            if (imageSources[i] == null) {
                imageSources[i] = new MatAdapter(matrices.get(i));
            }
            return imageSources[i];
        }

        @Override
        public ImageSource[] getImageSources() {
            return getImageSources(0, getFrameCount());
        }

        @Override
        public ImageSource[] getImageSources(int startInd) {
            return getImageSources(startInd, getFrameCount());
        }

        @Override
        public ImageSource[] getImageSources(int startInd, int endInd) {
            if (startInd < 0 || startInd > getFrameCount()) return null;
            if (endInd < 0 || endInd > getFrameCount()) return null;
            ImageSource[] subArray = new ImageSource[endInd - startInd];

            for (int i = 0; i < subArray.length; i++) {
                subArray[i] = getImageSource(startInd+i);
            }

            return subArray;
        }

        public void releaseNativeResources() {
            matrices.forEach(mat -> {
                if (mat.getNativeObjAddr() != 0) mat.release();
            });

            matrices = null;
            System.gc();
        }

    }

    private final LoadingDialog loadingDialog;

    private int frameInProgress;

    public MatListFactory(LoadingDialog loadingDialog) {
        this.loadingDialog = loadingDialog;
        frameInProgress = 0;
    }

    public VideoSource buildFromFile(File file) {
        VideoCapture vc = new VideoCapture(file.getPath());
        double fps = vc.get(Videoio.CAP_PROP_FPS);

        ArrayList<Mat> matrices = new ArrayList<>();
        Mat mat = new Mat();
        while (vc.read(mat)) {
            frameInProgress++;
            matrices.add(mat.clone());
            loadingDialog.update(frameInProgress);
        }
        vc.release();
        mat.release();

        return new VideoFileAdapter(file.getName(), fps, matrices);
    }
}
