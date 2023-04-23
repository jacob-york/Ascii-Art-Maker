package com.york.model;

public interface AsciiArt {

	/**
	 * The characters used in the AsciiArt, organized from darkest to lightest.
	 */
	String DEFAULT_PALETTE = "@N#bhyo+s/=-:.` ";

	/**
	 *
	 * @return
	 */
	int getWidth();

	/**
	 *
	 * @return
	 */
	int getHeight();

	/**
	 *
	 * @return
	 */
	/**
	 *
	 * @return
	 */
	int getArea();

	/**
	 *
	 * @return
	 */
	int getCharWidth();

	/**
	 *
	 * @return
	 */
	String getPalette();

	/**
	 * @param newCharWidth
	 * @return
	 */
	boolean setCharWidth(int newCharWidth);

	/**
	 * @param newPalette the new palette.
	 * @return
	 */
	boolean setPalette(String newPalette);

	/**
	 * @param invertShading
	 */
	void setInvertedShading(boolean invertShading);

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
