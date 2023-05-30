package com.york;

/**
 * Renders Ascii Videos into console
 */
import com.york.model.AsciiVideo;

import java.io.File;

public class VideoConsoleApp extends ConsoleApp {

	public VideoConsoleApp() {
		super();
	}

	@Override
	public String requestPath() {
		boolean validInput = false;
		String returnVal = null;

		while (!validInput) {
			System.out.print("Enter the absolute path to a video file (w/ extension):\n>");
			returnVal = getScanner().nextLine();
			if (new File(returnVal).exists()) {
				validInput = true;
			}
			else {
				System.out.println("File not found. Please try again.");
			}
		}
		return returnVal;
	}

	@Override
	public void generateAscii(String path, int charWidth, boolean invertedShading) {
		AsciiVideo asciiVideo = new AsciiVideo(path)
				.setCharWidth(charWidth)
				.setInvertedShading(invertedShading);
		try {
			asciiVideo.compileToConsole();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new VideoConsoleApp().launch();
	}
	
}
