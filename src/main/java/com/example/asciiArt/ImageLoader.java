package com.example.asciiArt;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class ImageLoader {
	
	public static final String[] ACCEPTED_FORMATS = {"jpg", "jpeg", "png"};
	private String path;
	private BufferedImage image;
	
	// constructor
	public ImageLoader() {
		this.path = null;
		this.image = null;
	}
	public ImageLoader(BufferedImage image) {
		this.path = null;
		this.image = image;
	}
	
	// getters
	public BufferedImage getImage() {
		return image;
	}
	public int[][] getShadingRaster() {
		
		if (this.image == null) return new int[][] {{255}};
		
		int sRWidth = image.getWidth();
		int sRHeight = image.getHeight();
		int[][] shadingRaster = new int[sRHeight][sRWidth];
		
		for (int y = 0; y < sRHeight; y++) {
			for(int x = 0; x < sRWidth; x++) {
				int pixelColor = image.getRGB(x, y);
				shadingRaster[y][x] = desaturate(pixelColor);
			}
		}
		return shadingRaster;
	}
	public String getName() {
		if (path == null)
			return "[ANON]";
		else
			return path.substring(path.lastIndexOf('\\') + 1); 
	}
	public String getFileExtension() {
		if (path == null) return null;
		
		int fileExtensionStart = path.lastIndexOf('.') + 1;
		return path.substring(fileExtensionStart);
	}
	public static String[] getAcceptedFormats() {
		return ACCEPTED_FORMATS.clone();
	}
	
	// setters
	public int setImage(BufferedImage newImage) {
		image = newImage;
		path = null;
		return 0;
	}
	public int loadFromFile(String path) {
		
		try {
			BufferedImage newImage = ImageIO.read(new File(path));
			if (!isAcceptedFormat(path)) {
				return 2;  // file was found, but the format is not accepted.
			}
			
			this.image = newImage;
			this.path = path;
			
			return 0;  // valid.
		}
		catch (IOException e) {
			return 1;  // file not found.
		}
		
		
	}
	
	// algorithm for desaturating a color. Returns an int value (0-255) to represent pixel's shade of grey.
	private static int desaturate(int color) {
	
		int a = (color & 0xff000000) >> 24;
		int r = (color & 0xff0000) >> 16;
		int g = (color & 0xff00) >> 8;
		int b = color & 0xff;
		
		if (a == 0) {  // TODO: quick-fix for alpha values. Make it more sophisticated.
			return 255;
		}
		else {
			if (r > g) {
				if (r > b) {
					// r is bigger than both, so it's the primary color
					while (r > g && r > b) {
						// desaturate the pixel by lowering primaries and raising secondaries until the secondaries pass the primaries
						r --;
						g ++;
						b ++;
					}
				}
				else if (r < b) {
					while (b > r && b > g) {
						r ++;
						g ++;
						b --;
					}
				}
				else if (r == b) {
					while (r > g) {
						r --;
						g ++;
						b --;
					}
				}
			}
			else if (g > r) {
				if (g > b) {
					while (g > r && g > b) {
						r ++;
						g --;
						b ++;
					}
				}
				else if (g < b) {
					while (b > r && b > g) {
						r ++;
						g ++;
						b --;
					}
	
				}
				else if (g == b) {
					while (g > r) {
						r ++;
						g --;
						b --;
					}
				}
			}
			else if (r == g) {
				if (r > b) {
					while (r > b) {
						r --;
						g --;
						b ++;
					}
				}
				else if (r < b) {
					while (b > r && b > g) {
						r ++;
						g ++;
						b --;
					}
				}
				else if (g == b) {
					return r;  // arriving here means the pixel is already grey, so just return any color value
				}
			}
			
			return (r + g + b) / 3;  // after your process, average the three
		}
	}
	
	public static boolean isAcceptedFormat(String path) {
		for (String format : ACCEPTED_FORMATS) {
			if (path.toLowerCase().endsWith("." + format)) return true;
		}
		return false;
	}
	
	public static int testPath(String path) {
		if (path == null) return 3;  // null pointer.
		if (!new File(path).exists()) return 1;  // file not found.
		if (!isAcceptedFormat(path)) return 2;  // file was found, but the format is not accepted.
		return 0;  // valid.
	}
	
	public boolean hasImage() {
		return image != null;
	}
	
	public void clear() {
		image = null;
	}
}
