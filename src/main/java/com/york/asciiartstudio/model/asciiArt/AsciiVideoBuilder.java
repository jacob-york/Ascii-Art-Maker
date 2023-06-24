/**
 * A class representing an AsciiArt video.
 */

package com.york.asciiartstudio.model.asciiArt;

import com.york.asciiartstudio.model.adapters.ImageSource;
import com.york.asciiartstudio.model.adapters.VideoSource;
import kotlin.contracts.Returns;

import java.util.Arrays;

public class AsciiVideoBuilder implements AsciiArtBuilder {

	private final String DEFAULT_PALETTE;

	private int charWidth;

	private VideoSource videoSource;

	private String basePalette;

	private String activePalette;

	private double fps;

	private String name;

	public AsciiVideoBuilder(VideoSource videoSource) {
		DEFAULT_PALETTE = "@N#bhyo+s/=-:.  ";
		reset(videoSource);
	}

	public AsciiVideoBuilder reset(VideoSource videoSource) {
		this.charWidth = 1;
		basePalette = DEFAULT_PALETTE;
		activePalette = DEFAULT_PALETTE;
		fps = videoSource.getFps();
		name = videoSource.getName();
		this.videoSource = videoSource;
		return this;
	}

	public String getFont() {
		return "Consolas";
	}

	/**
	 * a method for reversing a string.
	 * @param orig String to be reversed (original).
	 * @return orig reversed.
	 */
	private static String reverseString(String orig) {
		StringBuilder reversed = new StringBuilder();
		for (int i = (orig.length() - 1); i >= 0; i--) {
			reversed.append(orig.charAt(i));
		}
		return reversed.toString();
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
	 * Converts AsciiVideo to an array of Strings.
	 * @return all frames of AsciiVideo as an array of AsciiImages.
	 */
	public String[] getResult() {
		ImageSource[] imageSources = videoSource.getImageSources();

		return Arrays.stream(imageSources)
				.parallel()
				.map(imageSource -> new AsciiImageBuilder(imageSource)
					.setCharWidth(charWidth)
					.setInvertedShading(getInvertedShading())
					.setPalette(activePalette)
						.toString()
				)
				.toArray(String[]::new);
	}

	@Override
	public AsciiVideoBuilder setCharWidth(int newCharWidth) throws IllegalArgumentException {
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
	public AsciiVideoBuilder setPalette(String newPalette) throws IllegalArgumentException  {
		if (256 % newPalette.length() != 0) {
			throw new IllegalArgumentException ("char count in Palette must be divisible by 256.");
		}

		basePalette = newPalette;
		if (getInvertedShading())
			activePalette = reverseString(basePalette);
		else activePalette = basePalette;

		return this;
	}

	@Override
	public AsciiVideoBuilder setInvertedShading(boolean invertShading) {
		if (invertShading)
			activePalette = reverseString(basePalette);
		else activePalette = basePalette;
		return this;
	}

	@Override
	public AsciiVideoBuilder setName(String newName) {
		name = newName;
		return this;
	}

	@Override
	public boolean usingDefaultPalette() {
		return basePalette.equals(DEFAULT_PALETTE);
	}

	@Override
	public boolean getInvertedShading() {
		return activePalette.equals(reverseString(basePalette));
	}

}
