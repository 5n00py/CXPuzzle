package cpg.mvc.crossword.view;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 * This class opens a dialog window, that enables to set
 * the number of words to fill up an existing crossword puzzle.
 * This is a helper class inside the CrosswordPuzzleMenuBarController.
 *
 * @see CrosswordPuzzleMenuBarController.
 *
 */
public class FillUpDialogController
{
	@FXML
	private TextArea numberArea;


	private Stage dialogStage;

	private int wordNumber;


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
		wordNumber = Integer.parseInt(numberArea.getText());
        dialogStage.close();
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

    public int getWordNumber()
    {

    	return this.wordNumber;
    }
}
