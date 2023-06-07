package com.york.model.console;

import java.util.Scanner;

public final class LiveScreenMode extends Mode {

    public LiveScreenMode(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public String toString() {
        return "Live Screen Mode (Coming Soon!)";
    }

    @Override
    public void launch() {
        // TODO: implement this
    }


}
