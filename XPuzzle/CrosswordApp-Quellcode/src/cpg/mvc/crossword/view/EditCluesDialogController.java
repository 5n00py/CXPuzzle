package cpg.mvc.crossword.view;

import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 * This class opens a dialog window, that enables to edit
 * the clues for a specific keyword in a text area.
 * Every clue is represented by a new line in the text area.
 *
 */
public class EditCluesDialogController
{
	@FXML
	private TextArea textArea;


	private Stage dialogStage;

	private List<String> clueList = new ArrayList<>();

	private boolean okClicked = false;


	@FXML
	private void initialize()
	{

	}

	private void initTextArea()
	{
		for (String clue: clueList)
		{
			textArea.appendText(clue + "\n");
		}
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
		clueList.clear();
		for (String line : textArea.getText().split("\\n"))
		{
			clueList.add(line);
		}

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

    public List<String> getClueList()
    {

    	return this.clueList;
    }

    public void setClueList(List<String> clueList)
    {
    	this.clueList = clueList;
    	initTextArea();
    }

}