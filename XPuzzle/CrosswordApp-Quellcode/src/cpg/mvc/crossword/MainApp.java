package cpg.mvc.crossword;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.prefs.Preferences;

import cpg.mvc.crossword.model.Word;
import cpg.mvc.crossword.view.CrosswordPuzzleController;
import cpg.mvc.crossword.view.RootLayoutController;
import cpg.mvc.crossword.view.WordListController;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Pair;

/**
 * This is the ENTRY POINT for the application.
 * The JavaFX application extends from the Application
 * class. Note that the start() method is abstract
 * and must be overridden.<br>
 * <br>
 * First of all, this class initializes the rootLayout with the menuBar for the word editor
 * and places it into a new scene that is placed to the primaryStage.
 * Inside the rootLayout the wordListView is placed, this is the editor
 * for the word data (keywords and clues).<br>
 * <br>
 * Primary, the rootLayout and the wordListView are shown.<br>
 * <br>
 * The methods generateFromSelected() and generateRendom() enable to generate
 * a now crossword grid an present it over the crosswordPuzzleController.
 */
public class MainApp extends Application

{
	private Stage primaryStage;
	private BorderPane rootLayout;
	private AnchorPane WordListView;

	private WordListController wordListController;
	private RootLayoutController rootLayoutController;
	private CrosswordPuzzleController crosswordPuzzleController;


	/*
	 * THIS IS THE MAIN ENTRY POINT!
	 * ----------------------------- */

	 /** This method is called on the JavaFX Application thread.
	 * It receives the primary stage as parameter, onto which the
	 * application scene can be set.
	 * Note that there is actually no need of a main() method in JavaFX.
	 */
	@Override
	public void start(Stage primaryStage) throws IOException
	{
		/*
		 * The stage is the top level container in JavaFX. It is constructed
		 * by the platform.
		 */
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Crossword Puzzle Generator");


		// Now lets initialize the root layout by loading it from the fxml file.
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
        try
        {
			rootLayout = (BorderPane) loader.load();
		}
        catch (IOException e)
        {
			e.printStackTrace();
		}

        // Give the controller access to the main app.
        rootLayoutController = loader.getController();
        rootLayoutController.setMainApp(this);

        /*
         * Create a scene containing the rootLayout, add it to the stage
         * and show it
         */
        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        primaryStage.show();

        /*
         * After the application is started, the RootLayout loads first of all
         * the WordListView that can be seen as the word list editor.
         */
        showWordListView();
	}

	/**
	 * Show the WordListView inside the RootLayout
	 */
	public void showWordListView()
	{
        // Load the WordListView from the fxml file
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("view/WordListView.fxml"));
        try
        {
			WordListView = (AnchorPane) loader.load();
		}
        catch (IOException e)
        {
			e.printStackTrace();
		}


		// Set the word list overview into the center of the root layout
        rootLayout.setCenter(WordListView);


        // Give the controller access to the main app.
        wordListController = loader.getController();
        wordListController.setMainApp(this);
	}


	/**
	 * Generate a new crossword puzzle from the words that are selected
	 * in the wordList that is in the wordListController.
	 *
	 * @param numOfRows
	 * @param numOfColumns
	 */
	public void generateFromSelected()
	{
		Map<String,String> wordDictionary = getSelectedWordsAsDictionary();

		this.crosswordPuzzleController = new CrosswordPuzzleController(wordDictionary);
		this.crosswordPuzzleController.setMainApp(this);
		this.crosswordPuzzleController.generateFromSelection();
	}

	/**
	 * Generate a random crossword puzzle from all the words
	 * that are stored in the word list of the wordListController.
	 *
	 * @param numOfRows
	 * @param numOfColumns
	 */
	public void generateRandom(int numOfRows, int numOfColumns)
	{
		Map<String,String> wordDictionary = getAllWordsAsDictionary();

		this.crosswordPuzzleController = new CrosswordPuzzleController(wordDictionary);
		this.crosswordPuzzleController.setMainApp(this);
		this.crosswordPuzzleController.generateRandom(numOfRows, numOfColumns);
	}


	/**
	 * Returns all the words in the wordData of the wordListController that
	 * are marked as checked (selected) as a dictionary.
	 *
	 * @return selected word data
	 */
	public Map<String,String> getSelectedWordsAsDictionary()
	{
		Map<String,String> wordDictionary = new HashMap<>();

		for (Word element : this.wordListController.getWordList().getWordData())
		{
			if (element.getChecked())
			{
				int numberOfClues = element.getAll().size();
				if (numberOfClues >= 1)
				{
					List<String> clueList = new ArrayList<>();

					for (Pair<StringProperty, BooleanProperty> clue : element.getAll())
					{
						if (clue.getValue().get())
						{
							clueList.add(clue.getKey().get());
						}
					}

					String clue;
					if (! clueList.isEmpty())
					{
						int randomNum = ThreadLocalRandom.current().nextInt(0, clueList.size());
						clue = clueList.get(randomNum);
					}
					else
					{
						int randomNum = ThreadLocalRandom.current().nextInt(0, numberOfClues);
						clue = element.getAll().get(randomNum).getKey().get();
					}
					String keyword = element.getKeyword();
					wordDictionary.put(keyword, clue);
				}
				else
				{
					continue;
				}
			}

		}

		return wordDictionary;
	}

	/**
	 * Returns all the words in the wordData of the wordListController that
	 * are not (!) marked as checked (selected) as a dictionary.
	 *
	 * @return unselected word data
	 */
	public Map<String,String> getUnselectedWordsAsDictionary()
	{
		Map<String,String> wordDictionary = new HashMap<>();

		for (Word element : this.wordListController.getWordList().getWordData())
		{
			if (! element.getChecked())
			{
				int numberOfClues = element.getAll().size();
				if (numberOfClues >= 1)
				{
					List<String> clueList = new ArrayList<>();

					for (Pair<StringProperty, BooleanProperty> clue : element.getAll())
					{
						if (clue.getValue().get())
						{
							clueList.add(clue.getKey().get());
						}
					}

					String clue;
					if (! clueList.isEmpty())
					{
						int randomNum = ThreadLocalRandom.current().nextInt(0, clueList.size());
						clue = clueList.get(randomNum);
					}
					else
					{
						int randomNum = ThreadLocalRandom.current().nextInt(0, numberOfClues);
						clue = element.getAll().get(randomNum).getKey().get();
					}
					String keyword = element.getKeyword();
					wordDictionary.put(keyword, clue);
				}
				else
				{
					continue;
				}
			}

		}

		return wordDictionary;
	}


	/**
	 * Returns all the words in the wordData of the wordListController
	 * as a dictionary.
	 *
	 * @return selected word data
	 */
	public Map<String,String> getAllWordsAsDictionary()
	{
		Map<String,String> wordDictionary = new HashMap<>();

		for (Word element : this.wordListController.getWordList().getWordData())
		{
			// Choose the clue randomly.
			int numberOfClues = element.getAll().size();
			if (numberOfClues >= 1)
			{
				int randomNum = ThreadLocalRandom.current().nextInt(0, numberOfClues);
				String clue = element.getAll().get(randomNum).getKey().get();
				String keyword = element.getKeyword();
				wordDictionary.put(keyword, clue);
			}
			else
			{
				continue;
			}
		}

		return wordDictionary;
	}


    public Stage getPrimaryStage()
    {
        return primaryStage;
    }


    /**
     * The getter for the wordListController.
     * @return wordListController
     */
    public WordListController getWordListController()
    {
    	return this.wordListController;
    }

    public CrosswordPuzzleController getCrosswordPuzzleController()
    {
    	return this.crosswordPuzzleController;
    }

	public void setCrosswordPuzzleController(CrosswordPuzzleController crosswordPuzzleController)
	{
		this.crosswordPuzzleController = crosswordPuzzleController;
	}



    /**
     * Returns the WordList file preference, i.e. the file that was last opened.
     * The preference is read from the OS specific registry. If no such
     * preference can be found, null is returned.
     *
     * @return file preference
     */
    public File getPreferedFilePath()
    {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
        String filePath = prefs.get("filePath", null);
        if (filePath != null)
        {
            return new File(filePath);
        }
        else
        {
            return null;
        }
    }

    /**
     * Sets the file path of the currently loaded file. The path is persisted in
     * the OS specific registry.
     *
     * @param file or null to remove the path
     */
    public void setPreferedFilePath(File file)
    {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
        if (file != null)
        {
            prefs.put("filePath", file.getPath());

            // Update the stage title.
            primaryStage.setTitle("AddressApp - " + file.getName());
        }
        else
        {
            prefs.remove("filePath");

            // Update the stage title.
            primaryStage.setTitle("AddressApp");
        }
    }




    public static void main(String[] args)
    {
        launch(args);
    }
}
