package crm.navigation;

import crm.model.Model;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


public class FilterContextMenu extends ContextMenu
{
    private final TableColumn col;
    private final Map<TableColumn, Predicate> filterMap;
    Model model = Model.getModel();
    private boolean isDate = false;
    private ObservableList unfilteredList;
    private boolean isResetTreeView = true;
    
    public FilterContextMenu(TableColumn col, Map<TableColumn, Predicate> filterMap, ObservableList unfilteredList)
    {
        this(col, filterMap, unfilteredList, false);
    }
    
    public FilterContextMenu(TableColumn col, Map<TableColumn, Predicate> filterMap, ObservableList unfilteredList, boolean isDate)
    {
        this.col = col;
        this.filterMap = filterMap;
        this.unfilteredList = unfilteredList;
        this.isDate = isDate;
        
        setStrings();
        
        createMiClearAllFilters();
        createMiClearThisFilter();
        if (isDate)
        {
            createMiDatePickerFilter();
        }
        else
        {
            createMiTextFilterBox();
            createMiTreeViewButton();
        }
    }
    
    @Override
    public void show()
    {
        if (isResetTreeView && !getItems().contains(miCreateTreeView) && !isDate)
        {
            getItems().remove(miTreeView);
            createMiTreeViewButton();
        }
        
        super.show();
    }
    
    private String clearAllFilters;
    private String clearThisFilter;
    private String filter;
    private String start;
    private String end;
    private String selectItems;
    private String all;
    private void setStrings()
    {
        String language = Locale.getDefault().toString().substring(0, 2);
        switch(language)
        {
            case "de":
                clearAllFilters = "Alle Filter löschen";
                clearThisFilter = "Diesen Filter löschen";
                filter = "Filtern";
                start = "Start:";
                end = "Ende:";
                selectItems = "Items auswählen";
                all = "ALles";
                break;
            case "es":
                clearAllFilters = "Remover todos filtros";
                clearThisFilter = "Remover este filtro";
                filter = "Filtrar";
                start = "Comienzo:";
                end = "Fin:";
                selectItems = "Eligir items";
                all = "Todo";
                break;
            default:
                clearAllFilters = "Remove all filters";
                clearThisFilter = "Remove this filter";
                filter = "filter";
                start = "Start:";
                end = "End:";
                selectItems = "Select items";
                all = "All";                                                
        }
    }
    
    
    private void createMiClearAllFilters()
    {
        MenuItem mi = new MenuItem();
        mi.setText(clearAllFilters);
        mi.setOnAction(event -> clearAllFilters());
        getItems().add(mi);
    }
    protected void clearAllFilters()
    {
        for (TableColumn tc : filterMap.keySet())
        {
            ((FilterContextMenu)tc.getContextMenu()).resetCheckBoxes();
        }
        filterMap.clear();
        filter();
    }
    
    
    private void createMiClearThisFilter()
    {
        MenuItem mi = new MenuItem();
        mi.setText(clearThisFilter);
        mi.setOnAction(event -> clearThisFilter());
        getItems().add(mi);
    }
    protected void clearThisFilter()
    {
        filterMap.remove(col);
        resetCheckBoxes();
        filter();
    }
    
    
    private final TextField txtFilterBox = new TextField();
    private void createMiTextFilterBox()
    {
        HBox hBox = new HBox();
        txtFilterBox.setPrefWidth(140);
        hBox.getChildren().add(txtFilterBox);
        Button btn = new Button();
        btn.setText(filter);
        btn.setOnAction(event -> filterByTextBox());
        hBox.getChildren().add(btn);
        CustomMenuItem mi = new CustomMenuItem();
        mi.setContent(hBox);
        getItems().add(mi);
    }
    protected void filterByTextBox()
    {
        final String valueFilter = txtFilterBox.getText();
        
        if (valueFilter == null || valueFilter.equals(""))
        {
            filterMap.remove(col);
        }
        else
        {
            filterMap.put(col, e -> {
                
               if (col.getCellData(e) == null) 
               {
                   return false;
               }
               
               String valueElement = col.getCellData(e).toString();
               boolean isIncluded = false;
               boolean isWcStart = valueFilter.startsWith("*"); // starts with wildcard
               boolean isWcEnd = valueFilter.endsWith("*"); // ends with wildcard
               
               try
               {
                   // no usage of regex to avoid confusing user by meta characters
                   if (!isWcStart && !isWcEnd)
                   {
                       isIncluded = valueElement.equals(valueFilter);
                   }
                   else if (isWcStart && isWcEnd)
                   {
                       isIncluded = valueElement.contains(valueFilter.substring(1, valueFilter.length()-1));                       
                   }
                   else if (isWcStart && !isWcEnd)
                   {
                       isIncluded = valueElement.endsWith(valueFilter.substring(1));                       
                   }
                   else if (!isWcStart && isWcEnd)
                   {
                       isIncluded = valueElement.startsWith(valueFilter.substring(0, valueFilter.length()-1));
                   }
               }
               catch(Exception ex)
               {
                   isIncluded = false;
               }
               return isIncluded;
                       
            });
        }
        filter();
    }
    
    
    private final DatePicker startDatePicker = new DatePicker();
    private final DatePicker endDatePicker = new DatePicker();
    private void createMiDatePickerFilter()
    {
        VBox vBox = new VBox();
        
        // create date pickers
        vBox.getChildren().add(createDateFilterPanel(start, startDatePicker));
        vBox.getChildren().add(createDateFilterPanel(end, endDatePicker));
        startDatePicker.valueProperty().addListener(startDateListener);
        endDatePicker.valueProperty().addListener(endDateListener);
        
        // create filter button
        Button btn = new Button();
        btn.setText(filter);
        btn.setPrefWidth(102);
        btn.setOnAction(event -> filterByDatePicker());
        Label lbl = new Label(); // empty label in order to position button
        lbl.setPrefWidth(40);
        HBox btnPanel = new HBox();
        btnPanel.getChildren().add(lbl);
        btnPanel.getChildren().add(btn);
        vBox.getChildren().add(btnPanel);
        
        // create menu item
        CustomMenuItem mi = new CustomMenuItem();
        mi.setHideOnClick(false);
        mi.setContent(vBox);
        getItems().add(mi);
    }
    private ChangeListener<LocalDate> startDateListener = new ChangeListener<LocalDate>(){
        @Override
        public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue)
        {
            if (newValue == null)
            {
                return;
            }
            
            if (endDatePicker.getValue() == null || newValue.compareTo(endDatePicker.getValue()) > 0)
            {
                endDatePicker.setValue(newValue);
            }
            startDatePicker.setValue(newValue);
        }
    };
    private ChangeListener<LocalDate> endDateListener = new ChangeListener<LocalDate>() {
        @Override
        public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue) 
        {
            if (newValue == null)
            {
                return;
            }
            
            if (startDatePicker.getValue() == null || newValue.compareTo(startDatePicker.getValue()) <0)
            {
                startDatePicker.setValue(newValue);
            }
            endDatePicker.setValue(newValue);
        }
    };
    private HBox createDateFilterPanel(String caption, DatePicker datePicker)
    {
        Label lbl = new Label();
        lbl.setText(caption);
        lbl.setPrefWidth(40);
        lbl.setAlignment(Pos.BASELINE_RIGHT);
        HBox hBox = new HBox();
        hBox.getChildren().add(lbl);
        hBox.getChildren().add(datePicker);
        return hBox;
    }
    protected void filterByDatePicker()
    {
        if (startDatePicker.getValue() == null || endDatePicker.getValue() == null)
        {
            startDatePicker.setValue(null);
            endDatePicker.setValue(null);
            filterMap.remove(col);            
        }
        else
        {
            Calendar valueFilterStart = model.convertLocalDateToCalendar(startDatePicker.getValue());
            valueFilterStart.set(Calendar.MILLISECOND, 0);
            Calendar valueFilterEnd = model.convertLocalDateToCalendar(endDatePicker.getValue());
            valueFilterEnd.add(Calendar.DATE, 1);
            valueFilterEnd.set(Calendar.MILLISECOND, 0);
            
            filterMap.put(col, e -> {
                
                if (col.getCellData(e) == null)
                {
                    return false;
                }              
                    
                boolean isIncluded = false;
                try
                {
                    Calendar valueElement = (Calendar)col.getCellData(e);
                    if (valueElement.compareTo(valueFilterStart) >= 0 && valueElement.compareTo(valueFilterEnd) < 0)
                    {
                        isIncluded = true;
                    }
                    else
                    {
                        isIncluded = false;
                    }
                }
                catch(Exception ex)
                {
                    isIncluded = false;                        
                }
                return isIncluded;                
                    
            });
        }
        filter();
        hide();
    }
    
    private CustomMenuItem miCreateTreeView = null;
    private CustomMenuItem miTreeView = null;
    private TreeItem<CheckBox> rootTree = null;
    private void createMiTreeViewButton()
    {
        miCreateTreeView = new CustomMenuItem();
        miCreateTreeView.setOnAction(event -> onMiTreeViewButtonClick(event));
        miCreateTreeView.setHideOnClick(false);
        miCreateTreeView.setContent(new Label(selectItems));
        getItems().add(miCreateTreeView);
    }
    private void onMiTreeViewButtonClick(ActionEvent event)
    {
        this.setStyle("-fx-cursor: wait;");
        Object mi = event.getSource();
        getItems().remove(mi);
        createMiTreeView();
        this.setStyle("-fx-cursor: default;");
        
        // re-render context menu properly with new size
        isResetTreeView = false;
        this.hide();
        this.show();
        isResetTreeView = true;
    }
    private void createMiTreeView()
    {
        CheckBox cbAll = new CheckBox();
        cbAll.setText(all);
        cbAll.setOnAction(event -> onCbAllClick());
        cbAll.setSelected(true);
        rootTree = new TreeItem<>(cbAll);
        
        unfilteredList.stream()
                .map(e -> col.getCellData(e))
                .filter(e -> e != null)
                .distinct()
                .sorted()
                .forEach(e -> {
                    CheckBox cb = new CheckBox();
                    cb.setText(e.toString());
                    cb.setOnAction(event -> onCbClick(event));
                    cb.setSelected(true);
                    TreeItem<CheckBox> treeItem = new TreeItem<>();
                    treeItem.setValue(cb);
                    rootTree.getChildren().add(treeItem);                    
                });
        
        TreeView treeView = new TreeView(rootTree);
        treeView.setMaxHeight(200);
        treeView.setMinWidth(180);
        rootTree.setExpanded(true);
        
        Button btn = new Button();
        btn.setText(filter);
        btn.prefWidthProperty().bind(treeView.widthProperty());
        btn.setOnAction(event -> filterByTreeView());
        
        VBox vBox = new VBox();
        vBox.getChildren().add(treeView);
        vBox.getChildren().add(btn);
        
        miTreeView = new CustomMenuItem();
        miTreeView.setContent(vBox);
        miTreeView.setHideOnClick(false);
        
        getItems().add(miTreeView);        
    }
    /**
     * Select or unselect all checkboxes
     */
    private void onCbAllClick()
    {
        this.setStyle("-fx-cursor: wait");
        boolean cbState = rootTree.getValue().isSelected();
        rootTree.getChildren().parallelStream().forEach(e -> e.getValue().setSelected(cbState));
        this.setStyle("-fx-cursor: default");        
    }
    /**
     * Set 'all' checkbox to false
     */
    private void onCbClick(ActionEvent event)
    {
        if (!((CheckBox)event.getSource()).isSelected())
        {
            rootTree.getValue().setSelected(false);
        }
    }
    protected void filterByTreeView()
    {
        // create deep copy of rootTree to make filter independent of future selections.
        final TreeItem<CheckBox> rootFilter = new TreeItem<>();
        rootFilter.setValue(new CheckBox());
        rootFilter.getValue().setSelected(rootTree.getValue().isSelected());
        rootTree.getChildren().parallelStream().forEach(ti -> {
            
            final CheckBox cb = new CheckBox();
            cb.setText(ti.getValue().getText());
            cb.setSelected(ti.getValue().isSelected());
            final TreeItem<CheckBox> treeItem = new TreeItem<>(cb);
            synchronized(rootFilter)
            {
                rootFilter.getChildren().add(treeItem);
            }
        });
        
        if (rootFilter.getValue().isSelected()) // check if 'all' is selected
        {
            filterMap.remove(col);
        }
        else
        {
            filterMap.put(col, e -> {
                
                if (col.getCellData(e) == null)
                {
                    return false;
                }
                else
                {
                    boolean isIncluded = false;
                    try
                    {
                        isIncluded = rootFilter.getChildren().parallelStream()
                                // find tree item whichs checkbox has same text as string of cell data
                                .filter(ti -> ti.getValue().getText().equals(col.getCellData(e).toString()))
                                .findAny()
                                .get() // get tree item
                                .getValue() // get checkbox
                                .isSelected();
                    }
                    catch(Exception ex)
                    {
                        isIncluded = false;
                    }
                    return isIncluded;
                }

            });
        }
        filter();
        hide();
    }
    public void resetCheckBoxes()
    {
        if (rootTree != null)
        {
            rootTree.getValue().setSelected(true);
            onCbAllClick();
        }
    }
    
    
    protected void filter()
    {
        Predicate filter = null;
        if (filterMap.isEmpty())
        {
            filter = e -> true;
        }
        else
        {
            for(Predicate p : filterMap.values())
            {
                if (filter == null)
                {
                    filter = p;
                }
                else
                {
                    filter = filter.and(p);
                }
            }
        }
        
        TableView tv = col.getTableView();
        
        if (tv.getItems() instanceof FilteredList)
        {
            ((FilteredList)tv.getItems()).setPredicate(filter);            
        }
        else
        {
            tv.setItems(tv.getItems().filtered(filter));
        }
        
        // reset table view to sortable if no filter is available
        if (filterMap.isEmpty())
        {
            tv.setItems(unfilteredList.sorted());
            ((SortedList)tv.getItems()).comparatorProperty().bind(tv.comparatorProperty());
        }
        
        markFilteredColumnHeaders();
    }
    
    private final String filtered = "filtered"; // fx-CSS class
    private void markFilteredColumnHeaders()
    {
        // remove old colors
        col.getTableView().getColumns().stream().forEach(col-> ((TableColumn)col).getStyleClass().remove(filtered));
        
        // set color for currently filtreed columns
        filterMap.keySet().stream().forEach(col -> col.getStyleClass().add(filtered));
    }    
}
