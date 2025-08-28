package com.york.asciiArtMaker.model.asciiArt;

import com.york.asciiArtMaker.model.adapters.VideoFileConnectionTask;
import com.york.asciiArtMaker.model.adapters.VideoSource;

import java.util.Optional;
import java.util.stream.IntStream;

public class AsciiVideoBuilder implements AsciiArtBuilder {

    private VideoSource videoSource;
    private String name;
    private final AsciiImageBuilder asciiImageBuilder;
    private AsciiImage[] artCache;

    public AsciiVideoBuilder(VideoSource videoSource) {
        this.videoSource = videoSource;
        this.name = videoSource.getName().orElse(null);

        asciiImageBuilder = new AsciiImageBuilder(videoSource.getImageSource(0));
        artCache = new AsciiImage[getFrameCount()];
    }

    @Override
    public int getArtWidth() {
        return asciiImageBuilder.getArtWidth();
    }

    @Override
    public int getArtHeight() {
        return asciiImageBuilder.getArtHeight();
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
        return asciiImageBuilder.getMaxCharWidth();
    }

    @Override
    public AsciiVideoBuilder reset() {
        asciiImageBuilder.reset();
        artCache = new AsciiImage[getFrameCount()];
        return this;
    }

    public VideoSource getVideoSource() {
        return videoSource;
    }

    public AsciiVideoBuilder setVideoSource(VideoSource videoSource) {
        asciiImageBuilder.setImageSource(videoSource.getImageSource(0));  // IllegalArgumentException expected
        this.videoSource = videoSource;
        if (name == null) name = videoSource.getName().orElse(null);
        artCache = new AsciiImage[getFrameCount()];
        return this;
    }

    @Override
    public int getCharWidth() {
        return asciiImageBuilder.getCharWidth();
    }

    @Override
    public AsciiVideoBuilder setCharWidth(int charWidth) {
        asciiImageBuilder.setCharWidth(charWidth);
        artCache = new AsciiImage[getFrameCount()];
        return this;
    }

    @Override
    public String getPalette() {
        return asciiImageBuilder.getPalette();
    }

    @Override
    public AsciiVideoBuilder setPalette(String palette) {
        asciiImageBuilder.setPalette(palette);
        artCache = new AsciiImage[getFrameCount()];
        return this;
    }

    @Override
    public Optional<String> getArtName() {
        return Optional.of(name);
    }

    @Override
    public AsciiVideoBuilder setArtName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public boolean getInvertedShading() {
        return asciiImageBuilder.getInvertedShading();
    }

    @Override
    public AsciiVideoBuilder setInvertedShading(boolean invertedShading) {
        asciiImageBuilder.setInvertedShading(invertedShading);
        artCache = new AsciiImage[getFrameCount()];

        return this;
    }

    @Override
    public boolean isUsingDefaultPalette() {
        return asciiImageBuilder.isUsingDefaultPalette();
    }

    /**
     * @return the ith frame of the art.
     * @throws IndexOutOfBoundsException if the index i is out of bounds
     */
    public AsciiImage buildFrame(int i) throws IndexOutOfBoundsException {
        if (i < 0 || i >= getFrameCount()) throw new IndexOutOfBoundsException();

        if (artCache[i] == null) {
            artCache[i] = asciiImageBuilder.setImageSource(videoSource.getImageSource(i)).build();
        }

        return artCache[i];
    }

    public int getFrameCount() {
        return videoSource.getFrameCount();
    }

    /**
     * @return compiles the entire video into an array of ascii Images (frames).
     */
    public AsciiVideo build() {
        return build(0, getFrameCount());
    }

    /**
     * @param startInd start of frame range (inclusive)
     * @return all frames from startInd to the end of the imageSources.
     */
    public AsciiVideo build(int startInd) throws IndexOutOfBoundsException {
        return build(startInd, getFrameCount());
    }

    /**
     * @param startInd start of frame range (inclusive)
     * @param endInd end of frame range (exclusive)
     * @return all frames from startInd to endInd
     */
    public AsciiVideo build(int startInd, int endInd) throws IndexOutOfBoundsException {
        if (startInd < 0 || startInd >= getFrameCount()) throw new IndexOutOfBoundsException();
        if (endInd < 0 || endInd > getFrameCount()) throw new IndexOutOfBoundsException();
        if (endInd < startInd) throw new IndexOutOfBoundsException();

        IntStream.range(startInd, endInd)
                .parallel()
                .forEach(this::buildFrame);

        return new AsciiVideo(artCache.clone(), getArtName().orElse("ascii-video"), getFps());
    }

    public double getFps() {
        return videoSource.getFPS();
    }

    /**
     * calls release on this builder's VideoSource. DO NOT CALL until you're done with your builder.
     */
    public void releaseVideoSource() {
        if (videoSource instanceof VideoFileConnectionTask.VideoFileAdapter vfa) {
            vfa.release();
        }
    }
}