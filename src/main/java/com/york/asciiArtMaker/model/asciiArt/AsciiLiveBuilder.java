package com.york.asciiArtMaker.model.asciiArt;

import com.york.asciiArtMaker.model.adapters.LiveSource;

import java.util.Optional;

public class AsciiLiveBuilder implements AsciiArtBuilder {

    private LiveSource liveSource;
    private String name;
    private final AsciiImageBuilder asciiImageBuilder;

    public AsciiLiveBuilder(LiveSource liveSource) {
        this.liveSource = liveSource;
        this.name = liveSource.getName().orElse(null);
        asciiImageBuilder = new AsciiImageBuilder(liveSource.getCurrentImageSource());
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
        return liveSource.getWidth();
    }

    @Override
    public int getSourceHeight() {
        return liveSource.getHeight();
    }

    @Override
    public int getMaxCharWidth() {
        return asciiImageBuilder.getMaxCharWidth();
    }

    @Override
    public AsciiArtBuilder reset() {
        asciiImageBuilder.reset();
        return this;
    }

    public LiveSource getLiveSource() {
        return liveSource;
    }

    public AsciiLiveBuilder setLiveSource(LiveSource liveSource) {
        asciiImageBuilder.setImageSource(liveSource.getCurrentImageSource());
        this.liveSource.close();
        this.liveSource = liveSource;
        return this;
    }
    @Override
    public int getCharWidth() {
        return asciiImageBuilder.getCharWidth();
    }

    @Override
    public AsciiLiveBuilder setCharWidth(int charWidth) {
        asciiImageBuilder.setCharWidth(charWidth);
        return this;
    }

    @Override
    public String getPalette() {
        return asciiImageBuilder.getPalette();
    }

    @Override
    public AsciiLiveBuilder setPalette(String palette) {
        asciiImageBuilder.setPalette(palette);
        return this;
    }

    @Override
    public Optional<String> getArtName() {
        return liveSource.getName();
    }

    @Override
    public AsciiLiveBuilder setArtName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public boolean getInvertedShading() {
        return asciiImageBuilder.getInvertedShading();
    }

    @Override
    public AsciiLiveBuilder setInvertedShading(boolean invertedShading) {
        asciiImageBuilder.setInvertedShading(invertedShading);

        return this;
    }

    @Override
    public boolean isUsingDefaultPalette() {
        return asciiImageBuilder.isUsingDefaultPalette();
    }

    public double getFps() {
        return liveSource.getFps();
    }

    public AsciiImage buildCurrentFrame() {
        return asciiImageBuilder.setImageSource(liveSource.getCurrentImageSource()).build();
    }

    public void close() {
        liveSource.close();
    }
}
