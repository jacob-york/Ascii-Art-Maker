package com.york.asciiArtMaker.models;

import com.york.asciiArtMaker.asciiArt.AsciiArtBuilder;
import com.york.asciiArtMaker.controller.Controller;

import java.util.Optional;

// todo: I think I can do better.
/**
 * The MODEL part of MVC is itself an implementation of the State design pattern, cycling between different models
 * with different behavior when necessary.
 */
public interface AppModel {

    /**
     * <p>Ensures a value is between a MIN and a MAX.</p>
     *
     * <p>Given a VALUE, ensureRange() will return
     *  MIN if the VALUE is lower than MIN,
     *  MAX if the VALUE is higher than MAX,
     *  and VALUE otherwise.</p>
     */
    static double ensureInRange(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }

    void close();

    void configureGUI(Controller presenter);

    void toggleVideoPlayerState();

    void prevFrame();

    void nextFrame();

    void pauseVideoPlayer();

    double calcNewFontHeight(int newCharWidth, double curFontHeight);

    int getFrameCount();

    AsciiArtBuilder getArtBuilder();

    String getCurFrame();

    Optional<String> getMediaName();

    int getCharWidth();

    int setCharWidth(int newCharWidth);

    void setInvertedShading(boolean newVal);

    boolean getInvertedShading();
}
