package cpg.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a helper class in order to save a crossword that is
 * represented as a 2-dimensional string array as a SVG file.
 * For rendering the crossword, it provides some methods that
 * produce XML-based SVG "code".
 *
 * @author dave
 *
 */
public class SVGMakery
{
	static String[][] crossword;

	static String svgContent = "";


	/**
	 * The constructor.
	 *
	 * @param crossword
	 */
	public SVGMakery(String[][] crossword)
	{
		SVGMakery.crossword = crossword;

		int width = SVGMakery.crossword.length * 50;
		int height = SVGMakery.crossword[0].length * 50;

		setXMLHeader();
		setSVGStart(height, width);

		setCrosswordGrid();

		setCrosswordArrows();

		setSVGEnd();
	}

	/**
	 * Transform the crossword grid into SVG code that consists mainly
	 * of rectangles for every crossword field.
	 */
	private static void setCrosswordGrid()
	{
		int height = crossword.length;
		int width = crossword[0].length;

		for (int i=0; i<height; i++)
		{
			for (int j=0; j<width;j++)
			{
				if (crossword[i][j].length()<2)
				{
					setRectangle(j*50,i*50,50,50,"black","grey");
				}
				else if (crossword[i][j].length() == 2)
				{
					setRectangle(j*50,i*50,50,50,"black","white");
					//String letter = Character.toString(crossword[i][j].charAt(0));
					//setLetter(j*50,i*50, letter);
				}
				else
				{
					String clueString = crossword[i][j];
					String arr[] = clueString.split(" ", 3);
					String clue = arr[2];
					setRectangle(j*50,i*50,50,50,"black","black");
					setClue(j*50,i*50, clue);
				}
			}
		}
	}

	/**
	 * Set the arrows as svg code to indicate the directions of the
	 * words to be guessed.
	 */
	private static void setCrosswordArrows()
	{
		int height = crossword.length;
		int width = crossword[0].length;

		for (int i=0; i<height; i++)
		{
			for (int j=0; j<width;j++)
			{
				if (crossword[i][j].length()>2)
				{
					String clueString = crossword[i][j];
					String arr[] = clueString.split(" ", 3);
					String orientation = arr[1];

					if (orientation.equals("horizontal-right:"))
					{
						setHorizontalRightArrow(j*50,i*50);
					}
					else if (orientation.equals("vertical-down:"))
					{
						setVerticalDownArrow(j*50,i*50);
					}
					else if (orientation.equals("right-down:"))
					{
						setRightDownArrow(j*50,i*50);
					}
					else if (orientation.equals("left-down:"))
					{
						setLeftDownArrow(j*50,i*50);
					}
					else if (orientation.equals("top-right:"))
					{
						setTopRightArrow(j*50,i*50);
					}
					else if (orientation.equals("bottom-right:"))
					{
						setBottomRightArrow(j*50,i*50);
					}
				}
			}
		}
	}

	private static void setXMLHeader()
	{
		svgContent += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
	}

	private static void setSVGStart(int width, int height)
	{
		svgContent += "<svg xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n"
				+ "version=\"1.1\" baseProfile=\"full\"\n"
				//+ "width=\""
				//+ Integer.toString(width)
				//+ "mm\" height=\""
				//+ Integer.toString(height)
				//+ "mm\"\n"
				+ "viewBox=\"0 0 "
				+ Integer.toString(width) + " "
				+ Integer.toString(height)
				+ "\">\n"
				+ "<title>Crossword Puzzle</title>\n";
	}

	private static void setSVGEnd()
	{
		svgContent += "</svg>";
	}

	/**
	 * set the clue text inside a clue field.
	 * @param x position of the clue field.
	 * @param y position of the clue field.
	 * @param content
	 */
	private static void setClue(int x, int y, String content)
	{
		// Split the content into substrings of size 8:
		List<String> strings = new ArrayList<String>();
		int index = 0;
		while (index < content.length())
		{
		    strings.add(content.substring(index, Math.min(index + 8,content.length())));
		    index += 8;
		}
		// The number of rows in a clue field is maximal 5,
		// otherwise we have to cut the description
		int rows = strings.size();
		if (rows > 5)
			rows = 5;

		svgContent += "<text "
				+ "x=\"" + Integer.toString(x+3) + "\" "
				+ "y=\"" + Integer.toString(y+50) + "\" "
				+ "inline-size=\"48\" "
				+ "style = \""
				+ "font-size:9;"
				+ "font-family:Arial;"
				+ "stroke:white\"> \n";

				for (int i=0; i<rows; i++)
				{
					if (strings.get(i).length()>0)
					{
						svgContent += "\t"
								+ "<tspan x=\"" + Integer.toString(x+3) + "\" "
								+ "y=\"" + Integer.toString(y+10*(i+1)) + "\" >\n";

						svgContent += "\t \t"
								+ strings.get(i);

						svgContent += "\t"
								+ "</tspan>\n";
					}
				}

				svgContent += "</text>\n\n";
	}

	@SuppressWarnings("unused")
	private static void setLetter(int x, int y, String letter)
	{
		svgContent += "<text "
				+ "x=\"" + Integer.toString(x+20) + "\" "
				+ "y=\"" + Integer.toString(y+30) + "\" "
				+ "inline-size=\"48\" "
				+ "style = \""
				+ "font-size:15;"
				//+ "font-family:Arial;"
				+ "stroke:black\"> \n"

				+ letter + "\n"

				+ "</text> \n\n";
	}

	private static void setRectangle(int x, int y, int width, int height, String stroke, String fill)
	{
		svgContent += "<rect x=\""
					+ Integer.toString(x)
					+ "\" y=\""
					+ Integer.toString(y)
					+ "\" width=\""
					+ Integer.toString(width)
					+ "\" height=\""
					+ Integer.toString(height)
					+ "\" style=\""
					+ "stroke:" + stroke + "; "
					+ "fill:" + fill + " "
					+ "\" />\n";
	}

	private static void setHorizontalRightArrow(int x, int y)
	{
		svgContent += "<polygon points="
				+ "\"" + Integer.toString(x+50) + " " + Integer.toString(y+20) + ","
				+ Integer.toString(x+50) + " " + Integer.toString(y+30) + ","
				+ Integer.toString(x+60) + " " + Integer.toString(y+25)
				+ "\" />\n\n";
	}

	private static void setVerticalDownArrow(int x, int y)
	{
		svgContent += "<polygon points="
				+ "\"" + Integer.toString(x+20) + " " + Integer.toString(y+50) + ","
				+ Integer.toString(x+30) + " " + Integer.toString(y+50) + ","
				+ Integer.toString(x+25) + " " + Integer.toString(y+60)
				+ "\" />\n\n";
	}

	private static void setRightDownArrow(int x, int y)
	{
		svgContent += "<polyline points=\""
				+ Integer.toString(x+50) + " "
				+ Integer.toString(y+15) + ","
				+ Integer.toString(x+62) + " "
				+ Integer.toString(y+15) + ","
				+ Integer.toString(x+62) + " "
				+ Integer.toString(y+32) + "\" "
				+ "style=\"stroke:black;stroke-width:2;fill:none\" />\n";

		svgContent += "<polyline points=\""
				+ Integer.toString(x+57) + " "
				+ Integer.toString(y+24) + ","
				+ Integer.toString(x+62) + " "
				+ Integer.toString(y+32) + ","
				+ Integer.toString(x+67) + " "
				+ Integer.toString(y+24) + "\" "
				+ "style=\"stroke:black;stroke-width:2;fill:none\" />\n\n";
	}

	private static void setLeftDownArrow(int x, int y)
	{
		svgContent += "<polyline points=\""
				+ Integer.toString(x) + " "
				+ Integer.toString(y+15) + ","
				+ Integer.toString(x-12) + " "
				+ Integer.toString(y+15) + ","
				+ Integer.toString(x-12) + " "
				+ Integer.toString(y+32) + "\" "
				+ "style=\"stroke:black;stroke-width:2;fill:none\" />\n";

		svgContent += "<polyline points=\""
				+ Integer.toString(x-7) + " "
				+ Integer.toString(y+24) + ","
				+ Integer.toString(x-12) + " "
				+ Integer.toString(y+32) + ","
				+ Integer.toString(x-17) + " "
				+ Integer.toString(y+24) + "\" "
				+ "style=\"stroke:black;stroke-width:2;fill:none\" />\n\n";
	}

	private static void setTopRightArrow(int x, int y)
	{
		svgContent += "<polyline points=\""
				+ Integer.toString(x+15) + " "
				+ Integer.toString(y) + ","
				+ Integer.toString(x+15) + " "
				+ Integer.toString(y-12) + ","
				+ Integer.toString(x+32) + " "
				+ Integer.toString(y-12) + "\" "
				+ "style=\"stroke:black;stroke-width:2;fill:none\" />\n";

		svgContent += "<polyline points=\""
				+ Integer.toString(x+24) + " "
				+ Integer.toString(y-7) + ","
				+ Integer.toString(x+32) + " "
				+ Integer.toString(y-12) + ","
				+ Integer.toString(x+24) + " "
				+ Integer.toString(y-17) + "\" "
				+ "style=\"stroke:black;stroke-width:2;fill:none\" />\n\n";
	}

	private static void setBottomRightArrow(int x, int y)
	{
		svgContent += "<polyline points=\""
				+ Integer.toString(x+15) + " "
				+ Integer.toString(y+50) + ","
				+ Integer.toString(x+15) + " "
				+ Integer.toString(y+62) + ","
				+ Integer.toString(x+32) + " "
				+ Integer.toString(y+62) + "\" "
				+ "style=\"stroke:black;stroke-width:2;fill:none\" />\n";

		svgContent += "<polyline points=\""
				+ Integer.toString(x+24) + " "
				+ Integer.toString(y+57) + ","
				+ Integer.toString(x+32) + " "
				+ Integer.toString(y+62) + ","
				+ Integer.toString(x+24) + " "
				+ Integer.toString(y+67) + "\" "
				+ "style=\"stroke:black;stroke-width:2;fill:none\" />\n\n";
	}

	@SuppressWarnings("unused")
	private static void printSVG()
	{
		System.out.println(svgContent);
	}

	public static String getSVGContent()
	{
		return svgContent;
	}

	public void writeToFile(File file) throws IOException
	{
		BufferedWriter bw;
		bw = new BufferedWriter(new FileWriter(file,true));
		bw.write(svgContent);
		bw.flush();
		bw.close();
	}
}
