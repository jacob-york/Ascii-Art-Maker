package com.york.model.console;

import java.util.Scanner;

public class WebcamMode extends Mode {

    public WebcamMode(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public String toString() {
        return "Webcam Mode";
    }

    @Override
    public void launch() {
        //
        requestCharWidth();
        requestInvertedShading();
        //
    }
}
