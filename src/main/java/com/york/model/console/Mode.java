package com.york.model.console;

import com.york.model.asciiArt.AsciiImage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public abstract class Mode {

    protected Scanner scanner;

    protected int charWidth;

    protected boolean invertedShading;

    public abstract void launch();

    public abstract String toString();

    public String nextLine() {
        return scanner.nextLine();
    }

    public static String writeImageToOutput(AsciiImage asciiImage, String outPutLocation) throws IOException {
        String art = asciiImage.toString();

        String outPutPath = outPutLocation + "\\" + asciiImage.getName() + "-cw" + asciiImage.getCharWidth();
        if (asciiImage.shadingIsInverted()) {
            outPutPath += "-inv";
        }
        outPutPath += ".txt";

        File file = new File(outPutPath);
        FileOutputStream fos = new FileOutputStream(file);
        OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
        osw.write(art);
        osw.close();
        fos.close();

        return outPutPath;
    }

    protected void requestCharWidth() {

        while (true) {
            System.out.print("Enter character width:\n>");
            try {
                int charWidth = Integer.parseInt(nextLine());
                if (charWidth <= 0) {
                    throw new ArithmeticException();
                }
                this.charWidth = charWidth;
                return;
            }
            catch (NumberFormatException e) {
                System.out.println("Please enter a whole number in digit form. (If your input has commas in it, get rid of them)");
            }
            catch (ArithmeticException e) {
                System.out.println("Please enter an int greater than zero.");
            }
        }
    }

    protected void requestInvertedShading() {
        System.out.print("Would you like to render it with inverted shading? (y/n):\n>");
        while (true) {
            String input = nextLine().toLowerCase();
            if (input.equals("y") || input.equals("n")) {
                this.invertedShading = input.charAt(0) == 'y';
                return;
            }
            else System.out.print("Please try again ('y' or 'n'):\n>");
        }
    }

}
