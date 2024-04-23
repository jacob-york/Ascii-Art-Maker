package com.york.asciiartstudio.asciiArt;

import java.util.Optional;

public interface AsciiArtBuilder {

    /**
     * Get the width of the art in characters.
     * @return the width of the art in characters.
     */
    int getWidth();

    /**
     * Get the height of the image in characters.
     * @return the height of the image in characters.
     */
    int getHeight();

    /**
     * @return the max char width possible for the given image source.
     */
    int getMaxCharWidth();

    /**
     * Get how wide each character is in pixels (from the original image).
     * @return Width of each character in pixels
     */
    int getCharWidth();

    /**
     * Resets the builder to the default values.
     */
    AsciiArtBuilder reset();

    /**
     * Set the width of each character in pixels.
     * @param charWidth Width of each character in pixels
     */
    AsciiArtBuilder setCharWidth(int charWidth);

    /**
     * @return the palette of chars currently being used to compose the art.
     */
    String getPalette();

    /**
     * Set the palette of chars to be used to compose the art.
     * @param palette The new palette of chars
     */
    AsciiArtBuilder setPalette(String palette);

    /**
     * @return the art's name if it has one.
     */
    Optional<String> getName();

    /**
     * Set the name of the image.
     * @param name The new name of the image
     */
    AsciiArtBuilder setName(String name);

    /**
     * @return true if shading is inverted, false if not.
     */
    boolean getInvertedShading();

    /**
     * Set whether shading is inverted or not.
     * @param invertedShading true to invert shading, false otherwise
     */
    AsciiArtBuilder setInvertedShading(boolean invertedShading);

    /**
     * @return true if the art is using the default palette, false if not.
     */
    boolean isUsingDefaultPalette();

}