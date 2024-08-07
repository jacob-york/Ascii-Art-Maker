package com.york.asciiArtMaker.controller;

import com.york.asciiArtMaker.AppUtil;
import com.york.asciiArtMaker.AsciiArtMaker;
import com.york.asciiArtMaker.model.asciiArt.AsciiImage;
import com.york.asciiArtMaker.model.asciiArt.AsciiVideo;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.videoio.VideoWriter;

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
        selectFileChooser = new FileChooser();
        selectFileChooser.setTitle("Select a File...");
        selectFileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image or Video Files.",
                AppUtil.union(imageFileFormats, videoFileFormats).stream().map(str -> "*" + str).toList()));

        txtFileSaver = initFileChooser("Save As...", Set.of("txt"));
        imageFileSaver = initFileChooser("Save As...",  Set.of("jpg"));
        videoFileSaver = initFileChooser("Save As...", Set.of("mp4"));
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
    public Optional<File> selectFile() {
        File selected = selectFileChooser.showOpenDialog(new Stage());
        if (selected == null) return Optional.empty();

        selectFileChooser.setInitialDirectory(selected.getParentFile());
        return Optional.of(selected);
    }

    public boolean saveTxtFile(AsciiImage asciiImage) throws IOException {
        File output = getSaveLoc(asciiImage.getFileName(), txtFileSaver);
        if (output == null) return false;

        if (!writeTxtFile(asciiImage.toStr(), output)) return false;
        txtFileSaver.setInitialDirectory(output.getParentFile());
        return true;
    }

    public boolean saveVideo(AsciiVideo video, ImageRenderer imageRenderer) throws OutOfMemoryError {
        File output = getSaveLoc(video.getFileName(imageRenderer.getBgColor(), imageRenderer.getTextColor()),
                videoFileSaver);
        if (output == null) return false;

        // OutOfMemoryError expected
        List<BufferedImage> images = Arrays.stream(video.frames())
                    .map(imageRenderer::render)
                    .toList();

        writeMp4File(images, video.fps(), output);
        videoFileSaver.setInitialDirectory(output.getParentFile());
        return true;
    }

    public boolean saveImage(AsciiImage asciiImage, ImageRenderer imageRenderer) throws OutOfMemoryError, IOException {
        File output = getSaveLoc(asciiImage.getFileName(imageRenderer.getBgColor(), imageRenderer.getTextColor()),
                imageFileSaver);
        if (output == null) return false;

        ImageIO.write(imageRenderer.render(asciiImage), readFileExtension(output), output);
        imageFileSaver.setInitialDirectory(output.getParentFile());
        return true;
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

    private void writeMp4File(List<BufferedImage> images, double fps, File output) {
        int fourcc = VideoWriter.fourcc('m', 'p', '4', 'v');
        List<Mat> mats = images.stream().map(AppUtil::matify).toList();
        Size frameSize = new Size(mats.get(0).width(), mats.get(0).height());
        VideoWriter writer = new VideoWriter();
        writer.open(output.toString(), fourcc, fps, frameSize, true);

        for (Mat img : mats) {
            writer.write(img);
        }

        writer.release();
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
