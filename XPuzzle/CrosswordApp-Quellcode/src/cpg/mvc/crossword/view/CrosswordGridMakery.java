package cpg.mvc.crossword.view;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * This class provides methods to make a crossword as GridPane from a
 * two dimensional string array. A square is by default 50x50 px large.<br>
 * <br>
 * A square is either a clue field, thats consists of a black background
 * and a white description, a letter field, that consists of a single black
 * letter on white background and is part of a crossword entry, or an empty
 * field, that is represented as grey field.<br>
 * <br>
 * In addition, the arrows show the directions of the crossword entries.
 * At the end of an entry, a "word stopper" is drawn to indicate the end.<br>
 *
 */
@SuppressWarnings("unused")
public class CrosswordGridMakery
{
	private static final int SQUARE_WIDTH = 50;
	private static final int SQUARE_HEIGHT = 50;

	GridPane crosswordGrid;



	/**
	 * The constructor.
	 * @param crosswordArray
	 */
    public CrosswordGridMakery(String[][] crosswordArray)
    {
        makeFromStringArray(crosswordArray);
    }


    /**
     * Makes a new crossword GridPane from a 2d string array.
     * @param crosswordArray
     */
	public void makeFromStringArray(String[][] crosswordArray)
	{

		int height = crosswordArray.length;
		int width = crosswordArray[0].length;

		initCrosswordGrid(width, height);

		fillCrosswordGrid(crosswordArray);
	}


	/**
	 * Initializes the crossword GridPane with emtpy entries.
	 * @param width
	 * @param height
	 */
	public void initCrosswordGrid(int width, int height)
	{
		crosswordGrid = new GridPane();

        for (int i=0; i<width; i++)
        {
        	for (int j=0; j<height; j++)
        	{
        		Rectangle rect = new Rectangle(SQUARE_WIDTH, SQUARE_HEIGHT);
        		rect.setFill(Color.TRANSPARENT);
        		rect.setStroke(Color.BLACK);

        		Pane canvas = new Pane();
       	     	canvas.setMinHeight(SQUARE_WIDTH);
       	     	canvas.setMinWidth(SQUARE_HEIGHT);

       	     	canvas.getChildren().add(rect);

        		crosswordGrid.add(canvas, i, j);
        	}
        }
	}


	/**
	 * Fill the Grids as empty field, letter field or clue field.
	 * @param 2d stringArray
	 */
	public void fillCrosswordGrid(String[][] stringArray)
	{
		for (int i=0; i<stringArray.length; i++)
		{
			for (int j=0; j<stringArray[i].length; j++)
			{
				if (stringArray[i][j].length() < 2)
				{
					addEmptyField(j,i);
				}
				else if (stringArray[i][j].length() == 2)
				{
					addSingleLetterField(String.valueOf(stringArray[i][j].charAt(0)), j, i);
				}
				else if (stringArray[i][j].length() > 2)
				{
					addClue(stringArray[i][j], j, i);
				}
			}
		}
	}

	public void addClue(String clueString, int posX, int posY)
	{
		String arr[] = clueString.split(" ", 3);
		String orientation = arr[1];
		String clue = arr[2];

		if (orientation.equals("horizontal-right:"))
		{
			addClueField(clue, posX, posY);
			addHorizontalRightArrow(posX+1, posY);
		}
		else if (orientation.equals("vertical-down:"))
		{
			addClueField(clue, posX, posY);
			addVerticalDownArrow(posX, posY+1);
		}
		else if (orientation.equals("right-down:"))
		{
			addClueField(clue, posX, posY);
			addRightDownArrow(posX+1, posY);
		}
		else if (orientation.equals("left-down:"))
		{
			addClueField(clue, posX, posY);
			addLeftDownArrow(posX-1, posY);
		}
		else if (orientation.equals("top-right:"))
		{
			addClueField(clue, posX, posY);
			addTopRightArrow(posX, posY-1);
		}
		else if (orientation.equals("bottom-right:"))
		{
			addClueField(clue, posX, posY);
			addBottomRightArrow(posX, posY+1);
		}

	}

	public void addClueField(String clue, int posX, int posY)
	{
		StackPane stack = new StackPane();

		Rectangle rect = new Rectangle(SQUARE_WIDTH, SQUARE_HEIGHT);
		rect.setFill(Color.BLACK);
		rect.setStroke(Color.BLACK);

		stack.setMinHeight(SQUARE_WIDTH);
		stack.setMinWidth(SQUARE_HEIGHT);

		stack.getChildren().add(rect);

		Label label = new Label(clue);
		label.setWrapText(true);
		label.setMaxWidth(SQUARE_WIDTH - 2);
		label.setMaxHeight(SQUARE_HEIGHT - 2);
		label.setFont(new Font("Arial", 10));
	    label.setTextFill(Color.WHITE);

	    stack.getChildren().add(label);

		crosswordGrid.add(stack, posX, posY);
	}

	public void addEmptyField(int posX, int posY)
	{
		StackPane stack = new StackPane();

		Rectangle rect = new Rectangle(SQUARE_WIDTH-1, SQUARE_HEIGHT-1);
		rect.setFill(Color.GREY);
		// rect.setStroke(Color.BLACK);

		stack.setMinHeight(SQUARE_WIDTH);
		stack.setMinWidth(SQUARE_HEIGHT);

		stack.getChildren().add(rect);

		crosswordGrid.add(stack, posX, posY);
	}

	public void addSingleLetterField(String letter, int posX, int posY)
	{
		StackPane canvas = new StackPane();
		canvas.setMinHeight(SQUARE_WIDTH);
		canvas.setMinWidth(SQUARE_HEIGHT);

		// If you want to show the solution, uncomment the
		// following lines:

		/*
		Label label = new Label(letter);
		label.setFont(new Font("Arial", 24));
        label.setTextAlignment(TextAlignment.CENTER);


		canvas.getChildren().add(label);
		*/

		crosswordGrid.add(canvas, posX, posY);
	}

	public void addHorizontalRightArrow(int posX, int posY)
	{
		Pane canvas = new Pane();
		canvas.setMinHeight(SQUARE_WIDTH);
		canvas.setMinWidth(SQUARE_HEIGHT);

		Polygon triangle = new Polygon();
		triangle.getPoints().addAll(new Double[] {
				0.0, 0.0,
				0.0, 12.0,
				10.0, 6.0});
		triangle.relocate(0.0, 19.0);

		canvas.getChildren().add(triangle);

		crosswordGrid.add(canvas, posX, posY);
	}

	public void addVerticalDownArrow(int posX, int posY)
	{
		Pane canvas = new Pane();
		canvas.setMinHeight(SQUARE_WIDTH);
		canvas.setMinWidth(SQUARE_HEIGHT);

		Polygon triangle = new Polygon();
		triangle.getPoints().addAll(new Double[] {
				0.0, 0.0,
				12.0, 0.0,
				6.0, 10.0});
		triangle.relocate(19.0, 0.0);

		canvas.getChildren().add(triangle);

		crosswordGrid.add(canvas, posX, posY);
	}

	public void addRightDownArrow(int posX, int posY)
	{
		Pane canvas = new Pane();
		canvas.setMinHeight(SQUARE_WIDTH);
		canvas.setMinWidth(SQUARE_HEIGHT);

		Line line1 = new Line();
		line1.setStartX(0.0);
		line1.setStartY(8.0);
		line1.setEndX(14.0);
		line1.setEndY(8.0);

		Line line2 = new Line();
		line2.setStartX(14.0);
		line2.setStartY(8.0);
		line2.setEndX(14.0);
		line2.setEndY(35.0);

		Polygon triangle = new Polygon();
		triangle.getPoints().addAll(new Double[] {
				0.0, 0.0,
				12.0, 0.0,
				6.0, 10.0});
		triangle.relocate(8.0, 30.0);

		canvas.getChildren().add(line1);
		canvas.getChildren().add(line2);
		canvas.getChildren().add(triangle);

		crosswordGrid.add(canvas, posX, posY);
	}

	public void addLeftDownArrow(int posX, int posY)
	{
		Pane canvas = new Pane();
		canvas.setMinHeight(SQUARE_WIDTH);
		canvas.setMinWidth(SQUARE_HEIGHT);

		Line line1 = new Line();
		line1.setStartX(50.0);
		line1.setStartY(8.0);
		line1.setEndX(36.0);
		line1.setEndY(8.0);

		Line line2 = new Line();
		line2.setStartX(36.0);
		line2.setStartY(8.0);
		line2.setEndX(36.0);
		line2.setEndY(35.0);

		Polygon triangle = new Polygon();
		triangle.getPoints().addAll(new Double[] {
				0.0, 0.0,
				12.0, 0.0,
				6.0, 10.0});
		triangle.relocate(30.0, 30.0);

		canvas.getChildren().add(line1);
		canvas.getChildren().add(line2);
		canvas.getChildren().add(triangle);

		crosswordGrid.add(canvas, posX, posY);
	}

	public void addTopRightArrow(int posX, int posY)
	{
		Pane canvas = new Pane();
		canvas.setMinHeight(SQUARE_WIDTH);
		canvas.setMinWidth(SQUARE_HEIGHT);

		Line line1 = new Line();
		line1.setStartX(8.0);
		line1.setStartY(50.0);
		line1.setEndX(8.0);
		line1.setEndY(36.0);

		Line line2 = new Line();
		line2.setStartX(8.0);
		line2.setStartY(36.0);
		line2.setEndX(35.0);
		line2.setEndY(36.0);

		Polygon triangle = new Polygon();
		triangle.getPoints().addAll(new Double[] {
				0.0, 0.0,
				0.0, 12.0,
				10.0, 6.0});
		triangle.relocate(30.0, 30.0);

		canvas.getChildren().add(line1);
		canvas.getChildren().add(line2);
		canvas.getChildren().add(triangle);

		crosswordGrid.add(canvas, posX, posY);
	}

	public void addBottomRightArrow(int posX, int posY)
	{
		Pane canvas = new Pane();
		canvas.setMinHeight(SQUARE_WIDTH);
		canvas.setMinWidth(SQUARE_HEIGHT);

		Line line1 = new Line();
		line1.setStartX(8.0);
		line1.setStartY(0.0);
		line1.setEndX(8.0);
		line1.setEndY(14.0);

		Line line2 = new Line();
		line2.setStartX(8.0);
		line2.setStartY(14.0);
		line2.setEndX(35.0);
		line2.setEndY(14.0);

		Polygon triangle = new Polygon();
		triangle.getPoints().addAll(new Double[] {
				0.0, 0.0,
				0.0, 12.0,
				10.0, 6.0});
		triangle.relocate(30.0, 8.0);

		canvas.getChildren().add(line1);
		canvas.getChildren().add(line2);
		canvas.getChildren().add(triangle);

		crosswordGrid.add(canvas, posX, posY);
	}


	/**
	 * The getter method for the crossword GridPane.
	 * @return
	 */
	public GridPane getCrosswordGrid()
	{
		return this.crosswordGrid;
	}
}