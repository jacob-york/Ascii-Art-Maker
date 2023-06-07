package com.york.model.console;

import java.util.Scanner;

public final class ConsoleApp {

    private final Scanner scanner;

    private final Mode[] modes;

    public ConsoleApp() {
        scanner = new Scanner(System.in);
        modes = new Mode[] {
                new ImageFileMode(scanner),
                new VideoFileMode(scanner),
        };
    }

    private String getModesList() {
        StringBuilder stringBuilder = new StringBuilder("(");
        for (int i = 1; i <= modes.length; i++) {
            if (i != 1) {
                stringBuilder.append(", ");
            }
            stringBuilder.append(i);
        }
        stringBuilder.append(")");

        return stringBuilder.toString();
    }

    private Mode requestMode() {
        for (int i = 1; i <= modes.length; i++) {
            System.out.println(i + ") " + modes[i - 1]);
        }

        System.out.print("What would you like to do " + getModesList() + "?:\n>");

        while (true) {
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                return modes[choice - 1];
            }
            catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a mode " + getModesList() + ":\n>");
            }
        }
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
                requestMode().launch();
            } while (yesOrNo("Would you like to go again? (y/n):\n>") == 'y');
        } finally {
            scanner.close();
        }
    }

}
