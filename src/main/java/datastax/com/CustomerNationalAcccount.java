package datastax.com;

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import java.time.Instant;

@Entity
@CqlName("national_account_v1")
public class CustomerNationalAcccount {

    @PartitionKey @CqlName("account_number") private String accountNumber;
    @ClusteringColumn(0) @CqlName("opco") private String opco;
    @CqlName("national_account_detail__national_account_company_cd") private String nationalAccountCompanyCode;
    @ClusteringColumn(2) @CqlName("national_account_detail__national_account_nbr") private String nationalAccountNumber;
    @ClusteringColumn(3)@CqlName("national_account_detail__national_priority_cd") private String nationalPriorityCode;
    @ClusteringColumn(1)  @CqlName("national_account_detail__membership_eff_date_time") private Instant membershipEffectiveDateTime;
    @CqlName("national_account_detail__membership_eff_date_time") private Instant membershipExpirationDateTime;

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String val) {accountNumber = val; }

    public String getOpco() { return opco; }
    public void setOpco(String val) {opco = val; }

    public Instant getMembershipEffectiveDateTime() { return membershipEffectiveDateTime;}
    public void setMembershipEffectiveDateTime(Instant val) { membershipEffectiveDateTime = val;
    }
}
