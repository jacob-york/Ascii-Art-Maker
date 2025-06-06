package com.york.asciiArtMaker.model.adapters;

import java.util.Optional;

public interface LiveSource {

    /**
     * @return the width in pixels of the livestream.
     */
    int getWidth();

    /**
     * @return the height in pixels of the livestream.
     */
    int getHeight();

    /**
     * @return an Optional with the name of the live stream if it has one, otherwise it's empty.
     */
    Optional<String> getName();

    /**
     * @return the frames per second of the live stream.
     */
    double getFPS();

    /**
     * @return the livestream's current frame at the time of calling, represented as an ImageSource.
     */
    ImageSource getCurrentImageSource();

    /**
     * Pauses the liveSource on this interface's level (i.e. it doesn't actually halt the physical live source,
     * just this LiveSource interface/facade's connection to it).
     */
    void pause();

    /**
     * unpauses the liveSource on this interface's level (i.e. it reopens this interface's connection to the physical
     * live source).
     */
    void unpause();

    /**
     * Closes the LoveSource object and releases all of its resources
     */
    void close();
}
