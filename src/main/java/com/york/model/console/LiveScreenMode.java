package com.york.model.console;

import java.util.Scanner;

public class LiveScreenMode extends Mode {

    public LiveScreenMode(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public String toString() {
        return "Live Screen Mode";
    }

    @Override
    public void launch() {
        requestCharWidth();
        requestInvertedShading();
    }


}
