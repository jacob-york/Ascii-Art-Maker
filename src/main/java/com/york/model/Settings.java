package com.york.model;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Settings {

    public static final Path DOWNLOADS = Paths.get(
            "C:\\Users\\" + System.getProperty("user.name") + "\\Downloads"
    );

    private Settings instance;

    private Settings() {

    }

    public Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }
}
