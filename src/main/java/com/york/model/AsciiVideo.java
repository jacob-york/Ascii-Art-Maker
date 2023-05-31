/**
 * A class representing an AsciiArt video.
 * Powered by OpenCV.
 */

package com.york.model;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import com.york.util.Timer;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.List;

public class AsciiVideo implements AsciiArt {

	private String path;

	private final VideoCapture vc;

	private int charWidth;

	private double frameRate;

	String name;

	private String basePalette;

	private String activePalette;


	public AsciiVideo(String path) {
		nu.pattern.OpenCV.loadLocally();
		
		this.path = path;
		this.charWidth = 1;
		vc = new VideoCapture(path);
		basePalette = DEFAULT_PALETTE;
		activePalette = DEFAULT_PALETTE;
		frameRate =	vc.get(Videoio.CAP_PROP_FPS);
		name = "asciiVideo";

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

	@Override
	public String getName() {
		return name;
	}

	/**
	 *
	 * @return a list of asciiArt strings called Frames.
	 */
    public List<String> getFrames() {
    	
    	List<String> frameList = new ArrayList<>();
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

			AsciiImage curFrame = new AsciiImage(new RasterMaker(image).getShadingRaster())
					.setCharWidth(charWidth)
					.setInvertedShading(shadingIsInverted())
					.setPalette(activePalette);

			frameList.add(curFrame.toString());
		}
		vc.release();
		
		return frameList;
    }

    public String getPath() {
    	return path;
    }

	@Override
	public AsciiVideo setCharWidth(int newCharWidth) throws IllegalArgumentException {
		// Todo: input validation

		charWidth = newCharWidth;
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

	@Override
	public AsciiVideo setName(String newName) {
		name = newName;
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


	/**
	 * Generates frames, then prints them to console.
	 * @throws InterruptedException
	 */
	public void compileToConsole() throws InterruptedException {
		System.out.println("Rendering...");
		Timer timer = new Timer();
		double waitDouble = 1_000.0/frameRate;

		// render frames via getFrames()
		List<String> frames = getFrames();

		// calculate how long printing a single frame takes.
		timer.start();
		System.out.println(frames.get(0));
		timer.stop();
		frames.remove(0);

		// print frames
		for (String frame : frames) {
			System.out.println(frame);
			int sleepTimeMillis = (int) (waitDouble - timer.getTime());
			Thread.sleep(sleepTimeMillis);
		}
	}

	/**
	 * 1) generate a frame.
	 * 2) print it to console.
	 * 3) rinse and repeat.
	 * wait time between prints updates dynamically, i.e. if your System has a sudden performance drop
	 * and gets slower, frame rate won't be affected.
	 *
	 * @throws InterruptedException
	 */
	public void interpretToConsole() throws InterruptedException {
		vc.open(path);

		Mat mat = new Mat();
		Timer timer = new Timer();
		double waitDouble = 1_000.0/getFrameRate();

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

			AsciiImage curFrame = new AsciiImage(new RasterMaker(image).getShadingRaster())
					.setCharWidth(charWidth)
					.setInvertedShading(shadingIsInverted())
					.setPalette(activePalette);

			System.out.println(curFrame);

			timer.stop();

			int sleepTimeMillis = (int) (waitDouble - timer.getTime());
			// TODO: Timeout Value is negative on second frame.
			Thread.sleep(sleepTimeMillis);
		}
		vc.release();
	}
}
