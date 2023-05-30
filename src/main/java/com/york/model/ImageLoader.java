package com.york.model;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class ImageLoader {

	public enum Result {
		SUCCESS,
		FILE_NOT_FOUND,
		FILE_NOT_ACCEPTED,
		NULL_PATH,
	}
	private static final String[] ACCEPTED_FORMATS = {"jpg", "jpeg", "png"};
	private String path;
	private BufferedImage image;

	public ImageLoader() {
		this.path = null;
		this.image = null;
	}

	public ImageLoader(BufferedImage image) {
		this.path = null;
		this.image = image;
	}

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

	public void setImage(BufferedImage newImage) {
		image = newImage;
		path = null;
	}
	public Result loadFromFile(String path) {
		
		try {
			BufferedImage newImage = ImageIO.read(new File(path));
			if (!isAcceptedFormat(path)) {
				return Result.FILE_NOT_ACCEPTED;
			}
			
			this.image = newImage;
			this.path = path;
			
			return Result.SUCCESS;
		}
		catch (IOException e) {
			return Result.FILE_NOT_FOUND;
		}
		
	}

	/**
	 * algorithm for desaturating a color.
	 * @return an int value from 0 to 255 that represents a pixel's shade of grey.
	 */
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
					// r is bigger than both, so it's the primary color.
					while (r > g && r > b) {
						// desaturate the pixel by lowering primaries and
						// raising secondaries until the secondaries pass the primaries.
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

			// afterwards, average the three
			return (r + g + b) / 3;
		}
	}
	public static boolean isAcceptedFormat(String path) {
		for (String format : ACCEPTED_FORMATS) {
			if (path.toLowerCase().endsWith("." + format)) return true;
		}
		return false;
	}
	public static Result testPath(String path) {
		if (path == null) return Result.NULL_PATH;
		if (!new File(path).exists()) return Result.FILE_NOT_FOUND;
		if (!isAcceptedFormat(path)) return Result.FILE_NOT_ACCEPTED;
		return Result.SUCCESS;
	}
	public boolean hasImage() {
		return image != null;
	}
	public void clear() {
		image = null;
	}
}
