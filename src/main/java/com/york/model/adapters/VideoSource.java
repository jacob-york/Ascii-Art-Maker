package com.york.model.adapters;

public interface VideoSource {

    /**
     *
     * @return the width in pixels of the original video.
     */
    int getWidthPixels();

    /**
     *
     * @return the height in pixels of the original video.
     */
    int getHeightPixels();

    /**
     *
     * @return the frames per second of the original video.
     */
    int getFPS();

    /**
     *
     * @return the total number of frames in the original video.
     */
    int getFrameCount();

    /**
     *
     * @return the original video's frames as an array of image sources.
     */
    ImageSource[] getImageSourceArray();
}
