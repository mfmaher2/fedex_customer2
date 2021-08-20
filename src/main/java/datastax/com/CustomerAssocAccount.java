package datastax.com;

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;

@Entity
@CqlName("assoc_accounts_v1")
public class CustomerAssocAccount {

    @PartitionKey private String accountNumber;
    @ClusteringColumn(0) @CqlName("associated_account__opco") String opco;
    @ClusteringColumn(1) @CqlName("associated_account__number") String associatedAccountNumber;

    CustomerAssocAccount() {};

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String val) {accountNumber = val; }

    public String getOpco() { return opco; }
    public void setOpco(String val) {opco = val; }

    public String getAssociatedAccountNumber() { return associatedAccountNumber; }
    public void setAssociatedAccountNumber(String val) {associatedAccountNumber = val; }
}
