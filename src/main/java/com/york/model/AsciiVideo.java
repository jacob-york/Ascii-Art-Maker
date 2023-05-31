/**
 * A class representing an AsciiArt video.
 * Powered by OpenCV.
 */

package com.york.model;

import com.york.model.adapters.ShadingRaster;

public class AsciiVideo implements AsciiArt {

	private int charWidth;

	private String name;

	private ShadingRaster[] shadingRasters;

	private String basePalette;

	private String activePalette;

	private double frameRate;


	public AsciiVideo(ShadingRaster[] shadingRasters) {
		this.charWidth = 1;
		basePalette = DEFAULT_PALETTE;
		activePalette = DEFAULT_PALETTE;
		frameRate =	24;
		this.shadingRasters = shadingRasters;
		name = "asciiVideo";
	}

	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getArea() {
		return getWidth() * getHeight();
	}

	@Override
	public int getCharWidth() {
		return charWidth;
	}
	
	public double getFrameRate() {
		return frameRate;
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
	public AsciiVideo setCharWidth(int newCharWidth) throws IllegalArgumentException {
		// Todo: input validation

		charWidth = newCharWidth;
		return this;
	}

	@Override
	public AsciiVideo setPalette(String newPalette) throws IllegalArgumentException  {
		if (256 % newPalette.length() != 0) {
			throw new IllegalArgumentException ("char count in Palette must be divisible by 256.");
		}

		basePalette = newPalette;
		if (shadingIsInverted())
			activePalette = AsciiArt.reverseString(basePalette);
		else activePalette = basePalette;

		return this;
	}

	@Override
	public AsciiVideo setInvertedShading(boolean invertShading) {
		if (invertShading)
			activePalette = AsciiArt.reverseString(basePalette);
		else activePalette = basePalette;
		return this;
	}

	@Override
	public AsciiVideo setName(String newName) {
		name = newName;
		return this;
	}

	public AsciiVideo setFrameRate(double newFrameRate) {
		frameRate = newFrameRate;
		return this;
	}

	public AsciiVideo setShadingRasters(ShadingRaster[] newShadingRasters) {
		shadingRasters = newShadingRasters;
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

	/**
	 * Generates frames, then prints them to console.
	 * @throws InterruptedException
	 */
	public void compileToConsole() throws InterruptedException {
	}

}
