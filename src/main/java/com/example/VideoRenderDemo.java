package com.example;

import com.example.asciiArt.AsciiVideo;
import com.example.util.Timer;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class VideoRenderDemo {
    
	// TODO: press <Enter> to pause video and bring up menu to exit or resume
	
	public static void main(String[] args) {

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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			ans = ConsoleApp.yesOrNo("Would you like to go again? (y/n):\n>", scanner);
		}
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
	
    public static void compileToConsole(AsciiVideo asciiVideo, Scanner scanner) throws InterruptedException {
    	
    	Timer timer = new Timer();
    	
		System.out.println("Rendering...");
		ArrayList<String> frames = asciiVideo.getFrames();
		System.out.print("Video fully rendered. Press <Enter> to play:\n>");
		scanner.nextLine();
    	
    	double waitDouble = 1000.0 / asciiVideo.getFrameRate();
		
    	timer.start();
    	System.out.println(frames.get(0));
    	timer.stop();
    	
    	int waitInt = (int) (waitDouble - timer.getTime());
    	frames.remove(0);
    	
		for (String frame : frames) {
			System.out.println(frame);
		    Thread.sleep(waitInt);
		}
    }
    
    
	
}
