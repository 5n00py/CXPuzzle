package cpg.mvc.crossword.view;

import cpg.mvc.crossword.model.Word;
import cpg.util.FileHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * This class opens a new dialog window to add a new keyword to
 * the wordList inside the wordListController. The entry in the
 * windows consists of at least a new keyword with a clue and
 * optional up to three more clues.
 *
 */
public class AddKeywordDialogController
{
	@FXML
	private TextField keywordField;

	@FXML
	private TextField mainClueField;

	@FXML
	private TextField optClueField1;

	@FXML
	private TextField optClueField2;

	@FXML
	private TextField optClueField3;


	private Stage dialogStage;

	private Word newWord;

	private boolean okClicked = false;


	@FXML
	private void initialize()
	{

	}


	// Returns true if the user klicked ok, else false
	public boolean isOkClicked()
	{
		return okClicked;
	}


	// Called when the user clicked ok
	@FXML
	private void handleOk()
	{
		if (isValidInput())
		{
			String keyword = keywordField.getText();

			// Provides a method to convert the keyword string into an appropriate format.
			FileHandler loader = new FileHandler();
			keyword = loader.normalizeKeyword(keyword);

			this.newWord = new Word(keyword, mainClueField.getText());

			if (optClueField1.getText().length() > 0)
			{
				newWord.addClue(optClueField1.getText());
			}

			if (optClueField2.getText().length() > 0)
			{
				newWord.addClue(optClueField2.getText());
			}

			if (optClueField3.getText().length() > 0)
			{
				newWord.addClue(optClueField3.getText());
			}

			okClicked = true;
			dialogStage.close();
		}
		else
		{
			showErrorMessage();
		}
	}

	private boolean isValidInput()
	{
		// Provides a method to check if the keyword has a correct format.
		FileHandler loader = new FileHandler();

		if (keywordField.getText() == null || keywordField.getText().length() == 0)
		{
			return false;
		}
		else if (mainClueField.getText() == null || mainClueField.getText().length() == 0)
		{
			return false;
		}
		else if (! loader.isCorrectKeyword(keywordField.getText()))
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	private void showErrorMessage()
	{
		// Provides a method to check if the keyword has a correct format.
		FileHandler loader = new FileHandler();

		String errorMessage = "Error: \n";

		if (keywordField.getText() == null || keywordField.getText().length() == 0)
		{
			errorMessage += "No valid keyword!\n";
		}

		if (mainClueField.getText() == null || mainClueField.getText().length() == 0)
		{
			errorMessage += "No valid clue!\n";
		}

		if (! loader.isCorrectKeyword(keywordField.getText()))
		{
			errorMessage += "No valid keyword format!\n";
		}

		// Show the error message.
		Alert alert = new Alert(AlertType.ERROR);
		alert.initOwner(dialogStage);
		alert.setTitle("Invalid Fields");
		alert.setHeaderText("Please correct invalid fields");
		alert.setContentText(errorMessage);

		alert.showAndWait();
	}

	// Called when the user clicks cancel.
    @FXML
    private void handleCancel()
    {
        dialogStage.close();
    }

    // Sets the dialog controller:
    public void setDialogStage(Stage dialogStage)
    {
        this.dialogStage = dialogStage;
    }

    public Word getNewWord()
    {
    	return this.newWord;
    }

}