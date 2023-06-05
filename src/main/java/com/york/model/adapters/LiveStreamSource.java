package com.york.model.adapters;

// TODO: documentation
public interface LiveStreamSource {

    /**
     *
     * @return the width in pixels of the video stream.
     */
    int getWidthPixels();

    /**
     *
     * @return the height in pixels of the video stream.
     */
    int getHeightPixels();

    /**
     *
     * @return the frames per second of the video stream.
     */
    int getFPS();

    /**
     *
     * @return the current frame (at call time) of the live steam source as an ImageSource.
     */
    ImageSource getCurrentFrame();
}
