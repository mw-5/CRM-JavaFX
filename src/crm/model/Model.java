package crm.model;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.util.StringConverter;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;


public class Model
{
    private Model()
    {}
    
    private static Model model = null;
    public static Model getModel()
    {
        if (model == null)
        {
            model = new Model();
        }
        return model;
    }
    
    private EntityManager em = Persistence.createEntityManagerFactory("CRMPU").createEntityManager();
    public EntityManager getEntityManager()
    {
        return em;
    }
    
    public void persist(Object entity)
    {
        try
        {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
        }
        catch(Exception e)
        {
            displayError(e);
        }
    }
    
    public void merge(Object entity)
    {
        try
        {
            em.getTransaction().begin();
            em.merge(entity);
            em.getTransaction().commit();
        }
        catch(Exception e)
        {
            displayError(e);
        }
    }
    
    public Customer getCustomer(int cid)
    {
        return getEntityManager().find(Customer.class, cid);
    }
    
    public ObservableList<Customer> getCustomers()
    {
        ObservableList<Customer> list = FXCollections.observableArrayList(em.createNamedQuery("Customer.findAll").getResultList());
        return list.sorted((c1, c2) -> c1.getCid() - c2.getCid());
    }
    
    public Integer getNewId(EntityType entityType)
    {
        String queryName = "";
        switch(entityType)
        {
            case CUSTOMER:
                queryName = "Customer.maxId";
                break;
            case CONTACT_PERSON:
                queryName = "ContactPerson.maxId";
                break;
            case NOTE:
                queryName = "Note.maxId";
                break;
            default:
                throw new RuntimeException("EntityType enumeration value not implemented");
        }
        
        Integer id = (Integer)em.createNamedQuery(queryName).getSingleResult();
        id = id == null ? 0 : id;
        
        return ++id;
    }
    
    private List<Runnable> listRefreshCallbacks = new ArrayList<>();
    public void addRefreshCallback(Runnable callback)
    {
        listRefreshCallbacks.add(callback);
    }
    public void refresh()
    {
        listRefreshCallbacks.forEach(c -> c.run());
    }
    public void refreshEntity(Object entity)
    {
        if (entity != null && em.contains(entity))
        {
            em.refresh(entity);
        }
    }
    
    private final StringConverter<Calendar> converterStringCalendar = new StringConverter<Calendar>() {
        SimpleDateFormat format = new SimpleDateFormat(getMsg("dateFormat"));
        @Override
        public String toString(Calendar object) 
        {
            if (object == null)
            {
                return "";
            }
            else
            {
                return format.format(object.getTime());
            }
        }

        @Override
        public Calendar fromString(String string) 
        {
            try
            {
                if (!string.equals(""))
                {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(format.parse(string));
                    return calendar;
                }
                else
                {
                    return null;
                }
            }
            catch(Exception e)
            {
                displayError(e);
                return null;
            }
        }
    };
    public StringConverter<Calendar> getConverterStringCalendar()
    {
        return converterStringCalendar;
    }
    
    private Properties config = null;
    public Properties getConfig()
    {
        if (config == null || config.size() == 0)
        {
            loadConfig();
        }
        return config;
    }
    public boolean loadConfig()
    {
        boolean isSuccess = true;
        try
        {
            config = new Properties();
            InputStream is = ClassLoader.getSystemResourceAsStream("META-INF/Config.properties");
            config.load(is);
        }
        catch(Exception e)
        {
            isSuccess = false;
        }
        return isSuccess;
    }
    
    ResourceBundle rbMsg = null;
    public String getMsg(String key)
    {
        if (rbMsg == null)
        {
            String language = Locale.getDefault().toString().substring(0, 2);
            Locale locale;
            switch(language)
            {
                case "en":
                    locale = new Locale("en-US");
                    break;
                case "de":
                    locale = new Locale("de-DE");
                    break;
                case "es":
                    locale = new Locale("es-ES");
                    break;
                default:
                    locale = new Locale("en-US");
            }
            rbMsg = ResourceBundle.getBundle("crm.resources.MessagesBundle", locale);
        }
        return rbMsg.getString(key);
    }
    
    public LocalDate convertCalendarToLocalDate(Calendar cal)
    {
        if (cal == null)
        {
            return null;
        }
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH)+1;
        int year = cal.get(Calendar.YEAR);
        return LocalDate.of(year, month, day);
    }
    
    public Calendar convertLocalDateToCalendar(LocalDate localDate)
    {
        if (localDate == null)
        {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.set(localDate.getYear(), localDate.getMonthValue()-1, localDate.getDayOfMonth(), 0, 0, 0);
        return cal;
    }
    
    private boolean animated = true;
    public boolean isAnimated()
    {
        return animated;
    }
    public void setAnimated(boolean value)
    {
        animated = value;
    }
    
    public static void displayError(Exception e)
    {
        String msg = e.getMessage() + "\n\nStack trace:\n";
        for (StackTraceElement s : e.getStackTrace())
        {
            msg += s.toString() + "\n";
        }
        System.err.print(msg);
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).show();
    }
    
}
