package com.york.model.console;

import java.util.Scanner;

public abstract class Mode {

    protected Scanner scanner;

    public abstract void launch();

    public abstract String toString();

    public String nextLine() {
        return scanner.nextLine();
    }



    protected int requestCharWidth() {

        while (true) {
            System.out.print("Enter character width:\n>");
            try {
                int charWidth = Integer.parseInt(nextLine());
                if (charWidth <= 0) {
                    throw new ArithmeticException();
                }
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

    protected boolean requestInvertedShading() {
        System.out.print("Would you like to render it with inverted shading? (y/n):\n>");
        while (true) {
            String input = nextLine().toLowerCase();
            if (input.equals("y") || input.equals("n")) {
                return input.charAt(0) == 'y';
            }
            else System.out.print("Please try again ('y' or 'n'):\n>");
        }
    }

}
