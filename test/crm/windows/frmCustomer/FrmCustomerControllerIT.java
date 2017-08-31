package crm.windows.frmCustomer;

import crm.model.Customer;
import crm.model.EntityType;
import crm.model.Model;
import crm.navigation.Navigation;
import java.util.Calendar;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javax.persistence.EntityManager;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import testCases.TestCase;


public class FrmCustomerControllerIT 
{
    Model model = Model.getModel();
    private FrmCustomerController controller;
    private final int cid = model.getNewId(EntityType.CUSTOMER);
    private final Customer testCustomer = TestCase.createTestCustomer(cid);
    
    public static class TestApp extends Application
    {
        @Override
        public void start(Stage primaryStage) throws Exception
        {}
    }
    
    @BeforeClass
    public static void setUpClass() throws InterruptedException
    {
        // Initialize JavaFX
        Thread t = new Thread("JavafX TestApp Thread"){
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
    public void setUpController() throws Exception
    {
        Navigation nav = new Navigation();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(nav.fxmlFrmCustomer));
        loader.load();
        controller = loader.<FrmCustomerController>getController();
    }
    
    @After
    public void cleanUp()
    {
        Customer testCustomer = model.getCustomer(cid);
        if (testCustomer != null)
        {
            EntityManager em = model.getEntityManager();
            em.getTransaction().begin();
            em.remove(testCustomer);
            em.getTransaction().commit();
        }
    }
    
    @Test
    public void testNewSubmit()
    {
        // enter data
        controller.txtCompany.setText(testCustomer.getCompany());
        controller.txtAddress.setText(testCustomer.getAddress());
        controller.txtZip.setText(testCustomer.getZip());
        controller.txtCity.setText(testCustomer.getCity());
        controller.txtCountry.setText(testCustomer.getCountry());
        controller.txtContractId.setText(testCustomer.getContractId());
        controller.txtContractDate.setValue(model.convertCalendarToLocalDate(testCustomer.getContractDate()));
        
        // submit - ignore NullpPointerException caused by missing stage
        try
        {
            controller.submit();
        }
        catch(NullPointerException e)
        {}
        
        // validate
        Customer resultCustomer = model.getCustomer(cid);
        assertEquals(testCustomer.getCid(), resultCustomer.getCid());
        assertEquals(testCustomer.getCompany(), resultCustomer.getCompany());
        assertEquals(testCustomer.getAddress(), resultCustomer.getAddress());
        assertEquals(testCustomer.getZip(), resultCustomer.getZip());
        assertEquals(testCustomer.getCity(), resultCustomer.getCity());
        assertEquals(testCustomer.getCountry(),resultCustomer.getCountry());
        assertEquals(testCustomer.getContractId(), resultCustomer.getContractId());
        assertEquals(testCustomer.getContractDate().get(Calendar.YEAR), resultCustomer.getContractDate().get(Calendar.YEAR));
        assertEquals(testCustomer.getContractDate().get(Calendar.MONTH), resultCustomer.getContractDate().get(Calendar.MONTH));
        assertEquals(testCustomer.getContractDate().get(Calendar.DAY_OF_MONTH), resultCustomer.getContractDate().get(Calendar.DAY_OF_MONTH));        
    }
    
    @Test
    public void testEditSubmit()
    {
        // enter original data
        model.persist(testCustomer);
        controller.getCustomer().setCid(testCustomer.getCid());
        
        // create edited data
        Customer editedCustomer = TestCase.createTestCustomer(cid);
        editedCustomer.setCompany(testCustomer.getCompany() + "Edited");
        editedCustomer.setAddress(testCustomer.getAddress() + "Edited");
        editedCustomer.setZip(testCustomer.getZip() + "Edited");
        editedCustomer.setCity(testCustomer.getCity() + "Edied");
        editedCustomer.setCountry(testCustomer.getCountry() + "Edited");
        editedCustomer.setContractId(testCustomer.getContractId() + "Edited");
        editedCustomer.setContractDate(Calendar.getInstance());
        
        // enter edited data
        controller.txtCompany.setText(editedCustomer.getCompany());
        controller.txtAddress.setText(editedCustomer.getAddress());
        controller.txtZip.setText(editedCustomer.getZip());
        controller.txtCity.setText(editedCustomer.getCity());
        controller.txtCountry.setText(editedCustomer.getCountry());
        controller.txtContractId.setText(editedCustomer.getContractId());
        controller.txtContractDate.setValue(model.convertCalendarToLocalDate(editedCustomer.getContractDate()));
        
        // submit - ignore NullPointerException caused by missing stage
        try
        {
            controller.submit();
        }
        catch(NullPointerException e)
        {}
        
        // validate
        Customer resultCustomer = model.getCustomer(cid);
        assertEquals(editedCustomer.getCid(), resultCustomer.getCid());
        assertEquals(editedCustomer.getCompany(), resultCustomer.getCompany());
        assertEquals(editedCustomer.getAddress(), resultCustomer.getAddress());
        assertEquals(editedCustomer.getZip(), resultCustomer.getZip());
        assertEquals(editedCustomer.getCity(), resultCustomer.getCity());
        assertEquals(editedCustomer.getCountry(), resultCustomer.getCountry());
        assertEquals(editedCustomer.getContractId(), resultCustomer.getContractId());
        assertEquals(editedCustomer.getContractDate().get(Calendar.YEAR), resultCustomer.getContractDate().get(Calendar.YEAR));
        assertEquals(editedCustomer.getContractDate().get(Calendar.MONTH), resultCustomer.getContractDate().get(Calendar.MONTH));
        assertEquals(editedCustomer.getContractDate().get(Calendar.DAY_OF_MONTH), resultCustomer.getContractDate().get(Calendar.DAY_OF_MONTH));        
    }
    
}
