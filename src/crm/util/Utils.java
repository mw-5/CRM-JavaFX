package crm.util;

import java.util.function.Predicate;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TableView;


public class Utils 
{
    public static void updateTableView(TableView tv, ObservableList updatedList)
    {
        if (tv.getItems() instanceof FilteredList)
        {
            Predicate predicate = ((FilteredList)tv.getItems()).getPredicate();
            tv.setItems(updatedList.filtered(predicate));
        }
        else
        {
            tv.setItems(updatedList.sorted());
            ((SortedList)tv.getItems()).comparatorProperty().bind(tv.comparatorProperty());
        }
    }
}
