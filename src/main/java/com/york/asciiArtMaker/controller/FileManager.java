package com.york.asciiArtMaker.controller;

import com.york.asciiArtMaker.AppUtil;
import com.york.asciiArtMaker.AsciiArtMaker;
import com.york.asciiArtMaker.models.AppModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public class FileManager {

    public static final Set<String> imageFileFormats = Set.of("png", "jpg");
    public static final Set<String> videoFileFormats = Set.of("mp4");

    private final FileChooser selectFileChooser;
    private final FileChooser saveTxtFileChooser;
    private final FileChooser saveVideoFileChooser;


    public FileManager() {
        selectFileChooser = initFileChooser("Select a File...", "Image or Video Files",
                AppUtil.union(imageFileFormats, videoFileFormats));
        saveTxtFileChooser = initFileChooser("Save As...", "Text Files", Set.of("txt"));
        saveVideoFileChooser = initFileChooser("Save As...", "Video Files", videoFileFormats);
    }

    private static FileChooser initFileChooser(String title, String desc, Collection<String> fileTypes) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(desc, fileTypes.stream()
                        .map(str -> ("*" + str))
                        .toList())
        );
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

    public boolean saveTxtFile(String content, String name) {
        File output = getSaveLoc(name, saveTxtFileChooser);
        if (output == null) return false;

        if (writeTxtFile(content)) {
            saveTxtFileChooser.setInitialDirectory(output.getParentFile());
            return true;
        } else return false;
    }

    public boolean saveVideo(String[] content, String name) {
        File output = getSaveLoc(name, saveVideoFileChooser);
        if (output == null) return false;

        if (writeMp4File(content)) {
            saveVideoFileChooser.setInitialDirectory(output.getParentFile());
            return true;
        } else return false;
    }

    public boolean saveImage(BufferedImage content, String name) {
        File output = getSaveLoc(name, saveTxtFileChooser);
        if (output == null) return false;

        if (writeJpgFile(content)) {
            saveTxtFileChooser.setInitialDirectory(output.getParentFile());
            return true;
        } else return false;
    }

    private File getSaveLoc(String name, FileChooser fileChooser) {
        fileChooser.setInitialFileName(name);
        return fileChooser.showSaveDialog(new Stage());
    }

    public static String formatFileName(AppModel model) {
        final String imageName = model.getMediaName().orElse("asciiArt");
        final String invertedMkr = model.getInvertedShading() ? "-inv" : "";
        return imageName + "-cw" + model.getCharWidth() + invertedMkr;
    }

    private boolean writeTxtFile(String content) {
        try (FileOutputStream fos = new FileOutputStream(saveTxtFileChooser.getInitialDirectory());
             OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {
            osw.write(content);

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private boolean writeMp4File(String[] content) {
        // todo:
        //  > need ordered collection of image files
        //  > write that collection to saveFileChooser.getInitialDirectory()

        return false;
    }

    private boolean writeJpgFile(BufferedImage content) {
        // todo:

        return false;
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
