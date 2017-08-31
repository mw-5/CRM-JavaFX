package crm.navigation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.PropertyValueFactory;       
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


public class FilterContextMenuTest 
{
    FilterContextMenu filterCM;
    TableColumn col;
    Map<TableColumn, Predicate> filterMap;
    ObservableList unfilteredList;
    
    public static class TestApp extends Application
    {
        @Override
        public void start(Stage primaryStage) throws Exception
        {}
    }
    
    @BeforeClass
    public static void setUpClass() throws InterruptedException
    {
        // initialize Java FX
        Thread t = new Thread("JavaFX TestApp Thread"){
            public void run()
            {
                Application.launch(TestApp.class, new String[0]);
            }
        };
        t.setDaemon(true);
        t.start();
        Thread.sleep(500);
    }
    
    @Before
    public void setUp()
    {
        col = new TableColumn();
        TableView tv = new TableView();
        tv.getColumns().add(col);
        filterMap = new HashMap<>();
        unfilteredList = FXCollections.observableArrayList(new String[]{});
        filterCM = new FilterContextMenu(col, filterMap, unfilteredList);
        col.setContextMenu(filterCM);
    }
    
    private void mimicUserInputTextBox(String arg) throws Exception
    {
        Field field = FilterContextMenu.class.getDeclaredField("txtFilterBox");
        field.setAccessible(true);
        TextField txtFilterBox = (TextField)field.get(filterCM);
        txtFilterBox.setText(arg);
    }
    
    
    @Test
    public void testResetCheckBoxes() throws Exception
    {
        // set up test data
        unfilteredList.clear();
        SimpleStringProperty sp;
        for (String s : new String[]{"abc", "ab", "abcd", "bcd"})
        {
            sp = new SimpleStringProperty();
            sp.set(s);
            unfilteredList.add(sp);
        }
        col.setCellValueFactory(new PropertyValueFactory<SimpleStringProperty, String>("value"));
        col.getTableView().getItems().setAll(unfilteredList);
        
        // create tree view
        Method method = FilterContextMenu.class.getDeclaredMethod("createMiTreeView");
        method.setAccessible(true);
        method.invoke(filterCM);
        
        Field field = FilterContextMenu.class.getDeclaredField("rootTree");
        field.setAccessible(true);
        TreeItem<CheckBox> root = (TreeItem<CheckBox>)field.get(filterCM);
        assertEquals(4, root.getChildren().size());
        assertEquals(4, col.getTableView().getItems().size());
        
        // filter
        root.getChildren().get(1).getValue().fire();
        root.getChildren().get(3).getValue().fire();
        filterCM.filterByTreeView();
        
        assertEquals(2, col.getTableView().getItems().size());
        
        // check reset filter
        filterCM.resetCheckBoxes();
        
        for (TreeItem<CheckBox> ti : root.getChildren())
        {
            assertTrue(ti.getValue().isSelected());
        }
        
        unfilteredList.clear();        
    }
    
    @Test
    public void testClearAllFilters() throws Exception
    {
        unfilteredList.clear();
        unfilteredList.add("e1");
        unfilteredList.add("e2");
        
        mimicUserInputTextBox("e1");
        
        filterCM.filterByTextBox();
        
        assertTrue(filterMap.size() > 0);
        assertNotEquals(((FilteredList)col.getTableView().getItems()).getPredicate(), null);
        filterCM.clearAllFilters();
        
        assertTrue(filterMap.isEmpty());
        assertFalse(col.getTableView().getItems() instanceof FilteredList);
        unfilteredList.clear();
    }
    
    @Test
    public void testClearThisFilter() throws Exception
    {
        unfilteredList.clear();
        unfilteredList.add("e1");
        unfilteredList.add("e2");
        
        int initialFilterSize = filterMap.size();
                
        mimicUserInputTextBox("e1");
        
        filterCM.filterByTextBox();
        
        assertTrue(filterMap.size() == initialFilterSize + 1);
        assertNotEquals(((FilteredList)col.getTableView().getItems()).getPredicate(), null);
        
        filterCM.clearThisFilter();
        
        assertTrue(filterMap.size() == initialFilterSize);
        unfilteredList.clear();
    }
    
    @Test
    public void testFilterByTextBox() throws Exception
    {
        // set up test data
        unfilteredList.clear();
        SimpleStringProperty sp;
        for (String s : new String[]{"abc", "ab", "abcd", "bcd"})
        {
            sp = new SimpleStringProperty();
            sp.set(s);
            unfilteredList.add(sp);
        }       
        col.setCellValueFactory(new PropertyValueFactory<SimpleStringProperty, String>("value"));
        
        col.getTableView().getItems().setAll(unfilteredList);
        assertTrue(col.getTableView().getItems().size() >0);
        
        // test filter behavior
        mimicUserInputTextBox("abc");
        filterCM.filterByTextBox();
        assertTrue(col.getTableView().getItems().size() == 1);
        
        mimicUserInputTextBox("ab*");
        filterCM.filterByTextBox();
        assertTrue(col.getTableView().getItems().size() == 3);
        
        mimicUserInputTextBox("*cd");
        filterCM.filterByTextBox();
        assertTrue(col.getTableView().getItems().size() == 2);
        
        mimicUserInputTextBox("*c*");
        filterCM.filterByTextBox();
        assertTrue(col.getTableView().getItems().size() == 3);
        
        unfilteredList.clear();
    }
    
    @Test
    public void testFilterByDatePicker() throws Exception
    {
        // set up data
        unfilteredList.clear();
        // Calendar is used because JPA EclipseLink currently does not support LocalDate
        SimpleObjectProperty<Calendar> dp;
        for (int[] d : new int[][]{ {2017,1,1}, {2017,1,2},{ 2017,1,3} })
        {
            Calendar cal = Calendar.getInstance();
            cal.set(d[0], d[1]-1, d[2], 0, 0, 0);
            cal.set(Calendar.MILLISECOND, 0);
            dp = new SimpleObjectProperty<>();
            dp.set(cal);
            unfilteredList.add(dp);
        }
        col.setCellValueFactory(new PropertyValueFactory<SimpleObjectProperty<Calendar>, String>("Value"));
        col.getTableView().getItems().setAll(unfilteredList);
        
        // construct FilterContextMenu with date type
        filterCM = new FilterContextMenu(col, filterMap, unfilteredList, true);
                
        // mimic user input to data pickers
        Field field = FilterContextMenu.class.getDeclaredField("startDatePicker");
        field.setAccessible(true);
        DatePicker startPicker = (DatePicker)field.get(filterCM);
        field = FilterContextMenu.class.getDeclaredField("endDatePicker");
        field.setAccessible(true);
        DatePicker endPicker = (DatePicker)field.get(filterCM);
        
        assertTrue(col.getTableView().getItems().size() == 3);
        
        startPicker.setValue(LocalDate.of(2017,1,1));
        endPicker.setValue(LocalDate.of(2017,1,2));
        filterCM.filterByDatePicker();
        assertTrue(col.getTableView().getItems().size() == 2);

        startPicker.setValue(LocalDate.of(2017,1,2));
        endPicker.setValue(LocalDate.of(2017,1,2));
        filterCM.filterByDatePicker();
        assertTrue(col.getTableView().getItems().size() == 1);
        
        startPicker.setValue(LocalDate.of(2017,1,2));
        endPicker.setValue(LocalDate.of(2017,1,3));
        filterCM.filterByDatePicker();
        assertTrue(col.getTableView().getItems().size() == 2);
        
        // check reset of boundaries - end date before start date
        startPicker.setValue(LocalDate.of(2017,1,3));
        endPicker.setValue(LocalDate.of(2017,1,1));
        filterCM.filterByDatePicker();
        assertTrue(col.getTableView().getItems().size() == 1);
        
        // check reset of boundaries - start date after end date
        endPicker.setValue(LocalDate.of(2017,1,1));
        startPicker.setValue(LocalDate.of(2017,1,2));
        filterCM.filterByDatePicker();
        assertTrue(col.getTableView().getItems().size() == 1);
        
        unfilteredList.clear();
    }
    
    @Test
    public void testFilterByTreeView() throws Exception
    {
        // set up test data
        unfilteredList.clear();
        SimpleStringProperty sp;
        for (String s : new String[]{"abc", "ab", "abcd", "bcd"})
        {
            sp = new SimpleStringProperty();
            sp.set(s);
            unfilteredList.add(sp);
        }
        col.setCellValueFactory(new PropertyValueFactory<SimpleStringProperty, String>("value"));
        col.getTableView().getItems().setAll(unfilteredList);
        
        // create tree view
        Method method = FilterContextMenu.class.getDeclaredMethod("createMiTreeView");
        method.setAccessible(true);
        method.invoke(filterCM);
        
        Field field = FilterContextMenu.class.getDeclaredField("rootTree");
        field.setAccessible(true);
        TreeItem<CheckBox> root = (TreeItem<CheckBox>)field.get(filterCM);
        assertEquals(4, root.getChildren().size());
        assertEquals(4, col.getTableView().getItems().size());
        
        // filter
        root.getChildren().get(1).getValue().fire();
        root.getChildren().get(3).getValue().fire();
        filterCM.filterByTreeView();
        
        assertEquals(2, col.getTableView().getItems().size());
        
        unfilteredList.clear();
    }
    
    
    
    
}
