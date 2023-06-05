package com.york.model;

import java.io.File;
import java.util.Objects;

/**
 * preforms all input validation involved with file handling
 * so the programmer doesn't have to deal with IOExceptions (for example).
 */
public class FileIOHandler {

    public static final int NULL_PATH = 3;

    public static final int FILE_NOT_FOUND = 2;

    public static final int FILE_NOT_ACCEPTED = 1;

    public static final int SUCCESS = 0;


    private String getResource(String testImageName) {
        return Objects.requireNonNull(getClass().getResource("/com/york/images/" + testImageName)).toString();
    }

    /**
     * Returns the name of the original file (not including file extension).
     * @return The name of the original file (not including file extension).
     */
    public String getFileName() {
        return null;
    }

    public String getFileExtension() {
        return null;
    }

    public static String[] getAcceptedFormats() {
        return new String[] {"jpg", "jpeg", "png"};
    }

    public static boolean isAcceptedFormat(String path) {
        for (String format : getAcceptedFormats()) {
            if (path.toLowerCase().endsWith("." + format)) return true;
        }
        return false;
    }

    public static int testPath(String path) {
        if (path == null) return NULL_PATH;
        if (!new File(path).exists()) return FILE_NOT_FOUND;
        if (!isAcceptedFormat(path)) return FILE_NOT_ACCEPTED;
        return SUCCESS;
    }

}
