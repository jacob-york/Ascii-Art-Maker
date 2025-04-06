package com.york.asciiArtMaker.model.asciiArt;

import javafx.scene.paint.Color;

public record AsciiVideo(AsciiImage[] frames, String name, double fps) {

    /**
     * @return the video's file menuName for writing to memory WITH the text and background color (not including file extension, which is assigned later).
     */
    public String getFileName(Color bgColor, Color textColor) {
        return String.format("%s-bg%s-txt%s", getFileName(), bgColor.toString(), textColor.toString());
    }

    /**
     * @return the video's file menuName for writing to memory (not including file extension, which is assigned later).
     */
    public String getFileName() {
        final String invertedMkr = invertedShading() ? "-inv" : "";
        final String label = name.substring(0, name.lastIndexOf('.'));
        return String.format("%s-cw%d%s-%,.2f", label, charWidth(), invertedMkr, fps);
    }

    public int charWidth() {
        return frames()[0].charWidth();
    }

    public int width() {
        return frames()[0].width();
    }

    public int height() {
        return frames()[0].height();
    }

    public boolean invertedShading() {
        return frames()[0].invertedShading();
    }
}
