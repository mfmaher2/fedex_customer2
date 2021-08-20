package datastax.com;

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;

import java.time.Instant;

@Entity
@CqlName("apply_discount_detail_v1")
public class CustomerApplyDiscount {
    @PartitionKey private String accountNumber;
    @ClusteringColumn(0) private String opco;
    @ClusteringColumn(1) @CqlName("apply_discount__effective_date_time") private Instant disountEffectiveDateTime;
    @ClusteringColumn(2) @CqlName("last_updt_tmstp") private Instant lastUpdated;
    @CqlName("apply_discount__discount_flag") private Boolean applyDiscountFlag;
    @CqlName("apply_discount__expiration_date_time") private Instant disountExpirationateTime;

    public CustomerApplyDiscount() {};

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String val) {accountNumber = val; }

    public String getOpco() { return opco; }
    public void setOpco(String val) {opco = val; }

    public Instant getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Instant val) { lastUpdated = val;}

    public Boolean getApplyDiscountFlag() { return applyDiscountFlag;}
    public void setApplyDiscountFlag(Boolean val) { applyDiscountFlag = val;}

    public Instant getDisountEffectiveDateTime() { return disountEffectiveDateTime; }
    public void setDisountEffectiveDateTime(Instant val) { disountEffectiveDateTime = val; }

    public Instant getDisountExpirationateTime() { return disountExpirationateTime; }
    public void setDisountExpirationateTime(Instant val) { disountExpirationateTime = val; }
}
