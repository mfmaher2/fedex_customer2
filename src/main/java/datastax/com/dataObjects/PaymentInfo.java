package datastax.com.dataObjects;

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;

@Entity
@CqlName("payment_info_v1")
public class PaymentInfo {

    @PartitionKey private String accountNumber;
    @ClusteringColumn(0) private String opco;
    @ClusteringColumn(1) @CqlName("record_type_cd") private String recordType;
    @ClusteringColumn(2) private String recordKey;
    @ClusteringColumn(3) @CqlName("record_seq") private int recordSeq;
    @CqlName("payment_id") private String paymentID;

    PaymentInfo() {};

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String val) {accountNumber = val;}

    public String getOpco() { return opco; }
    public void setOpco(String val) { opco = val; }

    public String getRecordType() { return recordType; }
    public void setRecordType(String val) { recordType = val; }

    public String getRecordKey() { return recordKey; }
    public void setRecordKey(String val) { recordKey = val; }

    public int getRecordSeq() { return recordSeq; }
    public void setRecodSeq(int val) { recordSeq = val; }

    public String getPaymentID() { return paymentID; }
    public void setPaymentID(String val) { paymentID = val; }
}
