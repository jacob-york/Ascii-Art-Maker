package com.york.asciiartstudio.model.asciiArt

interface AsciiArtBuilder {

    /**
     * Get the width of the art in characters.
     * @return the width of the art in characters.
     */
    val width: Int

    /**
     * Get the height of the image in characters.
     * @return the height of the image in characters.
     */
    val height: Int

    /**
     * @return the max char width possible for the given image source.
     */
    val maxCharWidth: Int

    /**
     * get how wide each character is in pixels (from the original image).
     * @return Width of each character in pixels
     */
    val charWidth: Int

    /**
     * @return the palette of chars currently being used to compose the art.
     */
    val palette: String

    /**
     * @return The name of the image; or null if there is none.
     */
    val name: String?

    /**
     * @return an array of ascii art strings (will be length 1 if building an image).
     */
    val result: Array<String?>

    /**
     * @return true if shading is inverted, false if not.
     */
    val invertedShading: Boolean

    /**
     * @param newCharWidth
     * @return a reference to `this` so that setters can be chained together (ex: builder pattern).
     * @throws IllegalArgumentException if newCharWidth < 1, newCharWidth is greater than the image width, or if newCharWidth * 2 is greater than the image height.
     */
    @Throws(IllegalArgumentException::class)
    fun setCharWidth(newCharWidth: Int): AsciiArtBuilder

    /**
     * @param newPalette A string composed of characters the user wishes to make the art out of. newPalette.length() must be divisible by 256.
     * @return a reference to `this` so that setters can be chained together (ex: builder pattern).
     * @throws IllegalArgumentException if newPalette is not divisible by 256.
     */
    @Throws(IllegalArgumentException::class)
    fun setPalette(newPalette: String): AsciiArtBuilder

    /**
     * @param invertShading
     * @return a reference to `this` so that setters can be chained together (ex: builder pattern).
     */
    fun setInvertedShading(invertShading: Boolean): AsciiArtBuilder

    /**
     * sets the name of the ascii art object.
     * @param newName the new name of the object.
     * @return a reference to `this` so that setters can be chained together (ex: builder pattern).
     */
    fun setName(newName: String?): AsciiArtBuilder

    /**
     * @return true if the art is using the default palette, false if not.
     */
    fun usingDefaultPalette(): Boolean
}