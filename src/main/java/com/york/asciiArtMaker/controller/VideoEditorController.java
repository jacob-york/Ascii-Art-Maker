package com.york.asciiArtMaker.controller;

import com.york.asciiArtMaker.model.VideoPlayer;
import com.york.asciiArtMaker.model.adapters.VideoSource;
import com.york.asciiArtMaker.model.asciiArt.AsciiImage;
import com.york.asciiArtMaker.model.asciiArt.AsciiVideoBuilder;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;

public class VideoEditorController extends AsciiEditorController {

    private VideoPlayer videoPlayer;
    private VideoPlayer.State prevState;
    @FXML
    public Button togglePlayButton;
    @FXML
    public Button frameForwardButton;
    @FXML
    public Button frameBackwardButton;
    @FXML
    public Slider videoProgressSlider;

    @Override
    public void initialize() {
        super.initialize();
        videoPlayer = new VideoPlayer(togglePlayButton, frameForwardButton, frameBackwardButton, videoProgressSlider, asciiViewportPane);
    }

    @Override
    AsciiImage getAsciiImageFrame() {
        return ((AsciiVideoBuilder) asciiArtBuilder).buildFrame(videoPlayer.getCurFrameInd());
    }

    @Override
    protected void configureAppCharWidth(int newCharWidth) {
        super.configureAppCharWidth(newCharWidth);
        if (videoPlayer != null && videoPlayer.getState() != VideoPlayer.State.VIDEO_PLAYING) {
            asciiViewportPane.updateExistingContent(getAsciiImageFrame());
        }
    }

    @Override
    protected void configureAppInvertedShading(boolean newInvertedShading) {
        super.configureAppInvertedShading(newInvertedShading);
        if (videoPlayer != null && videoPlayer.getState() != VideoPlayer.State.VIDEO_PLAYING) {
            asciiViewportPane.unsafeSetContent(getAsciiImageFrame());
        }
    }

    public void setVideoSource(VideoSource videoSource) {
        asciiArtBuilder = new AsciiVideoBuilder(videoSource)
                .setCharWidth(INIT_CHAR_WIDTH)
                .setInvertedShading(activeColorTheme.invertedShading);
        videoPlayer.setVideoBuilder((AsciiVideoBuilder) asciiArtBuilder);
        charWidthField.setText(String.valueOf(INIT_CHAR_WIDTH));
        asciiViewportPane.setNewContent(getAsciiImageFrame());
        fineZoomBase = asciiViewportPane.getContentZoom();
        zoomSlider.setValue(0);
    }

    @FXML
    public void togglePlayAction() {
        videoPlayer.toggleState();
    }

    @FXML
    public void frameForwardAction() {
        if (videoPlayer.getState() != VideoPlayer.State.VIDEO_PLAYING) {
            videoPlayer.nextFrame();
        }
    }

    @FXML
    public void frameBackwardAction() {
        if (videoPlayer.getState() != VideoPlayer.State.VIDEO_PLAYING) {
            videoPlayer.prevFrame();
        }
    }

    @FXML
    public void grabSlider() {
        prevState = videoPlayer.getState();
        videoPlayer.pause();
        videoPlayer.setCurFrameInd((int) videoProgressSlider.getValue());
    }

    @FXML
    public void dragSlider() {
        videoPlayer.setCurFrameInd((int) videoProgressSlider.getValue());
    }

    @FXML
    public void releaseSlider() {
        if (videoProgressSlider.getValue() == videoProgressSlider.getMax())
            videoPlayer.finishVideo();
        else if (prevState != VideoPlayer.State.VIDEO_PAUSED)
            videoPlayer.play();
    }

    @Override
    public void bindShortcuts(Scene scene) {
        super.bindShortcuts(scene);
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.SPACE), this::togglePlayAction);
    }
}
