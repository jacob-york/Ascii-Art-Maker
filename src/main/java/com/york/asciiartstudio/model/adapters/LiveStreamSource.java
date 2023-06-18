package com.york.asciiartstudio.model.adapters;

public interface LiveStreamSource {
    /**
     *
     * @return the width in pixels of the original video.
     */
    int getWidth();

    /**
     *
     * @return the height in pixels of the original video.
     */
    int getHeight();

    /**
     *
     * @return the frames per second of the original video.
     */
    int getFPS();

    /**
     *
     * @return the current frame (at call time) of the live steam source as an ImageSource.
     */
    ImageSource getCurrentFrame();
}
