package com.york.asciiArtMaker;

import com.york.asciiArtMaker.controller.ImageEditorController;
import com.york.asciiArtMaker.controller.ProgressDialogController;
import com.york.asciiArtMaker.controller.VideoEditorController;
import com.york.asciiArtMaker.model.FileManager;
import com.york.asciiArtMaker.model.adapters.ImageSource;
import com.york.asciiArtMaker.model.adapters.VideoFileConnectionService;
import com.york.asciiArtMaker.model.adapters.VideoSource;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class AsciiArtMaker extends Application {

    public static final String TITLE = "Welcome to Ascii Art Maker";

    private static FileManager fileManager;

    public static FileManager getFileManager() {
        if (fileManager == null) {
            fileManager = new FileManager();
        }

        return fileManager;
    }

    public static String getResourcePath(String relativePath) {
        return Objects.requireNonNull(AsciiArtMaker.class.getResource(relativePath)).toExternalForm();
    }

    public static void copyToClipboard(String string) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        clipboard.clear();
        ClipboardContent content = new ClipboardContent();
        content.putString(string);
        clipboard.setContent(content);
    }

    static {nu.pattern.OpenCV.loadLocally();}

    public static void main(String[] args) {
        launch(args);
    }


    // why do I have to write these myself, java?
    public static <T> Set<T> union(Set<T> coll1, Set<T> coll2) {
        Set<T> returnVal = new HashSet<>(coll1);
        returnVal.addAll(coll2);
        return returnVal;
    }

    public static <T> Set<T> intersection(Set<T> coll1, Set<T> coll2) {
        return coll1.stream()
                .filter(coll2::contains)
                .collect(Collectors.toSet());
    }

    public static boolean isNumeric(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static double ensureInRange(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }

    /**
     * @author Javanaut
     * (<a href="https://answers.opencv.org/question/28348/converting-bufferedimage-to-mat-in-java/">...</a>)
     */
    public static Mat matify(BufferedImage sourceImg) {

        DataBuffer dataBuffer = sourceImg.getRaster().getDataBuffer();
        byte[] imgPixels = null;
        Mat imgMat = null;

        int width = sourceImg.getWidth();
        int height = sourceImg.getHeight();

        if(dataBuffer instanceof DataBufferByte) {
            imgPixels = ((DataBufferByte)dataBuffer).getData();
        }

        if(dataBuffer instanceof DataBufferInt) {

            int byteSize = width * height;
            imgPixels = new byte[byteSize*3];

            int[] imgIntegerPixels = ((DataBufferInt)dataBuffer).getData();

            for(int p = 0; p < byteSize; p++) {
                imgPixels[p*3 + 0] = (byte) ((imgIntegerPixels[p] & 0x00FF0000) >> 16);
                imgPixels[p*3 + 1] = (byte) ((imgIntegerPixels[p] & 0x0000FF00) >> 8);
                imgPixels[p*3 + 2] = (byte) (imgIntegerPixels[p] & 0x000000FF);
            }
        }

        if(imgPixels != null) {
            imgMat = new Mat(height, width, CvType.CV_8UC3);
            imgMat.put(0, 0, imgPixels);
        }

        return imgMat;
    }

    public static void launchImageEditor(ImageSource imageSource) {
        URL icon = AsciiArtMaker.class.getResource("icons/appIcon.png");
        FXMLLoader loader = new FXMLLoader(AsciiArtMaker.class.getResource("fxml/image_editor_view.fxml"));

        Stage imageEditorStage = null;  // IOException anticipated
        try {
            imageEditorStage = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ImageEditorController imageEditorController = loader.getController();
        imageEditorController.setImageSource(imageSource);
        assert icon != null;
        imageEditorStage.getIcons().add(new Image(icon.toExternalForm()));
        imageEditorStage.show();
    }

    public static void launchVideoFileConnectionService(File selectedFile) {
        URL icon = AsciiArtMaker.class.getResource("icons/appIcon.png");
        FXMLLoader loader = new FXMLLoader(AsciiArtMaker.class.getResource("fxml/progress_dialog.fxml"));
        Stage loadDialogStage;

        try {
            loadDialogStage = loader.load();  // IOException anticipated
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assert icon != null;
        loadDialogStage.getIcons().add(new Image(icon.toExternalForm()));
        ProgressDialogController progressDialogController = loader.getController();
        progressDialogController.setText("Gathering frame data...");
        VideoFileConnectionService vfcs = new VideoFileConnectionService(selectedFile, progressDialogController);
        progressDialogController.setCancellable(vfcs);
        vfcs.addProgressMonitor(progressDialogController);
        new Thread(vfcs).start();
        loadDialogStage.show();
    }

    public static void launchVideoEditor(VideoSource videoSource) {
        URL icon = AsciiArtMaker.class.getResource("icons/appIcon.png");
        FXMLLoader loader = new FXMLLoader(AsciiArtMaker.class.getResource("fxml/video_editor_view.fxml"));

        Stage videoEditorStage = null;  // IOException anticipated
        try {
            videoEditorStage = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        VideoEditorController videoEditorController = loader.getController();
        videoEditorController.setVideoSource(videoSource);
        assert icon != null;
        videoEditorStage.getIcons().add(new Image(icon.toExternalForm()));
        videoEditorStage.show();
    }

    @Override
    public void start(Stage mainStage) {
        URL icon = AsciiArtMaker.class.getResource("icons/appIcon.png");
        FXMLLoader loader = new FXMLLoader(AsciiArtMaker.class.getResource("fxml/home.fxml"));

        try {
            Stage stage = loader.load();  // IOException anticipated

            assert icon != null;
            stage.getIcons().add(new Image(icon.toExternalForm()));
            stage.show();

        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

}
