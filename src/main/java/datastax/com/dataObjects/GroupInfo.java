package datastax.com.dataObjects;

import com.datastax.oss.driver.api.mapper.annotations.*;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;

@Entity
@CqlName("group_info_v1")
public class GroupInfo implements Serializable {

    @PartitionKey private String accountNumber;
    @ClusteringColumn(0) private String opco;
    @ClusteringColumn(1) @CqlName("group_id__code") private String groupIdCode;
    @ClusteringColumn(2) @CqlName("group_id__number")  private String groupIdNumber;
    @ClusteringColumn(3) @CqlName("group_id__type") private String groupIdType;
    @ClusteringColumn(4) @CqlName("effective_date_time") private Instant effectiveDateTime;
    @CqlName("expiration_date_time") private Instant expirationDateTime;
    @CqlName("group_id_detail__master_account") private String groupIdDetailMasterAccount;
    @CqlName("group_id_detail__name")  private String groupIdDetailName;
    @CqlName("group_id_detail__requester") private String groupIdDetailRequester;
    @CqlName("last_update_tmstp") private Instant lastUpdate;

//    @Computed("writetime(profile__customer_type)")  private long profileCustomerType_wrtm;  //for test/verification purposes only
//    @Computed("writetime(profile__account_status__status_code)")  private long profileStatusCode_wrtm; //for test/verification purposes only
//    @CqlName("account_regulatory__regulated_agentRegimeEffYearMonth") private LocalDate acctRegRegimeEffYearMon;

    public GroupInfo() {};


    public String getAccountNumber() { return accountNumber;}
    public void setAccountNumber(String val) { accountNumber = val;}

    public String getOpco() {return opco;}
    public void setOpco(String val) {opco = val;}

    public String getGroupIdCode() { return groupIdCode;}
    public void setGroupIdCode(String val) { groupIdCode = val;}

    public String getGroupIdNumber() { return groupIdNumber;}
    public void setGroupIdNumber(String val) { groupIdNumber = val;}

    public String getGroupIdType() { return groupIdType;}
    public void setGroupIdType(String val) { groupIdType = val;}

    public Instant getEffectiveDateTime() { return effectiveDateTime;}
    public void setEffectiveDateTime(Instant val) { effectiveDateTime = val;}

    public Instant getExpirationDateTime() { return expirationDateTime;}
    public void setExpirationDateTime(Instant val) { expirationDateTime = val;}

    public String getGroupIdDetailMasterAccount() { return groupIdDetailMasterAccount;}
    public void setGroupIdDetailMasterAccount(String val) { groupIdDetailMasterAccount = val;}

    public String getGroupIdDetailRequester() { return groupIdDetailRequester; }
    public void setGroupIdDetailRequester(String val) {groupIdDetailRequester = val;}

    public Instant getLastUpdate() { return lastUpdate; }
    public void setLastUpdate(Instant val) {lastUpdate = val;}
}
