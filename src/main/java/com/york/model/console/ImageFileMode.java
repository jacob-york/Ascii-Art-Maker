package com.york.model.console;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Scanner;

import com.york.model.Settings;
import com.york.model.adapters.BufferedImageAdapter;
import com.york.model.adapters.ImageFileAdapter;
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

		boolean validInput = false;
		while (!validInput) {
			System.out.print("Enter the absolute path to an image file:\n>");
			try {
				File myFile = new File(scanner.nextLine());
				imageSource = new ImageFileAdapter(myFile);
				validInput = true;
			}
			catch (IOException e) {
				System.out.println("Invalid Input. Please try again.");
			}
			catch (IllegalArgumentException e) {
				System.out.println("Format not accepted. Please try again.");
			}
		}

		AsciiImage asciiImage = new AsciiImage(imageSource);
		asciiImage.setCharWidth(requestCharWidth());
		asciiImage.setInvertedShading(requestInvertedShading());

		System.out.println("Working...");

		try {
			String outputPath = writeImageToOutput(imageSource.getName(), asciiImage, Settings.DOWNLOADS);
			System.out.println(imageSource.getName() + " successfully printed art to: " + outputPath);
		}
		catch (IOException e) {
			System.out.print("Failed to output file:\n" + e + "\n");
		}
	}
	
}
