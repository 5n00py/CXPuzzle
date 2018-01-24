package cpg.mvc.crossword.view;


import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cpg.mvc.crossword.MainApp;
import cpg.mvc.crossword.model.Word;
import cpg.mvc.crossword.model.WordList;


/**
 * This is the controller class for the WordListView.fxml.<br>
 * <br>
 * The view consists of two parts:<br>
 * - The keyword editor with a table containing the list of keywords, and<br>
 * - the clue editor that shows for a selected keyword the list of clues in a table.<br>
 * The buttons in the view enable to add or remove keywords and respectively clues.<br>
 * <br>
 * This class gives access to the tables and labels inside the view. It accepts
 * inputs and converts it to commands for the model.<br>
 * <br>
 * The main data model is a list of Words (WordList). The associated object is
 * called wordList here. A WordList consists mainly of a list of Words and
 * and provides several methods on that data structure, like loading Words from
 * a text file. A Word on the other side consists of a keyword (a word to be
 * guessed in the crossword puzzle) and a list of clues for the keyword.<br>
 *
 * @see WordList
 * @see Word
 *
 */
public class WordListController
{

	// Reference to the main application:
	private MainApp mainApp;


	/* ------------------------------------------------------------------
	 * This is THE MAIN DATA STRUCTURE (MODEL) managed by the controller */

	private WordList wordList = new WordList();



	/* ---------------------------------------------------------------
	 * The following variables give access to the
	 * tables, labels and fields INSIDE THE VIEW                   */

	@FXML
	private TableView<Word> keywordTable;

	@FXML
	private TableColumn<Word, String> keywordColumn;

	@FXML
	private TableColumn<Word, Boolean> checkBoxColumn;

	@FXML
	private TableView<Pair<StringProperty,BooleanProperty>> clueListTable;

	@FXML
	private TableColumn<Pair<StringProperty,BooleanProperty>, String> clueColumn;

	@FXML
	private TableColumn<Pair<StringProperty,BooleanProperty>, Boolean> clueCheckBoxColumn;


	@FXML
	private Label keywordLabel;

	@FXML
	private TextField filterField;



	/* -------------------------------------------------------------------
	 * The initial methods: */

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
	public WordListController()
	{
	}


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
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize()
    {
		/* ---------------------------------------------------------
		 * Enable the wordList to be filtered and sorted,
		 * i.e. wrap it in a filter:
		 */

        // Wrap the ObservableList in a FilteredList (initially display all data).
        FilteredList<Word> filteredData = new FilteredList<>(wordList.getWordData(), p -> true);

        // Set the filter Predicate whenever the filter changes.
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(Word -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty())
                {
                    return true;
                }

                // Compare keyword of every Word with filter text.

                String lowerCaseFilter = newValue.toLowerCase();

                if (Word.getKeyword().toLowerCase().contains(lowerCaseFilter))
                {
                    return true; // Filter matches keyword.
                }
                return false; // Does not match.
            });
        });

        // Add filtered data to the table.
        keywordTable.setItems(filteredData);

        // Initialize the keyword table with the two columns.
        checkBoxColumn.setCellValueFactory(cellData -> cellData.getValue().checkedProperty());
        checkBoxColumn.setCellFactory(param -> new CheckBoxTableCell<Word, Boolean>());

        keywordColumn.setCellValueFactory(cellData -> cellData.getValue().keywordProperty());

        // Clear Word details, i.e. as long as there is no keyword selected,
        // don't show any clue
        showClueList(null);

        // Listen for selection changes and show the clues for a specific keyword when changed
        keywordTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showClueList(newValue));


    }


    /* -------------------------------------------------------------------------------
     * Methods that handle input events from the view:                              */

    /**
     * Shows the clues list of a specific keyword when it is selected in the keyword
     * table. There is a change listener on the keyword list for that cause.
     *
     * @param Word
     */
    private void showClueList(Word word)
    {
    	if (word != null)
    	{
    		// Shows the chosen keyword as title above the table
    		keywordLabel.setText(word.getKeyword());

    		// Show the clue list in the table
    		clueListTable.setItems(word.getAll());

    		clueColumn.setCellValueFactory(cellData -> cellData.getValue().getKey());
    		clueColumn.setCellFactory(TextFieldTableCell.<Pair<StringProperty,BooleanProperty>>forTableColumn());


    		clueCheckBoxColumn.setCellValueFactory(cellData -> cellData.getValue().getValue());
            clueCheckBoxColumn.setCellFactory(param -> new CheckBoxTableCell<Pair<StringProperty,BooleanProperty>, Boolean>());
    	}
    	else
    	{
    		// Make sure there is no content in an empty clue list.
    		keywordLabel.setText("");
    		clueListTable.getItems().clear();
    	}
    }

    /**
     * Handles the delete button in the keyword editor of the view:
     * When a keyword is selected, it will be removed after delete button action.
     */
    public void handleDeleteWord()
    {
    	int selectedIndex = keywordTable.getSelectionModel().getSelectedIndex();

    	if (selectedIndex >= 0)
    	{
    		wordList.removeWord(selectedIndex);
    	}
    	else
    	{
            // Nothing selected.
            Alert alert = new Alert(AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Word Selected");
            alert.setContentText("Please select a word in the table.");
            alert.showAndWait();
    	}
    }

    /**
     * Handles the Edit Button in the view.
     * @throws IOException
     */
    public void handleEditClues() throws IOException
    {

    	int index = keywordTable.getSelectionModel().getSelectedIndex();

    	if (index < 1)
    	{
    		return;
    	}

    	Word selectedWord = wordList.getWordData().get(index);

    	List<String> clueList = new ArrayList<>();;

		for (Pair<StringProperty, BooleanProperty> clue : selectedWord.getAll())
		{
			clueList.add(clue.getKey().get());
		}


    	// Load the fxml file and create a new stage for the popup dialog:
    	FXMLLoader loader = new FXMLLoader();
    	loader.setLocation(MainApp.class.getResource("view/EditCluesDialogView.fxml"));
        AnchorPane page = (AnchorPane) loader.load();

        // Create the dialog stage:
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Edit Clues");
        dialogStage.initModality(Modality.WINDOW_MODAL);

        Scene scene = new Scene(page);
        dialogStage.setScene(scene);

        EditCluesDialogController controller = loader.getController();
        controller.setDialogStage(dialogStage);

        controller.setClueList(clueList);

        // Show the dialog and wait until the user closed id:
        dialogStage.showAndWait();

        clueList = controller.getClueList();

        wordList.getWordData().get(index).replaceClues(clueList);

    }


    /**
     * Opens a new dialog, where a new keyword with clues can be edited.
     * After the dialog stage is closed, the new data is added to the wordList.
     * @return
     * @throws IOException
     */
    public boolean showKeywordEditDialog() throws IOException
    {
    	// Load the fxml file and create a new stage for the popup dialog:
    	FXMLLoader loader = new FXMLLoader();
    	loader.setLocation(MainApp.class.getResource("view/AddKeywordDialogView.fxml"));
        AnchorPane page = (AnchorPane) loader.load();

        // Create the dialog stage:
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Add New Keyword");
        dialogStage.initModality(Modality.WINDOW_MODAL);

        Scene scene = new Scene(page);
        dialogStage.setScene(scene);

        AddKeywordDialogController controller = loader.getController();
        controller.setDialogStage(dialogStage);

        // Show the dialog and wait until the user closed id:
        dialogStage.showAndWait();

        wordList.addWord(controller.getNewWord());

        return true;
    }


    /**
     * Clear the whole wordList
     */
    public void clearWordList()
    {
    	wordList.clearList();
    	// Make sure there is no more content in the clue list as well...
    	showClueList(null);
    }



    /**
     * Load a wordList from a file.
     * @param file
     */
    public void loadWordListFromFile(File file)
    {
    	try
    	{
			wordList.loadFromFile(file);
			initialize();
		}
    	catch (IOException e)
    	{
			e.printStackTrace();
		}

    }

    /**
     * Save the wordList to a file.
     * @param file
     */
    public void saveWordListToFile(File file)
    {
    	wordList.saveToFile(file);
    }


    /**
     * The getter-method for the wordList.
     * @return
     */
    public WordList getWordList()
    {
    	return wordList;
    }
}
