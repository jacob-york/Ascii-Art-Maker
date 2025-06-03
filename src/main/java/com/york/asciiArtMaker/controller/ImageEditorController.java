package com.york.asciiArtMaker.controller;


import com.york.asciiArtMaker.AsciiArtMaker;
import com.york.asciiArtMaker.model.adapters.ImageFileAdapter;
import com.york.asciiArtMaker.model.adapters.ImageSource;
import com.york.asciiArtMaker.model.asciiArt.AsciiImage;
import com.york.asciiArtMaker.model.asciiArt.AsciiImageBuilder;

import java.io.File;
import java.util.Optional;

public class ImageEditorController extends AsciiEditorController {

    @Override
    public boolean newAsciiImageFromFileChosen() {
        Optional<File> maybeFile = AsciiArtMaker.getFileManager().selectImageFile();
        if (maybeFile.isEmpty()) return false;

        File selectedFile = maybeFile.get();
        setImageSource(new ImageFileAdapter(selectedFile));

        return true;
    }

    @Override
    AsciiImage getAsciiImageFrame() {
        return ((AsciiImageBuilder) asciiArtBuilder).build();
    }

    public void setImageSource(ImageSource imageSource) {
        asciiArtBuilder = new AsciiImageBuilder(imageSource)
                .setCharWidth(INIT_CHAR_WIDTH)
                .setInvertedShading(activeColorTheme.invertedShading);
        charWidthField.setText(String.valueOf(INIT_CHAR_WIDTH));
        asciiViewportPane.setNewContent(getAsciiImageFrame());
        fineZoomBase = asciiViewportPane.getContentZoom();
        zoomSlider.setValue(0);
    }
}
