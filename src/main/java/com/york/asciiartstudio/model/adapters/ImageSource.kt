package com.york.asciiartstudio.model.adapters


/**
 * Interface for reading necessary image data.
 * Implements the Adapter design pattern:
 * for a new image format (be it a file, class, etc),
 * write a class that implements ImageSource, implement the methods,
 * and then pass that adapter to an AsciiArt.
 */
interface ImageSource {

    /**
     * @return the width in pixels of the video stream.
     */
    val width: Int

    /**
     * @return the height in pixels of the video stream.
     */
    val height: Int

    /**
     * @return The name of the image; or null if not applicable (i.e. object instances like BufferedImage).
     */
    val name: String?

    /**
     * @param x the desired pixel's column (read like a cartesian plane)
     * @param y the desired pixel's row (read like a cartesian plane)
     * @return an int value from 0 to 255 that represents the black-and-white color value at pixel (x, y),
     * or -1 if the pixel is completely transparent.
     */
    fun getBWValue(x: Int, y: Int): Int
}