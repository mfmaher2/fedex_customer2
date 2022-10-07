package datastax.com.dataObjects;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.SchemaHint;

import java.util.Set;

@Entity
@CqlName("audit_history_entry")
@SchemaHint(targetElement = SchemaHint.TargetElement.UDT)
public class AuditHistoryEntry {

    @CqlName("history_detail__descriptive_identifier") private String descriptiveIdentifier;
    @CqlName("history_detail__additional_identifier__key") private Set<AuditHistoryAdditionalIdentifier> additionalIdentifierKey;
    @CqlName("history_detail__entity") private Set<AuditHistoryEntityType> entity;
    @CqlName("history_detail__field") private Set<AuditHistoryFieldType> field;

    public AuditHistoryEntry() {};

    public String getDescriptiveIdentifier() { return descriptiveIdentifier;}
    public void setDescriptiveIdentifier(String val) { descriptiveIdentifier = val;}

    public Set<AuditHistoryAdditionalIdentifier> getAdditionalIdentifierKey() { return additionalIdentifierKey;}
    public void setAdditionalIdentifierKey(Set<AuditHistoryAdditionalIdentifier> val) {additionalIdentifierKey = val;}

    public Set<AuditHistoryEntityType> getEntity() { return entity;}
    public void setEntity(Set<AuditHistoryEntityType> val) {entity = val;}

    public Set<AuditHistoryFieldType> getField() { return field;}
    public void setField(Set<AuditHistoryFieldType> val) {field = val;}
}
