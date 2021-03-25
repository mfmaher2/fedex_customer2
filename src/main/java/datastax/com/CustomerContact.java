package datastax.com;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;

@Entity
@CqlName("contact")
public class CustomerContact {

    @PartitionKey private long contactDocumentId;
    @CqlName("person__first_name") String personFirstName;
    private String person__last_name;
    private String person__middle_name;

    public CustomerContact() {};

    public long getContactDocumentId() { return contactDocumentId;}
    public void setContactDocumentId(long id) { contactDocumentId = id;}

    public String getPersonFirstName() { return personFirstName;}
    public void setPersonFirstName(String name) { personFirstName = name;}

    public String getPerson__last_name() { return person__last_name;}
    public void setPerson__last_name(String name) { person__last_name = name;}

    public String getPerson__middle_name() { return person__middle_name;}
    public void setPerson__middle_name(String name) { person__middle_name = name;}
}
