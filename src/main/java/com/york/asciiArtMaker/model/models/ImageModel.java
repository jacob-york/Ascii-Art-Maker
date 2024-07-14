package com.york.asciiArtMaker.model.models;

import com.york.asciiArtMaker.AsciiArtMaker;
import com.york.asciiArtMaker.model.asciiArt.AsciiArtBuilder;
import com.york.asciiArtMaker.model.asciiArt.AsciiImage;
import com.york.asciiArtMaker.model.asciiArt.AsciiImageBuilder;
import com.york.asciiArtMaker.controller.MainController;

import java.util.Optional;

public class ImageModel implements AppModel {

    private final AsciiImageBuilder builder;


    public ImageModel(AsciiImageBuilder art) {
        super();
        this.builder = art;
    }

    @Override
    public void close() {

    }

    public void configureGUI(MainController mainController) {
        mainController.toolBar.setDisable(false);
        mainController.fontField.setText(String.valueOf(mainController.getAsciiArtPane().getFontSize()));
        mainController.charWidthField.setText("1");

        mainController.videoControlBox.setDisable(true);
        mainController.saveMp4Btn.setDisable(true);

        mainController.exportTxtMenuItem.setDisable(false);
        mainController.saveImageMenuItem.setDisable(false);
        mainController.saveMp4MenuItem.setDisable(true);
        mainController.compileFramesMenuItem.setDisable(true);

        setCharWidth(Integer.parseInt(mainController.charWidthField.getText()));
        setInvertedShading(mainController.invertedShadingBtn.isSelected());

        mainController.getAsciiArtPane().setText(getCurFrame());

        mainController.setTitle(getMediaName().orElse("untitled") + " - " + AsciiArtMaker.TITLE);
        mainController.borderPane.setCenter(mainController.getAsciiArtPane());
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
        final double charWidthChange = ((double) newCharWidth / (double) builder.getCharWidth());
        return curFontHeight * charWidthChange;
    }

    @Override
    public int getFrameCount() {
        return 1;
    }

    @Override
    public AsciiArtBuilder getArtBuilder() {
        return builder;
    }

    @Override
    public AsciiImage getCurFrame() {
        return builder.build();
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
}
