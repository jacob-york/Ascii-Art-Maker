import com.example.ConsoleApp;
import com.example.asciiArt.AsciiImage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class test {
	public static void main(String[] args) throws IOException {
		BufferedImage image = ImageIO.read(new File("E:\\Media++\\Pictures++\\image.jpg"));
		String art = new AsciiImage(image, 3).generateInParallel();
		ConsoleApp.writeToOutput(art, "D:\\Programming\\Projects\\JavaProjects\\Personal\\AsciiArtMaker\\output", "image.txt");
	}
}
