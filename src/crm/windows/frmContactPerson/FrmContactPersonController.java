package crm.windows.frmContactPerson;

import crm.model.ContactPerson;
import crm.model.EntityType;
import crm.model.Model;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.StringConverter;


public class FrmContactPersonController implements Initializable 
{
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        setLabels();
        setCmbGender();
        setBindings();
    }    
    
    private void setBindings()
    {
        txtForename.textProperty().bindBidirectional(contactPerson.forenameProperty());
        txtSurname.textProperty().bindBidirectional(contactPerson.surnameProperty());
        txtEmail.textProperty().bindBidirectional(contactPerson.emailProperty());
        txtPhone.textProperty().bindBidirectional(contactPerson.phoneProperty());
        cbMainContact.selectedProperty().bindBidirectional(contactPerson.mainContactProperty());
        
        
        listenerGenderPropertyChanged = new ChangeListener<String>(){
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
            {
                Gender gender = cmbGender.getItems().stream().filter(e -> {return e.getValue().equals(newValue);}).findFirst().orElse(null);
                cmbGender.getSelectionModel().select(gender);
            }
        };
        contactPerson.genderProperty().addListener(listenerGenderPropertyChanged);
                
        listenerCmbGenderChanged = new ChangeListener<Gender>(){
            @Override
            public void changed(ObservableValue<? extends Gender> observable, Gender oldValue, Gender newValue)
            {
                contactPerson.setGender(newValue.getValue());
            }
        };
        cmbGender.valueProperty().addListener(listenerCmbGenderChanged);                
    }
        
    private void unbind()
    {
        txtForename.textProperty().unbindBidirectional(contactPerson.forenameProperty());
        txtSurname.textProperty().unbindBidirectional(contactPerson.surnameProperty());
        txtEmail.textProperty().unbindBidirectional(contactPerson.emailProperty());
        txtPhone.textProperty().unbindBidirectional(contactPerson.phoneProperty());
        cbMainContact.selectedProperty().unbindBidirectional(contactPerson.mainContactProperty());
        
        contactPerson.genderProperty().removeListener(listenerGenderPropertyChanged);
        cmbGender.valueProperty().removeListener(listenerCmbGenderChanged);
    }
    
    private void setLabels()
    {
        lblForename.setText(model.getMsg("lblForename"));
        lblSurname.setText(model.getMsg("lblSurname"));
        lblGender.setText(model.getMsg("lblGender"));
        lblEmail.setText(model.getMsg("lblEmail"));
        lblPhone.setText(model.getMsg("lblPhone"));
        cbMainContact.setText(model.getMsg("cbMainContact"));
        btnSubmit.setText(model.getMsg("btnSubmit"));
    }
    
    @FXML
    public AnchorPane rootPane;
    
    @FXML
    public Label lblForename;
    
    @FXML
    public TextField txtForename;
    
    @FXML
    public Label lblSurname;
    
    @FXML
    public TextField txtSurname;
    
    @FXML
    public Label lblGender;
    
    @FXML
    public ComboBox<Gender> cmbGender;
    
    @FXML
    public Label lblEmail;
    
    @FXML
    public TextField txtEmail;
    
    @FXML
    public Label lblPhone;
    
    @FXML
    public TextField txtPhone;
    
    @FXML
    public CheckBox cbMainContact;
    
    @FXML
    public Button btnSubmit;
    
    Model model = Model.getModel();
    
    private ContactPerson contactPerson = new ContactPerson();
    public void setContactPerson(ContactPerson contactPerson)
    {
        // Add handler to refresh model if window is canceled
        // Cannot be set in initialize() method because window does not exist at that point in life cycle.
        // Only needed if user cancels window to show old values (reset persistence context to state of database).
        rootPane.getScene().getWindow().addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, (event) -> model.refresh());
        
        unbind();
        this.contactPerson = contactPerson;
        setBindings();
        
        // set selected gender
        Gender g = new Gender();
        g.setValue(contactPerson.getGender());
        cmbGender.getSelectionModel().select(g);
    }
    public ContactPerson getContactPerson()
    {
        return contactPerson;
    }
    
    private ChangeListener<String> listenerGenderPropertyChanged;
    private ChangeListener<Gender> listenerCmbGenderChanged;
    
    private void setCmbGender()
    {
        Gender m = new Gender();
        m.setValue("m");
        cmbGender.getItems().add(m);
        Gender f = new Gender();
        f.setValue("f");
        cmbGender.getItems().add(f);
        
        cmbGender.setConverter(new StringConverter<Gender>(){
            @Override
            public String toString(Gender object)
            {
                return object.getCaption();
            }
            
            @Override
            public Gender fromString(String string)
            {
                Gender gender = new Gender();
                gender.setCaption(string);
                return gender;
            }
        });
    }
    
    public class Gender
    {
        private final String male = model.getMsg("male");
        private final String female = model.getMsg("female");
        
        private String value;
        public String getValue()
        {
            return value;
        }
        public void setValue(String value)
        {
            if (!value.equals("m") && !value.equals("f"))
            {
                return;
            }
            this.value = value;
            caption = value.equals("m") ? male : female;                
        }
        
        private String caption;
        public String getCaption()
        {
            return caption;
        }
        public void setCaption(String caption)
        {
            if (!caption.equals(male) && !caption.equals(female))
            {
                return;
            }
            this.caption = caption;
            value = caption.equals(male) ? "m" : "f";
        }                
    }
    
    public void submit()
    {
        if (contactPerson.getId() == 0)
        {
            contactPerson.setId(model.getNewId(EntityType.CONTACT_PERSON));
            model.persist(contactPerson);
        }
        else
        {
            model.merge(contactPerson);
        }
        
        model.refresh();
        ((Stage)rootPane.getScene().getWindow()).close();            
    }
    
}
