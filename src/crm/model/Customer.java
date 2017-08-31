package crm.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;


@Entity
@Table(name = "customers")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Customer.findAll", query = "SELECT c FROM Customer c"),
    @NamedQuery(name = "Customer.maxId", query = "SELECT max(c.cid) FROM Customer c")        
})
public class Customer implements Serializable 
{

    private static final long serialVersionUID = 1L;
    
    private IntegerProperty cid = new SimpleIntegerProperty();
    @Id
    @Basic(optional = false)
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
    
    
    private StringProperty company = new SimpleStringProperty();
    @Column(name = "company")
    public final String getCompany()
    {
        return company.get();
    }
    public final void setCompany(String value)
    {
        company.set(value);
    }
    public StringProperty companyProperty()
    {
        return company;
    }
    
    
    private StringProperty address = new SimpleStringProperty();
    @Column(name = "address")
    public final String getAddress()
    {
        return address.get();
    }
    public final void setAddress(String value)
    {
        address.set(value);
    }
    public StringProperty addressProperty()
    {
        return address;
    }
    
    
    private StringProperty zip = new SimpleStringProperty();
    @Column(name = "zip")
    public final String getZip()
    {
        return zip.get();
    }
    public final void setZip(String value)
    {
        zip.set(value);
    }
    public StringProperty zipProperty()
    {
        return zip;
    }
    
    
    private StringProperty city = new SimpleStringProperty();
    @Column(name = "city")
    public final String getCity()
    {
        return city.get();
    }
    public final void setCity(String value)
    {
        city.set(value);
    }
    public StringProperty cityProperty()
    {
        return city;
    }
    
    
    private StringProperty country = new SimpleStringProperty();
    @Column(name = "country")
    public final String getCountry()
    {
        return country.get();
    }
    public final void setCountry(String value)
    {
        country.set(value);
    }
    public StringProperty countryProperty()
    {
        return country;
    }
    
    
    private StringProperty contractId = new SimpleStringProperty();
    @Column(name = "contract_id")
    public final String getContractId()
    {
        return contractId.get();
    }
    public final void setContractId(String value)
    {
        contractId.set(value);
    }
    public StringProperty contractIdProperty()
    {
        return contractId;
    }
    
    
    private ObjectProperty<Calendar> contractDate = new SimpleObjectProperty<>();
    @Column(name = "contract_date")
    @Temporal(TemporalType.TIMESTAMP)
    public final Calendar getContractDate()
    {
        return contractDate.get();
    }
    public final void setContractDate(Calendar value)
    {
        contractDate.set(value);
    }
    public ObjectProperty<Calendar> contractDateProperty()
    {
        return contractDate;
    }
    
    
    
    private List<Note> notesCollection;
    @XmlTransient
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "customer")
    public List<Note> getNotesCollection()
    {
        return notesCollection;
    }
    public void setNotesCollection(List<Note> notesCollection)
    {
        this.notesCollection = notesCollection;
    }
    
    
    private List<ContactPerson> contactPersonsCollection;
    @XmlTransient
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "customer")
    public List<ContactPerson> getContactPersonsCollection()
    {
        return contactPersonsCollection;
    }
    public void setContactPersonsCollection(List<ContactPerson> contactPersonsCollection)
    {
        this.contactPersonsCollection = contactPersonsCollection;
    }
      
    

    public Customer() 
    {}

    public Customer(Integer cid) 
    {
        setCid(cid);
    }
    

    @Override
    public int hashCode() 
    {
        int hash = 0;
        hash += (cid != null ? cid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) 
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Customer)) {
            return false;
        }
        Customer other = (Customer) object;
        if ((this.cid == null && other.cid != null) || (this.cid != null && !this.cid.equals(other.cid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() 
    {
        return "crm.model.Customer[ cid=" + cid + " ]";
    }
    
}
