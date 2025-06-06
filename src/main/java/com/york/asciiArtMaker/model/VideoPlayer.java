package com.york.asciiArtMaker.model;

import com.york.asciiArtMaker.AsciiArtMaker;
import com.york.asciiArtMaker.model.asciiArt.AsciiImage;
import com.york.asciiArtMaker.model.asciiArt.AsciiVideoBuilder;
import com.york.asciiArtMaker.view.AsciiViewportPane;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 * Coordinates the switching of frames on an asciiArtPane.
 */
public class VideoPlayer {

    public enum State {
        VIDEO_PAUSED,
        VIDEO_PLAYING,
        VIDEO_FINISHED
    }

    private Timeline playButtonLoop;
    private State state;
    private final Button playButton;
    private final Button frameForwardButton;
    private final Button frameBackwardButton;
    private final Slider slider;

    private final AsciiViewportPane asciiViewportPane;
    private AsciiVideoBuilder asciiVideoBuilder;
    private int curFrameInd;

    public VideoPlayer(Button playButton, Button frameForwardButton, Button frameBackwardButton, Slider slider,
                       AsciiViewportPane asciiViewportPane) {
        state = State.VIDEO_PAUSED;

        this.playButton = playButton;
        this.frameForwardButton = frameForwardButton;
        this.frameBackwardButton = frameBackwardButton;
        this.slider = slider;
        this.asciiViewportPane = asciiViewportPane;
    }

    public void setVideoBuilder(AsciiVideoBuilder asciiVideoBuilder) {
        if (playButtonLoop != null) pause();
        this.asciiVideoBuilder = asciiVideoBuilder;
        playButtonLoop = new Timeline(new KeyFrame(Duration.seconds(1.0 / asciiVideoBuilder.getFps()),
                (ActionEvent event) -> nextFrame()));
        playButtonLoop.setCycleCount(Timeline.INDEFINITE);

        curFrameInd = 0;
        slider.setValue(0);
        slider.setMax(asciiVideoBuilder.getFrameCount()-1);
    }

    public State getState() {
        return state;
    }

    private void setPlayButtonIcon(String name) {
        ImageView imageView = new ImageView(
                new Image(AsciiArtMaker.getResourcePath("icons/" + name + ".png")));
        imageView.setFitWidth(20);
        imageView.setFitHeight(20);
        playButton.setGraphic(imageView);
    }

    public void play() {
        playButtonLoop.play();
        setPlayButtonIcon("pause");
        frameForwardButton.setDisable(true);
        frameBackwardButton.setDisable(true);

        state = State.VIDEO_PLAYING;
    }

    public void pause() {
        playButtonLoop.stop();
        setPlayButtonIcon("play");
        frameForwardButton.setDisable(false);
        frameBackwardButton.setDisable(false);

        state = State.VIDEO_PAUSED;
    }

    public void finishVideo() {
        playButtonLoop.stop();
        setPlayButtonIcon("replay");
        frameForwardButton.setDisable(false);
        frameBackwardButton.setDisable(false);

        state = State.VIDEO_FINISHED;
    }

    public void replay() {
        curFrameInd = -1;
        frameForwardButton.setDisable(true);
        frameBackwardButton.setDisable(true);
        play();
    }

    public int getCurFrameInd() {
        return curFrameInd;
    }

    public void setCurFrameInd(int newFrameInd) {
        curFrameInd = newFrameInd;
        AsciiImage asciiImage = asciiVideoBuilder.buildFrame(curFrameInd);
        asciiViewportPane.updateExistingContent(asciiImage);
    }

    public boolean nextFrame() {
        if (curFrameInd + 1 >= asciiVideoBuilder.getFrameCount())
            return false;

        slider.setValue(++curFrameInd);
        AsciiImage asciiImage = asciiVideoBuilder.buildFrame(curFrameInd);
        asciiViewportPane.updateExistingContent(asciiImage);

        if (curFrameInd + 1 == asciiVideoBuilder.getFrameCount())
            finishVideo();

        return true;
    }

    public boolean prevFrame() {
        if (curFrameInd <= 0) return false;

        slider.setValue(--curFrameInd);
        AsciiImage asciiImage = asciiVideoBuilder.buildFrame(curFrameInd);
        asciiViewportPane.updateExistingContent(asciiImage);

        if (state == State.VIDEO_FINISHED) {
            state = State.VIDEO_PAUSED;
            setPlayButtonIcon("play");
        }

        return true;
    }

    public void toggleState() {
        switch (state) {
            case VIDEO_PLAYING -> pause();
            case VIDEO_PAUSED -> play();
            case VIDEO_FINISHED -> replay();
        }
    }
}
