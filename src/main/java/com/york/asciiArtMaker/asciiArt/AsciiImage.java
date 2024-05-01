package com.york.asciiArtMaker.asciiArt;

import javafx.scene.paint.Color;

public record AsciiImage(String toStr, String name, int charWidth, int width, int height, boolean invertedShading) {

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
        return String.format("%s-cw%d%s", label, charWidth, invertedMkr);
    }
}
