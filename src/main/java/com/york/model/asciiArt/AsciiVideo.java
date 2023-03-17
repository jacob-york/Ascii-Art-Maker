package com.york.model.asciiArt;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import com.york.util.Timer;

public class AsciiVideo implements AsciiArt {

	private String path;
	private VideoCapture vc;
	private int charWidth;

	private double frameRate;
	
	private String basePalette;
	private String activePalette;
	
	// constructor(s)
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
	public AsciiVideo(String path, int charWidth) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		this.path = path;
		this.charWidth = charWidth;
		vc = new VideoCapture(path);
		basePalette = DEFAULT_PALETTE;
		activePalette = DEFAULT_PALETTE;
		frameRate =	vc.get(Videoio.CAP_PROP_FPS);;
		//System.out.println("DEBUG : " + frameRate);
		
		vc.release();
	}
	public AsciiVideo(String path, int charWidth, boolean invertedShading) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		this.path = path;
		this.charWidth = charWidth;
		vc = new VideoCapture(path);
		basePalette = DEFAULT_PALETTE;
		if (invertedShading)
			activePalette = AsciiArt.reverseString(basePalette);
		else activePalette = basePalette;
		frameRate = vc.get(Videoio.CAP_PROP_FPS);;
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
	        
			AsciiImage curFrame = new AsciiImage(image, charWidth, shadingIsInverted(), activePalette);
			frameList.add(curFrame.toString());
		}
		vc.release();
		
		return frameList;
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
	        
			System.out.println(new AsciiImage(image, charWidth, shadingIsInverted(), activePalette).generateInParallel());
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
	public int setCharWidth(int newCharWidth) {
		// TODO Auto-generated method stub
		return 0;
	}
	public void setFrameRate(double newFrameRate) {
		frameRate = newFrameRate;
	}
	@Override
	public int setPalette(String newPalette) {
		if (newPalette.length() != 16)
			return 1;
		
		basePalette = newPalette;
		if (shadingIsInverted())
			activePalette = AsciiArt.reverseString(basePalette);
		else activePalette = basePalette;
		
		return 0;
	}
	
	@Override
	public void setInvertedShading(boolean invertShading) {
		if (invertShading)
			activePalette = AsciiArt.reverseString(basePalette);
		else activePalette = basePalette;
	}

	@Override
	public boolean usesDefaultPalette() {
		if (basePalette.equals(DEFAULT_PALETTE))
			return true;
		else return false;
	}

	@Override
	public boolean shadingIsInverted() {
		return activePalette.equals(AsciiArt.reverseString(basePalette));
	}

}
