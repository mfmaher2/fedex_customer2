package datastax.com;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;

import java.util.Set;

@Entity
@CqlName("contact")
public class Contact {

    @PartitionKey private long contactDocumentId;

    //multiple options of mapping variable names to table properties demonstrated
    //
    @CqlName("person__first_name") String personFirstName;  //annotated defition of table property
    private String person__last_name;                       //explicit use of table property
    private String person_MiddleName;                       //use default naming strategy mapping, property = person__middle_name

    @CqlName("tele_com") private Set<ContactTelecomDetails> teleCom;

    public Contact() {};

    public long getContactDocumentId() { return contactDocumentId;}
    public void setContactDocumentId(long id) { contactDocumentId = id;}

    public String getPersonFirstName() { return personFirstName;}
    public void setPersonFirstName(String name) { personFirstName = name;}

    public String getPerson__last_name() { return person__last_name;}
    public void setPerson__last_name(String name) { person__last_name = name;}

    public String getPerson_MiddleName() { return person_MiddleName;}
    public void setPerson_MiddleName(String name) { person_MiddleName = name;}

    public Set<ContactTelecomDetails> getTeleCom() { return teleCom; }
    public void setTeleCom(Set<ContactTelecomDetails> tc) { teleCom = tc; }
}
