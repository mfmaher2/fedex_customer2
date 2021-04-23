package datastax.com;

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;

@Entity
@CqlName("cust_acct_v1")
public class CustomerAccount {

    @PartitionKey private String accountNumber;
    @ClusteringColumn private String opco;
    @CqlName("profile__customer_type") private String profileCustomerType;
    @CqlName("profile__account_type") private String profileAccountType;


    public CustomerAccount() {};

    public String getAccountNumber() { return accountNumber;}
    public void setAccountNumber(String val) { accountNumber = val;}

    public String getOpco() {return opco;}
    public void setOpco(String val) {opco = val;}

    public String getProfileCustomerType() { return profileCustomerType;}
    public void setProfileCustomerType(String val) { profileCustomerType = val;}

    public String getProfileAccountType() { return profileAccountType;}
    public void setProfileAccountType(String val) { profileAccountType = val;}
}
