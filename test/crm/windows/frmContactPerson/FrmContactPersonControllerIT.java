package crm.windows.frmContactPerson;

import crm.model.ContactPerson;
import crm.model.Customer;
import crm.model.EntityType;
import crm.model.Model;
import crm.navigation.Navigation;
import crm.windows.frmContactPerson.FrmContactPersonController.Gender;
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


public class FrmContactPersonControllerIT 
{
    Model model = Model.getModel();
    private FrmContactPersonController controller;
    private final int cid = model.getNewId(EntityType.CUSTOMER);
    private final int id = model.getNewId(EntityType.CONTACT_PERSON);
    private final Customer testCustomer = TestCase.createTestCustomer(cid);
    private ContactPerson testCp;
    
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
    public void setUpData()
    {
        testCp = TestCase.createTestContactPerson(id, cid); // reset data of contact person
        model.persist(testCustomer);
    }
    
    @Before
    public void setUpController() throws Exception
    {
        Navigation nav = new Navigation();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(nav.fxmlFrmContactPerson));
        loader.load();
        controller = loader.<FrmContactPersonController>getController();
    }
    
    @After
    public void cleanUp()
    {
        EntityManager em = model.getEntityManager();
        em.getTransaction().begin();
        ContactPerson cp = em.find(ContactPerson.class, id);
        if (cp != null)
        {
            em.remove(cp);
        }
        Customer tesetCustomer = model.getCustomer(cid);
        if (testCustomer != null)
        {
            em.remove(testCustomer);
        }
        em.getTransaction().commit();
    }
    
    @Test
    public void testNewSubmit()
    {
        // entry data
        controller.getContactPerson().setCid(cid);
        controller.txtForename.setText(testCp.getForename());
        controller.txtSurname.setText(testCp.getSurname());
        
        Gender g = controller.new Gender();
        g.setValue(testCp.getGender());
        controller.cmbGender.setValue(g);
        
        controller.txtEmail.setText(testCp.getEmail());
        controller.txtPhone.setText(testCp.getPhone());
        controller.cbMainContact.setSelected(testCp.isMainContact());
        
        // submit - ignore NullPointerException caused by missing stage
        try
        {
            controller.submit();
        }
        catch(NullPointerException e)
        {}
        
        // validate
        ContactPerson resultCp = model.getEntityManager().find(ContactPerson.class, id);
        assertEquals(testCp.getId(), resultCp.getId());
        assertEquals(testCp.getCid(), resultCp.getCid());
        assertEquals(testCp.getForename(), resultCp.getForename());
        assertEquals(testCp.getSurname(), resultCp.getSurname());
        assertEquals(testCp.getGender(), resultCp.getGender());
        assertEquals(testCp.getEmail(), resultCp.getEmail());
        assertEquals(testCp.getPhone(), resultCp.getPhone());
        assertEquals(testCp.isMainContact(), resultCp.isMainContact());
    }
    
    @Test
    public void testEditSubmit()
    {
        // enter original data
        model.persist(testCp);
        controller.getContactPerson().setCid(cid);
        controller.getContactPerson().setId(id);
        
        // createe edited data
        ContactPerson editedCp = TestCase.createTestContactPerson(id, cid);
        editedCp.setForename(testCp.getForename() + "Edited");
        editedCp.setSurname(testCp.getSurname() + "Edited");
        editedCp.setGender("f");
        editedCp.setEmail(testCp.getEmail() + "Edited");
        editedCp.setPhone(testCp.getPhone() + "Edited");
        editedCp.setMainContact(!testCp.isMainContact());
        
        // enter edited data
        controller.txtForename.setText(editedCp.getForename());
        controller.txtSurname.setText(editedCp.getSurname());
        
        Gender g = controller.new Gender();
        g.setValue(editedCp.getGender());
        controller.cmbGender.setValue(g);
        
        controller.txtEmail.setText(editedCp.getEmail());
        controller.txtPhone.setText(editedCp.getPhone());
        controller.cbMainContact.setSelected(editedCp.isMainContact());
        
        // submit - ignore NullPointreException caused by missing stage
        try
        {
            controller.submit();
        }
        catch(NullPointerException e)
        {}
        
        // validate
        ContactPerson resultCp = model.getEntityManager().find(ContactPerson.class, id);
        assertEquals(editedCp.getId(), resultCp.getId());
        assertEquals(editedCp.getCid(), resultCp.getCid());
        assertEquals(editedCp.getForename(), resultCp.getForename());
        assertEquals(editedCp.getSurname(), resultCp.getSurname());
        assertEquals(editedCp.getGender(), resultCp.getGender());
        assertEquals(editedCp.getEmail(), resultCp.getEmail());
        assertEquals(editedCp.getPhone(), resultCp.getPhone());
        assertEquals(editedCp.isMainContact(), resultCp.isMainContact());                                
    }
    
}
