package com.york.asciiartstudio.model.asciiArt;

/**
 * Class representation of AsciiArt.
 */
public interface AsciiArt {

	/**
	 * The characters used in the AsciiArt, organized from darkest to lightest.
	 * This palette is internally measured in reverse when inverted shading is enabled.
	 */
	String DEFAULT_PALETTE = "@N#bhyo+s/=-:.  ";

	/**
	 * The intended font to render ascii art into.
	 */
	String FONT = "Consolas";

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
	 * Get the area of the image in pixels
	 * @return the area of the image in pixels.
	 */
	int getArea();

	/**
	 *
	 * @return the max char width possible for the given image source.
	 */
	int maxCharWidth();

	/**
	 * get how wide each character is in pixels (from the original image).
	 * @return Width of each character in pixels
	 */
	int getCharWidth();

	/**
	 *
	 * @return the palette of chars currently being used to compose the art.
	 */
	String getPalette();

	/**
	 *
	 * @return The name of the image; or null if there is none.
	 */
	String getName();

	/**
	 * @param newCharWidth
	 * @return a reference to `this` so that setters can be chained together (ex: builder pattern).
	 * @throws IllegalArgumentException if newCharWidth < 1, newCharWidth is greater than the image width, or if newCharWidth * 2 is greater than the image height.
	 */
	AsciiArt setCharWidth(int newCharWidth) throws IllegalArgumentException ;

	/**
	 * @param newPalette A string composed of characters the user wishes to make the art out of. newPalette.length() must be divisible by 256.
	 * @return a reference to `this` so that setters can be chained together (ex: builder pattern).
	 * @throws IllegalArgumentException if newPalette is not divisible by 256.
	 */
	AsciiArt setPalette(String newPalette) throws IllegalArgumentException ;

	/**
	 * @param invertShading
	 * @return a reference to `this` so that setters can be chained together (ex: builder pattern).
	 */
	AsciiArt setInvertedShading(boolean invertShading);

	/**
	 * sets the name of the ascii art object.
	 * @param newName the new name of the object.
	 * @return a reference to `this` so that setters can be chained together (ex: builder pattern).
	 */
	AsciiArt setName(String newName);

	/**
	 * @return true if the art is using the default palette, false if not.
	 */
	boolean usingDefaultPalette();

	/**
	 * @return true if shading is inverted, false if not.
	 */
	boolean getInvertedShading();


	
}
