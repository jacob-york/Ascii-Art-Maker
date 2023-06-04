package com.york.model;

import com.york.model.media.ShadingRaster;

public class CharBox {
	
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
	 * @return 0-255 avr that represents how dark the region outlined by CharBox is.
	 */
	public int getGrayVal(ShadingRaster shadingRaster) {
		int sumOfPixels = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				sumOfPixels += shadingRaster.getShadingAt(xPos + x, yPos + y);
			}
		}
		return sumOfPixels / area;
	}

	public void setPos(int x, int y) {
		xPos = x;
		yPos = y;
	}

	public char matchChar(ShadingRaster shadingRaster, String palette) {
		if (256 % palette.length() != 0) {
			throw new ArrayIndexOutOfBoundsException("Palette must be divisible by 256.");
		}
		
		int index = (int) Math.floor(getGrayVal(shadingRaster) / (double) (256/palette.length()));
		return palette.charAt(index);
	}
	
}
