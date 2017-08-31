package crm.pages.listOfCustomers;

import crm.model.Customer;
import crm.model.Model;
import crm.navigation.FilterContextMenu;
import crm.navigation.Navigation;
import crm.pages.cockpit.CockpitController;
import crm.util.Utils;
import crm.windows.frmCustomer.FrmCustomerController;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;        


public class ListOfCustomersController implements Initializable 
{
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        setLabels();
        setCellValueFactories();
        setContextMenus();
        model.addRefreshCallback(() -> loadTvCustomers());
        loadTvCustomers();
    }    
    
    private void setCellValueFactories()
    {
        colCid.setCellValueFactory(new PropertyValueFactory<Customer, String>("cid"));
        colCompany.setCellValueFactory(new PropertyValueFactory<Customer, String>("company"));
        colAddress.setCellValueFactory(new PropertyValueFactory<Customer, String>("address"));
        colZip.setCellValueFactory(new PropertyValueFactory<Customer, String>("zip"));
        colCity.setCellValueFactory(new PropertyValueFactory<Customer, String>("city"));
        colCountry.setCellValueFactory(new PropertyValueFactory<Customer, String>("country"));
        colContractId.setCellValueFactory(new PropertyValueFactory<Customer, String>("contractId"));
        
        colContractDate.setCellValueFactory(new PropertyValueFactory<Customer, Calendar>("contractDate"));
        final SimpleDateFormat dateFormat = new SimpleDateFormat(model.getMsg("dateFormat"));
        colContractDate.setCellFactory(col -> new TableCell<Customer, Calendar>(){
           @Override
           public void updateItem(Calendar item, boolean empty)
           {
               super.updateItem(item, empty);
               if (empty || item == null)
               {
                   setText(null);
               }
               else
               {
                   setText(dateFormat.format(item.getTime()));
               }
           }
        });        
    }
    
    private void setContextMenus()
    {
        colCid.setContextMenu(new FilterContextMenu(colCid, filterMap, listCustomers));
        colCompany.setContextMenu(new FilterContextMenu(colCompany, filterMap, listCustomers));
        colAddress.setContextMenu(new FilterContextMenu(colAddress, filterMap, listCustomers));
        colZip.setContextMenu(new FilterContextMenu(colZip, filterMap, listCustomers));
        colCity.setContextMenu(new FilterContextMenu(colCity, filterMap, listCustomers));
        colCountry.setContextMenu(new FilterContextMenu(colCountry, filterMap, listCustomers));
        colContractId.setContextMenu(new FilterContextMenu(colContractId, filterMap, listCustomers));
        colContractDate.setContextMenu(new FilterContextMenu(colContractDate, filterMap, listCustomers, true));
    }
    private Map<TableColumn, Predicate> filterMap = new HashMap<>();
    final private ObservableList listCustomers = FXCollections.observableArrayList();
    
    private void setLabels()
    {
        lblHeader.setText(model.getMsg("lblListOfCustomers"));
        btnGoToStart.setText(model.getMsg("btnGoToStart"));
        btnShowCustomerInCockpit.setText(model.getMsg("btnShowCustomerInCockpit"));
        btnNewCustomer.setText(model.getMsg("btnNewCustomer"));
        btnEditCustomer.setText(model.getMsg("btnEditCustomer"));
        lblHintContextMenu.setText(model.getMsg("hintFiltering"));
        colCid.setText(model.getMsg("colCid"));
        colCompany.setText(model.getMsg("colCompany"));
        colAddress.setText(model.getMsg("colAddress"));
        colZip.setText(model.getMsg("colZip"));
        colCity.setText(model.getMsg("colCity"));
        colCountry.setText(model.getMsg("colCountry"));
        colContractId.setText(model.getMsg("colContractId"));
        colContractDate.setText(model.getMsg("colContractDate"));
    }
    
    @FXML
    public AnchorPane rootPane;
    
    @FXML
    public Label lblHeader;
    
    @FXML
    public Button btnGoToStart;
    
    @FXML
    public Button btnShowCustomerInCockpit;
    
    @FXML
    public Button btnNewCustomer;
    
    @FXML
    public Button btnEditCustomer;
    
    @FXML
    public TableView tvCustomers;
    
    @FXML
    public TableColumn<Customer, String> colCid;
    
    @FXML
    public TableColumn<Customer, String> colCompany;
    
    @FXML
    public TableColumn<Customer, String> colAddress;
    
    @FXML
    public TableColumn<Customer, String> colZip;
    
    @FXML
    public TableColumn<Customer, String> colCity;
    
    @FXML
    public TableColumn<Customer, String> colCountry;
    
    @FXML
    public TableColumn<Customer, String> colContractId;
    
    @FXML
    public TableColumn<Customer, Calendar> colContractDate;
    
    @FXML
    public Label lblHintContextMenu;
    
    Model model = Model.getModel();
    Navigation nav = new Navigation();
    
    public void navToStart()
    {
        nav.navTo(rootPane, nav.fxmlStart);
    }
    
    public void showCustomerInCockpit()
    {
        Customer customer = (Customer)tvCustomers.getSelectionModel().getSelectedItem();
        if (customer != null)
        {
            CockpitController c = nav.<CockpitController>navToGetController(rootPane, nav.fxmlCockpit);
            c.setCustomer(customer);
        }
        else
        {
            new Alert(AlertType.INFORMATION, model.getMsg("selecteCustomer"), ButtonType.OK).show();
        }
    }
    
    public void openNewCustomer()
    {
        nav.openWindow(nav.fxmlFrmCustomer, model.getMsg("btnNewCustomer"));
    }
    
    public void openEditCustomer()
    {
        Customer customer = (Customer)tvCustomers.getSelectionModel().getSelectedItem();
        if (customer != null)
        {
            FrmCustomerController c = nav.<FrmCustomerController>openWindowGetController(nav.fxmlFrmCustomer, model.getMsg("btnEditCustomer"));
            c.setCustomer(customer);            
        }
        else
        {
            new Alert(AlertType.INFORMATION, model.getMsg("selectCustomer"), ButtonType.OK).show();
        }
    }
    
    private void loadTvCustomers()
    {
        listCustomers.setAll(model.getCustomers());
        Utils.updateTableView(tvCustomers, listCustomers);
    }    
}
