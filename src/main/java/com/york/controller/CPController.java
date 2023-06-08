package com.york.controller;

import com.york.model.adapters.BufferedImageAdapter;
import com.york.model.adapters.ImageFileAdapter;
import com.york.model.adapters.ImageSource;
import com.york.model.asciiArt.AsciiImage;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CPController {

    @FXML
    Spinner<Integer> charWidthSpinner;

    @FXML
    RadioButton invertShadingBtn;

    @FXML
    TextField paletteField;

    @FXML
    Text currentFileText;

    private ImageSource imageSource;

    @FXML
    void initialize() {
        SpinnerValueFactory<Integer> spinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1);
        spinnerValueFactory.setValue(1);
        charWidthSpinner.setValueFactory(spinnerValueFactory);
    }

    private String writeImageToOutput(AsciiImage asciiImage, Path writeTo) throws IOException {
        String art = asciiImage.toString();

        String outputPath = writeTo + "\\" + imageSource.getName() + "-cw" + asciiImage.getCharWidth();
        if (asciiImage.shadingIsInverted()) {
            outputPath += "-inv";
        }
        outputPath += ".txt";

        File file = new File(outputPath);
        FileOutputStream fos = new FileOutputStream(file);
        OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
        osw.write(art);
        osw.close();
        fos.close();

        return outputPath;
    }

    @FXML
    public void chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an image");
        File selected = fileChooser.showOpenDialog(new Stage());

        if (selected == null) return;

        try {
            imageSource = new ImageFileAdapter(selected);
        }
        catch (IOException e) {
            currentFileText.setText("Invalid file");
            return;
        }
        catch (IllegalArgumentException e) {
            currentFileText.setText("Invalid file format");
            return;
        }

        currentFileText.setText(selected.getName());

        boolean ge2 = imageSource.getHeight() / imageSource.getWidth() >= 2;
        int maxCW = ge2 ? imageSource.getWidth() : imageSource.getHeight() / 2;

        ((SpinnerValueFactory.IntegerSpinnerValueFactory) charWidthSpinner.getValueFactory()).setMax(maxCW);
    }

    @FXML
    public void download() {
        if (imageSource == null) {
            new Alert(Alert.AlertType.ERROR, "Please select an image.").showAndWait();
            return;
        }

        Path downloads = Paths.get("C:\\Users\\" + System.getProperty("user.name") + "\\Downloads"
        );
        AsciiImage asciiImage = new AsciiImage(imageSource)
                .setCharWidth(charWidthSpinner.getValue())
                .setPalette(paletteField.getText())
                .setInvertedShading(invertShadingBtn.selectedProperty().get());
        // TEMP
        try {
            writeImageToOutput(asciiImage, downloads);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void render() {
        if (imageSource == null) {
            new Alert(Alert.AlertType.ERROR, "Please select an image.").showAndWait();
            return;
        }

        Stage asciiStage = new Stage();
        asciiStage.setScene(new Scene(new Pane()));
        asciiStage.show();
   }
}
