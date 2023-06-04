package com.york.model;

import com.york.model.media.ShadingRaster;

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

	private void updateDomainAndRange() {
		domain = shadingRaster.getWidthPixels() - (shadingRaster.getWidthPixels() % charBox.getWidth());
		range = shadingRaster.getHeightPixels() - (shadingRaster.getHeightPixels() % charBox.getHeight());
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
		if (newCharWidth > shadingRaster.getWidthPixels()) {
			throw new IllegalArgumentException("Char width cannot be greater than the image width.");
		}
		if (2 * newCharWidth > shadingRaster.getHeightPixels()) {
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
		if (newShadingRaster.getWidthPixels() < charBox.getWidth() || newShadingRaster.getHeightPixels() < charBox.getHeight()) {
			throw new IllegalArgumentException("Raster is too small for current value of char width.");
		}

		shadingRaster = newShadingRaster;
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
												charBox.matchChar(shadingRaster, activePalette)
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
				charRaster[CRy][CRx] = charBox.matchChar(shadingRaster, activePalette);
				CRx++;
			}
			CRx = 0;
			CRy++;
		}
		
		return charRaster;
	}

}
