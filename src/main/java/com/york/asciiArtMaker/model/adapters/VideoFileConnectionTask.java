package com.york.asciiArtMaker.model.adapters;

import com.york.asciiArtMaker.AsciiArtMaker;
import com.york.asciiArtMaker.controller.Cancellable;
import com.york.asciiArtMaker.controller.LoadingDialogController;
import com.york.asciiArtMaker.controller.ProgressMonitor;
import com.york.asciiArtMaker.controller.ReturnLocation;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.stage.Stage;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VideoFileConnectionTask extends Task<VideoSource> implements Cancellable {

    /**
     * Nested class with a private constructor, for representing VideoSources created via a VideoFileConnectionTask.
     */
    public static class VideoFileAdapter implements VideoSource {

        private final String name;
        private final double fps;
        private List<Mat> frameData;
        private final ImageSource[] imageSources;
        private final int width;
        private final int height;
        private final int frameCount;

        private VideoFileAdapter(String name, double fps, List<Mat> frameData) {
            this.name = name;
            this.fps = fps;
            this.frameData = frameData;

            this.width = frameData.get(0).cols();
            this.height = frameData.get(0).rows();
            this.frameCount = frameData.size();

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
        public double getFPS() {
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
                imageSources[i] = new MatAdapter(frameData.get(i));
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

        /**
         * In OpenCV, many objects need to be manually freed/released. This method releases all of your VideoFileAdapter's
         * attributes which need releasing.
         * Treat it somewhat like free() in C/C++, i.e. DO NOT CALL until you're done with your VideoFileAdapter.
         */
        public void release() {
            frameData.forEach(mat -> {
                if (mat.getNativeObjAddr() != 0) mat.release();
            });

            frameData = null;
            System.gc();
        }
    }

    public static final int FRAME_BUFFER = 100;
    private final File file;
    private int frameInProgress;
    private final VideoCapture videoCapture;
    private final Mat frameBuffer;  // holds data for a frame

    public VideoFileConnectionTask(File file) {
        this.file = file;
        frameInProgress = 0;

        videoCapture = new VideoCapture();
        frameBuffer = new Mat();

        onCancelledProperty().addListener((obs, oldValue, newValue) -> {
            if (videoCapture.isOpened()) videoCapture.release();
            frameBuffer.release();
        });
    }

    @Override
    protected VideoSource call() {
        videoCapture.open(file.getPath());
        if (!videoCapture.isOpened()) {
            failed();
            frameBuffer.release();
            return null;
        }

        double fps = videoCapture.get(Videoio.CAP_PROP_FPS);
        double approxFrameCount = videoCapture.get(Videoio.CAP_PROP_FRAME_COUNT);
        List<Mat> matrices = new ArrayList<>();
        updateProgress(0, approxFrameCount);

        while (videoCapture.read(frameBuffer)) {
            frameInProgress++;
            matrices.add(frameBuffer.clone());

            if (matrices.size() % FRAME_BUFFER == 0) {
                updateProgress(frameInProgress, approxFrameCount);
            }
        }

        videoCapture.release();
        frameBuffer.release();

        return new VideoFileAdapter(file.getName(), fps, matrices);
    }
}
