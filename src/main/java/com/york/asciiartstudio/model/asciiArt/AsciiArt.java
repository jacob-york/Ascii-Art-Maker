package com.york.asciiartstudio.model.asciiArt;

/**
 * Class representation of AsciiArt.
 */
public interface AsciiArt {

	/**
	 * The characters used in the AsciiArt, organized from darkest to lightest.
	 * This palette is internally measured in reverse when inverted shading is enabled.
	 */
	String DEFAULT_PALETTE = "@N#bhyo+s/=-:.` ";

	/**
	 *
	 */
	String FONT = "Consolas"; // TODO: multi-font support

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

	int getMaxCharWidth();

	/**
	 * get how wide each character is in pixels (from the original image).
	 * @return Width of each character in pixels
	 */
	int getCharWidth();

	/**
	 *
	 * @return
	 */
	String getPalette();

	/**
	 *
	 * @return The name of the image; or null if there is none.
	 */
	String getName();

	/**
	 * @param newCharWidth
	 * @return
	 * @throws IllegalArgumentException if newCharWidth < 1, newCharWidth is greater than the image width, or if newCharWidth * 2 is greater than the image height.
	 */
	AsciiArt setCharWidth(int newCharWidth) throws IllegalArgumentException ;

	/**
	 * @param newPalette the new palette.
	 * @return
	 * @throws IllegalArgumentException if newPalette is not divisible by 256.
	 */
	AsciiArt setPalette(String newPalette) throws IllegalArgumentException ;

	/**
	 * @param invertShading
	 */
	AsciiArt setInvertedShading(boolean invertShading);

	/**
	 *
	 * @param newName
	 * @return
	 */
	AsciiArt setName(String newName);

	/**
	 * @return true or false of whether the art uses the default palette
	 */
	boolean usesDefaultPalette();

	/**
	 * @return true or false of whether shading is inverted
	 */
	boolean shadingIsInverted();

	/**
	 * @param orig String to be reversed.
	 * @return orig reversed.
	 */
	static String reverseString(String orig) {
		StringBuilder reversed = new StringBuilder();
		for (int i = (orig.length() - 1); i >= 0; i--) {
			reversed.append(orig.charAt(i));
		}
		return reversed.toString();
	}
	
}
