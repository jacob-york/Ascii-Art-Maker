package com.york.asciiArtMaker.model.models;

import com.york.asciiArtMaker.AsciiArtMaker;
import com.york.asciiArtMaker.model.asciiArt.AsciiArtBuilder;
import com.york.asciiArtMaker.model.asciiArt.AsciiImage;
import com.york.asciiArtMaker.model.asciiArt.AsciiVideo;
import com.york.asciiArtMaker.model.asciiArt.AsciiVideoBuilder;
import com.york.asciiArtMaker.controller.MainController;
import com.york.asciiArtMaker.controller.VideoPlayer;
import com.york.asciiArtMaker.view.AsciiArtPane;
import javafx.scene.control.Button;

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
        builder.releaseVideoSource();
    }

    @Override
    public void configureGUI(MainController mainController) {
        mainController.toolBar.setDisable(false);
        mainController.fontField.setText(String.valueOf(mainController.getAsciiArtPane().getFontSize()));
        mainController.charWidthField.setText("1");

        mainController.videoControlBox.setDisable(false);
        mainController.saveMp4Btn.setDisable(false);

        mainController.exportTxtMenuItem.setDisable(false);
        mainController.saveImageMenuItem.setDisable(false);
        mainController.saveMp4MenuItem.setDisable(false);
        mainController.compileFramesMenuItem.setDisable(false);

        setCharWidth(Integer.parseInt(mainController.charWidthField.getText()));
        setInvertedShading(mainController.invertedShadingBtn.isSelected());

        mainController.getAsciiArtPane().setText(getCurFrame());

        mainController.setTitle(getMediaName().orElse("untitled") + " - " + AsciiArtMaker.TITLE);
        mainController.borderPane.setCenter(mainController.getAsciiArtPane());
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

    public AsciiVideo compileArt() {
        return builder.build();
    }
}
