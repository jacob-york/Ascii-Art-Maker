package com.york.asciiartstudio.model.asciiArt;

import com.york.asciiartstudio.model.adapters.ImageSource;

import java.util.Arrays;
import java.util.stream.Collectors;

public class AsciiImage implements AsciiArt {

	/**
	 * Outlines a rectangle of pixels to be rendered into a char.
	 * PixelOutlines, like actual chars, are twice as tall as they are long.
	 */
	private static final class PixelOutline {

		// Top-left pixel of charOutline
		int x, y;

		final int width, height, area;

		public PixelOutline(int charWidth, int x, int y) {
			this.x = x;
			this.y = y;
			width = charWidth;
			height = charWidth * 2;
			area = charWidth * height;
		}

	}

	private int charWidth;

	private int domain;

	private int range;

	private String basePalette;

	private String activePalette;

	private final ImageSource imageSource;

	private String name;

	public AsciiImage(ImageSource imageSource) {
		charWidth = 1;
		basePalette = DEFAULT_PALETTE;
		activePalette = DEFAULT_PALETTE;
		this.imageSource = imageSource;
		name = imageSource.getName();
		updateDomainAndRange();
	}

	private void updateDomainAndRange() {
		domain = imageSource.getWidth() - (imageSource.getWidth() % charWidth);
		range = imageSource.getHeight() - (imageSource.getHeight() % (2 * charWidth));
	}

	/**
	 * Match a char to a pixelOutline.
	 * @param pixelOutline a pixelOutline to match a char to.
	 * @return the matched char.
	 * Will return the space character ' ' automatically if more than 50% of the pixels in pixelOutline are transparent.
	 */
	private char matchChar(PixelOutline pixelOutline) {
		if (256 % activePalette.length() != 0) {
			throw new ArrayIndexOutOfBoundsException("Palette must be divisible by 256.");
		}

		// 0-255 avr that represents the average Black and White value outlined by CharBox.
		int sumOfPixels = 0;
		int transparentPixels = 0;
		for (int y = 0; y < pixelOutline.height; y++) {
			for (int x = 0; x < pixelOutline.width; x++) {
				int pixelBWVal = imageSource.getBWValue(pixelOutline.x + x, pixelOutline.y + y);
				if (pixelBWVal == -1) {
					transparentPixels++;
				}
				else {
					sumOfPixels += pixelBWVal;
				}
			}
		}
		int opaguePixels = pixelOutline.area - transparentPixels;
		if (transparentPixels > opaguePixels) return ' ';

		int BWValue = sumOfPixels / pixelOutline.area;

		int index = (int) Math.floor(BWValue / (double) (256/activePalette.length()));
		return activePalette.charAt(index);
	}

	/**
	 * a method for reversing a string.
	 * @param orig String to be reversed (original).
	 * @return orig reversed.
	 */
	private static String reverseString(String orig) {
		StringBuilder reversed = new StringBuilder();
		for (int i = (orig.length() - 1); i >= 0; i--) {
			reversed.append(orig.charAt(i));
		}
		return reversed.toString();
	}

	@Override
	public int maxCharWidth() {
		boolean ge2 = imageSource.getHeight() / imageSource.getWidth() >= 2;
		return ge2 ? imageSource.getWidth() : imageSource.getHeight() / 2;
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
	public int getArea() {
		return getWidth() * getHeight();
	}

	@Override
	public int getCharWidth() {
		return charWidth;
	}

	@Override
	public String getPalette() {
		return basePalette;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public AsciiImage setCharWidth(int newCharWidth) throws IllegalArgumentException  {
		if (newCharWidth > maxCharWidth()) {
			throw new IllegalArgumentException("Char width cannot be greater than max char width: " + maxCharWidth());
		}
		if (newCharWidth < 1) {
			throw new IllegalArgumentException("Char width must be greater than 0.");
		}

		charWidth = newCharWidth;
		updateDomainAndRange();
		return this;
	}

	@Override
	public AsciiImage setPalette(String newPalette) throws IllegalArgumentException  {
		if (256 % newPalette.length() != 0) {
			throw new IllegalArgumentException ("Char count in Palette must be divisible by 256.");
		}

		basePalette = newPalette;
		if (getInvertedShading())
			activePalette = reverseString(basePalette);
		else activePalette = basePalette;
		return this;
	}

	@Override
	public AsciiImage setInvertedShading(boolean invertedShading) {
		if (invertedShading)
			activePalette = reverseString(basePalette);
		else activePalette = basePalette;
		return this;
	}

	@Override
	public AsciiImage setName(String newName) {
		name = newName;
		return this;
	}

	@Override
	public boolean usingDefaultPalette() {
		return basePalette.equals(DEFAULT_PALETTE);
	}
	
	@Override
	public boolean getInvertedShading() {
		return activePalette.equals(reverseString(basePalette));
	}

	public String toString() {
		String[] stringArray = toStringArray();
		StringBuilder returnVal = new StringBuilder();

		for (String line : stringArray) {
			returnVal.append(line);
			returnVal.append("\n");
		}
		return returnVal.toString();
	}
	
	public String[] toStringArray() {
		PixelOutline[][] pixelOutlines = new PixelOutline[getHeight()][getWidth()];

		// populate pixelOutline array
		for (int y = 0; y < pixelOutlines.length; y++) {
			for (int x = 0; x < pixelOutlines[0].length; x++) {
				pixelOutlines[y][x] = new PixelOutline(charWidth, x * charWidth, y * 2 * charWidth);
			}
		}

		// pick each Char in parallel
		return Arrays.stream(pixelOutlines).parallel()
				.map(row -> Arrays.stream(row)
						.map(pixelOutline -> String.valueOf(matchChar(pixelOutline))
						)
						.collect(Collectors.joining())
				)
				.toArray(String[]::new);
	}

}
