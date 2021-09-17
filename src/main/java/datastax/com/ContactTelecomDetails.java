package datastax.com;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.SchemaHint;

@Entity
@CqlName("telecom_details_type")
@SchemaHint(targetElement = SchemaHint.TargetElement.UDT)
public class ContactTelecomDetails {
    private String telecomMethod;
    private String areaCode;
    private String phoneNumber;

    public ContactTelecomDetails() {};

    public String getTelecomMethod() { return telecomMethod; }
    public void setTelecomMethod(String val) { telecomMethod = val;}

    public String getAreaCode() { return areaCode; }
    public void setAreaCode (String val) { areaCode = val;}

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String val) { phoneNumber = val;}
}
