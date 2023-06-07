package com.york.controller;

import com.york.model.adapters.BufferedImageAdapter;
import com.york.model.adapters.ImageSource;
import com.york.model.asciiArt.AsciiImage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
    TextField charWidthField;

    @FXML
    RadioButton invertShadingBtn;

    @FXML
    TextField paletteField;

    @FXML
    Button chooseFileBtn;

    @FXML
    Text currentFileText;

    @FXML
    Button downloadBtn;

    @FXML
    Button renderBtn;

    ImageSource imageSource;

    @FXML
    void initialize() {
        imageSource = null;
    }

    public boolean formatIsAccepted(String fileName, String[] validExtensions) {
        for (String format : validExtensions) {
            if (format.equals(fileName.substring(fileName.lastIndexOf('.') + 1))) return true;
        }
        return false;
    }

    public static String writeImageToOutput(String name, AsciiImage asciiImage, Path writeTo) throws IOException {
        String art = asciiImage.toString();

        String outputPath = writeTo + "\\" + name + "-cw" + asciiImage.getCharWidth();
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
    public void chooseFile(ActionEvent ae) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an image");
        File selected = fileChooser.showOpenDialog(new Stage());
        if (selected == null) return;
        try {
            BufferedImage bufferedImage = ImageIO.read(selected);
            currentFileText.setText(selected.getName());
            imageSource = new BufferedImageAdapter(bufferedImage);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    public void download(ActionEvent ae) {
        if (imageSource == null) return;

        Path downloads = Paths.get("C:\\Users\\" + System.getProperty("user.name") + "\\Downloads"
        );
        AsciiImage asciiImage = new AsciiImage(imageSource)
                .setCharWidth(Integer.parseInt(charWidthField.getText()))
                .setPalette(paletteField.getText())
                .setInvertedShading(invertShadingBtn.selectedProperty().get());
        // TEMP
        try {
            writeImageToOutput("asciiImage", asciiImage, downloads);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void render(ActionEvent ae) {
        if (imageSource == null) return;

        AsciiImage asciiImage = new AsciiImage(imageSource)
                .setCharWidth(Integer.parseInt(charWidthField.getText()))
                .setPalette(paletteField.getText())
                .setInvertedShading(invertShadingBtn.selectedProperty().get());
    }
}
