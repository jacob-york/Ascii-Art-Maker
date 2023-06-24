package com.york.asciiartstudio.model.adapters

interface LiveStreamSource {

    /**
     * @return the width in pixels of the original video.
     */
    val width: Int

    /**
     * @return the height in pixels of the original video.
     */
    val height: Int

    /**
     * @return the frames per second of the original video.
     */
    val fps: Int

    /**
     * @return the current frame (at call time) of the live steam source as an ImageSource.
     */
    val currentFrame: ImageSource
}