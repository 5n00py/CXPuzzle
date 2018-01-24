package cpg.mvc.crossword.view;

import java.io.File;
import java.io.IOException;

import cpg.mvc.crossword.MainApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * This is the controller class for the root layout that provides the
 * menu bar inside the primaryStage. It enables to load and store word
 * data from respectively to a file and generate a new crossword.
 */
public class RootLayoutController
{
    // Reference to the main application
    private MainApp mainApp;

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp)
    {
        this.mainApp = mainApp;
    }

    /**
     * Creates an empty Word list.
     */
    @FXML
    private void handleNew()
    {
    	mainApp.getWordListController().clearWordList();
    	mainApp.setPreferedFilePath(null);
    }

    /**
     * Opens a FileChooser to let the user select an Word list to load.
     * @throws IOException
     */
    @FXML
    private void handleOpen() throws IOException
    {
        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show open file dialog and receive the file to open
        File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());

        if (file != null)
        {
        	mainApp.getWordListController().getWordList().clearList();
        	mainApp.getWordListController().loadWordListFromFile(file);
        }
    }

    /**
     * Opens a FileChooser to let the user select a Word list to load to the current.
     * @throws IOException
     */
    @FXML
    private void handleAddFromFile() throws IOException
    {
        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show open file dialog and receive the file to open
        File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());

        if (file != null)
        {
        	mainApp.getWordListController().loadWordListFromFile(file);
        }
    }

    /**
     * Saves the file to the person file that is currently open. If there is no
     * open file, the "save as" dialog is shown.
     */
    @FXML
    private void handleSave()
    {
        File file = mainApp.getPreferedFilePath();
        if (file != null)
        {
            mainApp.getWordListController().saveWordListToFile(file);
        }
        else
        {
            handleSaveAs();
        }
    }

    /**
     * Opens a FileChooser to let the user select a file to save to.
     */
    @FXML
    private void handleSaveAs()
    {
        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show save file dialog
        File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());

        if (file != null)
        {
            // Make sure it has the correct extension
            if (!file.getPath().endsWith(".txt"))
            {
                file = new File(file.getPath() + ".txt");
            }
            mainApp.getWordListController().saveWordListToFile(file);
        }
    }

    /**
     * Generate a new crossword from the selected words in the wordList of the wordListController.
     * @throws IOException
     */
    @FXML
    private void handleGenerateFromSelected() throws IOException
    {
        this.mainApp.generateFromSelected();
    }

    /**
     * Generate a more or less random crossword puzzle from all the word data stored in
     * the wordList of the wordListController.
     * @throws IOException
     */
    @FXML
    private void handleGenerateRandom() throws IOException
    {
		// Load the fxml file and create a new stage for the popup dialog:
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource("view/GenerateDialogView.fxml"));
        AnchorPane page = (AnchorPane) loader.load();

        // Create the dialog stage:
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Number of rows and columns");
        dialogStage.initModality(Modality.WINDOW_MODAL);

        Scene scene = new Scene(page);
        dialogStage.setScene(scene);

        GenerateDialogController controller = loader.getController();
        controller.setDialogStage(dialogStage);

        // Show the dialog and wait until the user closed id:
        dialogStage.showAndWait();

        int numberOfColumns = controller.getNumberOfColumns();
        int numberOfRows = controller.getNumberOfRows();

        this.mainApp.generateRandom(numberOfRows, numberOfColumns);
    }
}