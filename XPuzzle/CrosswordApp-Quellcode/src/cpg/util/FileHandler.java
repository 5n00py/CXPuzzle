package cpg.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cpg.mvc.crossword.model.Word;
import cpg.mvc.crossword.model.WordList;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Pair;

/**
 * This is a helper class to load data from a file into a WordList or
 * save data from a WordList to a file.<br>
 * <br>
 * A line in a text file consists of a keyword (word to be guessed) and a
 * clue, separated by one ore more blanks. Lines beginning with a # are
 * comment lines and ignored when the file is read.<br>
 * <br>
 * Note that in a text file a keyword may appear several times (with different clues),
 * but in the WordList a key is unique. Instead, in the WordList a keyword has a list of clues,
 * where the clue is added for an already existing keyword.<br>
 * <br>
 * The loaded data is preloaded into a TreeMap and not directly into a WordList.
 * This makes is quicker to keep the data sorted while reading from file.<br>
 *
 * @see WordList
 *
 */
public class FileHandler
{
	Map<String,List<String>> loadedData = new TreeMap<String,List<String>>();

	private BufferedReader bufferedReader;


	/**
	 * Default constructor.
	 */
	public FileHandler()
	{
	}

	/**
	 * Constructor from file.
	 * @param file
	 * @throws IOException
	 */
	public FileHandler(File file) throws IOException
	{
		loadFile(file);
	}


	/**
	 * Load the data from a word list to loadedData as TreeMap.
	 * @param wordList
	 */
	public void loadDataFromWordList(WordList wordList)
	{
		for (Word word : wordList.getWordData())
		{
			for (Pair<StringProperty, BooleanProperty> clue : word.getAll())
			{
				if (this.loadedData.containsKey(word.getKeyword()))
				{
					this.loadedData.get(word.getKeyword()).add(clue.getKey().get());
				}
				else
				{
					List<String> newClueList = new ArrayList<>();
					newClueList.add(clue.getKey().get());
					loadedData.put(word.getKeyword(), newClueList);
				}
			}
		}
	}


	/**
	 * Transforms the loaded data as TreeMap into a WordList.
	 * @return
	 */
	public WordList getDataAsWordList()
	{
		WordList wordList = new WordList();

		for (String key : this.loadedData.keySet())
		{
			for (String clue : this.loadedData.get(key))
			{
				wordList.addClue(key, clue);
			}
		}

		return wordList;
	}




	public void loadFile(File file) throws IOException
	{
		loadTextFile(file);
		// To do: It would be nice, to support here more file types, like XML
	}


	/**
	 * Load the data from a text file into the TreeMap wordData of this class.
	 * @param file
	 * @throws IOException
	 */
	private void loadTextFile(File file) throws IOException
	{
		FileReader fileReader = new FileReader(file);
		bufferedReader = new BufferedReader(fileReader);

		// Iterate through the lines of the file
		String line = bufferedReader.readLine();
		while (line != null)
		{
			// Remove empty spaces at the beginning and end of the line
			line = line.trim();

			// Ignore comment lines, starting with a #
			if (line.startsWith("#"))
			{
				line = bufferedReader.readLine();
				continue;
			}

			// Split the line into two parts (a keyword and a clue)
			String splitLine[] = line.split(" ",2);

			// If there are not 2 parts, the keyword and/or the clue is missing
			// in that case ignore it...
			if (splitLine.length != 2)
			{
				line = bufferedReader.readLine();
				continue;
			}

			String keyword = splitLine[0].trim();
			String clue = splitLine[1].trim();

			/* Check if the keyword has a correct format,
			 * i.e. only alphabetical letters, no special signs,
			 * otherwise ignore it
			 */
			if (! isCorrectKeyword(keyword))
			{
				line = bufferedReader.readLine();
				continue;
			}

			/* Convert the keyword into an appropriate format,
			 * e.g. transform umlaute, only upper case letters...
			 */
			keyword = normalizeKeyword(keyword);

			// Finally, add the keyword with the clue to the map :-)
			if (loadedData.containsKey(keyword))
			{
				loadedData.get(keyword).add(clue);
			}
			else
			{
				List<String> newClueList = new ArrayList<>();
				newClueList.add(clue);
				loadedData.put(keyword, newClueList);
			}

			line = bufferedReader.readLine();
		}

	}




	/**
	 * write data from the wordData into a file
	 * @param wordMap
	 * @param file
	 */
	public void writeToFile(File file)
	{
		writeToTextFile(file);
		// To do: It would be nice, to support here more file types, like XML
	}

	public void writeToTextFile(File file)
	{
		// Standard header for the file
		String content = "# --------------- XPuzzle Wordlist -------------- \n";
		content +=       "# This file provides a list of keywords and clues \n";
		content +=       "# in order to generate a crossword puzzle. \n";
		content +=       "# -----------------------------------------------\n\n";

		// Iterate over all the elements in the wordMap:
		for (String keyword : this.loadedData.keySet())
		{
			// Iterate over all the clues for a specific keyword:
			for (String clue : this.loadedData.get(keyword))
			{
				// Write the keyword + clue combination into a line of the string content
				content = content + keyword + " " + clue + "\n";
			}
		}

		// Now write the content into a text file
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file)))
		{
			bw.write(content);

			// no need to close it.
			//bw.close();

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}



	/**
	 * Checks if the keyword has a correct format, that means
	 * only consists of letters (and no special signs or numbers)
	 *
	 * @param keyword
	 * @return is correct?
	 */
	public boolean isCorrectKeyword(String keyword)
	{
		char[] characters = keyword.toCharArray();

		for (char c : characters)
		{
			if (! Character.isLetter(c))
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Converts a keyword into a proper format, that means
	 * only upper case alphabets A-Z
	 *
	 * @param keyword
	 * @return normalized keyword
	 */
	public String normalizeKeyword(String keyword)
	{
		// Convert all letters to upper case letters
		keyword = keyword.toUpperCase();

		// Replace all upper case Umlauts (for German crossword puzzles)
		keyword = keyword.replace("Ü", "UE")
	                          .replace("Ö", "OE")
	                          .replace("Ä", "AE")
	                          .replace("ß", "SS");

	    // Remove all diacritical marks from unicode chars, e.g. É becomes E
		keyword = Normalizer
		        .normalize(keyword, Normalizer.Form.NFD)
		        .replaceAll("[^\\p{ASCII}]", "");

		return keyword;
	}




	/* -------------------------------
	 * The Getters and Setters
	 * ------------------------------- */

	public Map<String, List<String>> getLoadedData()
	{
		return this.loadedData;
	}

	public void setLoadedData(Map<String, List<String>> wordMap)
	{
		this.loadedData = wordMap;
	}
}