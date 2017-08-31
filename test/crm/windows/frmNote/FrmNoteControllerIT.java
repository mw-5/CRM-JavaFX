package crm.windows.frmNote;

import crm.model.Customer;
import crm.model.EntityType;
import crm.model.Model;
import crm.model.Note;
import crm.navigation.Navigation;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
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


public class FrmNoteControllerIT 
{
    Model model = Model.getModel();
    private FrmNoteController controller;
    private final int id = model.getNewId(EntityType.NOTE);
    private final int cid = model.getNewId(EntityType.CUSTOMER);
    private Customer testCustomer = TestCase.createTestCustomer(cid);
    private Note testNote;
    
    
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource(nav.fxmlFrmNote));
        loader.load();
        controller = loader.<FrmNoteController>getController();
    }
    
    @Before
    public void setUpData()
    {
        testNote = TestCase.createTestNote(id, cid); // reset data of note
        model.persist(testCustomer);
    }
    
    @After
    public void cleanUp()
    {
        EntityManager em = model.getEntityManager();
        em.getTransaction().begin();
        Note testNote = em.find(Note.class, id);
        if (testNote != null)
        {
            em.remove(testNote);
        }
        Customer testCustomer = model.getCustomer(cid);
        if (testCustomer != null)
        {
            em.remove(testCustomer);
        }
        em.getTransaction().commit();
    }
    
    
    @Test
    public void testNewSubmit()
    {
        // enter data
        controller.getNote().setCid(cid);
        controller.txtMemo.setText(testNote.getMemo());
        controller.txtCategory.setText(testNote.getCategory());
        // attachment is not included to avoid copying which is tested separately
        
        // submit - ignore NullPointerException caused by missing stage
        try
        {
            controller.submit();
        }
        catch(NullPointerException e)
        {}
        
        // validate
        Note resultNote = model.getEntityManager().find(Note.class, id);
        assertEquals(testNote.getId(), resultNote.getId());
        assertEquals(testNote.getCid(), resultNote.getCid());
        assertEquals(System.getenv("username"), resultNote.getCreatedBy());
        assertNotNull(resultNote.getEntryDate());
        assertEquals(testNote.getMemo(), resultNote.getMemo());
        assertEquals(testNote.getCategory(), resultNote.getCategory());
        assertEquals(null, resultNote.getAttachment());
    }
    
    @Test
    public void testEditSubmit()
    {
        // enter original data
        model.persist(testNote);
        controller.getNote().setId(id);
        controller.getNote().setCid(cid);
        controller.getNote().setCreatedBy(testNote.getCreatedBy());
        controller.getNote().setEntryDate(testNote.getEntryDate());
        controller.getNote().setAttachment(testNote.getAttachment());
        
        // create edited data
        Note editedNote = TestCase.createTestNote(id, cid);
        editedNote.setMemo(testNote.getMemo() + "Edited");
        editedNote.setCategory(testNote.getCategory() + "Edited");
        
        // enter edited data
        controller.txtMemo.setText(editedNote.getMemo());
        controller.txtCategory.setText(editedNote.getCategory());
        // attachment is not included to avoid copying which is tested separately
        
        // submit - ignore NullPointerException caused by missing stage
        try
        {
            controller.submit();
        }
        catch(NullPointerException e)
        {}
        
        // validate
        Note resultNote = model.getEntityManager().find(Note.class, id);
        assertEquals(editedNote.getId(), resultNote.getId());
        assertEquals(editedNote.getCid(), resultNote.getCid());
        assertEquals(testNote.getCreatedBy(), resultNote.getCreatedBy());
        assertEquals(testNote.getEntryDate(), resultNote.getEntryDate());
        assertEquals(editedNote.getMemo(), resultNote.getMemo());
        assertEquals(editedNote.getCategory(), resultNote.getCategory());
        assertEquals(editedNote.getAttachment(), resultNote.getAttachment());
    }
    
    @Test
    public void testCopyAttachment() throws Exception
    {
        // set up
        Properties config = new Properties();
        InputStream is = ClassLoader.getSystemResourceAsStream("META/Config.properties");
        config.load(is);
        String pathCustomersFolder = config.getProperty("customersFolder");
        
        Path srcPath = Paths.get(pathCustomersFolder, "test");
        File srcPathFile = srcPath.resolve(testNote.getAttachment()).toFile();
        File dstPathFile = Paths.get(pathCustomersFolder, testNote.getCid().toString(), testNote.getAttachment()).toFile();
        
        // set up source file
        if (!srcPath.toFile().exists())
        {
            srcPath.toFile().mkdir();
        }
        if (!srcPathFile.exists())
        {
            srcPathFile.createNewFile();
        }
        
        // clean up destination folder
        if (dstPathFile.exists())
        {
            dstPathFile.delete();
        }
        
        // set up data
        controller.getNote().setCid(cid);
        controller.setPathSrcAttachment(srcPath);
        controller.getNote().setAttachment(testNote.getAttachment());
        
        // execute copy
        try
        {
            model.getConfig().setProperty("customersFolder", pathCustomersFolder);
            controller.copyAttachment();
        }
        catch(Exception e)
        {}
        Thread.sleep(1000);
        
        // validate
        assertTrue(dstPathFile.exists());
        
        // clean up
        if (dstPathFile.exists())
        {
            dstPathFile.delete();
        }
        if (srcPath.toFile().exists())
        {
            srcPath.toFile().delete();
        }
        if (srcPathFile.exists())
        {
            srcPathFile.delete();
        }                
    }
    
    @Test
    public void testRemoveAttachment()
    {
        controller.getNote().setAttachment(testNote.getAttachment());
        Path path = Paths.get("path/test");
        controller.setPathSrcAttachment(path);
        controller.removeAttachment();
        assertNull(controller.getNote().getAttachment());
        assertNull(controller.getPathSrcAttachment());
    }
        
}
