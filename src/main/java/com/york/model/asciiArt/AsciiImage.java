package com.york.model.asciiArt;

import com.york.model.adapters.ImageSource;

import java.util.Arrays;
import java.util.stream.Collectors;

public class AsciiImage implements AsciiArt {

	private static final class CharBox {

		private int xPos;  // top-left pixel of charBox
		private int yPos;  //
		private final int width;
		private final int height;
		private final int area;

		public CharBox(int width) {
			xPos = 0;
			yPos = 0;
			this.width = width;
			this.height = width * 2;
			this.area = width * height;
		}

		public int getX() {
			return xPos;
		}

		public int getY() {
			return yPos;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}

		public int getArea() {
			return area;
		}

		/**
		 * @return 0-255 avr that represents the average Black and White value outlined by CharBox.
		 */
		private int getBWValue(ImageSource imageSource) {
			int sumOfPixels = 0;
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					sumOfPixels += imageSource.getBWValue(xPos + x, yPos + y);
				}
			}
			return sumOfPixels / area;
		}

		public void setPos(int x, int y) {
			xPos = x;
			yPos = y;
		}

		public char matchChar(ImageSource imageSource, String palette) {
			if (256 % palette.length() != 0) {
				throw new ArrayIndexOutOfBoundsException("Palette must be divisible by 256.");
			}

			int index = (int) Math.floor(getBWValue(imageSource) / (double) (256/palette.length()));
			return palette.charAt(index);
		}

	}

	private CharBox charBox;

	private int domain;

	private int range;

	private String name;

	private String basePalette;

	private String activePalette;

	private ImageSource imageSource;

	public AsciiImage(ImageSource imageSource) {
		charBox = new CharBox(1);
		basePalette = DEFAULT_PALETTE;
		activePalette = DEFAULT_PALETTE;
		this.imageSource = imageSource;
		name = "asciiImage";
		updateDomainAndRange();
	}

	private void updateDomainAndRange() {
		domain = imageSource.getWidthPixels() - (imageSource.getWidthPixels() % charBox.getWidth());
		range = imageSource.getHeightPixels() - (imageSource.getHeightPixels() % charBox.getHeight());
	}

	@Override
	public int getWidth() {
		return domain / charBox.getWidth();
	}
	@Override
	public int getHeight() {
		return range / charBox.getHeight();
	}
	@Override
	public int getArea() {
		return getWidth() * getHeight();
	}
	@Override
	public int getCharWidth() {
		return charBox.getWidth();
	}
	@Override
	public String getPalette() {
		return basePalette;
	}

	@Override
	public String getName() {
		return name;
	}

	public ImageSource getShadingRaster() {
		return imageSource;
	}

	@Override
	public AsciiImage setCharWidth(int newCharWidth) throws IllegalArgumentException  {
		if (newCharWidth > imageSource.getWidthPixels()) {
			throw new IllegalArgumentException("Char width cannot be greater than the image width.");
		}
		if (2 * newCharWidth > imageSource.getHeightPixels()) {
			throw new IllegalArgumentException("Char width cannot be greater than [image height / 2].");
		}
		if (newCharWidth < 1) {
			throw new IllegalArgumentException("Char width must be greater than 0.");
		}

		charBox = new CharBox(newCharWidth);
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

	public AsciiImage setShadingRaster(ImageSource newImageSource) {
		if (newImageSource.getWidthPixels() < charBox.getWidth() || newImageSource.getHeightPixels() < charBox.getHeight()) {
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
		CharBox[][] charBoxes = new CharBox[getHeight()][getWidth()];

		// might be parallelizable
		for (int y = 0; y < charBoxes.length; y++) {
			for (int x = 0; x < charBoxes[0].length; x++){
				CharBox curBox = new CharBox(charBox.getWidth());
				charBoxes[y][x] = curBox;
				curBox.setPos(x * charBox.getWidth(), y * charBox.getHeight());
			}
		}

		// pick each Char in parallel
		return Arrays.stream(charBoxes)
				.parallel()
				.map(
						row -> Arrays.stream(row)
								.map(
										charBox -> String.valueOf(
												charBox.matchChar(imageSource, activePalette)
										)
								)
								.collect(Collectors.joining())
				)
				.toArray(String[]::new);
	}
	
	public char[][] toCharRaster() {
		char[][] charRaster = new char[getHeight()][getWidth()];
		
		// shadingRaster position
		int SRy;
		int SRx;
		// charRaster position
		int CRy = 0;
		int CRx = 0;
		
		for (SRy = 0; SRy < range; SRy += charBox.getHeight()) {
			for (SRx = 0; SRx < domain; SRx += charBox.getWidth()) {
				charBox.setPos(SRx, SRy);
				charRaster[CRy][CRx] = charBox.matchChar(imageSource, activePalette);
				CRx++;
			}
			CRx = 0;
			CRy++;
		}
		
		return charRaster;
	}

}
