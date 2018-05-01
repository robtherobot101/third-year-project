package seng302.Controllers;

import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * Helper class that contains static methods to quickly create Alert dialogs
 */
public final class DialogWindowController
{
    private DialogWindowController() {}

    /**
     * Creates an Alert dialog with Warning theme.
     *
     * @param title Sets the title content of the dialog.
     * @param header Sets the header content of the dialog.
     * @param content Sets the content of the dialog.
     */
    public static void showWarning(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Creates an Alert dialog with Information theme.
     *
     * @param title Sets the title content of the dialog.
     * @param header Sets the header content of the dialog.
     * @param content Sets the content of the dialog.
     */
    public static void showInformation(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Opens a window to navigate to the CSV file user wants to import.
     *
     * @return The file path of the JSON to import
     */
    public static String getSelectedFilePath() {
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter fileExtensions =
                new FileChooser.ExtensionFilter(
                        "JSON Files", "*.json");

        fileChooser.getExtensionFilters().add(fileExtensions);
        try {
            File file = fileChooser.showOpenDialog(stage);
            return file.getAbsolutePath();
        } catch (NullPointerException e) {
            return null;
        }
    }
}
