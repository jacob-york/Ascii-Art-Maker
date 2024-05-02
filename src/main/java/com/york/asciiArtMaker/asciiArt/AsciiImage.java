package com.york.asciiArtMaker.asciiArt;

import javafx.scene.paint.Color;

public record AsciiImage(String toStr, String name, int charWidth, int width, int height, boolean invertedShading) {

    /**
     * @return the image's file name for writing to memory WITH the text and background color (not including file extension, which is assigned later).
     */
    public String getFileName(Color bgColor, Color textColor) {
        return String.format("%s-bg%s-txt%s", getFileName(), bgColor.toString(), textColor.toString());
    }

    /**
     * @return the image's file name for writing to memory (not including file extension, which is assigned later).
     */
    public String getFileName() {
        final String invertedMkr = invertedShading ? "-inv" : "";
        final String label = name.substring(0, name.lastIndexOf('.'));
        return String.format("%s-cw%d%s", label, charWidth, invertedMkr);
    }
}
