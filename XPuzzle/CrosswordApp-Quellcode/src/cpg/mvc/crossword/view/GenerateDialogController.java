package cpg.mvc.crossword.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/**
 * This class is the controller for a small dialog window called
 * by the rootLayoutClass to receive the parameters for the
 * crossword construction that are the number of rows (widths)
 * and number of columns (height) for the crossword to be generated.
 *
 */
public class GenerateDialogController
{
	@FXML
	private TextField columnsField;

	@FXML
	private TextField rowsField;


	private Stage dialogStage;

	private boolean okClicked = false;

	private int numberOfColumns;

	private int numberOfRows;


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
			numberOfColumns = Integer.parseInt(columnsField.getText());
			numberOfRows = Integer.parseInt(rowsField.getText());

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
		if (columnsField.getText() ==null || columnsField.getText().length()==0)
		{
			return false;
		}
		else if (Integer.parseInt(columnsField.getText()) < 10 || Integer.parseInt(columnsField.getText()) > 20)
		{
			return false;
		}
		else if (rowsField.getText() ==null || rowsField.getText().length()==0)
		{
			return false;
		}
		else if (Integer.parseInt(rowsField.getText()) < 10 || Integer.parseInt(rowsField.getText()) > 30)
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
		String errorMessage = "Error: \n";

		if (columnsField.getText() ==null || columnsField.getText().length()==0)
		{
			errorMessage += "No valid column number \n";
		}
		else if (Integer.parseInt(columnsField.getText()) < 10 || Integer.parseInt(columnsField.getText()) > 20)
		{
			errorMessage += "The number of columns has to be between 10 and 20\n";
		}
		else if (rowsField.getText() ==null || rowsField.getText().length()==0)
		{
			errorMessage += "No valid row number \n";
		}
		else if (Integer.parseInt(rowsField.getText()) < 10 || Integer.parseInt(rowsField.getText()) > 30)
		{
			errorMessage += "The number of rows has to be between 10 and 30\n";
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

    public int getNumberOfColumns()
    {
    	return this.numberOfColumns;
    }

    public int getNumberOfRows()
    {
    	return this.numberOfRows;
    }
}
