package crm.model;

import java.io.Serializable;
import java.util.Calendar;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;


@Entity
@Table(name = "notes")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Note.maxId", query = "SELECT max(n.id) FROM Note n")
})
public class Note implements Serializable 
{

    private static final long serialVersionUID = 1L;
    
    private IntegerProperty id = new SimpleIntegerProperty();    
    @Id
    @Basic(optional = false)
    @Column(name = "id")
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
    
    
    private StringProperty createdBy = new SimpleStringProperty();
    @Basic(optional = false)
    @Column(name = "created_by")
    public final String getCreatedBy()
    {
        return createdBy.get();
    }
    public final void setCreatedBy(String value)
    {
        createdBy.set(value);                
    }
    public StringProperty createdByProperty()
    {
        return createdBy;
    }
    
    
    private ObjectProperty<Calendar> entryDate = new SimpleObjectProperty<>();
    @Basic(optional = false)
    @Column(name = "entry_date")
    @Temporal(TemporalType.TIMESTAMP)
    public final Calendar getEntryDate()
    {
        return entryDate.get();
    }
    public final void setEntryDate(Calendar value)
    {
        entryDate.set(value);
    }
    public ObjectProperty<Calendar> entryDateProperty()
    {
        return entryDate;
    }
    
    
    private StringProperty memo = new SimpleStringProperty();
    @Column(name = "memo")
    public final String getMemo()
    {
        return memo.get();
    }
    public final void setMemo(String value)
    {
        memo.set(value);
    }
    public StringProperty memoProperty()
    {
        return memo;
    }
    
    
    private StringProperty category = new SimpleStringProperty();
    @Column(name = "category")
    public final String getCategory()
    {
        return category.get();
    }
    public final void setCategory(String value)
    {
        category.set(value);
    }
    public StringProperty categoryProperty()
    {
        return category;
    }
    
    
    private StringProperty attachment = new SimpleStringProperty();
    @Column(name = "attachment")
    public final String getAttachment()
    {
        return attachment.get();
    }
    public final void setAttachment(String value)
    {
        attachment.set(value);
    }
    public StringProperty attachmentProperty()
    {
        return attachment;
    }
    
    
    private ObjectProperty<Customer> customer = new SimpleObjectProperty<>();
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
           
    

    public Note() 
    {}

    public Note(Integer id) 
    {
        setId(id);
    }

    public Note(Integer id, String createdBy, Calendar entryDate) 
    {
        setId(id);
        setCreatedBy(createdBy);
        setEntryDate(entryDate);
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
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Note)) {
            return false;
        }
        Note other = (Note) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() 
    {
        return "crm.model.Note[ id=" + id + " ]";
    }
    
}
