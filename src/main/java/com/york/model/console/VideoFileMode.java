package com.york.model.console;

import com.york.model.media.VideoPathAdapter;
import javafx.scene.Parent;

import java.util.Scanner;

public class VideoFileMode extends Mode {

    public VideoFileMode(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public String toString() {
        return "Video File Mode";
    }
    private VideoPathAdapter requestPath() {
        // TODO
        return null;
    }

    private void asciiVideoFromPath(VideoPathAdapter videoPathAdapter, int charWidth, boolean invertedShading) {
        // TODO
    }

    public void launch() {
        VideoPathAdapter videoPathAdapter = requestPath();
        requestCharWidth();
        requestInvertedShading();

        asciiVideoFromPath(videoPathAdapter, charWidth, invertedShading);
    }

}
