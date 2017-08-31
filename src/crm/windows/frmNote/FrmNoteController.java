package crm.windows.frmNote;

import crm.model.EntityType;
import crm.model.Model;
import crm.model.Note;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class FrmNoteController implements Initializable 
{
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        setLabels();
        setBindings();
    }    
    
    private void setBindings()
    {
        txtMemo.textProperty().bindBidirectional(note.memoProperty());
        txtCategory.textProperty().bindBidirectional(note.categoryProperty());
        txtAttachment.textProperty().bindBidirectional(note.attachmentProperty());
    }
    
    private void unbind()
    {
        txtMemo.textProperty().unbindBidirectional(note.memoProperty());
        txtCategory.textProperty().unbindBidirectional(note.categoryProperty());
        txtAttachment.textProperty().unbindBidirectional(note.attachmentProperty());
    }
    
    private void setLabels()
    {
        btnAttach.setText(model.getMsg("btnAttach"));
        btnRemove.setText(model.getMsg("btnRemove"));
        btnSubmit.setText(model.getMsg("btnSubmit"));
        lblMemo.setText(model.getMsg("lblMemo"));
        lblCategory.setText(model.getMsg("lblCategory"));
        lblAttachment.setText(model.getMsg("lblAttachment"));
    }
    
    @FXML
    public AnchorPane rootPane;
    
    @FXML
    public Label lblMemo;
    
    @FXML
    public TextArea txtMemo;
    
    @FXML
    public Label lblCategory;
    
    @FXML
    public TextField txtCategory;
        
    @FXML
    public Label lblAttachment;
    
    @FXML
    public TextField txtAttachment;
    
    @FXML
    public Button btnAttach;
            
    @FXML
    public Button btnRemove;
    
    @FXML
    public Button btnSubmit;
    
    Model model = Model.getModel();
    
    private Note note = new Note();
    public void setNote(Note note)
    {
        // Add handler to refresh model if window is canceled
        // Cannot be set in initialize() method because window does not exist at that point in life cycle.
        // Only needed if user cancels window to show old values (reset persistence context to state of database).
        rootPane.getScene().getWindow().addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, (event) -> model.refresh());
        
        unbind();
        this.note = note;
        setBindings();
    }
    public Note getNote()
    {
        return note;
    }
    
    public void submit()
    {
        copyAttachment();
        
        if (note.getCreatedBy() == null){note.setCreatedBy(System.getenv("username"));}
        if (note.getEntryDate() == null){note.setEntryDate(Calendar.getInstance());}        
        
        if (note.getId() == 0)
        {
            Integer id = model.getNewId(EntityType.NOTE);
            note.setId(id);
            model.persist(note);
        }
        else
        {
            model.merge(note);
        }
        
        model.refresh();
        ((Stage)rootPane.getScene().getWindow()).close();
    }
    
    private Path pathSrcAttachment;
    public void setPathSrcAttachment(Path value)
    {
        pathSrcAttachment = value;
    }
    public Path getPathSrcAttachment()
    {
        return pathSrcAttachment;
    }
    
    public void copyAttachment()
    {
        if (getPathSrcAttachment() != null) // only copies new files as path is not set when existing note is loaded
        {
            boolean isCanceled = false;
            File pathDst = Paths.get(model.getConfig().getProperty("customersFolder"), note.getCid().toString()).toFile();
            if (!pathDst.exists())
            {
                pathDst.mkdir();
            }
            pathDst = pathDst.toPath().resolve(note.getAttachment()).toFile();
            if (pathDst.exists())
            {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, model.getMsg("msgFileAlreadyExists"), ButtonType.YES, ButtonType.NO);
                alert.setTitle(model.getMsg("captionFileAlreadyExists"));
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.YES)
                {
                    pathDst.delete();
                }
                else
                {
                    isCanceled = true;
                }
            }
            if (!isCanceled)
            {
                final Path src = getPathSrcAttachment();
                final Path dst = pathDst.toPath();                
                Thread t = new Thread(() -> {
                    try
                    {
                        Files.copy(src, dst);
                    }
                    catch(IOException e)
                    {
                        Model.displayError(e);
                    }
                });
                t.setDaemon(false);
                Platform.runLater(t);
            }
        }
    }
    
    public void removeAttachment()
    {
        note.setAttachment(null);
        setPathSrcAttachment(null);
    }
    
    public void attachFile()
    {
        FileChooser chooser = new FileChooser();
        File file = chooser.showOpenDialog(rootPane.getScene().getWindow());
        
        if (file != null && file.exists())
        {
            note.setAttachment(file.getName());
            setPathSrcAttachment(file.toPath());
        }        
    }

}
    

