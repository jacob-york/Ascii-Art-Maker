package com.york;

/**
 * A simple console interface for rendering images into txt files
 * @author Jacob York
 */

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import com.york.model.AsciiImage;
import com.york.model.adapters.PathAdapter;
import com.york.util.Timer;

import javax.imageio.ImageIO;

public class ConsoleApp {

	public static final String DOWNLOADS = "C:\\Users\\" + System.getProperty("user.name") + "\\Downloads";

	private final Scanner scanner;

	private PathAdapter pathAdapter;

	public ConsoleApp() {
		scanner = new Scanner(System.in);
		pathAdapter = null;
	}

	public Scanner getScanner() {
		return scanner;
	}

	public void requestPath() {

		while (true) {
			System.out.print("Enter the absolute path to an image file (w/ extension):\n>");
			String pathToImage = scanner.nextLine();

			try {
				pathAdapter = new PathAdapter(pathToImage);
				if (PathAdapter.testPath(pathToImage) == PathAdapter.FILE_NOT_ACCEPTED) {
					StringBuilder displayFormats = new StringBuilder();
					for (String format : PathAdapter.getAcceptedFormats()) {
						if (displayFormats.toString().equals("")) {
							displayFormats.append(format);
						}
						else displayFormats.append(", ").append(format);
					}
					System.out.println("Format is not accepted (Accepted formats: " + displayFormats + ").");
				}
				else return;
			}
			catch (IOException e) {
				System.out.println("File not found. Please try again.");
			}
		}
	}
	
	public int requestCharWidth() {
		
		while (true) {
			System.out.print("Enter character width:\n>");
			try {
				int charWidth = Integer.parseInt(scanner.nextLine());
				if (charWidth <= 0) {
					throw new ArithmeticException();
				}
				return charWidth;
			}
			catch (NumberFormatException e) {
				System.out.println("Please enter a whole number in digit form. (If your input has commas in it, get rid of them)");
			}
			catch (ArithmeticException e) {
				System.out.println("Please enter an int greater than zero.");
			}
		}
	}
	
	public boolean requestInvertedShading() {
		char invertedAns = yesOrNo("Would you like to render it with inverted shading? (y/n):\n>");
		return invertedAns == 'y';
	}

	/**
	 * writes the image's toString to a .txt file in the specified path.
	 * @param outPutLocation directory into which to write the Txt File
	 */
	public static String writeToOutput(AsciiImage asciiImage, String outPutLocation) throws IOException {
		String art = asciiImage.toString();

		String outPutPath = outPutLocation + "\\" + asciiImage.getName() + "-cw" + asciiImage.getCharWidth();
		if (asciiImage.shadingIsInverted()) {
			outPutPath += "-inv";
		}
		outPutPath += ".txt";

		File file = new File(outPutPath);
		FileOutputStream fos = new FileOutputStream(file);
		OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
		osw.write(art);
		osw.close();
		fos.close();

		return outPutPath;
	}

	public void generateAscii(int charWidth, boolean invertedShading) {

		Timer timer = new Timer();
		try {
			System.out.println("Working...");
			timer.start();

			AsciiImage asciiImage = new AsciiImage(pathAdapter)
					.setName(pathAdapter.getImageName())
					.setCharWidth(charWidth)
					.setInvertedShading(invertedShading);
			String outPutPath = writeToOutput(asciiImage, DOWNLOADS);

			System.out.println(asciiImage.getName() + " successfully printed art to: " + outPutPath);
			System.out.println(asciiImage.usesDefaultPalette());
		} catch (IOException e) {
			System.out.print("Failed to output file:\n" + e + "\n");
		}
	}
	
	public char yesOrNo(String prompt) {
		System.out.print(prompt);
		while (true) {
			String input = scanner.nextLine().toLowerCase();
			if (input.equals("y") || input.equals("n")) return input.charAt(0);
			else System.out.print("Please try again ('y' or 'n'):\n>");
		}
	}

	public void launch() {
		try {
			char ans = 'y';
			while (ans == 'y') {
				requestPath();
				int charWidth = requestCharWidth();
				boolean invertedShading = requestInvertedShading();

				// start generating...
				generateAscii(charWidth, invertedShading);
				ans = yesOrNo("Would you like to go again? (y/n):\n>");
			}
		} finally {
			getScanner().close();
		}
	}

	public static void main(String[] args) {
		new ConsoleApp().launch();
	}
	
}
