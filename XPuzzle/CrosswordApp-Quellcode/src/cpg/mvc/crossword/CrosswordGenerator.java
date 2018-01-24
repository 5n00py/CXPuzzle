package cpg.mvc.crossword;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cpg.util.WordStatistics;

/**
 * This class mainly provides an algorithm to make a crossword puzzle
 * from a crossword dictionary.<br>
 * <br>
 * The crossword dictionary is a Map with keyword (word to be guessed) and
 * clue (hint for a specific keyword). The crossword puzzle is a 2 dimensional
 * string array.<br>
 * <br>
 * The main goal of the algorithm is to generate as much crossings as possible and
 * as little empty fields as possible. For that purpose it follows different strategies:<br>
 * - The fillRandom() method places five random words in the four corners and the center
 *   of the grid and tries to cross another five words with the five random words.<br>
 * - The fillVertical(i) method iterates over every ith column and tries to place
 *   some good words vertical down.<br>
 * - The fillHorizontal(i) method iterates over every ith row and tries to place
 *   some good words horizontal right.<br>
 * - The fillDiagonal() method iterates diagonally over every crossword field and
 *   tries to place the best fit word, that is the word with the most crossings.<br>
 * - The fillWithGoodFrequency() method tries to place words with frequents letters
 *   (like E, N, etc.) in strategic good positions.<br>
 * <br>
 * For the last strategy, the word data is also held in a Map that is sorted by average
 * letter frequency.<br>
 * <br>
 * A keyword can have six different directions:<br>
 * - horizontal-right: to the right of the clue field in horizontal direction.<br>
 * - vertical-down: underneath the clue field in vertical direction.<br>
 * - left-down: to the left of the clue field in vertical direction.<br>
 * - right-down: to the right of the clue field in vertical direction.<br>
 * - top-right: above the clue field in horizontal direction.<br>
 * - bottom-right: underneath the clue field in horizontal direction.<br>
 * <br>
 * A word description in a clue field has the following format:
 * Number of letters + direction + clue, e.g.: "3 vertical-down: capital of Italy".<br>
 * <br>
 * For test purpose the crossword can also be printed to the console.<br>
 *
 * @see WordStatistics
 */
public class CrosswordGenerator
{
	static String[][] crosswordPuzzle;

	static Map<String,String> crosswordDictionary = new HashMap<>();

	static Map<String, Double> keywordsByFrequency = new HashMap<>();

	static int HEIGHT;
	static int WIDTH;


	/**
	 * Constructor. Makes a crossword from a given word dictionary.
	 * @param wordDictionary
	 */
	public CrosswordGenerator(Map<String,String> wordDictionary)
	{
		crosswordDictionary = wordDictionary;

	}

	/**
	 * Generate a random crossword puzzle from the crossword dictionary
	 * with a given width and height.
	 *
	 * @param width
	 * @param height
	 */
	public void generateRandom(int width, int height)
	{
		HEIGHT = height;
		WIDTH = width;

		crosswordPuzzle = new String[HEIGHT][WIDTH];

		WordStatistics stats = new WordStatistics(crosswordDictionary);
		keywordsByFrequency = stats.getWordMapByFrequency();

		makeEmptyCrossword();

		fillRandom();

		fillWithGoodFrequency();

		fillVertical(3);
		fillHorizontal(3);
		fillDiagonal();
	}

	/**
	 * Generate a crossword puzzle that contains every word in the
	 * crossword dictionary. The size is computed automatically.
	 */
	public void generateFromDictionary()
	{
		calculateSize();

		crosswordPuzzle = new String[HEIGHT][WIDTH];

		WordStatistics stats = new WordStatistics(crosswordDictionary);
		keywordsByFrequency = stats.getWordMapByFrequency();

		makeEmptyCrossword();

		fillRandom();

		fillWithGoodFrequency();

		fillVertical(3);
		fillHorizontal(3);
		fillDiagonal();

		while (! crosswordDictionary.isEmpty() && WIDTH < 30 && HEIGHT < 30)
		{
			if (WIDTH < HEIGHT)
			{
				resizeCrossword(3,0);
			}
			else
			{
				resizeCrossword(0,3);
			}

			fillVertical(3);
			fillHorizontal(3);
			fillDiagonal();
		}
	}

	/**
	 * Fill up an existing crossword from a given dictionary with a given
	 * number of words to fill with.
	 *
	 * @param dictionary
	 * @param numberOfWords
	 */
	public void fillUp(Map<String,String> dictionary, int numberOfWords)
	{
		if (dictionary.size() > numberOfWords)
		{
			List<String> randomKeywords = new ArrayList<String>(dictionary.keySet());
			Collections.shuffle( randomKeywords );

			Map<String,String> newDictionary = new HashMap<>();

			for (int i=0; i<numberOfWords; i++)
			{
				String key = randomKeywords.get(i);
				String clue = dictionary.get(key);
				newDictionary.put(key, clue);
			}

			crosswordDictionary = newDictionary;

			WordStatistics stats = new WordStatistics(crosswordDictionary);
			keywordsByFrequency = stats.getWordMapByFrequency();
		}
		else
		{
			crosswordDictionary = dictionary;

			WordStatistics stats = new WordStatistics(crosswordDictionary);
			keywordsByFrequency = stats.getWordMapByFrequency();
		}

		fillVertical(3);
		fillHorizontal(3);
		fillDiagonal();

	}

	/**
	 * Fill the crossword puzzle with five random words in the corners and the center
	 * and if possible with five other words that cross with the five random words.
	 */
	public void fillRandom()
	{
		List<String> randomKeywords = new ArrayList<String>(crosswordDictionary.keySet());
		Collections.shuffle( randomKeywords );

		int index = 0;

		// Place a random word at (1,0):
		int firstWordLength = 0;
		if (index < randomKeywords.size())
		{
			String key = randomKeywords.get(index);

			if (fitHorizontalRight(key, 2,0))
			{
				setKeyword(key,"horizontal-right",2,0);
				firstWordLength = key.length();
				index = index + 1;
			}

		}


		// Place a random word at the last row, at (HEIGHT-1,0):
		int secondWordLength = 0;
		if (index < randomKeywords.size() && ! isOccupiedField(HEIGHT-1,0))
		{
			String key = randomKeywords.get(index);

			if (fitHorizontalRight(key, HEIGHT-1,0))
			{
				setKeyword(key,"horizontal-right",HEIGHT-1,0);
				secondWordLength = key.length();
				index = index + 1;
			}

		}


		// Place a random word in the center
		int i1 = HEIGHT/2-3;
		if (i1<=0)
			i1=0;
		int j1 = WIDTH/2;

		int thirdWordLength = 0;
		if (index < randomKeywords.size() && ! isOccupiedField(i1,j1))
		{
			String key = randomKeywords.get(index);

			if (fitVerticalDown(key,i1,j1))
			{
				setKeyword(key,"vertical-down",i1,j1);
				thirdWordLength = key.length();
				index = index + 1;
			}

		}


		// Place another random word in the top right corner:
		int fourthWordLength = 0;
		if (index < randomKeywords.size())
		{
			String key = randomKeywords.get(index);

			if (fitVerticalDown(key,0,WIDTH-1) && ! isOccupiedField(0,WIDTH-1))
			{
				setKeyword(key,"vertical-down",0,WIDTH-1);
				fourthWordLength = key.length();
				index = index + 1;
			}

		}

		// Place a fifth random word in the bottom right corner;
		int fifthWordLength = 0;
		if (index < randomKeywords.size() && ! isOccupiedField(HEIGHT-fifthWordLength-1,WIDTH-1))
		{
			String key = randomKeywords.get(index);
			fifthWordLength = key.length();

			if (fitVerticalDown(key,HEIGHT-fifthWordLength-1,WIDTH-1))
			{
				setKeyword(key,"vertical-down",HEIGHT-fifthWordLength-1,WIDTH-1);
				index = index + 1;
			}

		}

		// try to cross with the first word
		for (int j=0; j<firstWordLength; j++)
		{
			setBestFrequency(0,j,3,"right-down");
			// Check if a word is set
			if (crosswordPuzzle[0][j].length() > 0)
				break;
			// Otherwise try at the next position
		}

		// try to cross with the second word
		boolean isSet = false;
		for (int i=3; i<6; i++)
		{
			for (int j=0; j<secondWordLength; j++)
			{
				setBestFrequency(HEIGHT-i,j,i,"right-down");
				// Check if a word is set
				if (crosswordPuzzle[HEIGHT-i][j].length() > 0)
				{
					isSet = true;
					break;
				}

				// Otherwise try at the next position
			}
			if (isSet)
			{
				break;
			}
		}

		// try to cross with the third word
		isSet = false;
		int i2 = HEIGHT/2-2;
		int j2 = HEIGHT/2-1;
		for (int i=0; i<=thirdWordLength; i++)
		{
			for (int j=0; j<3; j++)
			{
				setBestFrequency(i2+i,j2-j,3,"horizontal-right");
				// Check if a word is set
				if (crosswordPuzzle[i2+i][j2-j].length() > 0)
				{
					isSet = true;
					break;
				}

				// Otherwise try at the next position
			}
			if (isSet)
			{
				break;
			}
		}

		// try to cross with the fourth word
		isSet = false;
		for (int i=1; i<fourthWordLength; i++)
		{
			for (int j=3; j<6; j++)
			{
				setBestFrequency(i,WIDTH-j,j,"horizontal-right");
				// Check if a word is set
				if (crosswordPuzzle[i][WIDTH-j].length() > 0)
				{
					isSet = true;
					break;
				}

				// Otherwise try at the next position
			}
			if (isSet)
			{
				break;
			}
		}

		// try to cross with the fifth word, maximal 2 words
		int setNum = 0;
		for (int j=3; j<6; j++)
		{
			for (int i=1; i<fifthWordLength; i++)
			{
				setBestFrequency(HEIGHT-i,WIDTH-j,3,"bottom-right");
				// Check if a word is set
				if (crosswordPuzzle[HEIGHT-i][WIDTH-j].length() > 0)
				{
					setNum = setNum+1;
					break;
				}

				// Otherwise try at the next position
			}
			if (setNum > 2)
			{
				break;
			}
		}

	}


	/**
	 * Try to place words with good average letter frequency in
	 * good strategic positions.
	 */
	private static void fillWithGoodFrequency()
	{
		// Go through every 3rd column and fill it with words that have
		// good letter frequencies and have 3 or more letters.
		for (int i=1; i<HEIGHT-1; i+=4)
		{
			for (int j=0; j<WIDTH-1; j++)
			{
				setBestFrequency(i,j,4,"horizontal-right");
				//printCrosswordToConsole();
			}
		}

		// Do the same with the rows.
		for (int j=1; j<WIDTH-1; j+=4)
		{
			for (int i=0; i<HEIGHT-1; i++)
			{
				setBestFrequency(i,j,3,"vertical-down");
				//printCrosswordToConsole();
			}
		}
	}


	/**
	 * Traverse the crossword diagonally and try to fill it
	 * with words that fit the best, i.e. have the most crossings
	 * with other words.
	 */
	private static void fillDiagonal()
	{
		// Loop diagonally through the two dimensional array
		int numberOfDiags = WIDTH+HEIGHT-1;
		for (int diag=0; diag<numberOfDiags; diag++)
		{
			int rowStop = Math.max(0, diag-WIDTH+1);
			int rowStart = Math.min(diag, HEIGHT-1);

			for (int row = rowStart; row >= rowStop; row--)
			{
				int col = diag-row;

				if (! isOccupiedField(row,col))
				{
					setBestFit(row,col);
				}
			}
		}
	}

	/**
	 * Go through every ith (=steps) column and fill it with
	 * good vertical down words, starting with the 3rd row.
	 *
	 * @param steps
	 */
	private static void fillVertical(int steps)
	{
		for (int j=2; j<WIDTH-1; j+=steps)
		{
			for (int i=0; i<HEIGHT-1; i++)
			{
				setBestFitVertical(i,j);
			}
		}
	}

	/**
	 * Go through every ith (=steps) row and fill it with
	 * good horizontal right words, starting with the 3 row.
	 *
	 * @param steps
	 */
	private static void fillHorizontal(int steps)
	{
		for (int i=2; i<HEIGHT-1; i+=steps)
		{
			for (int j=0; j<WIDTH-1; j++)
			{
				setBestFitHorizontal(i,j);
			}
		}
	}


	/**
	 * Initialize the crossword with empty fields
	 */
	private static void makeEmptyCrossword()
	{
		for (int i=0; i<HEIGHT; i++)
		{
			for (int j=0; j<WIDTH; j++)
			{
				crosswordPuzzle[i][j] = "";
			}
		}
	}

	/**
	 * Calculate an estimating size for the crossword from
	 * the size of the word dictionary and the size of its words.
	 */
	private void calculateSize()
	{
		// The minimum size is 8x8
		HEIGHT = 8;
		WIDTH = 8;

		int[] lengths = getTheTwoLongestWordLengths();

		// The lengths and height should be bigger then the
		// two longest words + 2:

		if (lengths[0]+2 > WIDTH)
		{
			WIDTH = lengths[0] + 2;
		}

		if (lengths[1]+2 > HEIGHT)
		{
			HEIGHT = lengths[1] + 2;
		}


		// The factor of the side length of a quadratic crossword
		// to the number of words is approximately 2, so adjust
		// the this factor if necessary:

		int numberOfWords = crosswordDictionary.size();

		int length = (int) Math.ceil(numberOfWords / 2);

		if (length > HEIGHT)
		{
			HEIGHT = length;
		}

		if (length > WIDTH)
		{
			WIDTH = length;
		}
	}

	/**
	 * find the keyword that fits best into a specific position,
	 * i.e. has to most crossings with the surrounding words.
	 * @param row
	 * @param col
	 */
	private static void setBestFit(int row, int col)
	{
		if (isOccupiedField(row,col))
		{
			return;
		}

		String bestFit = "";
		String orientation = "";

		int bestFitLength = 0;
		int numberOfCrosses = 0;

		for (String key : crosswordDictionary.keySet())
		{


		    if (fitVerticalDown(key, row, col))
		    {
		    	int crossNum = numberOfVerticalDownCrosses(key,row,col);

		    	if (crossNum > numberOfCrosses)
		    	{
		    		bestFit = key;
		    		orientation = "vertical-down";
		    		bestFitLength = key.length();
		    		numberOfCrosses = crossNum;
		    	}
		    	else if (crossNum == numberOfCrosses && key.length() > bestFitLength)
		    	{
		    		bestFit = key;
		    		orientation = "vertical-down";
		    		bestFitLength = key.length();
		    		numberOfCrosses = crossNum;
		    	}
		    }


		    if (fitHorizontalRight(key, row, col))
		    {
		    	int crossNum = numberOfHorizontalRightCrosses(key,row,col);

		    	if (crossNum > numberOfCrosses)
		    	{
		    		bestFit = key;
		    		orientation = "horizontal-right";
		    		bestFitLength = key.length();
		    		numberOfCrosses = crossNum;
		    	}
		    	else if (crossNum == numberOfCrosses && key.length() > bestFitLength)
		    	{
		    		bestFit = key;
		    		orientation = "horizontal-right";
		    		bestFitLength = key.length();
		    		numberOfCrosses = crossNum;
		    	}
		    }


		    if (fitRightDown(key, row, col))
		    {
		    	int crossNum = numberOfRightDownCrosses(key,row,col);

		    	if (crossNum > numberOfCrosses)
		    	{
		    		bestFit = key;
		    		orientation = "right-down";
		    		bestFitLength = key.length();
		    		numberOfCrosses = crossNum;
		    	}
		    	else if (crossNum == numberOfCrosses && key.length() > bestFitLength)
		    	{
		    		bestFit = key;
		    		orientation = "right-down";
		    		bestFitLength = key.length();
		    		numberOfCrosses = crossNum;
		    	}
		    }


		    if (fitLeftDown(key, row, col))
		    {
		    	int crossNum = numberOfLeftDownCrosses(key,row,col);

		    	if (crossNum > numberOfCrosses)
		    	{
		    		bestFit = key;
		    		orientation = "left-down";
		    		bestFitLength = key.length();
		    		numberOfCrosses = crossNum;
		    	}
		    	else if (crossNum == numberOfCrosses && key.length() > bestFitLength)
		    	{
		    		bestFit = key;
		    		orientation = "left-down";
		    		bestFitLength = key.length();
		    		numberOfCrosses = crossNum;
		    	}
		    }

		    if (fitTopRight(key, row, col))
		    {
		    	int crossNum = numberOfTopRightCrosses(key,row,col);

		    	if (crossNum > numberOfCrosses)
		    	{
		    		bestFit = key;
		    		orientation = "top-right";
		    		bestFitLength = key.length();
		    		numberOfCrosses = crossNum;
		    	}
		    	else if (crossNum == numberOfCrosses && key.length() > bestFitLength)
		    	{
		    		bestFit = key;
		    		orientation = "top-right";
		    		bestFitLength = key.length();
		    		numberOfCrosses = crossNum;
		    	}
		    }


		    if (fitBottomRight(key, row, col))
		    {
		    	int crossNum = numberOfBottomRightCrosses(key,row,col);

		    	if (crossNum > numberOfCrosses)
		    	{
		    		bestFit = key;
		    		orientation = "bottom-right";
		    		bestFitLength = key.length();
		    		numberOfCrosses = crossNum;
		    	}
		    	else if (crossNum == numberOfCrosses && key.length() > bestFitLength)
		    	{
		    		bestFit = key;
		    		orientation = "bottom-right";
		    		bestFitLength = key.length();
		    		numberOfCrosses = crossNum;
		    	}
		    }

		}

		if (bestFit.length() > 0)
		{
			setKeyword(bestFit, orientation, row, col);
		}

	}


	private static void setBestFitVertical(int row, int col)
	{
		if (isOccupiedField(row,col))
		{
			return;
		}

		String bestFit = "";

		int bestFitLength = 0;
		int numberOfCrosses = 0;

		for (String key : crosswordDictionary.keySet())
		{
		    if (fitVerticalDown(key, row, col))
		    {
		    	int crossNum = numberOfVerticalDownCrosses(key,row,col);

		    	if (crossNum > numberOfCrosses)
		    	{
		    		bestFit = key;
		    		bestFitLength = key.length();
		    		numberOfCrosses = crossNum;
		    	}
		    	else if (crossNum == numberOfCrosses && key.length() > bestFitLength)
		    	{
		    		bestFit = key;
		    		bestFitLength = key.length();
		    		numberOfCrosses = crossNum;
		    	}
		    }
		}

		if (bestFit.length() > 0)
		{
			setKeyword(bestFit, "vertical-down", row, col);
		}
	}


	private static void setBestFitHorizontal(int row, int col)
	{
		if (isOccupiedField(row,col))
		{
			return;
		}

		String bestFit = "";

		int bestFitLength = 0;
		int numberOfCrosses = 0;

		for (String key : crosswordDictionary.keySet())
		{
		    if (fitHorizontalRight(key, row, col))
		    {
		    	int crossNum = numberOfHorizontalRightCrosses(key,row,col);

		    	if (crossNum > numberOfCrosses)
		    	{
		    		bestFit = key;
		    		bestFitLength = key.length();
		    		numberOfCrosses = crossNum;
		    	}
		    	else if (crossNum == numberOfCrosses && key.length() > bestFitLength)
		    	{
		    		bestFit = key;
		    		bestFitLength = key.length();
		    		numberOfCrosses = crossNum;
		    	}
		    }
		}

		if (bestFit.length() > 0)
		{
			setKeyword(bestFit, "horizontal-right", row, col);
		}
	}

	private static void setBestFrequency(int row, int col, int minLength, String orientation)
	{
		if (isOccupiedField(row,col))
		{
			return;
		}

		for (String key : keywordsByFrequency.keySet())
		{
			if (key.length() >= minLength)
			{
				if (orientation.equals("horizontal-right"))
				{
					if (fitHorizontalRight(key, row, col))
					{
						setKeyword(key, "horizontal-right", row, col);
						break;
					}
				}
				else if (orientation.equals("vertical-down"))
				{
					if (fitVerticalDown(key, row, col))
					{
						setKeyword(key, "vertical-down", row, col);
						break;
					}
				}
				else if (orientation.equals("right-down"))
				{
					if (fitRightDown(key, row, col))
					{
						setKeyword(key, "right-down", row, col);
						break;
					}
				}
				else if (orientation.equals("bottom-right"))
				{
					if (fitBottomRight(key, row, col))
					{
						setKeyword(key, "bottom-right", row, col);
						break;
					}
				}
			}
		}
	}


	private static boolean fitHorizontalRight(String keyword, int row, int col)
	{
		int len = keyword.length();

		if (row <= 0 || col <0)
		{
			return false;
		}

		if (len + col + 1> WIDTH)
		{
			return false;
		}

		if (len + col +1 < WIDTH)
		{
			if (crosswordPuzzle[row][col+len+1].length() == 2 || crosswordPuzzle[row][col+len+1].length() == 1)
			{
				return false;
			}
		}

		if (isOccupiedField(row,col))
		{
			return false;
		}

		for (int i = 0; i < len; i++)
		{
			String letter = String.valueOf(keyword.charAt(i));

			if (crosswordPuzzle[row][col+i+1].equals(""))
			{
				continue;
			}
			else if (letter.equals(String.valueOf(crosswordPuzzle[row][col+i+1].charAt(0))))
			{
				if ("v".equals(String.valueOf(crosswordPuzzle[row][col+i+1].charAt(1))))
					continue;
				else
					return false;
			}
			else
			{
				return false;
			}
		}
		return true;
	}

	private static boolean fitVerticalDown(String keyword, int row, int col)
	{

		int len = keyword.length();

		if (col <= 0 || row <0)
		{
			return false;
		}

		if (len + row + 1 > HEIGHT)
		{
			return false;

		}

		if (len + row +1 < HEIGHT)
		{
			if (crosswordPuzzle[row+len+1][col].length() == 2 || crosswordPuzzle[row+len+1][col].length() == 1)
			{
				return false;
			}
		}

		if (isOccupiedField(row,col))
		{
			return false;
		}

		for (int i = 0; i < len; i++)
		{
			String letter = String.valueOf(keyword.charAt(i));

			if (crosswordPuzzle[row+i+1][col].equals(""))
			{
				continue;
			}
			else if (letter.equals(String.valueOf(crosswordPuzzle[row+i+1][col].charAt(0))))
			{
				if ("h".equals(String.valueOf(crosswordPuzzle[row+i+1][col].charAt(1))))
					continue;
				else
					return false;
			}
			else
			{
				return false;
			}
		}
		return true;
	}

	private static boolean fitLeftDown(String keyword, int row, int col)
	{
		int len = keyword.length();

		// If the clue stands already in the first column, it would be out of bounds
		if (col <= 0 || row <0)
		{
			return false;
		}

		// Check if it is not too long.
		if (len + row > HEIGHT)
		{
			return false;

		}

		// The first field should be empty.
		if (crosswordPuzzle[row][col-1].length()>1)
		{
			return false;
		}

		if (len + row < HEIGHT)
		{
			if (crosswordPuzzle[row+len][col-1].length() == 2 || crosswordPuzzle[row+len][col-1].length() == 1)
			{
				return false;
			}
		}

		if (isOccupiedField(row,col))
		{
			return false;
		}

		for (int i = 0; i < len; i++)
		{
			String letter = String.valueOf(keyword.charAt(i));

			if (crosswordPuzzle[row+i][col-1].equals(""))
			{
				continue;
			}
			else if (letter.equals(String.valueOf(crosswordPuzzle[row+i][col-1].charAt(0))))
			{
				if ("h".equals(String.valueOf(crosswordPuzzle[row+i][col-1].charAt(1))))
					continue;
				else
					return false;
			}
			else
			{
				return false;
			}
		}

		return true;
	}

	private static boolean fitRightDown(String keyword, int row, int col)
	{
		int len = keyword.length();

		if (col<0 || row<0)
		{
			return false;
		}

		// If we are in the last column, it doesent fit.
		if (col == WIDTH-1)
		{
			return false;
		}

		// Check if it is not too long.
		if (keyword.length() + row > HEIGHT)
		{
			return false;
		}

		// The first field should be empty.
		if (crosswordPuzzle[row][col+1].length()>1)
		{
			return false;
		}

		if (len + row < HEIGHT)
		{
			if (crosswordPuzzle[row+len][col+1].length() == 2 || crosswordPuzzle[row+len][col+1].length() == 1)
			{
				return false;
			}
		}

		if (isOccupiedField(row,col))
		{
			return false;
		}

		for (int i = 0; i < len; i++)
		{
			String letter = String.valueOf(keyword.charAt(i));

			if (crosswordPuzzle[row+i][col+1].equals(""))
			{
				continue;
			}
			else if (letter.equals(String.valueOf(crosswordPuzzle[row+i][col+1].charAt(0))))
			{
				if ("h".equals(String.valueOf(crosswordPuzzle[row+i][col+1].charAt(1))))
				{
					continue;
				}
				else
				{
					return false;
				}
			}
			else
			{
				return false;
			}
		}
		return true;
	}

	private static boolean fitTopRight(String keyword, int row, int col)
	{
		int len = keyword.length();

		// Does not fit, if clue field is already in the top row
		if (row <= 0 || col<0)
		{
			return false;
		}

		// Check if keyword is not too long to fit in
		if (len + col > WIDTH)
		{
			return false;
		}

		// The first field should be empty.
		if (crosswordPuzzle[row-1][col].length() > 1)
		{
			return false;
		}

		if (len + col < WIDTH)
		{
			if (crosswordPuzzle[row-1][col+len].length() == 2 || crosswordPuzzle[row-1][col+len].length() == 1)
			{
				return false;
			}
		}

		if (isOccupiedField(row,col))
		{
			return false;
		}

		for (int i = 0; i < len; i++)
		{
			String letter = String.valueOf(keyword.charAt(i));

			if (crosswordPuzzle[row-1][col+i].equals(""))
			{
				continue;
			}
			else if (letter.equals(String.valueOf(crosswordPuzzle[row-1][col+i].charAt(0))))
			{
				if ("v".equals(String.valueOf(crosswordPuzzle[row-1][col+i].charAt(1))))
					continue;
				else
					return false;
			}
			else
			{
				return false;
			}
		}

		return true;
	}

	private static boolean fitBottomRight(String keyword, int row, int col)
	{
		int len = keyword.length();

		if (row <0 || col <0)
		{
			return false;
		}

		// If we are already in the bottom row, it does not fit.
		if (row == HEIGHT-1)
		{
			return false;
		}

		// Check if its not too long.
		if (len + col > WIDTH)
		{
			return false;
		}

		// The first field should be empty.
		if (crosswordPuzzle[row+1][col].length()>1)
		{
			return false;
		}

		if (len + col < WIDTH)
		{
			if (crosswordPuzzle[row+1][col+len].length() == 2 || crosswordPuzzle[row+1][col+len].length() == 1)
			{
				return false;
			}
		}

		if (isOccupiedField(row,col))
		{
			return false;
		}

		for (int i = 0; i < len; i++)
		{
			String letter = String.valueOf(keyword.charAt(i));

			if (crosswordPuzzle[row+1][col+i].equals(""))
			{
				continue;
			}
			else if (letter.equals(String.valueOf(crosswordPuzzle[row+1][col+i].charAt(0))))
			{
				if ("v".equals(String.valueOf(crosswordPuzzle[row+1][col+i].charAt(1))))
					continue;
				else
					return false;
			}
			else
			{
				return false;
			}
		}
		return true;
	}

	private static int numberOfHorizontalRightCrosses(String keyword, int row, int col)
	{
		int result = 0;

		for (int i = 0; i<keyword.length(); i++)
		{
			if (crosswordPuzzle[row][col+i+1].length() == 2)
			{
				result = result + 1;
			}
		}

		return result;
	}

	private static int numberOfVerticalDownCrosses(String keyword, int row, int col)
	{
		int result = 0;

		for (int i=0; i<keyword.length(); i++)
		{
			if (crosswordPuzzle[row+i+1][col].length()==2)
			{
				result = result + 1;
			}
		}

		return result;
	}

	private static int numberOfLeftDownCrosses(String keyword, int row, int col)
	{
		int result = 0;

		for (int i=0; i<keyword.length(); i++)
		{
			if (crosswordPuzzle[row+i][col-1].length()==2)
			{
				result = result + 1;
			}
		}

		return result;
	}

	private static int numberOfRightDownCrosses(String keyword, int row, int col)
	{
		int result = 0;

		for (int i=0; i<keyword.length(); i++)
		{
			if (crosswordPuzzle[row+i][col+1].length()==2)
			{
				result = result + 1;
			}
		}

		return result;
	}


	private static int numberOfTopRightCrosses(String keyword, int row, int col)
	{
		int result = 0;

		for (int i = 0; i<keyword.length(); i++)
		{
			if (crosswordPuzzle[row-1][col+i].length() == 2)
			{
				result = result + 1;
			}
		}

		return result;
	}


	private static int numberOfBottomRightCrosses(String keyword, int row, int col)
	{
		int result = 0;

		for (int i = 0; i<keyword.length(); i++)
		{
			if (crosswordPuzzle[row+1][col+i].length() == 2)
			{
				result = result + 1;
			}
		}

		return result;
	}


	private static void setKeyword(String keyword, String orientation, int row, int col)
	{
		if (orientation.equals("horizontal-right"))
		{
	    	setKeywordHorizontalRight(keyword, row, col);
		}
		else if (orientation.equals("vertical-down"))
		{
	    	setKeywordVerticalDown(keyword, row, col);
		}
		else if (orientation.equals("left-down"))
		{
	    	setKeywordLeftDown(keyword, row, col);
		}
		else if (orientation.equals("right-down"))
		{
	    	setKeywordRightDown(keyword, row, col);
		}
		else if (orientation.equals("top-right"))
		{
	    	setKeywordTopRight(keyword, row, col);
		}
		else if (orientation.equals("bottom-right"))
		{
	    	setKeywordBottomRight(keyword, row, col);
		}

		crosswordPuzzle[row][col] = Integer.toString(keyword.length()) + " " + orientation + ": " + crosswordDictionary.get(keyword);
    	crosswordDictionary.remove(keyword);
    	keywordsByFrequency.remove(keyword);
	}

	private static void setKeywordHorizontalRight(String keyword, int row, int col)
	{
		int len = keyword.length();

		for (int i=0; i<len; i++)
		{
			String letter = String.valueOf(keyword.charAt(i)) + "h";

			crosswordPuzzle[row][col+i+1] = letter;
		}

		if (col+len+1 < WIDTH && crosswordPuzzle[row][col+len+1].length() < 2)
		{
			crosswordPuzzle[row][col+len+1] = "0";
		}
	}


	private static void setKeywordVerticalDown(String keyword, int row, int col)
	{
		int len = keyword.length();

		for (int i=0; i<len; i++)
		{
			String letter = String.valueOf(keyword.charAt(i)) + "v";

			crosswordPuzzle[row+i+1][col] = letter;
		}

		if (row+len+1 < HEIGHT && crosswordPuzzle[row+len+1][col].length() <2)
		{
			crosswordPuzzle[row+len+1][col] = "0";
		}

	}


	private static void setKeywordRightDown(String keyword, int row, int col)
	{
		int len = keyword.length();

		for (int i=0; i<len; i++)
		{
			String letter = String.valueOf(keyword.charAt(i)) + "v";

			crosswordPuzzle[row+i][col+1] = letter;
		}

		if (len+row < HEIGHT && crosswordPuzzle[row+len][col+1].length()<2)
		{
			crosswordPuzzle[row+len][col+1] = "0";
		}
	}


	private static void setKeywordLeftDown(String keyword, int row, int col)
	{
		int len = keyword.length();

		for (int i=0; i<len; i++)
		{
			String letter = String.valueOf(keyword.charAt(i)) + "v";

			crosswordPuzzle[row+i][col-1] = letter;
		}

		if (len+row < HEIGHT && crosswordPuzzle[row+len][col-1].length()<2)
		{
			crosswordPuzzle[row+len][col-1] = "0";
		}
	}


	private static void setKeywordTopRight(String keyword, int row, int col)
	{
		int len = keyword.length();

		for (int i=0; i<len; i++)
		{
			String letter = String.valueOf(keyword.charAt(i)) + "h";

			crosswordPuzzle[row-1][col+i] = letter;
		}

		if (len + col < WIDTH && crosswordPuzzle[row-1][col+len].length() <2)
		{
			crosswordPuzzle[row-1][col+len] = "0";
		}
	}


	private static void setKeywordBottomRight(String keyword, int row, int col)
	{
		int len = keyword.length();

		for (int i=0; i<len; i++)
		{
			String letter = String.valueOf(keyword.charAt(i)) + "h";

			crosswordPuzzle[row+1][col+i] = letter;
		}

		if (len + col < WIDTH && crosswordPuzzle[row+1][col+len].length()<2)
		{
			crosswordPuzzle[row+1][col+len] = "0";
		}
	}


	private static boolean isOccupiedField(int row, int col)
	{
		// Make sure we are not out of bounds...
		if (row<0 || col<0 || row >= HEIGHT || col >= WIDTH)
		{
			return true;
		}

		if (crosswordPuzzle[row][col].length()<2)
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	private void resizeCrossword(int plusWidth, int plusHeight)
	{
		int newHEIGHT = HEIGHT + plusHeight;
		int newWIDTH = WIDTH + plusWidth;

		String[][] newCrosswordPuzzle = new String[newHEIGHT][newWIDTH];

		for (int i=0; i<newHEIGHT; i++)
		{
			for (int j=0; j<newWIDTH; j++)
			{
				newCrosswordPuzzle[i][j] = "";
			}
		}

		for (int i=0; i<HEIGHT; i++)
		{
			for (int j=0; j<WIDTH; j++)
			{
				newCrosswordPuzzle[i][j] = crosswordPuzzle[i][j];
			}
		}

		HEIGHT = newHEIGHT;
		WIDTH = newWIDTH;
		crosswordPuzzle = newCrosswordPuzzle;
	}

	private int[] getTheTwoLongestWordLengths()
	{
		int[] lengths = {0,0};

		for (String key : crosswordDictionary.keySet())
		{
			int keyLength = key.length();

			if (keyLength > lengths[0])
			{
				lengths[0] = keyLength;
			}
			else if (keyLength > lengths[1])
			{
				lengths[1] = keyLength;
			}
		}

		return lengths;
	}

	/**
	 * Print the 2 dimensional string array to the console. A clue entry
	 * is indicated by an index number that leads to a list of clues
	 * underneath the crossword grid.
	 */
	@SuppressWarnings("unused")
	private static void printCrosswordToConsole()
	{
		List<String> clueList = new ArrayList<>();

		String separator = "";
		String line = "";
		int clueIterator = 1;
		for (int i=0; i < HEIGHT; i++)
		{
			line = "";
			separator = "";
			for (int j=0; j < WIDTH; j++)
			{
				if (crosswordPuzzle[i][j].length() > 2)
				{
					if (clueIterator > 9)
					{
						line = line + " |" + Integer.toString(clueIterator);
					}
					else
					{
						line = line + " | " + Integer.toString(clueIterator);
					}
					clueList.add(crosswordPuzzle[i][j]);
					clueIterator = clueIterator + 1;
					separator = separator + "-----";
				}
				else if (crosswordPuzzle[i][j].length() == 2)
				{
					line = line + " | " + String.valueOf(crosswordPuzzle[i][j].charAt(0));
					separator = separator + "-----";
				}
				else
				{
					line = line + " | " + "0";
					separator = separator + "-----";
				}

			}
			line = line + " |";
			System.out.println(separator);
			System.out.println(line);
		}
		System.out.println(separator);

		System.out.println("\n \n");
		System.out.println("Clues: ");
		System.out.println("--------\n");
		int iterator = 1;

		for (String clue : clueList)
		{
			System.out.println(iterator + ": " + clue);
			iterator = iterator + 1;
		}
	}



	/**
	 * The getter for the 2 dimensional crossword array.
	 * @return
	 */
	public String[][] getCrosswordPuzzle()
	{
		return crosswordPuzzle;
	}
}