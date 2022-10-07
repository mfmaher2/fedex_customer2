package datastax.com.dataObjects;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.SchemaHint;

@Entity
@CqlName("history_field_type")
@SchemaHint(targetElement = SchemaHint.TargetElement.UDT)
public class AuditHistoryFieldType {

    @CqlName("action") private String action;
    @CqlName("stanza_name") private String stanzaName;
    @CqlName("field_name") private String fieldName;
    @CqlName("previous_value") private String previousValue;
    @CqlName("new_value") private String newValue;

    AuditHistoryFieldType() {} ;

    public String getAction() { return action;}
    public void setAction(String val) { action = val;}

    public String getStanzaName() { return stanzaName;}
    public void setStanzaName(String val) { stanzaName = val;}

    public String getFieldName() { return fieldName;}
    public void setFieldName(String val) { fieldName = val;}

    public String getPreviousValue() { return previousValue;}
    public void setPreviousValue(String val) { previousValue = val;}

    public String getNewValue() { return newValue;}
    public void setNewValue(String val) { newValue = val;}
}
