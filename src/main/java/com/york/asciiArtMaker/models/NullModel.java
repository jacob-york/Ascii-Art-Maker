package com.york.asciiArtMaker.models;

import com.york.asciiArtMaker.AsciiArtMaker;
import com.york.asciiArtMaker.asciiArt.AsciiArtBuilder;
import com.york.asciiArtMaker.asciiArt.AsciiImage;
import com.york.asciiArtMaker.asciiArt.AsciiVideo;
import com.york.asciiArtMaker.controller.Controller;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Optional;

public class NullModel implements AppModel {

    public NullModel() {
    }

    @Override
    public void close() {

    }

    @Override
    public void configureGUI(Controller controller) {
        controller.toolBar.setDisable(true);
        controller.fontField.setText("");
        controller.charWidthField.setText("");

        controller.exportTxtMenuItem.setDisable(true);
        controller.saveImageMenuItem.setDisable(true);

        ((Stage) controller.borderPane.getScene().getWindow())
                .setTitle(AsciiArtMaker.TITLE);

        controller.borderPane.setCenter(new Text("No Media Selected."));
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
        return 0;
    }

    @Override
    public int getFrameCount() {
        return 0;
    }

    @Override
    public AsciiArtBuilder getArtBuilder() {
        return null;
    }

    @Override
    public AsciiImage getCurFrame() {
        return null;
    }

    @Override
    public Optional<String> getMediaName() {
        return Optional.empty();
    }

    @Override
    public int getCharWidth() {
        return 0;
    }

    @Override
    public int setCharWidth(int newCharWidth) {
        return 0;
    }

    @Override
    public void setInvertedShading(boolean newVal) {

    }

    @Override
    public boolean getInvertedShading() {
        return false;
    }

}
