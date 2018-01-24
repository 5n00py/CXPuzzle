package cpg.util;

import java.util.Comparator;

import cpg.mvc.crossword.model.Word;

/**
 * A helper class to compare two keywords of a Word, specially for
 * sorting the Words in a WordList alphabetically.
 *
 *@see WordList
 */
public class KeywordComparator implements Comparator<Word>
{
	@Override
	public int compare(Word w1, Word w2)
	{
		return w1.getKeyword().compareTo(w2.getKeyword());
	}
}
