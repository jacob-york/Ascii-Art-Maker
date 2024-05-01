package com.york.asciiArtMaker.asciiArt;

import javafx.scene.paint.Color;

public record AsciiVideo(String[] frames, String name, int charWidth, int width, int height, boolean invertedShading, double fps) {

    /**
     * DOES NOT INCLUDE ANY FILE NAME.
     * @return
     */
    public String getFileName(Color bgColor, Color textColor) {
        return String.format("%s-bg%s-txt%s", getFileName(), bgColor.toString(), textColor.toString());
    }

    public String getFileName() {
        final String invertedMkr = invertedShading ? "-inv" : "";
        final String label = name.substring(0, name.lastIndexOf('.'));
        return String.format("%s-cw%d%s-%,.2f", label, charWidth, invertedMkr, fps);
    }

    public AsciiImage getAsciiImage(int i) {
        return new AsciiImage(frames[i], name + ":" + i, charWidth, width, height, invertedShading);
    }
}
