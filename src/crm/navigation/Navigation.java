package crm.navigation;

import crm.model.Model;
import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class Navigation 
{
    private final double width = 450;
    private final double height = 450;
    
    public final String fxmlStart = "/crm/pages/start/Start.fxml";
    public final String fxmlCockpit = "/crm/pages/cockpit/Cockpit.fxml";
    public final String fxmlListOfCustomers = "/crm/pages/listOfCustomers/ListOfCustomers.fxml";
    public final String fxmlFrmContactPerson = "/crm/windows/frmContactPerson/FrmContactPerson.fxml";
    public final String fxmlFrmCustomer = "/crm/windows/frmCustomer/FrmCustomer.fxml";
    public final String fxmlFrmNote = "/crm/windows/frmNote/FrmNote.fxml";
    
    
    public void openWindow(String pathFxml, String title)
    {
        try
        {
            URL url = getClass().getResource(pathFxml);
            Parent root = FXMLLoader.load(url);
            Stage stage = new Stage();
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/crm/resources/crm.png")));
            stage.setScene(new Scene(root, width, height));
            stage.setTitle(title);
            stage.show();
        }
        catch(IOException e)
        {
            Model.displayError(e);
        }
    }
    
    public <T> T openWindowGetController(String pathFxml, String title)
    {
        try
        {
            FXMLLoader loader  = new FXMLLoader(getClass().getResource(pathFxml));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/crm/resources/crm.png")));
            stage.setScene(new Scene(root, width, height));
            stage.setTitle(title);
            stage.show();
            return loader.<T>getController();
        }
        catch(IOException e)
        {
            Model.displayError(e);
            return null;
        }
    }
    
    /**
     * Replaces root in current Scene with root generated from FXML file.
     * @param rootPane used to get Scene
     * @param pathFxml format: /pagkage/Filename.fxml
     */
    public void navTo(Parent rootPane, String pathFxml)
    {
        try
        {
            Parent root = FXMLLoader.load(getClass().getResource(pathFxml));
            rootPane.getScene().setRoot(root);
        }
        catch(IOException e)
        {
            Model.displayError(e);
        }
    }
    
    public <T> T navToGetController(Parent rootPane, String pathFxml)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(pathFxml));
            Parent root = loader.load();
            rootPane.getScene().setRoot(root);
            return loader.<T>getController();
        }
        catch(IOException e)
        {
            Model.displayError(e);
            return null;
        }
    }    
}
