package com.york.asciiArtMaker.model.asciiArt;

import com.york.asciiArtMaker.model.adapters.VideoFileConnectionService;
import com.york.asciiArtMaker.model.adapters.VideoSource;

import java.util.Optional;
import java.util.stream.IntStream;

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
    private AsciiImage[] artCache;

    public AsciiVideoBuilder(VideoSource videoSource) {
        this.videoSource = videoSource;
        this.charWidth = 1;
        this.invertedShading = false;
        this.name = videoSource.getName().orElse(null);
        this.palette = defaultPalette;
        this.activePalette = palette;
        this.frameCount = videoSource.getFrameCount();
        this.fps = videoSource.getFps();

        artCache = new AsciiImage[frameCount];
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
    public int getSourceWidth() {
        return videoSource.getWidth();
    }

    @Override
    public int getSourceHeight() {
        return videoSource.getHeight();
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

        artCache = new AsciiImage[getFrameCount()];
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
        artCache = new AsciiImage[getFrameCount()];
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
        artCache = new AsciiImage[getFrameCount()];
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
        artCache = new AsciiImage[getFrameCount()];
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
    public AsciiImage buildFrame(int i) throws IndexOutOfBoundsException {
        if (i < 0 || i >= frameCount) throw new IndexOutOfBoundsException();

        if (artCache[i] == null) {
            artCache[i] = new AsciiImageBuilder(videoSource.getImageSource(i))
                    .setName(getName().orElse("ascii-video") + "-" + i)
                    .setCharWidth(charWidth)
                    .setInvertedShading(invertedShading)
                    .setPalette(palette)
                    .build();
        }

        return artCache[i];
    }

    public int getFrameCount() {
        return frameCount;
    }

    /**
     * @return compiles the entire video into an array of ascii Images (frames).
     */
    public AsciiVideo build() {
        return build(0, frameCount);
    }

    /**
     * @param startInd start of frame range (inclusive)
     * @return all frames from startInd to the end of the imageSources.
     */
    public AsciiVideo build(int startInd) throws IndexOutOfBoundsException {
        return build(startInd, frameCount);
    }

    /**
     * @param startInd start of frame range (inclusive)
     * @param endInd end of frame range (exclusive)
     * @return all frames from startInd -> endInd
     */
    public AsciiVideo build(int startInd, int endInd) throws IndexOutOfBoundsException {
        if (startInd < 0 || startInd >= frameCount) throw new IndexOutOfBoundsException();
        if (endInd < 0 || endInd > frameCount) throw new IndexOutOfBoundsException();
        if (endInd < startInd) throw new IndexOutOfBoundsException();

        IntStream.range(startInd, endInd)
                .parallel()
                .forEach(this::buildFrame);

        return new AsciiVideo(artCache.clone(), getName().orElse("ascii-video"), getFps());
    }

    public double getFps() {
        return fps;
    }

    /**
     * calls release on this builder's VideoSource. DO NOT CALL until you're done with your builder.
     */
    public void releaseVideoSource() {
        if (videoSource instanceof VideoFileConnectionService.VideoFileAdapter vfa) {
            vfa.release();
        }
    }
}