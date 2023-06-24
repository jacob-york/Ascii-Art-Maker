package com.york.asciiartstudio.model.adapters

interface VideoSource {

    /**
     * @return the width in pixels of the video stream.
     */
    val width: Int

    /**
     * @return the height in pixels of the video stream.
     */
    val height: Int

    /**
     * @return the name of the video file, or null if not applicable.
     */
    val name: String?

    /**
     * @return the frames per second of the original video.
     */
    val fps: Int

    /**
     * @return the total number of frames in the original video.
     */
    val frameCount: Int

    /**
     * @return the original video's frames as an array of image sources.
     */
    val imageSources: Array<ImageSource>
}