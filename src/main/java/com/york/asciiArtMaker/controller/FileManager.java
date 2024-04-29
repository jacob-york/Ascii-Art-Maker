package com.york.asciiArtMaker.controller;

import com.york.asciiArtMaker.AppUtil;
import com.york.asciiArtMaker.AsciiArtMaker;
import com.york.asciiArtMaker.asciiArt.AsciiImage;
import com.york.asciiArtMaker.asciiArt.AsciiVideo;
import com.york.asciiArtMaker.models.AppModel;
import com.york.asciiArtMaker.models.VideoModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FileManager {

    public static final Set<String> imageFileFormats = Set.of("png", "jpg");
    public static final Set<String> videoFileFormats = Set.of("mp4");

    private final FileChooser selectFileChooser;
    private final FileChooser txtFileSaver;
    private final FileChooser imageFileSaver;
    private final FileChooser videoFileSaver;

    public FileManager() {
        selectFileChooser = initFileChooser("Select a File...", AppUtil.union(imageFileFormats, videoFileFormats));
        txtFileSaver = initFileChooser("Save As...", Set.of("txt"));
        imageFileSaver = initFileChooser("Save As...",  Set.of("jpg"));
        videoFileSaver = initFileChooser("Save As...", videoFileFormats);
    }

    private static FileChooser initFileChooser(String title, Collection<String> fileTypes) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);

        fileTypes.stream()
                .map(str -> new FileChooser.ExtensionFilter(str.toUpperCase(), "*" + str))
                .forEach(fileChooser.getExtensionFilters()::add);

        return fileChooser;
    }

    public static String readFileExtension(File file) {
        String absPath = file.getAbsolutePath();
        int readFromInd = absPath.lastIndexOf('.');
        return absPath.substring(readFromInd+1).toLowerCase();
    }

    public static boolean isVideoFile(File file) {
        return videoFileFormats.contains(readFileExtension(file).toLowerCase());
    }

    public static boolean isImageFile(File file) {
        return imageFileFormats.contains(readFileExtension(file).toLowerCase());
    }

    /**
     * uses a FileChooser to select a file.
     * @return the user-selected file (null if cancelled).
     */
    public File selectFile() {
        File selected = selectFileChooser.showOpenDialog(new Stage());
        if (selected != null) {
            selectFileChooser.setInitialDirectory(selected.getParentFile());
        }

        return selected;
    }

    public boolean saveTxtFile(AsciiImage asciiImage) {
        File output = getSaveLoc(asciiImage.getFileName(), txtFileSaver);
        if (output == null) return false;

        if (writeTxtFile(asciiImage.toStr(), output)) {
            txtFileSaver.setInitialDirectory(output.getParentFile());
            return true;
        } else return false;
    }

    public boolean saveVideo(AsciiVideo video, ImageRenderer imageRenderer) {
        File output = getSaveLoc(video.getFileName(), videoFileSaver);
        if (output == null) return false;

        List<BufferedImage> images = Arrays.stream(video.frames())
                .map(str -> imageRenderer.render(str, BufferedImage.TYPE_3BYTE_BGR))
                .toList();

        if (writeMp4File(images, video.fps(), output)) {
            videoFileSaver.setInitialDirectory(output.getParentFile());
            return true;
        } else return false;
    }

    public boolean saveImage(AsciiImage asciiImage, ImageRenderer imageRenderer) {
        File output = getSaveLoc(asciiImage.getFileName(), imageFileSaver);
        if (output == null) return false;

        BufferedImage image = imageRenderer.render(asciiImage.toStr(), BufferedImage.TYPE_3BYTE_BGR);

        if (writeImageFile(image, output)) {
            imageFileSaver.setInitialDirectory(output.getParentFile());
            return true;
        } else return false;
    }

    private File getSaveLoc(String name, FileChooser fileChooser) {
        fileChooser.setInitialFileName(name);
        File selected = fileChooser.showSaveDialog(new Stage());
        if (selected == null) return null;

        String firstExtensionElem = fileChooser.getSelectedExtensionFilter().getExtensions().get(0);
        String extension = firstExtensionElem.substring(firstExtensionElem.lastIndexOf('*')+1);
        return new File(selected + "." + extension);
    }

    private boolean writeTxtFile(String content, File output) {
        try (FileOutputStream fos = new FileOutputStream(output);
             OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {
            String returnVal = String.join("\n", content);
            osw.write(returnVal);

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private boolean writeMp4File(List<BufferedImage> images, double fps, File output) {
        // todo:
        //  > need ordered collection of image files
        //  > write that collection to saveTxtFileChooser.getInitialDirectory()

        return false;
    }

    private boolean writeImageFile(BufferedImage image, File output) {
        try {
            ImageIO.write(image, readFileExtension(output), output);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public Optional<Parent> safeLoadFXML(String location) {
        FXMLLoader loader = new FXMLLoader(AsciiArtMaker.class.getResource("fxml/" + location));
        Parent root;
        try {
            root = loader.load();
            return Optional.of(root);
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}
