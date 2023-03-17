/**
 * A simple console interface for the project
 * @author Jacob York
 */

package com.york;

import java.io.*;
import java.util.Scanner;

import com.york.model.asciiArt.AsciiImage;
import com.york.model.asciiArt.ImageLoader;
import com.york.util.Timer;

public class ConsoleApp {
	
	// edit this to your needs
	public static final String DOWNLOADS = "C:\\Users\\" + System.getProperty("user.name") + "\\Downloads";
	public static String outPath = "C:\\Users\\party\\OneDrive\\Documents\\AsciiArtMaker Output";
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		
		try {
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
		finally {
			scanner.close();
		}
	}
	
	public static String getPathToImage(Scanner scanner) {
	
		while (true) {
			System.out.print("Enter the absolute path to an image file (w/ extension):\n>");
			String pathToImage = scanner.nextLine();
			
			int success = ImageLoader.testPath(pathToImage);
			
			if (success == 0) return pathToImage;
			else if (success == 1) {
				System.out.println("File not found. Please try again.");
			}
			else if (success == 2) {
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

			
			System.out.print("Initializing ascii art...");
			timer.start();
			AsciiImage ascii = new AsciiImage(pathToImage, charWidth, invertedShading);
			timer.stop(); 
			System.out.println("(time: " + timer.getTime() + " ms).");
			
			
			System.out.print("Generating...");
			timer.start();
			String finalArt = ascii.toString();
			timer.stop(); 
			System.out.println("(time: " + timer.getTime() + " ms).");

			
			
			System.out.print("Writing to txt file...");
			timer.start();
			writeToOutput(finalArt, outPath, ascii.getName());
			timer.stop();
			System.out.println("(time: " + timer.getTime() + " ms).");
			
			
			System.out.println(ascii.getName() + " successfully printed art to: \n" + outPath.toString() + "\\" + ascii.getName());
			System.out.println(ascii.usesDefaultPalette());
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
		}
	}
	// writes the image's toString to a .txt file in the specified path.
	public static void writeToOutput(String art, String outputPath, String fileName) throws IOException {
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		
		try {
			File file = new File(outputPath + "\\" + fileName);
			fos = new FileOutputStream(file);
			osw = new OutputStreamWriter(fos, "UTF-8");
			osw.write(art);
		}
		finally {
			if (osw != null)
				osw.close();
			if (fos != null)
				fos.close();
		}
	}
	
}
