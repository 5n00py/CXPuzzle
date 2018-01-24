package cpg.mvc.crossword.view;

import java.io.IOException;

import cpg.mvc.crossword.MainApp;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Shows the crossword puzzle as GridPane in a new window.
 *
 *@see CrossworPuzzleController
 */
public class CrosswordPuzzleView
{
	private Stage crosswordStage;

	private GridPane crosswordGrid;

	private BorderPane crosswordPuzzleMenuBar;
	private CrosswordPuzzleMenuBarController crosswordPuzzleMenuBarController;

	private CrosswordPuzzleController myController;


	/**
	 * The constructor.
	 */
	CrosswordPuzzleView()
	{

	}


	/**
	 * Show the crossword puzzle in a new window.
	 */
	public void show()
	{
		GridPane mainGrid = new GridPane();
		mainGrid.setHgap(10);
		mainGrid.setVgap(10);

		mainGrid.add(this.crosswordGrid, 0, 1);

		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setContent(mainGrid);


        // Create the crossword stage:
        crosswordStage = new Stage();
        crosswordStage.setTitle("Crossword Puzzle");
        crosswordStage.initModality(Modality.WINDOW_MODAL);


		// Now lets initialize the menu bar by loading it from the fxml file.
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("view/CrosswordPuzzleMenuBarView.fxml"));

        try
        {
			crosswordPuzzleMenuBar = (BorderPane) loader.load();
		}
        catch (IOException e)
        {
			e.printStackTrace();
		}

        // Give the controller access to this view.
        crosswordPuzzleMenuBarController = loader.getController();
        crosswordPuzzleMenuBarController.setCrosswordPuzzleView(this);

        /*
         * Create a scene containing the menu bar, add it to the stage
         * and show it
         */
        Scene scene = new Scene(crosswordPuzzleMenuBar);
        crosswordStage.setScene(scene);
        crosswordStage.show();

        crosswordPuzzleMenuBar.setCenter(scrollPane);
	}





	/* ************************
	 * The Getters and Setters
	 * *************************/

	public GridPane getCrosswordGrid()
	{
		return this.crosswordGrid;
	}

	public void setCrosswordGrid(GridPane crosswordGrid)
	{
		this.crosswordGrid = crosswordGrid;
	}

	public void setMyController(CrosswordPuzzleController controller)
	{
	    this.myController = controller;
	}

	public CrosswordPuzzleController getMyController()
	{
		return this.myController;
	}

	public Stage getCrosswordStage()
	{
		return this.crosswordStage;
	}
}
