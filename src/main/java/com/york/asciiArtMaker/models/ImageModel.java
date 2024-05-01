package com.york.asciiArtMaker.models;

import com.york.asciiArtMaker.AsciiArtMaker;
import com.york.asciiArtMaker.asciiArt.AsciiArtBuilder;
import com.york.asciiArtMaker.asciiArt.AsciiImage;
import com.york.asciiArtMaker.asciiArt.AsciiImageBuilder;
import com.york.asciiArtMaker.controller.Controller;
import javafx.stage.Stage;

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

    public void configureGUI(Controller controller) {
        controller.toolBar.setDisable(false);
        controller.fontField.setText(String.valueOf(controller.getAsciiArtPane().getFontSize()));
        controller.charWidthField.setText("1");

        controller.videoControlBox.setDisable(true);
        controller.saveAsMp4Btn.setDisable(true);

        controller.exportTxtMenuItem.setDisable(false);
        controller.saveImageMenuItem.setDisable(false);

        setCharWidth(Integer.parseInt(controller.charWidthField.getText()));
        setInvertedShading(controller.invertedShadingBtn.isSelected());

        controller.getAsciiArtPane().setText(getCurFrame());

        ((Stage) controller.borderPane.getScene().getWindow())
                .setTitle(getMediaName().orElse("untitled") + " - " + AsciiArtMaker.TITLE);
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
