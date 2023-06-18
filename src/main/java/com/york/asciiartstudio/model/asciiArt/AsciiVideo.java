/**
 * A class representing an AsciiArt video.
 * Powered by OpenCV.
 */

package com.york.asciiartstudio.model.asciiArt;

import com.york.asciiartstudio.model.adapters.ImageSource;
import com.york.asciiartstudio.model.adapters.VideoSource;

import java.util.Arrays;

public class AsciiVideo implements AsciiArt {

	private int charWidth;

	private final VideoSource videoSource;

	private String basePalette;

	private String activePalette;

	private final double fps;

	private String name;

	public AsciiVideo(VideoSource videoSource) {
		this.charWidth = 1;
		basePalette = DEFAULT_PALETTE;
		activePalette = DEFAULT_PALETTE;
		fps = videoSource.getFPS();
		name = videoSource.getName();
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
	public int getMaxCharWidth() {
		boolean ge2 = videoSource.getHeight() / videoSource.getWidth() >= 2;
		return ge2 ? videoSource.getWidth() : videoSource.getHeight() / 2;
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

	@Override
	public String getName() {
		return name;
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
		if (newCharWidth > getMaxCharWidth()) {
			throw new IllegalArgumentException("Char width cannot be greater than max char width: " + getMaxCharWidth());
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

	@Override
	public AsciiVideo setName(String newName) {
		name = newName;
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
