package datastax.com;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.SchemaHint;

@Entity
@CqlName("address_secondary_type")
@SchemaHint(targetElement = SchemaHint.TargetElement.UDT)
public class CustomerContactAddressSecondary {
    private String unit;
    private String value;

    public CustomerContactAddressSecondary() {};

    public String getUnit() { return unit; }
    public void setUnit(String unitVal) { unit = unitVal;}

    public String getValue() { return value; }
    public void setValue(String val) { value = val;}
}
