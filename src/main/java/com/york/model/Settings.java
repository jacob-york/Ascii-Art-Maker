package com.york.model;

import java.io.File;

public class Settings {

    public static final File DOWNLOADS = new File("C:\\Users\\" + System.getProperty("user.name") + "\\Downloads");


    private File curChooseDir = Settings.DOWNLOADS;

    private File curSaveDir = Settings.DOWNLOADS;

    private static Settings instance;

    private Settings() {

    }

    public static Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }

    public File getCurSaveDir() {
        return curSaveDir;
    }

    public void setCurSaveDir(File newSaveDir) {
        curSaveDir = newSaveDir;
    }

    public File getCurChooseDir() {
        return curChooseDir;
    }

    public void setCurChooseDir(File newChooseDir) {
        curChooseDir = newChooseDir;
    }
}
