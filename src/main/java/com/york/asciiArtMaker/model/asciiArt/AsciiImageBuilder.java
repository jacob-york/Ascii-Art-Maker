package com.york.asciiArtMaker.model.asciiArt;

import com.york.asciiArtMaker.model.adapters.ImageSource;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AsciiImageBuilder implements AsciiArtBuilder {

    private int charWidth;
    private String name;
    private final ImageSource imageSource;
    private final String defaultPalette = "@N#bhyo+s/=-:.  ";
    private String activePalette;
    private String palette;
    private boolean invertedShading;
    private int domain;
    private int range;

    private AsciiImage artCache;

    public AsciiImageBuilder(ImageSource imageSource) {
        this.imageSource = imageSource;
        this.charWidth = 1;
        this.invertedShading = false;

        this.name = imageSource.getName().orElse(null);
        this.palette = defaultPalette;
        this.activePalette = palette;

        this.artCache = null;

        updateDomainAndRange();
    }

    @Override
    public int getWidth() {
        return domain / charWidth;
    }

    @Override
    public int getHeight() {
        return range / (2 * charWidth);
    }

    @Override
    public int getSourceWidth() {
        return imageSource.getWidth();
    }

    @Override
    public int getSourceHeight() {
        return imageSource.getHeight();
    }

    @Override
    public int getMaxCharWidth() {
        boolean ge2 = imageSource.getHeight() / imageSource.getWidth() >= 2;
        return ge2 ? imageSource.getWidth() : imageSource.getHeight() / 2;
    }

    @Override
    public int getCharWidth() {
        return charWidth;
    }

    @Override
    public AsciiImageBuilder reset() {
        this.charWidth = 1;
        this.invertedShading = false;

        this.palette = defaultPalette;
        this.activePalette = palette;
        updateDomainAndRange();
        artCache = null;
        return this;
    }

    @Override
    public AsciiImageBuilder setCharWidth(int charWidth) {
        if (charWidth > getMaxCharWidth()) {
            throw new IllegalArgumentException("Char width cannot be greater than max char width: " + getMaxCharWidth());
        }
        if (charWidth < 1) {
            throw new IllegalArgumentException("Char width must be greater than 0.");
        }

        this.charWidth = charWidth;
        updateDomainAndRange();
        artCache = null;
        return this;
    }

    @Override
    public String getPalette() {
        return palette;
    }

    @Override
    public AsciiImageBuilder setPalette(String palette) {
        if (256 % palette.length() != 0) {
            throw new IllegalArgumentException("Char count in Palette must be divisible by 256.");
        }

        this.palette = palette;
        this.activePalette = invertedShading ? new StringBuilder(palette).reverse().toString() : palette;

        artCache = null;
        return this;
    }

    @Override
    public Optional<String> getName() {
        return name == null ? Optional.empty() : Optional.of(name);
    }

    @Override
    public AsciiImageBuilder setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public boolean getInvertedShading() {
        return invertedShading;
    }

    @Override
    public AsciiImageBuilder setInvertedShading(boolean invertedShading) {
        if (this.invertedShading == invertedShading) return this;

        this.activePalette = new StringBuilder(activePalette).reverse().toString();
        this.invertedShading = invertedShading;

        artCache = null;
        return this;
    }

    @Override
    public boolean isUsingDefaultPalette() {
        return palette.equals(defaultPalette);
    }


    private static class PixelOutline {
        int width;
        int x;
        int y;
        int height;
        int area;

        PixelOutline(int width, int x, int y) {
            this.width = width;
            this.x = x;
            this.y = y;
            this.height = width * 2;
            this.area = width * height;
        }
    }

    private char matchChar(PixelOutline pixelOutline) {
        int sumOfPixels = 0;
        int transparentPixels = 0;
        for (int y = 0; y < pixelOutline.height; y++) {
            for (int x = 0; x < pixelOutline.width; x++) {
                int pixelBWVal = imageSource.getDesaturatedPixel(pixelOutline.x + x, pixelOutline.y + y);
                if (pixelBWVal == -1) {
                    transparentPixels++;
                } else {
                    sumOfPixels += pixelBWVal;
                }
            }
        }
        int opaguePixels = pixelOutline.area - transparentPixels;
        if (transparentPixels > opaguePixels) return ' ';
        int bWValue = sumOfPixels / pixelOutline.area;
        int charIndex = (int) Math.floor(bWValue / (double) (256/activePalette.length()));  // TODO: can this be more concise?
        return activePalette.charAt(charIndex);
    }

    public AsciiImage build() {
        if (artCache == null) {
            artCache = new AsciiImage(generateArtStr(), getName().orElse("ascii-image"),
                    getCharWidth(), getWidth(), getHeight(), invertedShading
            );
        }
        return artCache;
    }

    /**
     * @return the actual string of ascii art for the given image source. build() calls this method then wraps it
     * as an AsciiImage object.
     */
    private String generateArtStr() {
        return IntStream.range(0, getHeight())
                .parallel()
                .mapToObj(rowInd -> IntStream.range(0, getWidth())
                        .parallel()
                        .mapToObj(colInd -> String.valueOf(matchChar(
                                new PixelOutline(charWidth, colInd * charWidth, rowInd * 2 * charWidth))))
                        .collect(Collectors.joining()))
                .collect(Collectors.joining("\n"));
    }

    private void updateDomainAndRange() {
        domain = imageSource.getWidth() - imageSource.getWidth() % charWidth;
        range = imageSource.getHeight() - imageSource.getHeight() % (2 * charWidth);
    }
}