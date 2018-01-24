package cpg.mvc.crossword.view;

import java.util.Map;

import cpg.mvc.crossword.CrosswordGenerator;
import cpg.mvc.crossword.MainApp;
import javafx.scene.layout.GridPane;

/**
 * This class takes a word dictionary with keywords (words to be guessed) and clues
 * (hints for a specific word) and gives it to the crosswordGenerator to generate
 * a 2 dimensional crossword array as crossword puzzle. After the crossword is
 * generated, the crosswordGridMakery makes a crossword GridPane out of the
 * 2 dimensional string array. To show this in a new window, the grid is set
 * into the crosswordPuzzleView.
 *
 */
public class CrosswordPuzzleController
{
	// Reference to the main application:
	private MainApp mainApp;

	String[][] crosswordArray;

	CrosswordGenerator crosswordGenerator;

	CrosswordPuzzleView crosswordPuzzleView = new CrosswordPuzzleView();


	/**
	 * The constructor.
	 * @param wordDictionary
	 * @param width
	 * @param height
	 */
	public CrosswordPuzzleController(Map<String,String> wordDictionary)
	{
		crosswordGenerator = new CrosswordGenerator(wordDictionary);
		crosswordPuzzleView.setMyController(this);
	}

	public void generateRandom(int width, int height)
	{
		crosswordGenerator.generateRandom(width, height);
		crosswordArray = crosswordGenerator.getCrosswordPuzzle();

		CrosswordGridMakery crosswordGridMakery = new CrosswordGridMakery(this.crosswordArray);
		GridPane crosswordGrid = crosswordGridMakery.getCrosswordGrid();

		crosswordPuzzleView.setCrosswordGrid(crosswordGrid);
		crosswordPuzzleView.show();
	}

	public void generateFromSelection()
	{
		crosswordGenerator.generateFromDictionary();
		crosswordArray = crosswordGenerator.getCrosswordPuzzle();

		CrosswordGridMakery crosswordGridMakery = new CrosswordGridMakery(this.crosswordArray);
		GridPane crosswordGrid = crosswordGridMakery.getCrosswordGrid();

		crosswordPuzzleView.setCrosswordGrid(crosswordGrid);
		crosswordPuzzleView.show();
	}

	public void fillWithWords(int numberOfWords)
	{
		Map<String,String> dictionary = this.mainApp.getUnselectedWordsAsDictionary();

		crosswordGenerator.fillUp(dictionary, numberOfWords);

		CrosswordGridMakery crosswordGridMakery = new CrosswordGridMakery(this.crosswordArray);
		GridPane crosswordGrid = crosswordGridMakery.getCrosswordGrid();

		crosswordPuzzleView.setCrosswordGrid(crosswordGrid);

		this.crosswordPuzzleView.getCrosswordStage().close();
		this.crosswordPuzzleView.show();

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

	public String[][] getCrosswordArray()
	{
		return this.crosswordArray;
	}
}