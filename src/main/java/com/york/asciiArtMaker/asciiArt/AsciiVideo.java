package com.york.asciiArtMaker.asciiArt;

public record AsciiVideo(String[] frames, String name, int charWidth, int width, int height, boolean invertedShading, double fps) {

    public String getFileName() {
        final String invertedMkr = invertedShading ? "-inv" : "";
        final String label = name.substring(0, name.lastIndexOf('.'));
        return String.format("%s-cw%d%s-%,.2f", label, charWidth, invertedMkr, fps);
    }

    public AsciiImage getAsciiImage(int i) {
        return new AsciiImage(frames[i], name + ":" + i, charWidth, width, height, invertedShading);
    }
}
