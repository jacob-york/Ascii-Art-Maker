package com.york.asciiartstudio.models;

import com.york.asciiartstudio.AsciiArtStudio;
import com.york.asciiartstudio.asciiArt.AsciiArtBuilder;
import com.york.asciiartstudio.asciiArt.AsciiImageBuilder;
import com.york.asciiartstudio.controller.Controller;
import javafx.stage.Stage;

import java.util.Optional;

public class ImageModel implements AppModel {

    private final AsciiImageBuilder art;
    private String artCache;

    public ImageModel(AsciiImageBuilder art) {
        super();
        this.art = art;
        artCache = null;
    }

    @Override
    public void close() {

    }

    @Override
    public void configureGUI(Controller presenter) {
        presenter.fontField.setText(String.valueOf(presenter.getAsciiArtPane().getFontSize()));

        setCharWidth(Integer.parseInt(presenter.charWidthField.getText()));
        setInvertedShading(presenter.invertedShadingBtn.isSelected());

        presenter.charWidthField.setText(String.valueOf(getCharWidth()));

        presenter.getAsciiArtPane().setText(getCurFrame());
        ((Stage) presenter.borderPane.getScene().getWindow())
                .setTitle(getMediaName() + " - " + AsciiArtStudio.TITLE);

        presenter.borderPane.setCenter(presenter.getAsciiArtPane());
    }

    @Override
    public void toggleVideoPlayerState() {

    }

    @Override
    public void prevFrame() {

    }

    @Override
    public void nextFrame() {

    }

    @Override
    public void pauseVideoPlayer() {

    }

    @Override
    public double calcNewFontHeight(int newCharWidth, double curFontHeight) {
        final double charWidthChange = ((double) newCharWidth / (double) art.getCharWidth());
        return curFontHeight * charWidthChange;
    }

    @Override
    public int getFrameCount() {
        return 1;
    }

    @Override
    public AsciiArtBuilder getArtBuilder() {
        return art;
    }

    @Override
    public String getCurFrame() {
        if (artCache == null) {
            artCache = art.build();
        }
        return artCache;
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

        artCache = null;
        return newCharWidth;
    }

    @Override
    public void setInvertedShading(boolean newVal) {
        art.setInvertedShading(newVal);
        artCache = null;
    }
}
