package cpg.mvc.crossword.model;

import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;

/**
 * Model Class for a word in the crossword puzzle.
 * It is a WordList entry and consists of a keyword, that is
 * the word to be guessed in the crossword puzzle and a list of clues,
 * that are hints for a specific keyword. It also gives information about
 * whether a keyword is checked, e.g. selected in a CheckBox.<br>
 * <br>
 * An entry in the clueList is a Pair that consists of a StringProperty
 * and a BooleanProperty, that represent the clue and the checked status of a
 * clue.<br>
 *
 * @see WordList
 */
public class Word
{

	private final StringProperty keyword;

	private BooleanProperty checked = new SimpleBooleanProperty(false);

	private ObservableList<Pair<StringProperty,BooleanProperty>> clueList = FXCollections.observableArrayList();



	/**
	 * Constructor to make an new Word from keyword and single clue.
	 * @param keyword
	 * @param clue
	 */
	public Word(String keyword, String clue)
	{
		this.keyword = new SimpleStringProperty(keyword);
		this.clueList.add(new Pair<StringProperty, BooleanProperty>(new SimpleStringProperty(clue), new SimpleBooleanProperty(false)));
	}

	/**
	 * Constructor to make a new Word from keyword and a list of clues.
	 * @param keyword
	 * @param clueList
	 */
	public Word(String keyword, List<String> clueList)
	{
		this.keyword = new SimpleStringProperty(keyword);

		for (String clue : clueList)
		{
			this.clueList.add(new Pair<StringProperty, BooleanProperty>(new SimpleStringProperty(clue), new SimpleBooleanProperty(false)));
		}
	}



	/**
	 * Add a now clue to the clueList.
	 * @param newClue
	 */
	public void addClue(String newClue)
	{
		// Check if the Word already contains the new clue
		// In that case don't add it and return
		for (Pair<StringProperty,BooleanProperty> clue : clueList)
		{
			if (newClue == clue.getKey().get())
			{
				return;
			}
		}
		// Otherwise add the clue to the clue list and set the selection to false
		this.clueList.add(new Pair<StringProperty,BooleanProperty>(new SimpleStringProperty(newClue), new SimpleBooleanProperty(false)));
	}



	/**
	 * Replace all the clues by a new list of clues.
	 * @param newClueList
	 */
	public void replaceClues(List<String> newClueList)
	{
		this.clueList.clear();

		for (String clue : newClueList)
		{
			addClue(clue);
		}
	}




	/* ******************************
	 * The Getter and Setter Methods:
	 ********************************/

	public String getKeyword()
	{
		return keyword.get();
	}

	public void setKeyword(String keyword)
	{
		this.keyword.set(keyword);
	}

	public StringProperty keywordProperty()
	{
		return keyword;
	}

	public Boolean getChecked()
	{
		return this.checked.get();
	}

	public void setChecked(Boolean checked)
	{
		this.checked.set(checked);
	}

	public BooleanProperty checkedProperty()
	{
		return this.checked;
	}

	public ObservableList<Pair<StringProperty,BooleanProperty>> getAll()
	{
		return this.clueList;
	}
}