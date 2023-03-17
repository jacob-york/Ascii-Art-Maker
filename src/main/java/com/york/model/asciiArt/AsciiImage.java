package com.york.model.asciiArt;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.stream.Collectors;

// written by Jacob York

public class AsciiImage implements AsciiArt {
	
	private final ImageLoader imgLoader;
	private CharBox charBox;
	
	private int domain;
	private int range;
	
	private String basePalette;
	private String activePalette;
	
	private int[][] shadingRaster;
	
	// constructor(s)
	public AsciiImage(String path) {
		imgLoader = new ImageLoader();
		imgLoader.loadFromFile(path);
		charBox = new CharBox(1);
		basePalette = DEFAULT_PALETTE;
		activePalette = DEFAULT_PALETTE;
		
		this.shadingRaster = imgLoader.getShadingRaster();
		
		updateDomainAndRange();
	}
	public AsciiImage(String path, int charWidth) {
		imgLoader = new ImageLoader();
		imgLoader.loadFromFile(path);
		charBox = new CharBox(charWidth);
		basePalette = DEFAULT_PALETTE;
		activePalette = DEFAULT_PALETTE;
		
		this.shadingRaster = imgLoader.getShadingRaster();
		
		updateDomainAndRange();
	}
	public AsciiImage(String path, int charWidth, boolean invertedShading) {
		imgLoader = new ImageLoader();
		imgLoader.loadFromFile(path);
		charBox = new CharBox(charWidth);
		basePalette = DEFAULT_PALETTE;
		if (invertedShading)
			activePalette = AsciiArt.reverseString(basePalette);
		else activePalette = basePalette;	
		
		this.shadingRaster = imgLoader.getShadingRaster();
		
		updateDomainAndRange();
	}
	public AsciiImage(String path, int charWidth, boolean invertedShading, String palette) {
		imgLoader = new ImageLoader();
		imgLoader.loadFromFile(path);
		charBox = new CharBox(charWidth);
		basePalette = palette;
		if (invertedShading)
			activePalette = AsciiArt.reverseString(basePalette);
		else activePalette = basePalette;	
		
		this.shadingRaster = imgLoader.getShadingRaster();
		
		updateDomainAndRange();
	}
	public AsciiImage(BufferedImage image) {
		imgLoader = new ImageLoader(image);
		charBox = new CharBox(1);
		basePalette = DEFAULT_PALETTE;
		activePalette = DEFAULT_PALETTE;
		
		this.shadingRaster = imgLoader.getShadingRaster();
		
		updateDomainAndRange();
	}
	public AsciiImage(BufferedImage image, int charWidth) {
		imgLoader = new ImageLoader(image);
		charBox = new CharBox(charWidth);
		basePalette = DEFAULT_PALETTE;
		activePalette = DEFAULT_PALETTE;
		
		this.shadingRaster = imgLoader.getShadingRaster();
		
		updateDomainAndRange();
	}
	public AsciiImage(BufferedImage image, int charWidth, boolean invertedShading) {
		imgLoader = new ImageLoader(image);
		charBox = new CharBox(charWidth);
		basePalette = DEFAULT_PALETTE;
		if (invertedShading)
			activePalette = AsciiArt.reverseString(basePalette);
		else activePalette = basePalette;	
		
		this.shadingRaster = imgLoader.getShadingRaster();
		
		updateDomainAndRange();
	}
	public AsciiImage(BufferedImage image, int charWidth, boolean invertedShading, String palette) {
		imgLoader = new ImageLoader(image);
		charBox = new CharBox(charWidth);
		basePalette = palette;
		if (invertedShading)
			activePalette = AsciiArt.reverseString(basePalette);
		else activePalette = basePalette;	
		
		this.shadingRaster = imgLoader.getShadingRaster();
		
		updateDomainAndRange();
	}
	
	// getters
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
		return imgLoader.getImage();
	}
	public int[][] getShadingRaster() {
		return shadingRaster;
	}
	public String getName() {
		String imgName = imgLoader.getName();
		String artName = 
				imgName.contains(".") ? imgName : imgName.substring(0, imgName.lastIndexOf('.'));
		artName += "-charWidth-" + charBox.getWidth();
		artName += shadingIsInverted() ? "-negative.txt" : ".txt";
		return artName;
	}
	
	// setters
	@Override
	public int setCharWidth(int newCharWidth) {
		if (newCharWidth <= 0)
			return 1;  // error code 1: charWidth is less than or equal to 0
		
		if (newCharWidth > getImage().getWidth() || (newCharWidth * 2) > getImage().getHeight())
			return 2;  // error code 2: charWidth is too large for this shadingRaster
		
		charBox = new CharBox(newCharWidth);
		updateDomainAndRange();
		return 0;
	}
	@Override
	public int setPalette(String newPalette) {
		
		if (256 % newPalette.length() != 0)
			return 1;
		basePalette = newPalette;
		if (shadingIsInverted())
			activePalette = AsciiArt.reverseString(basePalette);
		else activePalette = basePalette;
		
		return 0;
	}
	public int setImage(BufferedImage image) {
		
		if (image.getWidth() < charBox.getWidth() || image.getHeight() < charBox.getHeight())
			return 1;
		
		imgLoader.setImage(image);
		shadingRaster = imgLoader.getShadingRaster();
		return 0;
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
		if (basePalette.equals(DEFAULT_PALETTE))
			return true;
		else return false;
	}
	
	@Override
	public boolean shadingIsInverted() {
		return activePalette.equals(AsciiArt.reverseString(basePalette));
	}
	
	public String toString() {

		String[] stringArray = toStringArray();
		String returnVal = "";
		for (String line : stringArray) {
			returnVal += line;
			returnVal += "\n";
		}
		return returnVal;
	}
	
	public String[] toStringArray() {
		String[] stringArray = new String[getHeight()];
		
		// shadingRaster position
		int y;
		int x;
		// stringArray position
		int SAy = 0;

		for (y = 0; y < range; y += charBox.getHeight()) {
			stringArray[SAy] = "";
			for (x = 0; x < domain; x += charBox.getWidth()) {
				charBox.setPos(x, y);
				stringArray[SAy] += charBox.pickChar(shadingRaster, activePalette);
			}
			SAy++;
		}
		
		return stringArray;
	}
	
	public char[][] toCharRaster() {
		char[][] charRaster = new char[getHeight()][getWidth()];
		
		// shadingRaster position
		int SRy;
		int SRx;		// charRaster position
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
	
	
	public String generateInParallel() {
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
		String[] stringArray = Arrays.stream(charBoxes)
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
		
		// turn into final art
		String returnVal = "";
		for (String line : stringArray) {
			returnVal += line;
			returnVal += "\n";
		}
		return returnVal;
	}
}
