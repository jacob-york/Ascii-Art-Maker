package com.york.asciiArtMaker.adapters;

import com.york.asciiArtMaker.controller.LoadDialogController;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.io.File;
import java.util.*;

/**
 * <p>Abstraction for a thread that reads frame data from a video file.</p>
 *
 * <p>For each frame, VideoFileConnectionService extracts the bare minimum data that's necessary to compute an asciiImage.
 *  The actual algorithm that computes ascii art will later run this data.</p>
 *
 *  <p>Due to API limitations regarding OpenCV, the process is slow and must be done linearly from frame 0
 *  to the last frame, hence the motivation to abstract it into a Service that can run on a separate thread.</p>
 */
public class VideoFileConnectionService extends Service<Void> {

    /**
     * Nested class with a private constructor for representing a VideoSource that was created with a
     * VideoFileConnectionService.
     *
     * Since, in AsciiArtMaker, you need a VFCS to read and work with a video file source,
     * the class is nested in VFCS and has a private constructor. VFCS is the only one that can instantiate this type
     * of video source.
     */
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

        /**
         * In OpenCV, many objects need to be manually freed/released. This method releases all of your VideoFileAdapter's
         * attributes that need releasing.
         *
         * DO NOT CALL until you're done with your VideoFileAdapter.
         */
        public void release() {
            matrices.forEach(mat -> {
                if (mat.getNativeObjAddr() != 0) mat.release();
            });

            matrices = null;
            System.gc();
        }

    }

    private final List<Mat> fcsMatrices;
    private final File file;
    private int frameInProgress;
    private final LoadDialogController loadDialogController;

    public VideoFileConnectionService(File file, LoadDialogController controller) {
        this.fcsMatrices = new ArrayList<>();
        this.file = file;
        frameInProgress = 0;
        loadDialogController = controller;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<>() {
            @Override
            protected Void call() {
                VideoCapture vc = new VideoCapture(file.getPath());
                double fps = vc.get(Videoio.CAP_PROP_FPS);
                int frameCount = (int) vc.get(Videoio.CAP_PROP_FRAME_COUNT);
                Platform.runLater(() -> loadDialogController.setTotalFrames(frameCount));

                Mat mat = new Mat();
                boolean aborted = false;

                while (vc.read(mat)) {
                    frameInProgress++;
                    fcsMatrices.add(mat.clone());
                    Platform.runLater(() -> loadDialogController.update(frameInProgress));

                    if (loadDialogController.isCancelled()) {
                        aborted = true;
                    }
                }

                vc.release();
                mat.release();

                if (!aborted) {
                    Platform.runLater(() -> loadDialogController.finish(
                            new VideoFileAdapter(file.getName(), fps, fcsMatrices)));
                }
                return null;
            }
        };
    }

}
