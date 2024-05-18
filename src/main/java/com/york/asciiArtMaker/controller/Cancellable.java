package com.york.asciiArtMaker.controller;

public interface Cancellable {
    /**
     * @return true if the cancel was successful.
     */
    boolean cancel();
}
