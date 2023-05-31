package com.york.model;

import com.york.model.adapters.ShadingRaster;

import java.util.Arrays;
import java.util.stream.Collectors;

/*
 * Todo: consider builder design pattern.
 */
public class AsciiImage implements AsciiArt {

	private CharBox charBox;

	private int domain;

	private int range;

	private String name;

	private String basePalette;

	private String activePalette;

	private ShadingRaster shadingRaster;

	public AsciiImage(ShadingRaster shadingRaster) {
		charBox = new CharBox(1);
		basePalette = DEFAULT_PALETTE;
		activePalette = DEFAULT_PALETTE;
		this.shadingRaster = shadingRaster;
		name = "asciiImage";
		updateDomainAndRange();
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

	public ShadingRaster getShadingRaster() {
		return shadingRaster;
	}

	@Override
	public AsciiImage setCharWidth(int newCharWidth) throws IllegalArgumentException  {
		if (newCharWidth > shadingRaster.getWidth()) {
			throw new IllegalArgumentException("Char width cannot be greater than the image width.");
		}
		if (2 * newCharWidth > shadingRaster.getHeight()) {
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

	public AsciiImage setShadingRaster(ShadingRaster newShadingRaster) {
		if (newShadingRaster.getWidth() < charBox.getWidth() || newShadingRaster.getHeight() < charBox.getHeight()) {
			throw new IllegalArgumentException("Raster is too small for current value of char width.");
		}

		shadingRaster = newShadingRaster;
		return this;
	}

	private void updateDomainAndRange() {
		domain = shadingRaster.getWidth() - (shadingRaster.getWidth() % charBox.getWidth());
		range = shadingRaster.getHeight() - (shadingRaster.getHeight() % charBox.getHeight());
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
		int charWidth = charBox.getWidth();

		int SRWidth = shadingRaster.getWidth();
		int SRHeight = shadingRaster.getHeight();

		int domain = SRWidth - (SRWidth % charWidth);
		int range = SRHeight - (SRHeight % (2 * charWidth));

		// final dimensions of asciiImage
		int asciiImageWidth = domain / charWidth;
		int asciiImageHeight = range / (2 * charWidth);

		// fill and populate charBox
		CharBox[][] charBoxes = new CharBox[asciiImageHeight][asciiImageWidth];

		// setPos (might be parallelizable)
		for (int y = 0; y < charBoxes.length; y++) {
			for (int x = 0; x < charBoxes[0].length; x++){
				CharBox curBox = new CharBox(charWidth);
				charBoxes[y][x] = curBox;
				curBox.setPos((x * charWidth), (y * 2 * charWidth));
			}
		}

		// pick each Char in parallel
		return Arrays.stream(charBoxes)
				.parallel()
				.map(
						row -> Arrays.stream(row)
								.map(
										charBox -> String.valueOf(
												charBox.pickChar(shadingRaster, activePalette)
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
				charRaster[CRy][CRx] = charBox.pickChar(shadingRaster, activePalette);
				CRx++;
			}
			CRx = 0;
			CRy++;
		}
		
		return charRaster;
	}

}
