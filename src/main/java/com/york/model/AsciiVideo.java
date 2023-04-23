/**
 * A class representing an AsciiArt video.
 * TODO: Disabled until issue with external library "openCV" is resolved.
 */

package com.york.model;

/*
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
*/

public class AsciiVideo implements AsciiArt {

	private String path;
	//private VideoCapture vc;
	private int charWidth;
	private double frameRate;
	private String basePalette;
	private String activePalette;

	/*
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
	*/
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

	/*
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
	        
			System.out.println(new AsciiImage(image, charWidth, shadingIsInverted(), activePalette).generateInParallel());
			timer.stop();
			
			int waitInt = (int) (waitDouble - timer.getTime());
			Thread.sleep(waitInt);
		}
		vc.release();
    }
    */
    public String getPath() {
    	return path;
    }

	@Override
	public boolean setCharWidth(int newCharWidth) {
		// TODO Auto-generated method stub
		return false;
	}

	public void setFrameRate(double newFrameRate) {
		frameRate = newFrameRate;
	}

	@Override
	public boolean setPalette(String newPalette) {
		if (256 % newPalette.length() != 0) return false;

		basePalette = newPalette;
		if (shadingIsInverted())
			activePalette = AsciiArt.reverseString(basePalette);
		else activePalette = basePalette;

		return true;
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
