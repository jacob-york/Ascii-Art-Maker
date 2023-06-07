package com.york.model.asciiArt;

import com.york.model.adapters.ImageSource;

import java.util.Arrays;
import java.util.stream.Collectors;

public class AsciiImage implements AsciiArt {

	private static final class CharOutline {

		// Top-left pixel of charOutline
		int x, y;

		final int width, height, area;

		public CharOutline(int width, int x, int y) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = width * 2;
			this.area = width * height;
		}

	}

	private char matchChar(CharOutline charOutline) {
		if (256 % activePalette.length() != 0) {
			throw new ArrayIndexOutOfBoundsException("Palette must be divisible by 256.");
		}

		// 0-255 avr that represents the average Black and White value outlined by CharBox.
		int sumOfPixels = 0;
		for (int y = 0; y < charOutline.height; y++) {
			for (int x = 0; x < charOutline.width; x++) {
				sumOfPixels += imageSource.getBWValue(charOutline.x + x, charOutline.y + y);
			}
		}
		int BWValue = sumOfPixels / charOutline.area;

		int index = (int) Math.floor(BWValue / (double) (256/activePalette.length()));
		return activePalette.charAt(index);
	}

	private int charWidth;

	private int domain;

	private int range;

	private String basePalette;

	private String activePalette;

	private ImageSource imageSource;

	private void updateDomainAndRange() {
		domain = imageSource.getWidth() - (imageSource.getWidth() % charWidth);
		range = imageSource.getHeight() - (imageSource.getHeight() % (2 * charWidth));
	}

	public AsciiImage(ImageSource imageSource) {
		charWidth = 1;
		basePalette = DEFAULT_PALETTE;
		activePalette = DEFAULT_PALETTE;
		this.imageSource = imageSource;
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

	public ImageSource getImageSource() {
		return imageSource;
	}

	@Override
	public AsciiImage setCharWidth(int newCharWidth) throws IllegalArgumentException  {
		if (newCharWidth > imageSource.getWidth()) {
			throw new IllegalArgumentException("Char width cannot be greater than the image width.");
		}
		if (2 * newCharWidth > imageSource.getHeight()) {
			throw new IllegalArgumentException("Char width cannot be greater than [image height / 2].");
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

	public AsciiImage setImageSource(ImageSource newImageSource) {
		if (newImageSource.getWidth() < charWidth || newImageSource.getHeight() < (2 * charWidth)) {
			throw new IllegalArgumentException("Raster is too small for current value of char width.");
		}

		imageSource = newImageSource;
		updateDomainAndRange();
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
		CharOutline[][] charOutlines = new CharOutline[getHeight()][getWidth()];

		// might be parallelizable
		for (int y = 0; y < charOutlines.length; y++) {
			for (int x = 0; x < charOutlines[0].length; x++) {
				charOutlines[y][x] = new CharOutline(charWidth, x * charWidth, y * 2 * charWidth);
			}
		}

		// pick each Char in parallel
		return Arrays.stream(charOutlines)
				.parallel()
				.map(row ->
						Arrays.stream(row)
								.map(charOutline ->
										String.valueOf(matchChar(charOutline))
								)
								.collect(Collectors.joining())
				)
				.toArray(String[]::new);
	}

}
