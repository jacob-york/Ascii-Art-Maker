package com.york.model.asciiArt;

import com.york.model.adapters.ImageSource;

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

	/**
	 * Match a char to a pixelOutline.
	 * @param pixelOutline a pixelOutline to match a char to.
	 * @return the matched char.
	 */
	private char matchChar(PixelOutline pixelOutline) {
		if (256 % activePalette.length() != 0) {
			throw new ArrayIndexOutOfBoundsException("Palette must be divisible by 256.");
		}

		// 0-255 avr that represents the average Black and White value outlined by CharBox.
		int sumOfPixels = 0;
		for (int y = 0; y < pixelOutline.height; y++) {
			for (int x = 0; x < pixelOutline.width; x++) {
				sumOfPixels += imageSource.getBWValue(pixelOutline.x + x, pixelOutline.y + y);
			}
		}
		int BWValue = sumOfPixels / pixelOutline.area;

		int index = (int) Math.floor(BWValue / (double) (256/activePalette.length()));
		return activePalette.charAt(index);
	}

	private int charWidth;

	private int domain;

	private int range;

	private String basePalette;

	private String activePalette;

	private final ImageSource imageSource;

	private String name;

	private void updateDomainAndRange() {
		domain = imageSource.getWidth() - (imageSource.getWidth() % charWidth);
		range = imageSource.getHeight() - (imageSource.getHeight() % (2 * charWidth));
	}

	public AsciiImage(ImageSource imageSource) {
		charWidth = 1;
		basePalette = DEFAULT_PALETTE;
		activePalette = DEFAULT_PALETTE;
		this.imageSource = imageSource;
		name = imageSource.getName();
		updateDomainAndRange();
	}

	@Override
	public int getMaxCharWidth() {
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
		if (newCharWidth > getMaxCharWidth()) {
			throw new IllegalArgumentException("Char width cannot be greater than max char width: " + getMaxCharWidth());
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
		if (shadingIsInverted())
			activePalette = AsciiArt.reverseString(basePalette);
		else activePalette = basePalette;
		return this;
	}

	@Override
	public AsciiImage setInvertedShading(boolean invertedShading) {
		if (invertedShading)
			activePalette = AsciiArt.reverseString(basePalette);
		else activePalette = basePalette;
		return this;
	}

	@Override
	public AsciiImage setName(String newName) {
		name = newName;
		return this;
	}

	@Override
	public boolean usesDefaultPalette() {
		return basePalette.equals(DEFAULT_PALETTE);
	}
	
	@Override
	public boolean shadingIsInverted() {
		return activePalette.equals(AsciiArt.reverseString(basePalette));
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
