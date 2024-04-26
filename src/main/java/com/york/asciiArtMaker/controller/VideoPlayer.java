package com.york.asciiArtMaker.controller;

import com.york.asciiArtMaker.AsciiArtMaker;
import com.york.asciiArtMaker.asciiArt.AsciiVideoBuilder;
import com.york.asciiArtMaker.models.VideoModel;
import com.york.asciiArtMaker.view.AsciiArtPane;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
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

    private final Timeline playButtonLoop;
    private State state;
    private final Button playButton;
    private final AsciiArtPane asciiArtPane;
    private final AsciiVideoBuilder asciiVideoBuilder;
    private int curFrameInd;

    public VideoPlayer(Button playButton, AsciiArtPane asciiArtPane, VideoModel model) {
        state = State.VIDEO_PAUSED;

        this.playButton = playButton;
        this.asciiArtPane = asciiArtPane;
        this.asciiVideoBuilder = (AsciiVideoBuilder) model.getArtBuilder();

        playButtonLoop = new Timeline(new KeyFrame(Duration.seconds(1.0 / asciiVideoBuilder.getFps()),
                (ActionEvent event) -> nextFrame(model)));

        playButtonLoop.setCycleCount(Timeline.INDEFINITE);

        curFrameInd = 0;
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
        state = State.VIDEO_PLAYING;
    }

    public void pause() {
        playButtonLoop.stop();
        setPlayButtonIcon("play");
        state = State.VIDEO_PAUSED;
    }

    public void finishVideo() {
        playButtonLoop.stop();
        setPlayButtonIcon("replay");
        state = State.VIDEO_FINISHED;
    }

    public void replay() {
        curFrameInd = -1;
        play();
    }

    public int getCurFrameInd() {
        return curFrameInd;
    }

    public boolean nextFrame(VideoModel model) {
        if (curFrameInd + 1 >= asciiVideoBuilder.getFrameCount()) return false;
        curFrameInd++;
        asciiArtPane.setText(model.getFrameAt(curFrameInd));

        if (curFrameInd + 1 == asciiVideoBuilder.getFrameCount()) finishVideo();

        return true;
    }

    public boolean prevFrame(VideoModel model) {
        if (curFrameInd <= 0) return false;
        curFrameInd--;
        asciiArtPane.setText(model.getFrameAt(curFrameInd));

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
