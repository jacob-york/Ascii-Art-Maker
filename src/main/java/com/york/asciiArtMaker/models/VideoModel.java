package com.york.asciiArtMaker.models;

import com.york.asciiArtMaker.AsciiArtMaker;
import com.york.asciiArtMaker.asciiArt.AsciiArtBuilder;
import com.york.asciiArtMaker.asciiArt.AsciiVideoBuilder;
import com.york.asciiArtMaker.controller.Controller;
import com.york.asciiArtMaker.controller.VideoPlayer;
import com.york.asciiArtMaker.view.AsciiArtPane;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.util.Optional;

public class VideoModel implements AppModel {

    private final AsciiVideoBuilder art;
    private final VideoPlayer videoPlayer;

    private String[] artCache;

    public VideoModel(AsciiVideoBuilder art, Button playButton, AsciiArtPane asciiArtPane) {
        this.art = art;
        this.videoPlayer = new VideoPlayer(playButton, asciiArtPane, this);

        artCache = new String[art.getFrameCount()];
    }

    @Override
    public void close() {
        videoPlayer.pause();
        art.releaseNativeResources();
    }

    @Override
    public void configureGUI(Controller presenter) {
        presenter.toolBar.setDisable(false);
        presenter.fontField.setText(String.valueOf(presenter.getAsciiArtPane().getFontSize()));
        presenter.charWidthField.setText("1");

        presenter.videoControlBox.setDisable(false);
        presenter.compileVideoBtn.setDisable(false);
        presenter.saveAsMp4Btn.setDisable(false);

        setCharWidth(Integer.parseInt(presenter.charWidthField.getText()));
        setInvertedShading(presenter.invertedShadingBtn.isSelected());

        presenter.getAsciiArtPane().setText(getCurFrame());
        ((Stage) presenter.borderPane.getScene().getWindow())
                .setTitle(getMediaName() + " - " + AsciiArtMaker.TITLE);

        presenter.borderPane.setCenter(presenter.getAsciiArtPane());
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
        final double charWidthChange = ((double) newCharWidth / (double) art.getCharWidth());
        return curFontHeight * charWidthChange;
    }

    @Override
    public int getFrameCount() {
        return art.getFrameCount();
    }

    @Override
    public AsciiArtBuilder getArtBuilder() {
        return art;
    }

    @Override
    public String getCurFrame() {
        return getFrameAt(videoPlayer.getCurFrameInd());
    }

    @Override
    public Optional<String> getMediaName() {
        return art.getName();
    }

    @Override
    public int getCharWidth() {
        return art.getCharWidth();
    }

    @Override
    public boolean getInvertedShading() {
        return art.getInvertedShading();
    }

    @Override
    public int setCharWidth(int newCharWidth) {
        newCharWidth = (int) AppModel.ensureInRange(newCharWidth, 1, art.getMaxCharWidth());
        art.setCharWidth(newCharWidth);

        artCache = new String[getFrameCount()];
        return newCharWidth;
    }

    @Override
    public void setInvertedShading(boolean newVal) {
        art.setInvertedShading(newVal);
        artCache = new String[getFrameCount()];
    }

    public String getFrameAt(int i) {
        if (i < 0 || i >= getFrameCount()) return null;

        if (artCache[i] == null) {
            artCache[i] = art.buildFrame(i);
        }

        return artCache[i];
    }

    public String[] getCompiledArt() {
        for (int i = 0; i < artCache.length; i++) {
            if (artCache[i] == null) {
                artCache[i] = art.buildFrame(i);
            }
        }
        return artCache.clone();
    }

    public void compileArtFrom(int i) {
        if (i < 0 || i >= getFrameCount()) return;

        for (int j = i; j < getFrameCount(); j++) {
            artCache[j] = art.buildFrame(i);
        }
    }
}
