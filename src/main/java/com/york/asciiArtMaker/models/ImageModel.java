package com.york.asciiArtMaker.models;

import com.york.asciiArtMaker.AsciiArtMaker;
import com.york.asciiArtMaker.asciiArt.AsciiArtBuilder;
import com.york.asciiArtMaker.asciiArt.AsciiImageBuilder;
import com.york.asciiArtMaker.controller.Controller;
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

    public void configureGUI(Controller controller) {
        controller.toolBar.setDisable(false);
        controller.fontField.setText(String.valueOf(controller.getAsciiArtPane().getFontSize()));
        controller.charWidthField.setText("1");

        controller.videoControlBox.setDisable(true);
        controller.compileVideoBtn.setDisable(true);
        controller.saveAsMp4Btn.setDisable(true);

        setCharWidth(Integer.parseInt(controller.charWidthField.getText()));
        setInvertedShading(controller.invertedShadingBtn.isSelected());

        controller.getAsciiArtPane().setText(getCurFrame());

        ((Stage) controller.borderPane.getScene().getWindow())
                .setTitle(getMediaName() + " - " + AsciiArtMaker.TITLE);
        controller.borderPane.setCenter(controller.getAsciiArtPane());
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
