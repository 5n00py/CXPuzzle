package cpg.util;

import javafx.scene.control.TableCell;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.util.Callback;
import cpg.mvc.crossword.model.Word;

/**
 * Helper class to make a CheckBox inside a TableCell.
 *
 *@see WordListController
 */
public class CheckBoxCellFactory implements Callback<Object, Object>
{
    @Override
    public TableCell<Word, Boolean> call(Object param)
    {
        CheckBoxTableCell<Word,Boolean> checkBoxCell = new CheckBoxTableCell<Word, Boolean>();
        checkBoxCell.setEditable(true);
        return checkBoxCell;
    }
}