package datastax.com.dataObjects;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.SchemaHint;

@Entity
@CqlName("history_entity_type")
@SchemaHint(targetElement = SchemaHint.TargetElement.UDT)
public class AuditHistoryEntityType {

    @CqlName("action") private String action;
    @CqlName("stanza_name") private String stanzaName;
    @CqlName("stanza") private String stanza;

    public AuditHistoryEntityType() {};

    public String getAction() { return action;}
    public void setAction(String val) { action = val;}

    public String getStanzaName() { return stanzaName;}
    public void setStanzaName(String val) { stanzaName = val;}

    public String getStanza() { return stanza;}
    public void setStanza(String val) { stanza = val;}

}
