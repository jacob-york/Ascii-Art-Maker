package com.york.asciiArtMaker.model.adapters;

import com.york.asciiArtMaker.controller.Cancellable;
import com.york.asciiArtMaker.controller.ProgressMonitor;
import com.york.asciiArtMaker.controller.ReturnLocation;
import javafx.application.Platform;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.io.File;
import java.util.*;

/**
 * <p>Abstraction for a thread that reads frame data from a video file.</p>
 *
 * <p>For each frame, VideoFileConnectionService extracts the bare minimum data that's necessary to compute an asciiImage.</p>
 *
 *  <p>The algorithm which computes ascii images from image data will be run lazily on this frame data later,
 *  once each frame has been extracted.</p>
 *
 *  <p>Due to API limitations with OpenCV, the process of reading frame data is slow and must be done linearly from frame 0
 *  to the last frame, hence the motivation to abstract this process into a Runnable which can be ran on its own thread.</p>
 */
public class VideoFileConnectionService implements Cancellable, Runnable {

    /**
     * <p>Nested class with a private constructor, for representing VideoSources created via a
     * VideoFileConnectionService.</p>
     *
     * <p>The reason for the private constructor is that in AsciiArtMaker, the only way to read a video file is
     * via a VideoFileConnectionService. So, a VideoFileConnectionService is the only object that
     * can instantiate a VideoFileAdapter. The user is meant to access the VideoFileAdapter as a VideoSource.</p>
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
    private boolean cancelled;
    private final List<ProgressMonitor> observers;
    private final ReturnLocation<VideoSource> returnLocation;
    private final VideoCapture videoCapture;
    private final Mat frameBuffer;  // holds data for a frame

    public VideoFileConnectionService(File file, ReturnLocation<VideoSource> returnLocation) {
        this.file = file;
        this.returnLocation = returnLocation;
        frameInProgress = 0;

        videoCapture = new VideoCapture(file.getPath());
        frameBuffer = new Mat();
        observers = new ArrayList<>();
    }

    @Override
    public void run() {
        if (!videoCapture.isOpened()) {
            Platform.runLater(() -> observers.forEach(ProgressMonitor::cancel));
            return;
        }

        double fps = videoCapture.get(Videoio.CAP_PROP_FPS);
        double approxFrameCount = videoCapture.get(Videoio.CAP_PROP_FRAME_COUNT);
        List<Mat> matrices = new ArrayList<>();

        while (!cancelled && videoCapture.read(frameBuffer)) {
            frameInProgress++;
            matrices.add(frameBuffer.clone());

            if (matrices.size() % FRAME_BUFFER == 0) {
                Platform.runLater(() -> observers.forEach(observer ->
                        observer.setProgress((double) frameInProgress / approxFrameCount)));
            }
        }

        videoCapture.release();
        frameBuffer.release();

        if (!cancelled) {
            VideoSource videoSource = new VideoFileAdapter(file.getName(), fps, matrices);
            Platform.runLater(() -> {
                observers.forEach(ProgressMonitor::finish);
                returnLocation.acceptResult(videoSource);
            });
        }
    }

    @Override
    public boolean cancel() {
        cancelled = true;
        videoCapture.release();
        frameBuffer.release();
        return true;
    }

    public void addProgressMonitor(ProgressMonitor observer) {
        observers.add(observer);
    }

}
