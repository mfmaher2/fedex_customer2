package datastax.com;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.SchemaHint;

@Entity
@CqlName("telecom_details_type")
@SchemaHint(targetElement = SchemaHint.TargetElement.UDT)
public class CustomerContactAddressSecondary {
    private String telecomMethod;
    private String areaCode;
    private String phoneNumber;
    private String unit;
    private String value;

    public CustomerContactAddressSecondary() {};

    public String getTelecomMethod() { return telecomMethod; }
    public void setTelecomMethod(String methodVal) { telecomMethod = methodVal;}

    public String getValue() { return value; }
    public void setValue(String val) { value = val;}

    public String getValue() { return value; }
    public void setValue(String val) { value = val;}
}
