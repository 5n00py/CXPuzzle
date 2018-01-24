package cpg.mvc.crossword.view;

import java.io.File;
import java.io.IOException;

import cpg.mvc.crossword.MainApp;
import cpg.util.SVGMakery;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * This is the controller class for the CrosswordPuzzleMenuBar.fxml that provides the
 * menu bar inside the CrosswordPuzzleView. It enables to save the crossword as an
 * SVG file, print the crossword puzzle and fill it up with a selected number of words.
 *
 */
public class CrosswordPuzzleMenuBarController
{
	// reference to the crossword puzzle view
	private CrosswordPuzzleView crosswordPuzzleView;


    @FXML
    private void handleSaveAsSVG() throws IOException
    {
        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "TXT files (*.svg)", "*.svg");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show save file dialog
        File file = fileChooser.showSaveDialog(crosswordPuzzleView.getCrosswordStage());

        if (file != null)
        {
            // Make sure it has the correct extension
            if (!file.getPath().endsWith(".svg"))
            {
                file = new File(file.getPath() + ".svg");
            }

        	String[][] crossword = crosswordPuzzleView.getMyController().getCrosswordArray();

        	SVGMakery svgMakery = new SVGMakery(crossword);

        	svgMakery.writeToFile(file);
        }
    }

    @FXML
    private void handlePrintCrossword()
    {
    	String[][] crosswordArray = crosswordPuzzleView.getMyController().getCrosswordArray();

    	int height = crosswordArray.length;
    	int width = crosswordArray[0].length;

		for (int i=0; i<height; i+=13)
		{
			for (int j=0; j<width; j+=9)
			{
				String[][] subarray = get9x13Subarray(crosswordArray,i,j);

				CrosswordGridMakery cgm = new CrosswordGridMakery(subarray);
				GridPane gridPane = cgm.getCrosswordGrid();

				 PrinterJob job = PrinterJob.createPrinterJob();

				 if (job != null) {
				    boolean success = job.printPage(gridPane);
				    if (success) {
				        job.endJob();
				    }
				 }

			}
		}
    }

    @FXML
    private void handleFillUp() throws IOException
    {
		// Load the fxml file and create a new stage for the popup dialog:
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource("view/FillUpDialogView.fxml"));
        AnchorPane page = (AnchorPane) loader.load();

        // Create the dialog stage:
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Fill Up");
        dialogStage.initModality(Modality.WINDOW_MODAL);

        Scene scene = new Scene(page);
        dialogStage.setScene(scene);

        FillUpDialogController controller = loader.getController();
        controller.setDialogStage(dialogStage);

        // Show the dialog and wait until the user closed id:
        dialogStage.showAndWait();

        int numberOfWords = controller.getWordNumber();

        this.crosswordPuzzleView.getMyController().fillWithWords(numberOfWords);

    }

    // In order to print the crossword Array on A4 pages, it has to be split up
    // into 9x13 subarrays.
	private static String[][] get9x13Subarray(String[][] array, int startX, int startY)
	{
		int height = array.length;
		int width = array[0].length;

		int stopX = startX + 13;
		int stopY = startY + 9;

		if (stopX > height)
		{
			stopX = height;
		}

		if (stopY > width)
		{
			stopY = width;
		}

		String[][] subarray = new String[stopX-startX][stopY-startY];

		for (int i=0; i<stopX-startX; i++)
		{
			for (int j=0;j<stopY-startY;j++)
			{
				subarray[i][j] = array[startX+i][startY+j];
			}
		}

		return subarray;
	}


    public void setCrosswordPuzzleView(CrosswordPuzzleView cpv)
    {
    	this.crosswordPuzzleView = cpv;
    }
}
