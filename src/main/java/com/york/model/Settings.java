package com.york.model;

/**
 * Singleton for user Application Settings.
 */
public class Settings {

    public static final String DOWNLOADS = "C:\\Users\\" + System.getProperty("user.name") + "\\Downloads";

    private Settings instance;

    private Settings() {}

    public Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }
}
