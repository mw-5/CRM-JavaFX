package crm.windows.frmCustomer;

import crm.model.Customer;
import crm.model.EntityType;
import crm.model.Model;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class FrmCustomerController implements Initializable
{
    /**
     * initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        setLabels();
        setBindings();
    }
    
    private void setBindings()
    {
        txtCid.textProperty().bindBidirectional(customer.cidProperty(), new DecimalFormat());
        txtCompany.textProperty().bindBidirectional(customer.companyProperty());
        txtAddress.textProperty().bindBidirectional(customer.addressProperty());
        txtZip.textProperty().bindBidirectional(customer.zipProperty());
        txtCity.textProperty().bindBidirectional(customer.cityProperty());
        txtCountry.textProperty().bindBidirectional(customer.countryProperty());
        txtContractId.textProperty().bindBidirectional(customer.contractIdProperty());
        
        listenerTxtContractDateChanged = new ChangeListener<LocalDate>(){
            @Override
            public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue, LocalDate newValue)
            {
                customer.setContractDate(model.convertLocalDateToCalendar(newValue));                
            }
        };
        txtContractDate.valueProperty().addListener(listenerTxtContractDateChanged);
        
        listenerContractDatePropertyChanged = new ChangeListener<Calendar>(){
            @Override
            public void changed(ObservableValue<? extends Calendar> observable, Calendar olValue, Calendar newValue)
            {
                txtContractDate.setValue(model.convertCalendarToLocalDate(newValue));
            }
        };
        customer.contractDateProperty().addListener(listenerContractDatePropertyChanged);
    }
    
    private void unbind()
    {
        txtCid.textProperty().unbindBidirectional(customer.cidProperty());
        txtCompany.textProperty().unbindBidirectional(customer.companyProperty());
        txtAddress.textProperty().unbindBidirectional(customer.addressProperty());
        txtZip.textProperty().unbindBidirectional(customer.zipProperty());
        txtCity.textProperty().unbindBidirectional(customer.cityProperty());
        txtCountry.textProperty().unbindBidirectional(customer.countryProperty());
        txtContractId.textProperty().unbindBidirectional(customer.contractIdProperty());
        txtContractDate.valueProperty().removeListener(listenerTxtContractDateChanged);
        customer.contractDateProperty().removeListener(listenerContractDatePropertyChanged);
    }
    
    private void setLabels()
    {
        lblCid.setText(model.getMsg("lblCid"));
        lblCompany.setText(model.getMsg("lblCompany"));
        lblAddress.setText(model.getMsg("lblAddress"));
        lblZip.setText(model.getMsg("lblZip"));
        lblCity.setText(model.getMsg("lblCity"));
        lblCountry.setText(model.getMsg("lblCountry"));
        lblContractId.setText(model.getMsg("lblContractId"));
        lblContractDate.setText(model.getMsg("lblContractDate"));
        btnSubmit.setText(model.getMsg("btnSubmit"));
    }
    
    private ChangeListener<Calendar> listenerContractDatePropertyChanged;
    private ChangeListener<LocalDate> listenerTxtContractDateChanged;
    
    @FXML
    public AnchorPane rootPane;
    
    @FXML
    public Label lblCid;
    
    @FXML
    public TextField txtCid;
    
    @FXML
    public Label lblCompany;
    
    @FXML
    public TextField txtCompany;
    
    @FXML
    public Label lblAddress;
    
    @FXML
    public TextField txtAddress;
    
    @FXML
    public Label lblZip;
    
    @FXML
    public TextField txtZip;
    
    @FXML
    public Label lblCity;
    
    @FXML
    public TextField txtCity;
    
    @FXML
    public Label lblCountry;
    
    @FXML
    public TextField txtCountry;
    
    @FXML
    public Label lblContractId;
    
    @FXML
    public TextField txtContractId;
    
    @FXML
    public Label lblContractDate;
    
    @FXML
    public DatePicker txtContractDate;
    
    @FXML
    public Button btnSubmit;
    
    Model model = Model.getModel();
    
    private Customer customer = new Customer();
    public void setCustomer(Customer customer)
    {
        // Add handler to refresh model if window is canceled
        // Cannot be set in initialize() method because window does not exist at that point in life cycle.
        // Only needed if user cancels window to show old values (reset persistence context to state of database).
        rootPane.getScene().getWindow().addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, (event) -> model.refresh());
        
        unbind();
        this.customer = customer;
        setBindings();
        
        txtContractDate.setValue(model.convertCalendarToLocalDate(customer.getContractDate()));
    }
    public Customer getCustomer()
    {
        return customer;
    }
    
    public void submit()
    {
        if (customer.getCid() == 0)
        {
            customer.setCid(model.getNewId(EntityType.CUSTOMER));
            model.persist(customer);
        }
        else
        {
            model.merge(customer);
        }
        
        model.refresh();
        ((Stage)rootPane.getScene().getWindow()).close();
    }
}


