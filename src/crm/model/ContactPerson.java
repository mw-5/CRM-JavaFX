package crm.model;

import java.io.Serializable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;


@Entity
@Table(name = "contact_persons")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ContactPerson.maxId", query = "SELECT max(c.id) FROM ContactPerson c")
})
public class ContactPerson implements Serializable 
{
    
    private static final long serialVersionUID = 1L;
    
    private IntegerProperty id = new SimpleIntegerProperty();
    @Id
    @Basic(optional = false)
    @Column(name = "Id")
    public final Integer getId()
    {
        return id.get();
    }
    public final void setId(Integer value)
    {
        id.set(value);
    }
    public IntegerProperty idProperty()
    {
        return id;
    }
    
    
    private IntegerProperty cid = new SimpleIntegerProperty();
    @Column(name = "cid")
    public final Integer getCid()
    {
        return cid.get();
    }
    public final void setCid(Integer value)
    {
        cid.set(value);
    }
    public IntegerProperty cidProperty()
    {
        return cid;
    }
    
    
    private StringProperty forename = new SimpleStringProperty();
    @Column(name = "forename")
    public final String getForename()
    {
        return forename.get();
    }
    public final void setForename(String value)
    {
        forename.set(value);
    }
    public StringProperty forenameProperty()
    {
        return forename;
    }
    
    
    private StringProperty surname = new SimpleStringProperty();
    @Column(name = "surname")
    public final String getSurname()
    {
        return surname.get();
    }
    public final void setSurname(String value)
    {
        surname.set(value);
    }
    public StringProperty surnameProperty()
    {
        return surname;
    }
    
    
    private StringProperty gender = new SimpleStringProperty();
    @Column(name = "gender")
    public final String getGender()
    {
        return gender.get();
    }
    public final void setGender(String value)
    {
        gender.set(value);
    }
    public StringProperty genderProperty()
    {
        return gender;
    }
    
    
    private StringProperty email = new SimpleStringProperty();
    @Column(name = "email")
    public final String getEmail()
    {
        return email.get();
    }
    public final void setEmail(String value)
    {
        email.set(value);
    }
    public StringProperty emailProperty()
    {
        return email;
    }
    
    
    private StringProperty phone = new SimpleStringProperty();
    @Column(name = "phone")
    public final String getPhone()
    {
        return phone.get();
    }
    public final void setPhone(String value)
    {
        phone.set(value);
    }
    public StringProperty phoneProperty()
    {
        return phone;
    }
    
    
    private BooleanProperty mainContact = new SimpleBooleanProperty();
    @Column(name = "main_contact")
    public final Boolean isMainContact()
    {
        return mainContact.get();
    }
    public final void setMainContact(Boolean value)
    {
        mainContact.set(true);
    }
    public BooleanProperty mainContactProperty()
    {
        return mainContact;
    }
    
    
    private ObjectProperty<Customer> customer = new SimpleObjectProperty<Customer>();
    @PrimaryKeyJoinColumn(name = "cid", referencedColumnName = "cid")
    @ManyToOne(optional = false)
    public final Customer getCustomer()
    {
        return customer.get();
    }
    public final void setCustomer(Customer value)
    {
        customer.set(value);
    }
    public ObjectProperty<Customer> customerProperty()
    {
        return customer;
    }
    
    
    
    public ContactPerson()
    {}
    
    public ContactPerson(Integer id)
    {
        setId(id);
    }
    
    @Override
    public int hashCode()
    {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        
        return hash;
    }
    
    @Override
    public boolean equals(Object object)
    {
        // TODO: Warning - this method won't work in the case the Id fields ar not set
        if (!(object instanceof ContactPerson))
        {
            return false;
        }
        ContactPerson other = (ContactPerson)object;
        if ((this.id == null && other.id != null || (this.id != null && !this.id.equals(other.id))))
        {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString()
    {
        return "crm.model.ContactPerson[ id=" + id + " ]";
    }
        
}
