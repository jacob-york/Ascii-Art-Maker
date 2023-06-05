package com.york.model.console;

import java.io.*;
import java.util.Scanner;

import com.york.model.asciiArt.AsciiImage;
import com.york.model.Settings;
import com.york.model.adapters.ImagePathAdapter;
import com.york.util.Timer;

public final class ImageFileMode extends Mode {

	public ImageFileMode(Scanner scanner) {
		this.scanner = scanner;
	}

	@Override
	public String toString() {
		return "Image Mode";
	}

	private ImagePathAdapter requestPath() {

		while (true) {
			System.out.print("Enter the absolute path to an image file (w/ extension):\n>");
			String pathToImage = nextLine();

			ImagePathAdapter pathAdapter;
			try {
				pathAdapter = new ImagePathAdapter(pathToImage);
				if (ImagePathAdapter.testPath(pathToImage) == ImagePathAdapter.FILE_NOT_ACCEPTED) {
					StringBuilder displayFormats = new StringBuilder();
					for (String format : ImagePathAdapter.getAcceptedFormats()) {
						if (displayFormats.toString().equals("")) {
							displayFormats.append(format);
						}
						else displayFormats.append(", ").append(format);
					}
					System.out.println("Format is not accepted (Accepted formats: " + displayFormats + ").");
				}
				else return pathAdapter;
			}
			catch (IOException e) {
				System.out.println("File not found. Please try again.");
			}
		}
	}

	private void asciiImageFromPath(ImagePathAdapter pathAdapter, int charWidth, boolean invertedShading) {

		Timer timer = new Timer();
		try {
			System.out.println("Working...");
			timer.start();

			AsciiImage asciiImage = new AsciiImage(pathAdapter)
					.setName(pathAdapter.getFileName())
					.setCharWidth(charWidth)
					.setInvertedShading(invertedShading);
			String outPutPath = writeImageToOutput(asciiImage, Settings.DOWNLOADS);

			System.out.println(asciiImage.getName() + " successfully printed art to: " + outPutPath);
			System.out.println(asciiImage.usesDefaultPalette());
		} catch (IOException e) {
			System.out.print("Failed to output file:\n" + e + "\n");
		}
	}

	public void launch() {
		ImagePathAdapter pathAdapter = requestPath();
		requestCharWidth();
		requestInvertedShading();

		asciiImageFromPath(pathAdapter, charWidth, invertedShading);
	}
	
}
