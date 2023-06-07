package com.york.model.console;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Scanner;

import com.york.model.Settings;
import com.york.model.adapters.BufferedImageAdapter;
import com.york.model.adapters.ImageSource;
import com.york.model.asciiArt.AsciiImage;

import javax.imageio.ImageIO;

public final class ImageFileMode extends Mode {

	public ImageFileMode(Scanner scanner) {
		this.scanner = scanner;
	}

	@Override
	public String toString() {
		return "Image Mode";
	}

	public boolean formatIsAccepted(String fileName, String[] validExtensions) {
		for (String format : validExtensions) {
			if (format.equals(fileName.substring(fileName.lastIndexOf('.') + 1))) return true;
		}
		return false;
	}

	public static String writeImageToOutput(String name, AsciiImage asciiImage, Path writeTo) throws IOException {
		String art = asciiImage.toString();

		String outputPath = writeTo + "\\" + name + "-cw" + asciiImage.getCharWidth();
		if (asciiImage.shadingIsInverted()) {
			outputPath += "-inv";
		}
		outputPath += ".txt";

		File file = new File(outputPath);
		FileOutputStream fos = new FileOutputStream(file);
		OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
		osw.write(art);
		osw.close();
		fos.close();

		return outputPath;
	}

	public void launch() {

		ImageSource imageSource = null;
		String imageName = null;

		boolean validInput = false;
		while (!validInput) {
			System.out.print("Enter the absolute path to an image file:\n>");
			try {
				Path myPath = Paths.get(scanner.nextLine());	// InvalidPathException
				BufferedImage bufferedImage = ImageIO.read(myPath.toFile());	// IOException
				imageName = myPath.getFileName().toString();

				if (!formatIsAccepted(imageName, new String[] {"png", "jpg", "jpeg"})) {
					System.out.println("Format not accepted. Please try again (must be png or jpg).");
					continue;
				}

				imageName = imageName.substring(0, imageName.lastIndexOf('.'));
				imageSource = new BufferedImageAdapter(bufferedImage);
				validInput = true;
			}
			catch (InvalidPathException e) {
				System.out.println("Error reading path. Please try again.");
			}
			catch (IOException e) {
				System.out.println("File not found. Please try again.");
			}
		}

		AsciiImage asciiImage = new AsciiImage(imageSource);
		asciiImage.setCharWidth(requestCharWidth());
		asciiImage.setInvertedShading(requestInvertedShading());

		System.out.println("Working...");

		try {
			String outputPath = writeImageToOutput(imageName, asciiImage, Settings.DOWNLOADS);
			System.out.println(imageName + " successfully printed art to: " + outputPath);
		}
		catch (IOException e) {
			System.out.print("Failed to output file:\n" + e + "\n");
		}
	}
	
}
