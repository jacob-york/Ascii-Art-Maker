/**
 * A class representing an AsciiArt video.
 * Powered by OpenCV.
 */

package com.york.model.asciiArt;

import com.york.model.adapters.ImageSource;
import com.york.model.adapters.VideoSource;

import java.util.Arrays;

public class AsciiVideo implements AsciiArt {

	private int charWidth;

	private VideoSource videoSource;

	private String basePalette;

	private String activePalette;

	private final double fps;


	public AsciiVideo(VideoSource videoSource) {
		this.charWidth = 1;
		basePalette = DEFAULT_PALETTE;
		activePalette = DEFAULT_PALETTE;
		fps = videoSource.getFPS();
		this.videoSource = videoSource;
	}

	@Override
	public int getWidth() {
		int domain = videoSource.getWidth() - (videoSource.getWidth() % charWidth);
		return domain / charWidth;
	}

	@Override
	public int getHeight() {
		int range = videoSource.getHeight() - (videoSource.getHeight() % (2 * charWidth));
		return range / (2 * charWidth);
	}

	@Override
	public int getArea() {
		return getWidth() * getHeight();
	}

	@Override
	public int getCharWidth() {
		return charWidth;
	}
	
	public double getFPS() {
		return fps;
	}
	
	@Override
	public String getPalette() {
		return basePalette;
	}

	/**
	 * Converts AsciiVideo to an array of Ascii Images.
	 * @return all frames of AsciiVideo as an array of AsciiImages.
	 */
	public AsciiImage[] toAsciiImageArray() {
		ImageSource[] imageSources = videoSource.getImageSourceArray();

		return Arrays.stream(imageSources)
				.parallel()
				.map(imageSource -> new AsciiImage(imageSource)
					.setCharWidth(charWidth)
					.setInvertedShading(shadingIsInverted())
					.setPalette(activePalette)
				)
				.toArray(AsciiImage[]::new);
	}

	@Override
	public AsciiVideo setCharWidth(int newCharWidth) throws IllegalArgumentException {
		if (newCharWidth > videoSource.getWidth()) {
			throw new IllegalArgumentException("Char width cannot be greater than the media width.");
		}
		if (2 * newCharWidth > videoSource.getHeight()) {
			throw new IllegalArgumentException("Char width cannot be greater than [media height / 2].");
		}
		if (newCharWidth < 1) {
			throw new IllegalArgumentException("Char width must be greater than 0.");
		}

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

	public AsciiVideo setVideoSource(VideoSource newVideoSource) {
		// todo: input validation

		videoSource = newVideoSource;
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

}
