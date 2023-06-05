package com.york.model.adapters;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class VideoPathAdapterTest {
    @Test
    void getWidthPixels() {
        // TODO: implement this
    }

    @Test
    void getHeightPixels() {
        // TODO: implement this
    }

    @Test
    void getFPS() {
        // TODO: implement this
    }

    @Test
    void getFrameCount() {
        // TODO: implement this
    }

    @Test
    void getImageSourceArray() {
        // TODO: implement this
    }

    @Test
    void getFileName() {
        try {
            String path = Objects.requireNonNull(getClass().getResource("/com.york.videos/Dancing horse.mp4")).toString();
            VideoPathAdapter vpa = new VideoPathAdapter(path);
            assertEquals(vpa.getFileName(), "Dancing horse");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getFileExtension() {
        try {
            String path = Objects.requireNonNull(getClass().getResource("/com.york.videos/Dancing horse.mp4")).toString();
            VideoPathAdapter vpa = new VideoPathAdapter(path);
            assertEquals(vpa.getFileExtension(), ".mp4");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getAcceptedFormats() {
        // TODO: implement this
    }

    @Test
    void isAcceptedFormat() {
        // TODO: implement this
    }

    @Test
    void testPath() {
        // TODO: implement this
    }
}
