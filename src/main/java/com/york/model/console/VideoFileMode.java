package com.york.model.console;

import com.york.model.asciiArt.AsciiVideo;
import com.york.model.adapters.ImagePathAdapter;
import com.york.model.adapters.VideoPathAdapter;

import java.io.IOException;
import java.util.Scanner;

public final class VideoFileMode extends Mode {

    public VideoFileMode(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public String toString() {
        return "Video File Mode";
    }

    private VideoPathAdapter requestPath() {
        while (true) {
            System.out.print("Enter the absolute path to a video file (w/ extension):\n>");
            String pathToImage = nextLine();

            VideoPathAdapter pathAdapter;
            try {
                pathAdapter = new VideoPathAdapter(pathToImage);
                if (VideoPathAdapter.testPath(pathToImage) == VideoPathAdapter.FILE_NOT_ACCEPTED) {
                    StringBuilder displayFormats = new StringBuilder();
                    for (String format : ImagePathAdapter.getAcceptedFormats()) {
                        if (displayFormats.toString().equals("")) {
                            displayFormats.append(format);
                        }
                        else displayFormats.append(", ").append(format);
                    }
                    System.out.println("Format is not accepted (Accepted formats: " + displayFormats + ").");
                }
                else return pathAdapter;
            }
            catch (IOException e) {
                System.out.println("File not found. Please try again.");
            }
        }
    }

    private void asciiVideoFromPath(VideoPathAdapter videoPathAdapter, int charWidth, boolean invertedShading) {
        // TODO: implement this
        AsciiVideo asciiVideo = new AsciiVideo(videoPathAdapter)
                .setName(videoPathAdapter.getFileName())
                .setCharWidth(charWidth)
                .setInvertedShading(invertedShading);
    }

    public void launch() {
        VideoPathAdapter videoPathAdapter = requestPath();
        requestCharWidth();
        requestInvertedShading();

        asciiVideoFromPath(videoPathAdapter, charWidth, invertedShading);
    }

}
