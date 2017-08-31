package crm.pages.cockpit;

import crm.model.ContactPerson;
import crm.model.Customer;
import crm.model.EntityType;
import crm.model.Model;
import crm.model.Note;
import crm.navigation.Navigation;
import java.text.SimpleDateFormat;
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


public class CockpitControllerIT 
{
    Model model = Model.getModel();
    
    private final int cid = model.getNewId(EntityType.CUSTOMER);
    private final int nid = model.getNewId(EntityType.NOTE);
    private final int cpid = model.getNewId(EntityType.CONTACT_PERSON);
    private final Customer testCustomer = TestCase.createTestCustomer(cid);
    private final Note testNote = TestCase.createTestNote(nid, cid);
    private final ContactPerson testCp = TestCase.createTestContactPerson(cpid, cid);
    private CockpitController controller;
    
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
    public void setUpTestData()
    {
        model.persist(testCustomer);
        model.persist(testNote);
        model.persist(testCp);
    }
    
    @Before
    public void setUpController() throws Exception
    {
        Navigation nav = new Navigation();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(nav.fxmlCockpit));
        loader.load();
        controller = loader.<CockpitController>getController();
    }
    
    @After
    public void cleanUp()
    {
        EntityManager em = model.getEntityManager();
        em.getTransaction().begin();
        em.remove(testNote);
        em.remove(testCp);
        em.remove(testCustomer);
        em.getTransaction().commit();
    }
    
    @Test
    public void testSearch() throws Exception
    {
        // execute search()
        controller.txtSearchBox.setText(Integer.toString(cid));
        controller.search();
        
        // validate date
        assertTrue(controller.tvContactPersons.getItems().contains(testCp));
        assertTrue(controller.tvNotes.getItems().contains(testNote));
        assertEquals(testCustomer.getCompany(), controller.lblCompanyValue.getText());
        assertTrue(testCustomer.getCid() == Integer.parseInt(controller.lblCidValue.getText().replace(".","").replace(",","")));
        assertEquals(testCustomer.getAddress(), controller.lblAddressValue.getText());
        assertEquals(testCustomer.getZip(), controller.lblZipValue.getText());
        assertEquals(testCustomer.getCity(), controller.lblCityValue.getText());
        assertEquals(testCustomer.getCountry(), controller.lblCountryValue.getText());
        assertEquals(testCustomer.getContractId(), controller.lblContractIdValue.getText());
        SimpleDateFormat sdf = new SimpleDateFormat(model.getMsg("dateFormat"));
        assertEquals(sdf.format(testCustomer.getContractDate().getTime()), controller.lblContractDateValue.getText());                
    }
    
}
