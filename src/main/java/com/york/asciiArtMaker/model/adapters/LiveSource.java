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
    double getFps();

    /**
     * @return the livestream's current frame at the time of calling, represented as an ImageSource.
     */
    ImageSource getCurrentImageSource();

    /**
     * Closes the LoveSource object and releases all of its resources
     */
    void close();
}
