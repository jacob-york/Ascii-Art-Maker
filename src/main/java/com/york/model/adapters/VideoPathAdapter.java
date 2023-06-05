package com.york.model.adapters;

import java.io.File;
import java.io.IOException;

// TODO: kinda bad

public final class VideoPathAdapter implements VideoSource {

    public static final int NULL_PATH = 3;

    public static final int FILE_NOT_FOUND = 2;

    public static final int FILE_NOT_ACCEPTED = 1;

    public static final int SUCCESS = 0;

    private final String path;

    public VideoPathAdapter(String path) throws IOException {
        this.path = path;
    }

    @Override
    public int getWidthPixels() {
        // TODO: implement this
        return 0;
    }

    @Override
    public int getHeightPixels() {
        // TODO: implement this
        return 0;
    }

    @Override
    public int getFPS() {
        // TODO: implement this
        return 0;
    }

    @Override
    public int getFrameCount() {
        // TODO: implement this
        return 0;
    }

    @Override
    public ImageSource[] getImageSourceArray() {
        // TODO: implement this
        return null;
    }

    /**
     * Returns the name of the original file (not including file extension).
     * @return The name of the original file (not including file extension).
     */
    public String getFileName() {
        return path.substring(path.lastIndexOf('\\') + 1, path.lastIndexOf('.'));
    }

    public String getFileExtension() {
        if (path == null) return null;

        int fileExtensionStart = path.lastIndexOf('.') + 1;
        return path.substring(fileExtensionStart);
    }

    public static String[] getAcceptedFormats() {
        return new String[] {"mp4"};
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
