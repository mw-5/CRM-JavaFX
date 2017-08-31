package testCases;

import crm.model.ContactPerson;
import crm.model.Model;
import crm.model.Customer;
import crm.model.Note;
import java.util.Calendar;


public class TestCase 
{
    Model model = Model.getModel();
    
    public static Customer createTestCustomer(int cid)
    {
        Customer c = new Customer();
        c.setCid(cid);
        c.setCompany("testCompany");
        c.setAddress("testAddress");
        c.setZip("testZip");
        c.setCity("testCity");
        c.setCountry("testCountry");
        c.setContractId("testContractId");
        Calendar cal = Calendar.getInstance();
        cal.set(2017, 1, 2, 0, 0, 0);
        c.setContractDate(cal);
        return c;
    }
    
    public static ContactPerson createTestContactPerson(int id, int cid)
    {
        ContactPerson c = new ContactPerson();
        c.setId(id);
        c.setCid(cid);
        c.setForename("testForename");
        c.setSurname("testSurname");
        c.setGender("m");
        c.setEmail("test@test.com");
        c.setPhone("123");
        c.setMainContact(true);
        return c;
    }
    
    public static Note createTestNote(int id, int cid)
    {
        Note n = new Note();
        n.setId(id);
        n.setCid(cid);
        n.setCreatedBy("testUser");
        Calendar cal = Calendar.getInstance();
        cal.set(2017, 1, 2, 3, 4, 5);
        n.setEntryDate(cal);
        n.setMemo("testMemo");
        n.setCategory("testCategory");
        n.setAttachment("test.txt");
        return n;
    }
            
}
