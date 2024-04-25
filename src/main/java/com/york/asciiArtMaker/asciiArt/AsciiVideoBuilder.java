package com.york.asciiArtMaker.asciiArt;

import com.york.asciiArtMaker.adapters.MatListFactory;
import com.york.asciiArtMaker.adapters.VideoSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class AsciiVideoBuilder implements AsciiArtBuilder {

    private int charWidth;
    private String name;
    private final VideoSource videoSource;
    private final String defaultPalette = "@N#bhyo+s/=-:.  ";
    private String activePalette;
    private String palette;
    private boolean invertedShading;
    private final int frameCount;
    private final double fps;

    public AsciiVideoBuilder(VideoSource videoSource) {
        this.videoSource = videoSource;
        this.charWidth = 1;
        this.invertedShading = false;
        this.name = videoSource.getName().orElse(null);
        this.palette = defaultPalette;
        this.activePalette = palette;
        this.frameCount = videoSource.getFrameCount();
        this.fps = videoSource.getFps();
    }

    @Override
    public int getWidth() {
        return (videoSource.getWidth() - videoSource.getWidth() % charWidth) / charWidth;
    }

    @Override
    public int getHeight() {
        return (videoSource.getHeight() - videoSource.getHeight() % (2 * charWidth)) / (2 * charWidth);
    }

    @Override
    public int getMaxCharWidth() {
        boolean ge2 = videoSource.getHeight() / videoSource.getWidth() >= 2;
        return ge2 ? videoSource.getWidth() : videoSource.getHeight() / 2;
    }

    @Override
    public int getCharWidth() {
        return charWidth;
    }

    @Override
    public AsciiVideoBuilder reset() {
        this.charWidth = 1;
        this.invertedShading = false;
        this.palette = defaultPalette;
        this.activePalette = palette;

        return this;
    }

    @Override
    public AsciiVideoBuilder setCharWidth(int charWidth) {
        if (charWidth > getMaxCharWidth()) {
            throw new IllegalArgumentException("Char width cannot be greater than max char width: " + getMaxCharWidth());
        }
        if (charWidth < 1) {
            throw new IllegalArgumentException("Char width must be greater than 0.");
        }

        this.charWidth = charWidth;

        return this;
    }

    @Override
    public String getPalette() {
        return palette;
    }

    @Override
    public AsciiVideoBuilder setPalette(String palette) {
        if (256 % palette.length() != 0) {
            throw new IllegalArgumentException("Char count in Palette must be divisible by 256.");
        }

        this.palette = palette;
        this.activePalette = invertedShading ? new StringBuilder(palette).reverse().toString() : palette;

        return this;
    }

    @Override
    public Optional<String> getName() {
        return Optional.of(name);
    }

    @Override
    public AsciiVideoBuilder setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public boolean getInvertedShading() {
        return invertedShading;
    }

    @Override
    public AsciiVideoBuilder setInvertedShading(boolean invertedShading) {
        if (this.invertedShading == invertedShading) return this;

        this.activePalette = new StringBuilder(activePalette).reverse().toString();
        this.invertedShading = invertedShading;
        return this;
    }

    @Override
    public boolean isUsingDefaultPalette() {
        return palette.equals(defaultPalette);
    }

    /**
     * @return the ith frame of the art.
     * @throws IndexOutOfBoundsException if the index i is out of bounds
     */
    public String buildFrame(int i) throws IndexOutOfBoundsException {
        if (i < 0 || i >= videoSource.getFrameCount()) throw new IndexOutOfBoundsException();

        return new AsciiImageBuilder(videoSource.getImageSource(i))
                .setCharWidth(charWidth)
                .setInvertedShading(invertedShading)
                .setPalette(palette)
                .build();
    }

    public int getFrameCount() {
        return frameCount;
    }

    /**
     * @return compiles the entire video into an array of ascii art strings (frames).
     */
    public String[] build() {
        return build(0, frameCount);
    }

    /**
     *
     * @param startInd start of frame range (inclusive)
     * @return all frames from startInd to the end of the imageSources.
     */
    public String[] build(int startInd) {
        return build(startInd, frameCount);
    }

    /**
     *
     * @param startInd start of frame range (inclusive)
     * @param endInd end of frame range (exclusive)
     * @return all frames from startInd -> endInd
     */
    public String[] build(int startInd, int endInd) {
        return Arrays.stream(videoSource.getImageSources(startInd, endInd))
                .parallel()
                .map(imageSource -> new AsciiImageBuilder(imageSource)
                        .setCharWidth(charWidth)
                        .setInvertedShading(invertedShading)
                        .setPalette(palette)
                        .build()
                )
                .toArray(String[]::new);
    }

    public double getFps() {
        return fps;
    }

    public void releaseNativeResources() {
        if (videoSource instanceof MatListFactory.VideoFileAdapter vfa) {
            vfa.releaseNativeResources();
        }
    }
}