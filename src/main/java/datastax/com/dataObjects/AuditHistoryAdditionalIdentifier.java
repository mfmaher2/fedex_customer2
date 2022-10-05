package datastax.com.dataObjects;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.SchemaHint;

@Entity
@CqlName("history_additional_identifier_type")
@SchemaHint(targetElement = SchemaHint.TargetElement.UDT)
public class AuditHistoryAdditionalIdentifier {

    @CqlName("type") private String type;
    @CqlName("value")private String value;

    public AuditHistoryAdditionalIdentifier() {};

    public String getType() {return type;}
    public void setType(String val) { type = val; }

    public String getValue() { return value;}
    public void setValue(String val) { value = val;}
}
