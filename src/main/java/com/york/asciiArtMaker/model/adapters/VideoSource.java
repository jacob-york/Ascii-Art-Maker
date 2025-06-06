package com.york.asciiArtMaker.model.adapters;

import java.util.Optional;

public interface VideoSource {

    /**
     * @return the width in pixels of the video.
     */
    int getWidth();

    /**
     * @return the height in pixels of the video.
     */
    int getHeight();

    /**
     * @return an Optional with the name of the video if it has one, otherwise it's empty.
     */
    Optional<String> getName();

    /**
     * @return the frames per second of the original video.
     */
    double getFPS();

    /**
     * @return the total number of frames in the original video.
     */
    int getFrameCount();

    /**
     * @return the ith image source in the video's frames.
     */
    ImageSource getImageSource(int i);

    /**
     * @return the original video's frames as an array of image sources.
     */
    ImageSource[] getImageSources();

    /**
     * @param startInd begin index of the range (inclusive)
     * @return the specified range of frames as an array of image sources.
     */
    ImageSource[] getImageSources(int startInd);

    /**
     * @param startInd begin index of the range (inclusive)
     * @param endInd end index of the range (exclusive)
     * @return the specified range of frames as an array of image sources.
     */
    ImageSource[] getImageSources(int startInd, int endInd);
}
