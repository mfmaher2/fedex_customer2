package datastax.com.dataObjects;

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;

import java.util.Map;
import java.util.Set;

@Entity
@CqlName("account_contact")
public class AccountContact {
    @PartitionKey private String accountNumber;
    @ClusteringColumn(0) private String opco;
    @ClusteringColumn(1) @CqlName("contact_type_code")private String contactTypeCode;
    @ClusteringColumn(2) @CqlName("contact_business_id")private String contactBusinessID;
    @CqlName("person__first_name") private String personFirstName;
    @CqlName("person__last_name") private String personLastName;

    public AccountContact() {};

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String val) {accountNumber = val; }

    public String getOpco() { return opco; }
    public void setOpco(String val) {opco = val; }

    public String getContactTypeCode() { return contactTypeCode; }
    public void setContactTypeCode(String val) {contactTypeCode = val; }

    public String getContactBusinessID() { return contactBusinessID; }
    public void setContactBusinessID(String val) {contactBusinessID = val; }

    public String getPersonFirstName() { return personFirstName; }
    public void setPersonFirstName(String val) {personFirstName = val; }

    public String getPersonLastName() { return personLastName; }
    public void setPersonLastName(String val) {personLastName = val; }
}
