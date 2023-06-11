package com.york.controller;

import com.york.App;
import com.york.model.Settings;
import com.york.model.adapters.ImageFileAdapter;
import com.york.model.adapters.ImageSource;
import com.york.model.asciiArt.AsciiArt;
import com.york.model.asciiArt.AsciiImage;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;


// TODO: file size is getting hard to maintain

public class MainController {

    @FXML
    public BorderPane body;
    @FXML
    public ToolBar toolBar;
    @FXML
    public ScrollPane scrollPane;

    private Text asciiText;

    // charWidth
    @FXML
    public Button charWidthUpBtn;
    @FXML
    public TextField charWidthField;
    @FXML
    public Button charWidthDownBtn;

    // zoom
    @FXML
    public HBox zoomBox;
    @FXML
    public Button zoomOutBtn;
    @FXML
    public TextField fontField;
    @FXML
    public Button zoomInBtn;


    // invertedShading
    @FXML
    public RadioButton invertedShadingBtn;

    // save/copy controls
    @FXML
    public HBox saveCopyBox;
    @FXML
    public Button copyBtn;
    @FXML
    public Button saveBtn;


    // color pickers
    @FXML
    public ColorPicker bgColorPicker;
    @FXML
    public ColorPicker textColorPicker;

    private AsciiImage asciiImage;
    private ImageSource imageSource;



    private static final double INIT_ZOOM = 8.0;

    @FXML
    public void initialize() {
        asciiText = new Text();
        scrollPane.setContent(asciiText);
        scrollPane.setStyle("-fx-font-size: 30px;");
    }

    private static String calculateName(AsciiImage asciiImage) {
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
        }
        catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "UnexpectedError: " + e).showAndWait();
            return;
        }
        asciiImage = new AsciiImage(imageSource);

        toolBar.setDisable(false);
        fontField.setText(String.valueOf(INIT_ZOOM));
        charWidthField.setText(String.valueOf(asciiImage.getCharWidth()));
        setAsciiTextFont(INIT_ZOOM);
        Settings.getInstance().setCurChooseDir(new File(selected.getParent()));
        asciiText.setText(asciiImage.toString());
        ((Stage) body.getScene().getWindow())
                .setTitle(asciiImage.getName() + " - " + App.TITLE);
    }

    private void setAsciiTextFont(double newFont) {
        asciiText.setFont(new Font(AsciiArt.FONT, newFont));
    }

    @FXML
    public void zoomIn() {
       double fontSize = Double.parseDouble(fontField.getText());

       fontSize++;
       setAsciiTextFont(fontSize);

       fontField.setText(String.valueOf(fontSize));
    }

    @FXML
    public void zoomOut() {
        double fontSize = Double.parseDouble(fontField.getText());

        if (fontSize - 1 < 1) return;
        fontSize--;
        setAsciiTextFont(fontSize);

        fontField.setText(String.valueOf(fontSize));
    }

    @FXML
    public void setFontFromField(KeyEvent keyEvent) {
        if (!keyEvent.getCode().equals(KeyCode.ENTER)) return;

        double newFontSize;
        try {
            newFontSize = Double.parseDouble(fontField.getText());
        }
        catch (NumberFormatException e) {
            return;
        }
        if (newFontSize < 1) return;

        fontField.setText(new DecimalFormat("#.0").format(newFontSize));
        setAsciiTextFont(newFontSize);
    }

    @FXML
    public void lowerCharWidth() {
        assert asciiImage != null;

        int newCharWidth = asciiImage.getCharWidth();
        if (newCharWidth - 1 <= 0) return;
        newCharWidth--;

        asciiImage.setCharWidth(newCharWidth);
        charWidthField.setText(String.valueOf(newCharWidth));
        asciiText.setText(asciiImage.toString());
    }

    @FXML
    public void raiseCharWidth() {
        assert asciiImage != null;

        int newCharWidth = asciiImage.getCharWidth();
        newCharWidth++;

        asciiImage.setCharWidth(newCharWidth);
        charWidthField.setText(String.valueOf(newCharWidth));
        asciiText.setText(asciiImage.toString());
    }

    @FXML
    public void charWidthFromField(KeyEvent keyEvent) {
        if (!keyEvent.getCode().equals(KeyCode.ENTER)) return;
        assert asciiImage != null;

        int newCharWidth;
        try {
            newCharWidth = Integer.parseInt(charWidthField.getText());
        }
        catch (NumberFormatException e) {
            return;
        }
        if (newCharWidth <= 0) return;

        asciiImage.setCharWidth(newCharWidth);
        asciiText.setText(asciiImage.toString());
    }

    @FXML
    public void copyToClipboard() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        clipboard.clear();

        ClipboardContent content = new ClipboardContent();
        content.putString(asciiText.getText());
        clipboard.setContent(content);
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
    public void setInvertedShading() {
        asciiImage.setInvertedShading(invertedShadingBtn.isSelected());
        asciiText.setText(asciiImage.toString());
    }

    @FXML
    public void stopRequest() {
        Platform.exit();
    }

}
