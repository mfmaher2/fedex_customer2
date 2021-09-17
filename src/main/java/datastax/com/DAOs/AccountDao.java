package datastax.com.DAOs;

import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.mapper.annotations.*;
import datastax.com.dataObjects.Account;

import java.util.concurrent.CompletableFuture;

@Dao
public interface AccountDao {

    @Select
    Account findByAccountNumber(String accountNum);

    @Select
    PagingIterable<Account> findAllByAccountNumber(String accountNum);

    @Select
    CompletableFuture<Account> findByAccountNumberAsync(String accountNum);

    @Insert
    void save(Account account);

    @Update
    void update(Account account);

    @Delete
    void delete(Account account);

//    @Query("UPDATE ${keyspaceId}.cust_acct_v1 SET duty_tax_info = duty_tax_info + {:newKey : :newValue} WHERE account_number = :acctNum AND opco = :opco") //TODO use common table ID instead of hard coded value
//    void addDutyTaxInfoEntry(String acctNum, String opco, String newKey, String newValue);

    @Query("UPDATE ${keyspaceId}.cust_acct_v1 SET duty_tax_info = duty_tax_info + {'key4': 'val4'} WHERE account_number = :acctNum AND opco = :opco") //TODO use common table ID instead of hard coded value
    void addDutyTaxInfoEntry(String acctNum, String opco);

    @Query("UPDATE ${keyspaceId}.cust_acct_v1 SET duty_tax_info = duty_tax_info + :mapEnt WHERE account_number = :acctNum AND opco = :opco") //TODO use common table ID instead of hard coded value
    void addDutyTaxInfoMapEntry(String acctNum, String opco, String mapEnt);

    @Query("UPDATE ${keyspaceId}.cust_acct_v1 SET profile__customer_type = :custType WHERE account_number = :acctNum AND opco = :opco") //TODO use common table ID instead of hard coded value
    void updateCustomerType(String acctNum, String opco, String custType);
}

