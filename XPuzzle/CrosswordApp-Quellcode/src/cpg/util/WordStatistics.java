package cpg.util;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a helper class that provides information about the German language
 * letter frequencies. It can calculate the average letter frequency for a
 * specific word. The words can be sorted by frequency, i.e. the words
 * with the best average frequencies are on the top of the map. This provides
 * the crossword generator with statistic information, to place good words at
 * good positions.<br>
 *
 * @see CrosswordGenerator
 */
public class WordStatistics
{
	private Map<String, Double> keywordFrequencies = new HashMap<>();

	private Map<String, Double> wordMapByFrequency = new HashMap<>();


	/**
	 * The constructor.
	 * @param wordMap
	 */
	public WordStatistics(Map<String, String> wordMap)
	{
		setGermanKeywordFrequencies();

		for (String key : wordMap.keySet())
		{
			double frequency = getAverageLetterFrequency(key);
			wordMapByFrequency.put(key, frequency);
		}

		wordMapByFrequency = MapSortByValue.sortByValue(wordMapByFrequency);
	}


	/**
	 * Calculates the average word frequency for a specific word.
	 * @param word
	 * @return the average letter frequency
	 */
	public double getAverageLetterFrequency(String word)
	{
		double result = 0.0;

		for (int i=0; i<word.length(); i++)
		{
			String letter = String.valueOf(word.charAt(i));
			result = result + keywordFrequencies.get(letter);
		}

		result = result / word.length();

		return result;
	}


	/**
	 * The frequencies of the german language letters.
	 */
	private void setGermanKeywordFrequencies()
	{
		keywordFrequencies.put("E", 17.40);
		keywordFrequencies.put("N", 9.78);
		keywordFrequencies.put("I", 7.55);
		keywordFrequencies.put("S", 7.27);
		keywordFrequencies.put("R", 7.00);
		keywordFrequencies.put("A", 6.51);
		keywordFrequencies.put("T", 6.15);
		keywordFrequencies.put("D", 5.08);
		keywordFrequencies.put("H", 4.76);
		keywordFrequencies.put("U", 4.35);
		keywordFrequencies.put("L", 3.44);
		keywordFrequencies.put("C", 3.06);
		keywordFrequencies.put("G", 3.01);
		keywordFrequencies.put("M", 2.53);
		keywordFrequencies.put("O", 2.51);
		keywordFrequencies.put("B", 1.89);
		keywordFrequencies.put("W", 1.89);
		keywordFrequencies.put("F", 1.66);
		keywordFrequencies.put("K", 1.21);
		keywordFrequencies.put("Z", 1.13);
		keywordFrequencies.put("P", 0.79);
		keywordFrequencies.put("V", 0.67);
		keywordFrequencies.put("J", 0.27);
		keywordFrequencies.put("Y", 0.04);
		keywordFrequencies.put("X", 0.03);
		keywordFrequencies.put("Q", 0.02);
	}



	/**
	 * Getter for the word map with words sorted by average
	 * letter frequency.
	 * @return
	 */
	public Map<String, Double> getWordMapByFrequency()
	{
		return this.wordMapByFrequency;
	}
}