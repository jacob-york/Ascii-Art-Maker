/**
 * Renders Ascii Videos into console
 * TODO: Disabled until issue with external library "openCV" is resolved.
 */

import java.io.File;
import java.util.Scanner;

public class VideoConsoleApp {
    
	// TODO: press <Enter> to pause video and bring up menu to exit or resume
	
	public static void main(String[] args) {
/*
		Scanner scanner = new Scanner(System.in);
		char ans = 'y';
		while (ans == 'y') {
			
			String path = getPath(scanner);
			int charWidth = ConsoleApp.getCharWidth(scanner);
			boolean invertedShading = ConsoleApp.getInvertedShading(scanner);
			AsciiVideo asciiVideo = new AsciiVideo(path, charWidth, invertedShading);
	
			try {
				asciiVideo.interpretToConsole();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}	
			ans = ImageConsoleApp.yesOrNo("Would you like to go again? (y/n):\n>", scanner);
		}
*/
	}
	
	public static String getPath(Scanner scanner) {
		boolean validInput = false;
		String returnVal = null;
		
		while (!validInput) {
			System.out.print("Enter the absolute path to a video file (w/ extension):\n>");
			returnVal = scanner.nextLine();
			File testFile = new File(returnVal);
			if (testFile.exists()) {
				validInput = true;
			}
			else {
				System.out.println("File not found. Please try again.");
			}
		}
		return returnVal;
	}
	
}
