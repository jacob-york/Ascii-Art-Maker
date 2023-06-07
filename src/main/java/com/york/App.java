package com.york;

import com.york.model.console.ConsoleApp;

public class App {

    public static void main(String[] args) {
        nu.pattern.OpenCV.loadLocally();
        new ConsoleApp().launch();
    }
}
