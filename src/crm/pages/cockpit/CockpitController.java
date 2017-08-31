package crm.pages.cockpit;

import crm.model.ContactPerson;
import crm.model.Customer;
import crm.model.Model;
import crm.model.Note;
import crm.navigation.FilterContextMenu;
import crm.navigation.Navigation;
import crm.util.Utils;
import crm.windows.frmContactPerson.FrmContactPersonController;
import crm.windows.frmNote.FrmNoteController;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import java.util.Calendar;
import java.util.HashMap;
import java.util.function.Predicate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.control.TableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;



public class CockpitController implements Initializable 
{
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        setLabels();
        setBindings();
        setCellValueFactories();
        setContextMenus();
        model.addRefreshCallback(() -> loadTvContactPersons());
        model.addRefreshCallback(() -> loadTvNotes());
    }    
    
    private void setBindings()
    {
        lblCompanyValue.textProperty().bindBidirectional(customer.companyProperty());
        lblCidValue.textProperty().bindBidirectional(customer.cidProperty(), new DecimalFormat());
        lblAddressValue.textProperty().bindBidirectional(customer.addressProperty());
        lblZipValue.textProperty().bindBidirectional(customer.zipProperty());
        lblCityValue.textProperty().bindBidirectional(customer.cityProperty());
        lblCountryValue.textProperty().bindBidirectional(customer.countryProperty());
        lblContractIdValue.textProperty().bindBidirectional(customer.contractIdProperty());
        lblContractDateValue.textProperty().bindBidirectional(customer.contractDateProperty(), model.getConverterStringCalendar());
    }
    
    private void unbind()
    {
        lblCompanyValue.textProperty().unbindBidirectional(customer.companyProperty());
        lblCidValue.textProperty().unbindBidirectional(customer.cidProperty());
        lblAddressValue.textProperty().unbindBidirectional(customer.addressProperty());
        lblZipValue.textProperty().unbindBidirectional(customer.zipProperty());
        lblCityValue.textProperty().unbindBidirectional(customer.cityProperty());
        lblCountryValue.textProperty().unbindBidirectional(customer.countryProperty());
        lblContractIdValue.textProperty().unbindBidirectional(customer.contractIdProperty());
        lblContractDateValue.textProperty().unbindBidirectional(customer.contractDateProperty());
    }
    
    private void setCellValueFactories()
    {
        colCpId.setCellValueFactory(new PropertyValueFactory<ContactPerson, String>("id"));
        colCpCid.setCellValueFactory(new PropertyValueFactory<ContactPerson, String>("cid"));
        colForename.setCellValueFactory(new PropertyValueFactory<ContactPerson, String>("forename"));
        colSurname.setCellValueFactory(new PropertyValueFactory<ContactPerson, String>("surname"));
        colGender.setCellValueFactory(new PropertyValueFactory<ContactPerson, String>("gender"));
        colEmail.setCellValueFactory(new PropertyValueFactory<ContactPerson, String>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<ContactPerson, String>("phone"));
        
        colMainContact.setCellValueFactory(new PropertyValueFactory<ContactPerson, Boolean>("mainContact"));
        colMainContact.setCellFactory(col -> new TableCell<ContactPerson, Boolean>(){
            @Override
            protected void updateItem(Boolean item, boolean empty)
            {
                super.updateItem(item, empty);
                if (empty)
                {
                    setText(null);
                }
                else
                {
                    setText(item.toString());
                    if (item == true)
                    {
                        getTableRow().setText("-fx-font-weight: bold;");
                    }
                    else
                    {
                        getTableRow().setStyle("-fx-font-weight: normal");
                    }
                }
                    
            }
        });
        
        colNoteId.setCellValueFactory(new PropertyValueFactory<Note, String>("id"));
        colNoteCid.setCellValueFactory(new PropertyValueFactory<Note, String>("cid"));
        colCreatedBy.setCellValueFactory(new PropertyValueFactory<Note, String>("createdBy"));
        
        colEntryDate.setCellValueFactory(new PropertyValueFactory<Note, Calendar>("entryDate"));
        final SimpleDateFormat dateFormat = new SimpleDateFormat(model.getMsg("dateFormat"));
        colEntryDate.setCellFactory(col -> new TableCell<Note, Calendar>(){
            @Override
            public void updateItem(Calendar item, boolean empty)
            {
                super.updateItem(item, empty);
                if (empty)
                {
                    setText(null);
                }
                else
                {
                    setText(dateFormat.format(item.getTime()));
                }
            }
        });
        
        colMemo.setCellValueFactory(new PropertyValueFactory<Note, String>("memo"));
        colCategory.setCellValueFactory(new PropertyValueFactory<Note, String>("category"));
        
        colAttachment.setCellValueFactory(new PropertyValueFactory<Note, String>("attachment"));
        colAttachment.setCellFactory(col -> new TableCell<Note, String>(){
            @Override
            protected void updateItem(String item, boolean empty)
            {
                super.updateItem(item, empty);
                if (item == null)
                {
                    setText(null);                   
                }
                else
                {
                    setText(item);
                    setStyle("-fx-text-fill: blue; -fx-underline: true;");
                    final String fileName = item;
                    addEventHandler(MouseEvent.MOUSE_ENTERED, event -> setCursor(Cursor.HAND));
                    addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {openAttachment(fileName); event.consume();});
                    addEventHandler(MouseEvent.MOUSE_EXITED, event -> setCursor(Cursor.DEFAULT));
                }                        
            }
        });
    }
    
    private void setContextMenus()
    {
        colCpCid.setContextMenu(new FilterContextMenu(colCpCid, filterMapContactPersons, listContactPersons));
        colForename.setContextMenu(new FilterContextMenu(colForename, filterMapContactPersons, listContactPersons));
        colSurname.setContextMenu(new FilterContextMenu(colSurname, filterMapContactPersons, listContactPersons));
        colGender.setContextMenu(new FilterContextMenu(colGender, filterMapContactPersons, listContactPersons));
        colEmail.setContextMenu(new FilterContextMenu(colEmail, filterMapContactPersons, listContactPersons));
        colPhone.setContextMenu(new FilterContextMenu(colPhone, filterMapContactPersons, listContactPersons));
        colMainContact.setContextMenu(new FilterContextMenu(colMainContact, filterMapContactPersons, listContactPersons));
        
        colNoteId.setContextMenu(new FilterContextMenu(colNoteId, filterMapNotes, listNotes));
        colCreatedBy.setContextMenu(new FilterContextMenu(colCreatedBy, filterMapNotes, listNotes));
        colEntryDate.setContextMenu(new FilterContextMenu(colEntryDate, filterMapNotes, listNotes, true));
        colMemo.setContextMenu(new FilterContextMenu(colMemo, filterMapNotes, listNotes));
        colCategory.setContextMenu(new FilterContextMenu(colCategory, filterMapNotes, listNotes));
        colAttachment.setContextMenu(new FilterContextMenu(colAttachment, filterMapNotes, listNotes));
    }
    private HashMap<TableColumn, Predicate> filterMapContactPersons = new HashMap<>();
    private HashMap<TableColumn, Predicate> filterMapNotes = new HashMap<>();
    final private ObservableList listContactPersons = FXCollections.observableArrayList();
    final private ObservableList listNotes = FXCollections.observableArrayList();
    
    private void loadTvContactPersons()
    {
        if (customer != null && customer.getCid() >0)
        {
            model.refreshEntity(customer);
            listContactPersons.setAll(FXCollections.observableArrayList(customer.getContactPersonsCollection()).sorted( (cp1, cp2) -> cp1.getId() - cp2.getId() ));
            Utils.updateTableView(tvContactPersons, listContactPersons);            
        }
    }
    
    private void loadTvNotes()
    {
        if (customer != null && customer.getCid() > 0)
        {
            model.refreshEntity(customer);
            listNotes.setAll(FXCollections.observableArrayList(customer.getNotesCollection()).sorted( (n1, n2) -> n2.getId() - n1.getId() ));
            Utils.updateTableView(tvNotes, listNotes);
        }
    }
    
    private void setLabels()
    {
        btnGoToStart.setText(model.getMsg("btnGoToStart"));
        btnGoToListOfCustomers.setText(model.getMsg("btnGoToListOfCustomers"));
        btnNewContactPerson.setText(model.getMsg("btnNewContactPerson"));
        btnEditContactPerson.setText(model.getMsg("btnEditContactPerson"));
        btnNewNote.setText(model.getMsg("btnNewNote"));
        btnEditNote.setText(model.getMsg("btnEditNote"));
        btnOpenCustomerFolder.setText(model.getMsg("btnOpenCustomerFolder"));
        lblCid.setText(model.getMsg("lblCid"));
        lblAddress.setText(model.getMsg("lblAddress"));
        lblZip.setText(model.getMsg("lblZip"));
        lblCity.setText(model.getMsg("lblCity"));
        lblCountry.setText(model.getMsg("lblCountry"));
        lblContractId.setText(model.getMsg("lblContractId"));
        lblContractDate.setText(model.getMsg("lblContractDate"));
        colForename.setText(model.getMsg("colForename"));
        colSurname.setText(model.getMsg("colSurname"));
        colGender.setText(model.getMsg("colGender"));
        colEmail.setText(model.getMsg("colEmail"));
        colPhone.setText(model.getMsg("colPhone"));
        colCreatedBy.setText(model.getMsg("colCreatedBy"));
        colEntryDate.setText(model.getMsg("colEntryDate"));
        colCategory.setText(model.getMsg("colCategory"));
        colMemo.setText(model.getMsg("colMemo"));
        colAttachment.setText(model.getMsg("colAttachment"));
    }
    
    
    @FXML
    public Button btnGoToStart;
    
    @FXML
    public Button btnGoToListOfCustomers;
    
    @FXML
    public AnchorPane rootPane;
    
    @FXML
    public Label lblCompanyValue;
    
    @FXML
    public TextField txtSearchBox;
    
    @FXML
    public Button btnSearch;
    
    @FXML
    public Label lblCid;
    
    @FXML
    public Label lblCidValue;
    
    @FXML
    public Label lblAddress;
    
    @FXML
    public Label lblAddressValue;
    
    @FXML
    public Label lblZip;
    
    @FXML
    public Label lblZipValue;
    
    @FXML
    public Label lblCity;
    
    @FXML
    public Label lblCityValue;
    
    @FXML
    public Label lblCountry;
    
    @FXML
    public Label lblCountryValue;
    
    @FXML
    public Label lblContractId;
    
    @FXML
    public Label lblContractIdValue;
    
    @FXML
    public Label lblContractDate;
    
    @FXML
    public Label lblContractDateValue;
    
    @FXML
    public TableView tvContactPersons;
    
    @FXML
    public TableColumn<ContactPerson, String> colCpId;
    
    @FXML
    public TableColumn<ContactPerson, String> colCpCid;
    
    @FXML
    public TableColumn<ContactPerson, String> colForename;
    
    @FXML
    public TableColumn<ContactPerson, String> colSurname;
    
    @FXML
    public TableColumn<ContactPerson, String> colGender;
    
    @FXML
    public TableColumn<ContactPerson, String> colEmail;
    
    @FXML
    public TableColumn<ContactPerson, String> colPhone;
    
    @FXML
    public TableColumn<ContactPerson, Boolean> colMainContact;
    
    @FXML
    public Button btnNewContactPerson;
    
    @FXML
    public Button btnEditContactPerson;
    
    @FXML
    public TableView tvNotes;
    
    @FXML
    public TableColumn<Note, String> colNoteId;
    
    @FXML
    public TableColumn<Note, String> colNoteCid;
    
    @FXML
    public TableColumn<Note, String> colCreatedBy;
    
    @FXML
    public TableColumn<Note, Calendar> colEntryDate;
    
    @FXML
    public TableColumn<Note, String> colMemo;
    
    @FXML
    public TableColumn<Note, String> colCategory;
    
    @FXML
    public TableColumn<Note, String> colAttachment;
    
    @FXML
    public Button btnNewNote;
    
    @FXML
    public Button btnEditNote;
    
    @FXML
    public Button btnOpenCustomerFolder;
            
    Model model = Model.getModel();
    
    Navigation nav = new Navigation();
    
    private Customer customer = new Customer();
    public void setCustomer(Customer customer)
    {
        unbind();
        this.customer = customer;
        setBindings();
        loadTvContactPersons();
        loadTvNotes();
    }
    public Customer getCustomer()
    {
        return customer;
    }
        
    public void search()
    {
        int cid;
        
        try
        {
            cid = Integer.valueOf(txtSearchBox.getText());
        }
        catch(NumberFormatException e)
        {
            new Alert(Alert.AlertType.ERROR, model.getMsg("invalidValueEntered"), ButtonType.OK).show();
            return;
        }
        
        Customer c = model.getCustomer(cid);
        if (c == null)
        {
            new Alert(Alert.AlertType.INFORMATION, model.getMsg("customerNotFound"), ButtonType.OK).show();
        }
        else
        {
            setCustomer(c);
        }
    }
            
    public void navToStart()
    {
        nav.navTo(rootPane, nav.fxmlStart);
    }
    
    public void navToListOfCustomers()
    {
        nav.navTo(rootPane, nav.fxmlListOfCustomers);
    }
    
    public void openNewContactPerson()
    {
        if (customer.getCid() == 0)
        {
            new Alert(Alert.AlertType.INFORMATION, model.getMsg("selectCustomer"), ButtonType.OK).show();
            return;
        }
        FrmContactPersonController c = nav.<FrmContactPersonController>openWindowGetController(nav.fxmlFrmContactPerson, model.getMsg("btnNewContactPerson"));
        c.getContactPerson().setCid(customer.getCid());
    }
    
    public void openNewNote()
    {
        if (customer.getCid() == 0)
        {
            new Alert(Alert.AlertType.INFORMATION, model.getMsg("selectCustomer"), ButtonType.OK).show();
            return;            
        }
        FrmNoteController c = nav.<FrmNoteController>openWindowGetController(nav.fxmlFrmNote, model.getMsg("btnNewNote"));
        c.getNote().setCid(customer.getCid());
    }
    
    public void openEditNote()
    {
        Note note = (Note)tvNotes.getSelectionModel().getSelectedItem();
        if (note != null)
        {
            nav.<FrmNoteController>openWindowGetController(nav.fxmlFrmNote, model.getMsg("btnEditNote")).setNote(note);            
        }
        else
        {
            new Alert(Alert.AlertType.INFORMATION, model.getMsg("selectNote"), ButtonType.OK).show();
        }
    }
    
    public void openEditContactPerson()
    {
        ContactPerson cp = (ContactPerson)tvContactPersons.getSelectionModel().getSelectedItem();
        if (cp != null)
        {
            nav.<FrmContactPersonController>openWindowGetController(nav.fxmlFrmContactPerson, model.getMsg("btnEditContactPerson")).setContactPerson(cp);            
        }
        else
        {
            new Alert(Alert.AlertType.INFORMATION, model.getMsg("selectCp"), ButtonType.OK).show();
        }
    }
    
    
    public void openCustomerFolder()
    {
        try
        {
            Path path = Paths.get(model.getConfig().getProperty("customersFolder"), lblCidValue.getText());
            File file = path.toFile();
            if (!file.exists())
            {
                file.mkdir();
            }
            Desktop.getDesktop().open(file);
        }
        catch (IOException e)
        {
            Model.displayError(e);
        }
    }
    
    private void openAttachment(String fileName)
    {
        if (fileName != null)
        {
            Path path = Paths.get(model.getConfig().getProperty("customersFolder"), Integer.toString(customer.getCid()), fileName);
            try
            {
                Desktop.getDesktop().open(path.toFile());
            }
            catch(Exception e)
            {
                Model.displayError(e);
            }
        }
    }
                
}
