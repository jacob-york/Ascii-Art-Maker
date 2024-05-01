package com.york.asciiArtMaker.models;

import com.york.asciiArtMaker.AsciiArtMaker;
import com.york.asciiArtMaker.asciiArt.AsciiArtBuilder;
import com.york.asciiArtMaker.asciiArt.AsciiImage;
import com.york.asciiArtMaker.asciiArt.AsciiVideo;
import com.york.asciiArtMaker.asciiArt.AsciiVideoBuilder;
import com.york.asciiArtMaker.controller.Controller;
import com.york.asciiArtMaker.controller.VideoPlayer;
import com.york.asciiArtMaker.view.AsciiArtPane;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.util.Optional;

public class VideoModel implements AppModel {

    private final AsciiVideoBuilder builder;
    private final VideoPlayer videoPlayer;

    public VideoModel(AsciiVideoBuilder art, Button playButton, AsciiArtPane asciiArtPane) {
        this.builder = art;
        this.videoPlayer = new VideoPlayer(playButton, asciiArtPane, this);
    }

    @Override
    public void close() {
        videoPlayer.pause();
        builder.releaseNativeResources();
    }

    @Override
    public void configureGUI(Controller controller) {
        controller.toolBar.setDisable(false);
        controller.fontField.setText(String.valueOf(controller.getAsciiArtPane().getFontSize()));
        controller.charWidthField.setText("1");

        controller.videoControlBox.setDisable(false);
        controller.saveAsMp4Btn.setDisable(false);

        controller.exportTxtMenuItem.setDisable(false);
        controller.saveImageMenuItem.setDisable(false);
        controller.compileFramesMenuItem.setDisable(false);

        setCharWidth(Integer.parseInt(controller.charWidthField.getText()));
        setInvertedShading(controller.invertedShadingBtn.isSelected());

        controller.getAsciiArtPane().setText(getCurFrame());

        controller.setTitle(getMediaName().orElse("untitled") + " - " + AsciiArtMaker.TITLE);
        controller.borderPane.setCenter(controller.getAsciiArtPane());
    }

    @Override
    public void toggleVideoPlayerState() {
        videoPlayer.toggleState();
    }

    @Override
    public void prevFrame() {
        videoPlayer.prevFrame(this);
    }

    @Override
    public void nextFrame() {
        videoPlayer.nextFrame(this);
    }

    @Override
    public void pauseVideoPlayer() {
        videoPlayer.pause();
    }

    @Override
    public double calcNewFontHeight(int newCharWidth, double curFontHeight) {
        final double charWidthChange = ((double) newCharWidth / (double) builder.getCharWidth());
        return curFontHeight * charWidthChange;
    }

    @Override
    public int getFrameCount() {
        return builder.getFrameCount();
    }

    @Override
    public AsciiArtBuilder getArtBuilder() {
        return builder;
    }

    @Override
    public AsciiImage getCurFrame() {
        return getFrameAt(videoPlayer.getCurFrameInd());
    }

    @Override
    public Optional<String> getMediaName() {
        return builder.getName();
    }

    @Override
    public int getCharWidth() {
        return builder.getCharWidth();
    }

    @Override
    public boolean getInvertedShading() {
        return builder.getInvertedShading();
    }

    @Override
    public int setCharWidth(int newCharWidth) {
        newCharWidth = (int) AppModel.ensureInRange(newCharWidth, 1, builder.getMaxCharWidth());
        builder.setCharWidth(newCharWidth);

        return newCharWidth;
    }

    @Override
    public void setInvertedShading(boolean newVal) {
        builder.setInvertedShading(newVal);
    }

    public AsciiImage getFrameAt(int i) {
        if (i < 0 || i >= getFrameCount()) return null;

        return builder.buildFrame(i);
    }

    public AsciiVideo getCompiledArt() {
        return builder.build();
    }
}
