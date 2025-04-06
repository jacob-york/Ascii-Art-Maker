package com.york.asciiArtMaker.model.asciiArt;

import com.york.asciiArtMaker.model.adapters.ImageSource;
import javafx.scene.paint.Color;

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
    public int getArtWidth() {
        return domain / charWidth;
    }

    @Override
    public int getArtHeight() {
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

    /**
     * charWidth change is only written if there are no exceptions.
     * @param charWidth Width of each character in pixels
     * @return
     */
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
    public Optional<String> getArtName() {
        return name == null ? Optional.empty() : Optional.of(name);
    }

    @Override
    public AsciiImageBuilder setArtName(String name) {
        this.name = name;
        return this;
    }

    /**
     * @return the image's file menuName for writing to memory WITH the text and background color (not including file extension, which is assigned later).
     */
    public String getFileName(Color bgColor, Color textColor) {
        return String.format("%s-bg%s-txt%s", getFileName(), bgColor.toString(), textColor.toString());
    }

    /**
     * @return the image's file menuName for writing to memory (not including file extension, which is assigned later).
     */
    public String getFileName() {
        final String invertedMarker = invertedShading ? "-inv" : "";
        final String label = name.substring(0, name.lastIndexOf('.'));
        return String.format("%s-cw%d%s", label, charWidth, invertedMarker);
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

    /**
     * Maps a 1:2 (taller than wide) section of pixels to a Character.
     * <p></p>
     * <p>Internally, it reads from the current activePalette as a sort of mapping key,
     * the current imageSource to read pixel data from, and
     * the current charWidth integer to calculate the bounds of your pixel section (either 1-by-2, or 2-by-4, or 4-by-8, etc).
     * </p>
     * @param row an index into imageSource for the topmost row of your section of pixels (i.e. the row of your section's top-left pixel).
     * @param col an index into imageSource for the leftmost column of your section of pixels (i.e. the column of your section's top-left pixel).
     *
     * @return a Character to abstractly represent your section of pixels.
     */
    private Character mapPixelSectionToChar(int row, int col) {
        final int sectionWidthPixels = charWidth;
        final int sectionHeightPixels = charWidth * 2;
        final int sectionAreaPixels = sectionWidthPixels * sectionHeightPixels;

        int runningLuminositySum = 0;
        int transparentPixelCnt = 0;
        for (int y = 0; y < sectionHeightPixels; y++) {
            for (int x = 0; x < sectionWidthPixels; x++) {
                final int pixelLuminance = imageSource.getPixelLuminance(col + x, row + y);
                if (pixelLuminance == -1) {
                    transparentPixelCnt++;
                } else {
                    runningLuminositySum += pixelLuminance;
                }
            }
        }

        // quick check to decide if we should deem the entire section "transparent":
        final int opaquePixelCnt = sectionAreaPixels - transparentPixelCnt;
        if (transparentPixelCnt > opaquePixelCnt) return ' ';

        final int sectionLuminance = runningLuminositySum / sectionAreaPixels;
        final int charIndex = (int) Math.floor(sectionLuminance / (double) (256/activePalette.length()));
        return activePalette.charAt(charIndex);
    }

    public AsciiImage build() {
        if (artCache == null) {
            artCache = new AsciiImage(generateArtStr(), getArtName().orElse("ascii-image"),
                    getCharWidth(), getArtWidth(), getArtHeight(), invertedShading
            );
        }
        return artCache;
    }

    /**
     * @return the actual String of ascii art computed from a given image source.
     * <p>The difference between AsciiImageBuilder's build() method and its generateArtStr() method is that generateArtStr()
     * actually does the heavy lifting of computing ascii art and outputting a finished String, whereas build() calls generateArtStr()
     * internally and wraps its return value in an AsciiImage object.</p>
     */
    private String generateArtStr() {
        return IntStream.range(0, getArtHeight())
                .parallel()
                .mapToObj(artRowInd -> // mapping each row index of the art to a row of chars (i.e. rows of the final art)
                        IntStream.range(0, getArtWidth())
                                .parallel()
                                .mapToObj(artColInd ->  // mapping each (row, col) coord of the art to a specific char

                                        // (requires math to convert art indices into imageSource indices):
                                                mapPixelSectionToChar(artRowInd * charWidth * 2,artColInd * charWidth)
                                                        .toString()
                                )
                                .collect(Collectors.joining())
                )
                .collect(Collectors.joining("\n"));  // collecting all rows into a single String of ascii art.
    }

    private void updateDomainAndRange() {
        domain = imageSource.getWidth() - imageSource.getWidth() % charWidth;
        range = imageSource.getHeight() - imageSource.getHeight() % (2 * charWidth);
    }
}