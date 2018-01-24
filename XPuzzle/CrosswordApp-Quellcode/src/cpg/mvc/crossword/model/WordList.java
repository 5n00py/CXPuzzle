package cpg.mvc.crossword.model;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.List;

import cpg.util.FileHandler;
import cpg.util.KeywordComparator;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;


/**
 * The WordList consists of an ObservableList of Words. This is the main
 * word list to use for crossword construction. A "Word" consists of keyword
 * (the word to be guessed) and a list of clues (hints for a specific keyword).<br>
 * <br>
 * The word list is sorted at any time.<br>
 *
 * @see Word
 */
public class WordList
{
	private ObservableList<Word> wordData = FXCollections.observableArrayList();



	/**
	 * Default constructor.
	 */
	public WordList()
	{
	}

	/**
	 * Constructor for a new word list from a file.
	 * @param file
	 * @throws IOException
	 */
	public WordList(File file) throws IOException
	{
		loadFromFile(file);

		// Keep the WordList sorted:
		Collections.sort(wordData, new KeywordComparator());
	}


	/**
	 * Add a new word entry consisting of keyword and clue or
	 * a new clue to an existing keyword.
	 * @param keyword
	 * @param clue
	 */
	public void addWord(String keyword, String clue)
	{
		// If the WordList already contains an Word with the
		// given keyword, only add the clue to the existing keyword
		if (containsKeyword(keyword))
		{
			addClue(keyword, clue);
		}
		// else add the new Word to the list
		else
		{
			Word newWordData = new Word(keyword, clue);
			this.wordData.add(newWordData);

			// Keep the WordList sorted:
			Collections.sort(wordData, new KeywordComparator());
		}
	}

	public void addWord(Word newWord)
	{
		// If the WordList already contains an Word with the
		// given keyword, only add the clue to the existing keyword
		if (containsKeyword(newWord.getKeyword()))
		{
			// Go through all the clues of the Word and add them to the existing Word
			for (Pair<StringProperty,BooleanProperty> clue : newWord.getAll())
			{
				addClue(newWord.getKeyword(), clue.getKey().get());
			}
		}
		else
		{
			this.wordData.add(newWord);

			// Keep the WordList sorted:
			Collections.sort(wordData, new KeywordComparator());
		}
	}

	public void addClue(String keyword, String newClue)
	{
		for (Word wordElement : wordData)
		{
			if (keyword.equals(wordElement.getKeyword()))
			{
				wordElement.addClue(newClue);
				break;
			}
		}
	}

	public void addClue(int WordIndex, String clue)
	{
		this.wordData.get(WordIndex).addClue(clue);
	}

	public void removeWord(int index)
	{
		wordData.remove(index);
	}

	public int getListLength()
	{
		return wordData.size();
	}

	public void clearList()
	{
		wordData.clear();
	}

	public boolean containsKeyword(String keyword)
	{
		for (Word wordElement : wordData)
		{
			if (keyword.equals(wordElement.getKeyword()))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Load a new word list from a file.
	 * @param file
	 * @throws IOException
	 */
	public void loadFromFile(File file) throws IOException
	{
		FileHandler loader = new FileHandler(file);

		Map<String,List<String>> wordDictionary = loader.getLoadedData();

		for (String key : wordDictionary.keySet())
		{

			if (containsKeyword(key))
			{
				// Go through all the clues of the Word and add them to the existing Word
				for (String clue : wordDictionary.get(key))
				{
					addClue(key, clue);
				}
			}
			else
			{
				wordData.add(new Word(key, wordDictionary.get(key)));

				// Keep the WordList sorted:

			}
		}

		// Keep the WordList sorted:
		Collections.sort(wordData, new KeywordComparator());
	}

	/**
	 * Save the current word list to a file.
	 * @param file
	 */
	public void saveToFile(File file)
	{
		FileHandler loader = new FileHandler();
		loader.loadDataFromWordList(this);
		loader.writeToFile(file);
	}


	public ObservableList<Word> getWordData()
	{
		return this.wordData;
	}
}