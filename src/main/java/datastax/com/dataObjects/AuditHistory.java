package datastax.com.dataObjects;

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;

import java.time.Instant;

@Entity
@CqlName("audit_history_v1")
public class AuditHistory {
    @PartitionKey
    private String accountNumber;
    @ClusteringColumn(1) private String opco;
    @ClusteringColumn(0) @CqlName("last_update_tmstp") private Instant lastUpdated;
    @ClusteringColumn(2) @CqlName("transaction_id") private String transactionID;
    @CqlName("request_action") private String requestAction;
//    @CqlName("history_detail__descriptive_identifier") private String histDetailDescID;
    @CqlName("app_id") private String appID;
    @CqlName("user_id") private String userID;
    @CqlName("source") private String source;
    @CqlName("request_type") private String requestType;

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String val) {accountNumber = val; }

    public String getOpco() { return opco; }
    public void setOpco(String val) {opco = val; }

    public Instant getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Instant val) { lastUpdated = val;}

    public String getRequestAction() { return requestAction; }
    public void setRequestAction(String val) {requestAction = val; }

//    public String getHistDetailDescID() { return histDetailDescID; }
//    public void setHistDetailDescID(String val) {histDetailDescID = val; }

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