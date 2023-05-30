/**
 * A class representing an AsciiArt video.
 * Powered by OpenCV.
 */

package com.york.model;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import com.york.util.Timer;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Scanner;

public class AsciiVideo implements AsciiArt {

	private String path;
	private VideoCapture vc;
	private int charWidth;
	private double frameRate;
	private String basePalette;
	private String activePalette;

	public AsciiVideo(String path) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		this.path = path;
		this.charWidth = 1;
		vc = new VideoCapture(path);
		basePalette = DEFAULT_PALETTE;
		activePalette = DEFAULT_PALETTE;
		frameRate =	vc.get(Videoio.CAP_PROP_FPS);;
		//System.out.println("DEBUG : " + frameRate);
		
		vc.release();
	}

	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getArea() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getCharWidth() {
		return charWidth;
	}
	
	public double getFrameRate() {
		return frameRate;
	}
	
	@Override
	public String getPalette() {
		return basePalette;
	}

    public ArrayList<String> getFrames() {
    	
    	ArrayList<String> frameList = new ArrayList<String>();
    	ImageLoader imgHandler = new ImageLoader();
    	vc.open(path);
		Mat mat = new Mat();
		
		vc.read(mat);
		
		while (vc.read(mat)) {
			
			// convert mat to a BufferedImage
			BufferedImage image;
	        int type = 0;
	        if (mat.channels() == 1) {
	            type = BufferedImage.TYPE_BYTE_GRAY;
	        } else if (mat.channels() == 3) {
	            type = BufferedImage.TYPE_3BYTE_BGR;
	        }
	        image = new BufferedImage(mat.width(), mat.height(), type);
	        WritableRaster raster = image.getRaster();
	        DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
	        byte[] data = dataBuffer.getData();
	        mat.get(0, 0, data);
	        //
	        
			AsciiImage curFrame = new AsciiImage(image);
			curFrame
					.setCharWidth(charWidth)
					.setInvertedShading(shadingIsInverted())
					.setPalette(activePalette);

			frameList.add(curFrame.toString());
		}
		vc.release();
		
		return frameList;
    }
    public static void compileToConsole(Scanner scanner) throws InterruptedException {

    	Timer timer = new Timer();

		System.out.println("Rendering...");
		ArrayList<String> frames = getFrames();
		System.out.print("Video fully rendered. Press <Enter> to play:\n>");
		scanner.nextLine();

    	double waitDouble = 1000.0 / frameRate;

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
	public void interpretToConsole() throws InterruptedException {
    	
    	vc.open(path);
		Mat mat = new Mat();
		Timer timer = new Timer();
		
		double waitDouble = 1000.0 / getFrameRate();
		
		while (vc.read(mat)) {
			
			timer.start();
			// convert mat to a BufferedImage
			BufferedImage image;
	        int type = 0;
	        if (mat.channels() == 1) {
	            type = BufferedImage.TYPE_BYTE_GRAY;
	        } else if (mat.channels() == 3) {
	            type = BufferedImage.TYPE_3BYTE_BGR;
	        }
	        image = new BufferedImage(mat.width(), mat.height(), type);
	        WritableRaster raster = image.getRaster();
	        DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
	        byte[] data = dataBuffer.getData();
	        mat.get(0, 0, data);
	        //

	        AsciiImage asciiImage = new AsciiImage(image);
	        asciiImage.setCharWidth(charWidth)
	        		.setInvertedShading(shadingIsInverted())
	        		.setPalette(activePalette);

			System.out.println(asciiImage.generateInParallel());
			timer.stop();
			
			int waitInt = (int) (waitDouble - timer.getTime());
			Thread.sleep(waitInt);
		}
		vc.release();
    }

    public String getPath() {
    	return path;
    }

	@Override
	public AsciiVideo setCharWidth(int newCharWidth) throws IllegalArgumentException {
		// TODO Auto-generated method stub

		return this;
	}

	@Override
	public AsciiVideo setPalette(String newPalette) throws IllegalArgumentException  {
		if (256 % newPalette.length() != 0) {
			throw new IllegalArgumentException ("char count in Palette must be divisible by 256.");
		}

		basePalette = newPalette;
		if (shadingIsInverted())
			activePalette = AsciiArt.reverseString(basePalette);
		else activePalette = basePalette;

		return this;
	}

	@Override
	public AsciiVideo setInvertedShading(boolean invertShading) {
		if (invertShading)
			activePalette = AsciiArt.reverseString(basePalette);
		else activePalette = basePalette;
		return this;
	}

	public AsciiVideo setFrameRate(double newFrameRate) {
		frameRate = newFrameRate;
		return this;
	}

	@Override
	public boolean usesDefaultPalette() {
		return basePalette.equals(DEFAULT_PALETTE);
	}

	@Override
	public boolean shadingIsInverted() {
		return activePalette.equals(AsciiArt.reverseString(basePalette));
	}

}
