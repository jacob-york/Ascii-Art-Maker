package com.york.model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

public class AsciiImage implements AsciiArt {
	
	private final ImageLoader imageLoader;
	private CharBox charBox;
	private int domain;
	private int range;
	private String basePalette;
	private String activePalette;
	private int[][] shadingRaster;

	public AsciiImage(String path, int charWidth) {
		imageLoader = new ImageLoader();
		imageLoader.loadFromFile(path);
		charBox = new CharBox(charWidth);
		basePalette = DEFAULT_PALETTE;
		activePalette = DEFAULT_PALETTE;
		this.shadingRaster = imageLoader.getShadingRaster();

		updateDomainAndRange();
	}
	public AsciiImage(BufferedImage image, int charWidth) {
		imageLoader = new ImageLoader(image);
		charBox = new CharBox(charWidth);
		basePalette = DEFAULT_PALETTE;
		activePalette = DEFAULT_PALETTE;
		this.shadingRaster = imageLoader.getShadingRaster();

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
	public BufferedImage getImage() {
		return imageLoader.getImage();
	}
	public int[][] getShadingRaster() {
		return shadingRaster;
	}
	public String getName() {
		String imgName = imageLoader.getName();
		String artName = 
				imgName.contains(".") ? imgName : imgName.substring(0, imgName.lastIndexOf('.'));
		artName += "-charWidth-" + charBox.getWidth();
		artName += shadingIsInverted() ? "-negative.txt" : ".txt";
		return artName;
	}

	@Override
	public boolean setCharWidth(int newCharWidth) {
		if (newCharWidth > getImage().getWidth()) return false;
		if (2 * newCharWidth > getImage().getHeight()) return false;
		if (newCharWidth <= 0) return false;
		
		charBox = new CharBox(newCharWidth);
		updateDomainAndRange();
		return true;
	}

	@Override
	public boolean setPalette(String newPalette) {
		if (256 % newPalette.length() != 0) return false;

		basePalette = newPalette;
		if (shadingIsInverted())
			activePalette = AsciiArt.reverseString(basePalette);
		else activePalette = basePalette;
		
		return true;
	}

	public boolean setImage(BufferedImage image) {
		if (image.getWidth() < charBox.getWidth() || image.getHeight() < charBox.getHeight())return false;

		imageLoader.setImage(image);
		shadingRaster = imageLoader.getShadingRaster();
		return true;
	}

	private void updateDomainAndRange() {
		int SRWidth = shadingRaster[0].length;
		int SRHeight = shadingRaster.length;
		
		domain = SRWidth - (SRWidth % charBox.getWidth());
		range = SRHeight - (SRHeight % charBox.getHeight());
	}
	
	@Override
	public void setInvertedShading(boolean invertedShading) {
		if (invertedShading)
			activePalette = AsciiArt.reverseString(basePalette);
		else activePalette = basePalette;
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

		// shadingRaster via ImageLoader

		int SRWidth = shadingRaster[0].length;
		int SRHeight = shadingRaster.length;

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

	/**
	 * writes the image's toString to a .txt file in the specified path.
	 * @param outPath directory into which to write the Txt File
	 */
	public void writeToOutput(String outPath) throws IOException {
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;

		String art = toString();

		try {
			File file = new File(outPath + "\\" + getName());
			fos = new FileOutputStream(file);
			osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
			osw.write(art);
		}
		finally {
			if (osw != null)
				osw.close();
			if (fos != null)
				fos.close();
		}
	}
}
