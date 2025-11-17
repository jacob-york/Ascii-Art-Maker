package com.york.asciiArtMaker;

import com.york.asciiArtMaker.controller.*;
import com.york.asciiArtMaker.model.FileManager;
import com.york.asciiArtMaker.model.adapters.*;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class AsciiArtMaker extends Application {

    // todo: there need to be some kind of tooltips explaining the app's keyboard shortcuts

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

    public static ImageView generateIconImageView(String name, double width, double height) {
        String path = "/com/york/asciiartmaker/icons/" + name + ".png";

        InputStream inputStream = Objects.requireNonNull(AsciiArtMaker.class.getResourceAsStream(path));
        Image image = new Image(inputStream);
        ImageView returnVal = new ImageView(image);
        returnVal.setFitWidth(width);
        returnVal.setFitHeight(height);

        return returnVal;
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

    /**
     * Tries to load an FXML with a given relative project path as a string. If there's an issue, it returns an empty
     * optional. Otherwise, it returns the FXML's associated Controller object s.t. final changes could be made by the
     * user.
     *
     * @param path a relative path to the fxml file of the resource that you'd like to open.
     * @return an empty optional if there was any issue in loading a resource, a Controller otherwise.
     * If there is any issue in loading a resource, then an alert dialog is shown to the user as a side effect.
     */
    public static Optional<Object> tryLaunchView(String path) {
        try {
            URL icon = AsciiArtMaker.class.getResource("icons/appIcon.png");
            assert icon != null;
            URL fxml = AsciiArtMaker.class.getResource(path);
            assert fxml != null;
            FXMLLoader loader = new FXMLLoader(fxml);
            Stage stage = loader.load();
            stage.getIcons().add(new Image(icon.toExternalForm()));

            stage.show();
            return Optional.of(loader.getController());
        } catch (IOException | AssertionError e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
            return Optional.empty();
        }
    }

    public static void launchHome() {
        tryLaunchView("fxml/home.fxml");
    }

    public static void launchImageEditor(ImageSource imageSource) {
        tryLaunchView("fxml/image_editor_view.fxml").ifPresent(controller ->
                ((ImageEditorController) controller).setImageSource(imageSource));
    }

    public static void launchVideoFileConnectionTask(File selectedFile, Closable closable) {
        tryLaunchView("fxml/progress_dialog.fxml").ifPresent(controller -> {
            ProgressDialogController pdc = (ProgressDialogController) controller;

            VideoFileConnectionTask vfct = new VideoFileConnectionTask(selectedFile);
            vfct.progressProperty().addListener((obs, oldValue, newValue) -> pdc.setProgress((Double) newValue));
            vfct.setOnFailed(event -> pdc.stage.close());
            vfct.setOnSucceeded(event -> {
                pdc.stage.close();
                closable.close();
                AsciiArtMaker.launchVideoEditor(vfct.getValue());
            });

            pdc.setCancellable(vfct);

            new Thread(vfct).start();
        });
    }

    public static void launchWebcamConnectionTask(Closable closable) {
        AsciiArtMaker.tryLaunchView("fxml/webcam_loading_dialog.fxml").ifPresent(controller -> {
            WebcamLoadingDialogController wldc = (WebcamLoadingDialogController) controller;

            Task<LiveSource> task = new Task<>() {
                @Override
                protected LiveSource call() {
                    return new DefaultCameraAdapter();
                }
            };
            task.setOnSucceeded(event -> {
                wldc.stage.close();
                closable.close();
                AsciiArtMaker.launchLiveEditor(task.getValue());
            });

            new Thread(task).start();
        });
    }

    public static void launchVideoEditor(VideoSource videoSource) {
        tryLaunchView("fxml/video_editor_view.fxml").ifPresent(controller ->
                ((VideoEditorController) controller).setVideoSource(videoSource));
    }

    public static void launchLiveEditor(LiveSource liveSource) {
        tryLaunchView("fxml/live_editor_view.fxml").ifPresent(controller ->
                ((LiveEditorController) controller).setLiveSource(liveSource));
    }

    @Override
    public void start(Stage mainStage) {
        launchHome();
    }

}
