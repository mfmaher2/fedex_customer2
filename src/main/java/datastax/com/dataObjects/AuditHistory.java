package datastax.com.dataObjects;

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;

import java.time.Instant;
import java.util.Set;

@Entity
@CqlName("audit_history_v1")
public class AuditHistory {
    @PartitionKey private String accountNumber;
    @ClusteringColumn(1) private String opco;
    @ClusteringColumn(0) @CqlName("last_update_tmstp") private Instant lastUpdated;
    @ClusteringColumn(2) @CqlName("transaction_id") private String transactionID;
    @CqlName("audit_details") private Set<AuditHistoryEntry> auditDetails;
    @CqlName("request_action") private String requestAction;
    @CqlName("app_id") private String appID;
    @CqlName("user_id") private String userID;
    @CqlName("source") private String source;
    @CqlName("request_type") private String requestType;

    //example helper function for constructing solr queries
    //this example shows how to query for nested collection value
    public static String construcAuditEntryEntityStanzaSolrQuery(String stanza){
        return "{\"q\": \"{!tuple}audit_details.history_detail__entity.stanza:" + stanza + "\" }";
    }


    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String val) {accountNumber = val; }

    public String getOpco() { return opco; }
    public void setOpco(String val) {opco = val; }

    public Instant getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Instant val) { lastUpdated = val;}

    public String getRequestAction() { return requestAction; }
    public void setRequestAction(String val) {requestAction = val; }

    public Set<AuditHistoryEntry> getAuditDetails() { return  auditDetails ;}
    public void setAuditDetails(Set<AuditHistoryEntry> val) {auditDetails = val;}

    public String getAppID() { return appID; }
    public void setAppID(String val) {appID = val; }

    public String getUserID() { return userID; }
    public void setUserID(String val) {userID = val; }

    public String getSource() { return source; }
    public void setSource(String val) {source = val; }

    public String getRequestType() { return requestType; }
    public void setRequestType(String val) {requestType = val; }

    public String getTransactionID() { return transactionID; }
    public void setTransactionID(String val) {transactionID = val; }
}