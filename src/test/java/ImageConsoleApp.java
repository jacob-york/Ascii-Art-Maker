/**
 * A simple console interface for rendering images into txt files
 * @author Jacob York
 */

import java.io.*;
import java.util.Scanner;

import com.york.model.AsciiImage;
import com.york.model.ImageLoader;
import com.york.util.Timer;

public class ImageConsoleApp {

	public static final String DOWNLOADS = "C:\\Users\\" + System.getProperty("user.name") + "\\Downloads";
	
	public static void main(String[] args) {
		try (Scanner scanner = new Scanner(System.in)) {
			String path = null;
			int charWidth = 1;
			char ans = 'y';
			boolean invertedShading = false;

			// core while loop
			while (ans == 'y') {
				path = getPathToImage(scanner);
				charWidth = getCharWidth(scanner);
				invertedShading = getInvertedShading(scanner);

				// start generating...
				generateAscii(path, charWidth, invertedShading);
				ans = yesOrNo("Would you like to go again? (y/n):\n>", scanner);
			}
		}
	}
	
	public static String getPathToImage(Scanner scanner) {
	
		while (true) {
			System.out.print("Enter the absolute path to an image file (w/ extension):\n>");
			String pathToImage = scanner.nextLine();

			switch (ImageLoader.testPath(pathToImage)) {
				case SUCCESS -> {
					return pathToImage;
				}
				case FILE_NOT_FOUND -> System.out.println("File not found. Please try again.");
				case FILE_NOT_ACCEPTED -> {
					StringBuilder displayFormats = new StringBuilder();
					for (String format : ImageLoader.getAcceptedFormats()) {
						if (displayFormats.toString().equals(""))
							displayFormats.append(format);
						else displayFormats.append(", ").append(format);
					}
					System.out.println("Format is not accepted (Accepted formats: " + displayFormats + ").");
				}
			}
		}
	}
	
	public static int getCharWidth(Scanner scanner) {
		
		while (true) {
			System.out.print("Enter character width:\n>");
			try {
				int charWidth = Integer.parseInt(scanner.nextLine());
				if (charWidth <= 0)
					throw new ArithmeticException();
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
	
	public static boolean getInvertedShading(Scanner scanner) {		
		char invertedAns = yesOrNo("Would you like to render it with inverted shading? (y/n):\n>", scanner);
		return invertedAns == 'y';
	}
	
	public static void generateAscii(String pathToImage, int charWidth, boolean invertedShading) { 

		Timer timer = new Timer();
		try {
			System.out.println("Working...");
			timer.start();
			AsciiImage asciiImage = new AsciiImage(pathToImage, charWidth);
			asciiImage.setInvertedShading(invertedShading);
			asciiImage.writeToOutput(DOWNLOADS);

			System.out.println(asciiImage.getName() + " successfully printed art to: \n" + DOWNLOADS + "\\" + asciiImage.getName());
			System.out.println(asciiImage.usesDefaultPalette());
		} catch (IOException e) {
			System.out.print("Failed to output file:\n" + e + "\n");
		}
	}
	
	public static char yesOrNo(String prompt, Scanner scanner) {
		System.out.print(prompt);
		while (true) {
			String input = scanner.nextLine().toLowerCase();
			if (input.equals("y") || input.equals("n"))
				return input.charAt(0);
			else
				System.out.print("Please try again ('y' or 'n'):\n>");
		}}

	
}
