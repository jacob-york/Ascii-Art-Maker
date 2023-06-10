package com.york.controller;

import com.york.App;
import com.york.model.Settings;
import com.york.model.adapters.ImageFileAdapter;
import com.york.model.adapters.ImageSource;
import com.york.model.asciiArt.AsciiArt;
import com.york.model.asciiArt.AsciiImage;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class MainController {

    @FXML
    public Spinner<Integer> charWidthSpinner;

    @FXML
    public ToggleButton invertShadingBtn;

    @FXML
    public Button charWidthBtn;

    private Text asciiText;

    @FXML
    public BorderPane body;

    @FXML
    public ScrollPane scrollPane;

    @FXML
    public VBox controls;

    @FXML
    public Button zoomOutBtn;

    @FXML
    public Button zoomInBtn;

    @FXML
    public Button copyBtn;

    private AsciiImage asciiImage;

    private ImageSource imageSource;

    @FXML
    public void initialize() {
        SpinnerValueFactory<Integer> spinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1);
        spinnerValueFactory.setValue(1);
        charWidthSpinner.setValueFactory(spinnerValueFactory);
        asciiText = new Text();

        asciiText.setFont(new Font(AsciiArt.FONT, 8));
        scrollPane.setContent(asciiText);
        scrollPane.setStyle("-fx-font-size: 30px;");
    }


    public static String calculateName(AsciiImage asciiImage) {
        String name = (asciiImage.getName() == null ? "asciiImage" : asciiImage.getName());
        String outputPath = name + "-cw" + asciiImage.getCharWidth();
        if (asciiImage.shadingIsInverted()) {
            outputPath += "-inv";
        }
        return outputPath;
    }

    @FXML
    public void chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(Settings.getInstance().getCurChooseDir());
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg")
        );
        fileChooser.setTitle("Select an image");
        File selected = fileChooser.showOpenDialog(new Stage());

        if (selected == null) return;
        try {
            imageSource = new ImageFileAdapter(selected);
            charWidthSpinner.setDisable(false);
            charWidthBtn.setDisable(false);
            invertShadingBtn.setDisable(false);
            charWidthSpinner.getValueFactory().setValue(1);
            Settings.getInstance().setCurChooseDir(new File(selected.getParent()));
        }
        catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "UnexpectedError: " + e).showAndWait();
            return;
        }

        boolean ge2 = imageSource.getHeight() / imageSource.getWidth() >= 2;
        int maxCharWidth = ge2 ? imageSource.getWidth() : imageSource.getHeight() / 2;

        // whoever implemented the Spinner can fuck off
        ((SpinnerValueFactory.IntegerSpinnerValueFactory) charWidthSpinner.getValueFactory()).setMax(maxCharWidth);

        asciiImage = new AsciiImage(imageSource);
        asciiText.setText(asciiImage.toString());
        ((Stage) body.getScene().getWindow())
                .setTitle(asciiImage.getName() + " - " + App.TITLE);
    }

    @FXML
    public void zoomIn() {
       double fontSize = asciiText.getFont().getSize();
        fontSize += 1;
       asciiText.setFont(new Font(AsciiArt.FONT, fontSize));
    }

    @FXML
    public void zoomOut() {
        double fontSize = asciiText.getFont().getSize();
        if (fontSize - 1 < 1) return;
        fontSize -= 1;
        asciiText.setFont(new Font(AsciiArt.FONT, fontSize));
    }

    @FXML
    public void copyToClipboard() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        clipboard.clear();

        ClipboardContent content = new ClipboardContent();
        content.putString(asciiText.getText());
        clipboard.setContent(content);
        new Alert(Alert.AlertType.CONFIRMATION, "Copied to clipboard.").showAndWait();
    }

    @FXML
    public void saveAsTxt() throws IOException {
        boolean emptyTxt = imageSource == null || asciiImage == null;

        FileChooser save = new FileChooser();
        save.setTitle("Save As...");
        save.setInitialDirectory(Settings.getInstance().getCurSaveDir());
        save.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        if (emptyTxt) save.setInitialFileName("empty-file");
        else save.setInitialFileName(calculateName(asciiImage));
        File output = save.showSaveDialog(new Stage());

        if (output == null) return;

        Settings.getInstance().setCurSaveDir(new File(output.getParent()));
        FileOutputStream fos = new FileOutputStream(output);
        OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
        if (emptyTxt) osw.write("");
        else osw.write(asciiImage.toString());

        osw.close();
        fos.close();
    }

    @FXML
    public void setCharWidth() {
        asciiImage.setCharWidth(charWidthSpinner.getValue());
        asciiText.setText(asciiImage.toString());
    }

    @FXML
    public void setInvertedShading() {
        asciiImage.setInvertedShading(invertShadingBtn.isSelected());
        asciiText.setText(asciiImage.toString());
    }
}
