package datastax.com.dataObjects;

import com.datastax.oss.driver.api.mapper.annotations.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

@Entity
@CqlName("cust_acct_v1")
public class Account implements Serializable {

    @PartitionKey private String accountNumber;
    @ClusteringColumn private String opco;
    @CqlName("profile__customer_type") private String profileCustomerType;
    @CqlName("profile__account_type") private String profileAccountType;
    @CqlName("profile__account_status__status_code") private String profileAccountStatusCode;
    @CqlName("profile__account_status__reason_code") private String profileAccountReasonCode;
    @CqlName("profile__enterprise_source") private String profileEnterpriseSource;
    @CqlName("profile__hazardous_shipper_flag") private String hazardousShipperFlag;
    @CqlName("duty_tax_info") private Map<String, String> dutyTaxInfo;
    @CqlName("profile__archive_date") private LocalDate profileArchiveDate;

    @Computed("writetime(profile__customer_type)")  private long profileCustomerType_wrtm;
    @Computed("writetime(profile__account_status__status_code)")  private long profileStatusCode_wrtm;
//    @CqlName("account_regulatory__regulated_agentRegimeEffYearMonth") private LocalDate acctRegRegimeEffYearMon;

    public Account() {};


    public long getProfileCustomerType_wrtm() {
        return profileCustomerType_wrtm;
    }

    public void setProfileCustomerType_wrtm(long profileCustomerType_wrtm) {
        this.profileCustomerType_wrtm = profileCustomerType_wrtm;
    }

    public long getProfileStatusCode_wrtm() { return profileStatusCode_wrtm;}

    public void setProfileStatusCode_wrtm(long profileStatusCode_wrtm) {
        this.profileStatusCode_wrtm = profileStatusCode_wrtm;
    }

    public String getAccountNumber() { return accountNumber;}
    public void setAccountNumber(String val) { accountNumber = val;}

    public String getOpco() {return opco;}
    public void setOpco(String val) {opco = val;}

    public String getProfileCustomerType() { return profileCustomerType;}
    public void setProfileCustomerType(String val) { profileCustomerType = val;}

    public String getProfileAccountType() { return profileAccountType;}
    public void setProfileAccountType(String val) { profileAccountType = val;}

    public String getProfileAccountStatusCode() { return profileAccountStatusCode;}
    public void setProfileAccountStatusCode(String val) { profileAccountStatusCode = val;}

    public String getProfileAccountReasonCode() { return profileAccountReasonCode;}
    public void setProfileAccountReasonCode(String val) { profileAccountReasonCode = val;}

    public String getProfileEnterpriseSource() { return profileEnterpriseSource;}
    public void setProfileEnterpriseSource(String val) { profileEnterpriseSource = val;}

    public String getHazardousShipperFlag() { return hazardousShipperFlag; }
    public void setHazardousShipperFlag(String val) {hazardousShipperFlag = val;}

    public Map<String, String> getDutyTaxInfo() { return dutyTaxInfo; }
    public void setDutyTaxInfo(Map<String, String> val) { dutyTaxInfo = val; }

    public LocalDate getProfileArchiveDate() { return profileArchiveDate; }
    public void setProfileArchiveDate(LocalDate val) { profileArchiveDate = val; }

//    public LocalDate getAcctRegRegimeEffYearMon() { return acctRegRegimeEffYearMon; }
//    public void setAcctRegRegimeEffYearMon(LocalDate val) { acctRegRegimeEffYearMon = val; }

//    public Date getAcctRegRegimeEffYearMon() { return acctRegRegimeEffYearMon; }
//    public void setAcctRegRegimeEffYearMon(Date val) { acctRegRegimeEffYearMon = val; }
}
