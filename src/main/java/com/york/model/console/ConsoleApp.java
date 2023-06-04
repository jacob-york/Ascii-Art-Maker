package com.york.model.console;

import java.util.Scanner;

public class ConsoleApp {

    private final Scanner scanner;

    private final Mode[] modes;

    public ConsoleApp() {
        scanner = new Scanner(System.in);
        modes = new Mode[] {
                new ImageFileMode(scanner),
                new VideoFileMode(scanner),
                new LiveScreenMode(scanner),
                new WebcamMode(scanner),
        };
    }

    private Mode getMode() {

        for (int i = 1; i <= modes.length; i++) {
            System.out.println(i + ") " + modes[i - 1]);
        }

        System.out.print("What would you like to do (");

        for (int i = 1; i <= modes.length; i++) {
            if (i != 1) {
                System.out.print(", ");
            }
            System.out.print(i);
        }
        System.out.print(")?:\n>");

        return modes[Integer.parseInt(scanner.nextLine()) - 1];
    }

    private char yesOrNo(String prompt) {
        System.out.print(prompt);
        while (true) {
            String input = scanner.nextLine().toLowerCase();
            if (input.equals("y") || input.equals("n")) return input.charAt(0);
            else System.out.print("Please try again ('y' or 'n'):\n>");
        }
    }

    public void launch() {
        try {
            do {
                getMode().launch();
            } while (yesOrNo("Would you like to go again? (y/n):\n>") == 'y');
        } finally {
            scanner.close();
        }
    }

    public static void main(String[] args) {
        new ConsoleApp().launch();
    }
}
