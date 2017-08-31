package crm.pages.start;

import crm.model.Model;
import crm.navigation.Navigation;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;


public class StartController implements Initializable 
{
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        setLabels();
        setAnimation();
    }    
    
    private void setLabels()
    {
        btnCockpit.setText(model.getMsg("btnCockpit"));
        btnCustomers.setText(model.getMsg("btnCustomers"));
        btnRefresh.setText(model.getMsg("btnRefresh"));
    }
    
    @FXML
    public AnchorPane rootPane;
    
    @FXML
    public Rectangle rctCockpit;
    
    @FXML
    public Button btnCockpit;
    
    @FXML
    public Rectangle rctCustomers;
    
    @FXML
    public Button btnCustomers;
    
    @FXML
    public Rectangle rctRefresh;
    
    @FXML
    public Button btnRefresh;
    
    Model model = Model.getModel();
    Navigation nav = new Navigation();
    
    public void navToCockpit()
    {
        nav.navTo(rootPane, nav.fxmlCockpit);
    }
    
    public void navToListOfCustomers()
    {
        nav.navTo(rootPane, nav.fxmlListOfCustomers);
    }
    
    public void refresh()
    {
        model.refresh();
    }
    
    private void setAnimation()
    {
        if (model.isAnimated())
        {
            rctCockpit.setVisible(false);
            rctCustomers.setVisible(false);
            rctRefresh.setVisible(false);
            Timer timer = new Timer();
            timer.schedule(new TimerTask(){
                @Override
                public void run()
                {
                    Platform.runLater(() -> animate());
                }
            }, 1000);
        }
    }
    
    private void animate()
    {
        Duration aniDuration = new Duration(1000);
        
        TranslateTransition transCockpit = new TranslateTransition(aniDuration, rctCockpit);
        transCockpit.setFromX(2000);
        transCockpit.setToX(0);;
        transCockpit.setInterpolator(Interpolator.LINEAR);
        transCockpit.setCycleCount(1);
        
        TranslateTransition transCustomers = new TranslateTransition(aniDuration, rctCustomers);
        transCustomers.setFromX(2000);
        transCustomers.setToX(0);
        transCustomers.setFromY(1100);
        transCustomers.setToY(0);
        transCustomers.setInterpolator(Interpolator.LINEAR);
        transCustomers.setCycleCount(1);
        
        TranslateTransition transRefresh = new TranslateTransition(aniDuration, rctRefresh);
        transRefresh.setFromY(1100);
        transRefresh.setToY(0);
        transRefresh.setInterpolator(Interpolator.LINEAR);
        transRefresh.setCycleCount(1);
        
        rctCockpit.setVisible(true);
        rctCustomers.setVisible(true);
        rctRefresh.setVisible(true);
        
        transCockpit.play();
        transCustomers.play();
        transRefresh.play();
        
        model.setAnimated(false);
    }
    
}
