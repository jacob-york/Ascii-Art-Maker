package com.example.asciiArt;

public interface AsciiArt {

	String DEFAULT_PALETTE = "@N#bhyo+s/=-:.` ";
	
	// getters
	int getWidth();
	int getHeight();
	int getArea();
	int getCharWidth();
	String getPalette();
	
	// setters
	int setCharWidth(int newCharWidth);
	int setPalette(String newPalette);
	
	void setInvertedShading(boolean invertShading);
	boolean usesDefaultPalette();
	boolean shadingIsInverted();
	
	static String reverseString(String orig) {
		String reversed = "";
		for (int i = (orig.length() - 1); i >= 0; i--) {
			reversed += orig.charAt(i);
		}
		return reversed;
	}
	
}
