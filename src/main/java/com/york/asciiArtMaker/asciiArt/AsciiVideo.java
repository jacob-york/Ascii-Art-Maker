package com.york.asciiArtMaker.asciiArt;

import javafx.scene.paint.Color;

public record AsciiVideo(String[] frames, String name, int charWidth, int width, int height, boolean invertedShading, double fps) {

    /**
     * @return the video's file name for writing to memory WITH the text and background color (not including file extension, which is assigned later).
     */
    public String getFileName(Color bgColor, Color textColor) {
        return String.format("%s-bg%s-txt%s", getFileName(), bgColor.toString(), textColor.toString());
    }

    /**
     * @return the video's file name for writing to memory (not including file extension, which is assigned later).
     */
    public String getFileName() {
        final String invertedMkr = invertedShading ? "-inv" : "";
        final String label = name.substring(0, name.lastIndexOf('.'));
        return String.format("%s-cw%d%s-%,.2f", label, charWidth, invertedMkr, fps);
    }

    /**
     * @param i the frame index for a specific ascii art frame in the video.
     * @return an AsciiImage view into the requested frame (since they're stored internally as Strings).
     */
    public AsciiImage getAsciiImage(int i) {
        return new AsciiImage(frames[i], name + ":" + i, charWidth, width, height, invertedShading);
    }
}
