package com.york.model.asciiArt;

public class CharBox {
	
	private int xPos;  // top-left pixel of rectangle
	private int yPos;  // 
	private int width;
	private int height;
	private int area;  // I'm intentionally breaking the "Resource acquisition is Initialization" rule for the sake of speed.
	
	public CharBox(int width) {
		xPos = 0;
		yPos = 0;
		this.width = width;
		this.height = width * 2;
		this.area = width * height;
	}
	
	// getters
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
	public int getGrayVal(int[][] shadingRaster) { // 0-255 avrg that represents how dark the region outlined by Rectangle is
		int sumOfPixels = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				sumOfPixels += shadingRaster[yPos + y][xPos + x];
			}
		}
		return sumOfPixels / area;
	}
	
	// setters
	public void setPos(int x, int y) {
		xPos = x;
		yPos = y;
	}
	
	public String toString()
	{
		String returnVal = "";
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				returnVal += "[]";
			}
			returnVal += "\n";
		}
		returnVal += ("top-left pixel coords: (" + xPos + ", " + yPos + ")\n");
		returnVal += ("width: " + width + " pixels\n");
		returnVal += ("height: " + height + " pixels\n");
		returnVal += ("greyVal: " + "\n");
		return returnVal;
	}

	public char pickChar(int[][] shadingRaster, String palette) {
		// checks
		if (256 % palette.length() != 0)
			throw new ArrayIndexOutOfBoundsException("Palette must be divisible by 256.");
		
		int index = (int) Math.floor(getGrayVal(shadingRaster) / (256 / palette.length()));
		return palette.charAt(index);
	}
	
}
