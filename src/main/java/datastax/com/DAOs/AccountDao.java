package datastax.com.DAOs;

import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.mapper.annotations.*;
import datastax.com.dataObjects.Account;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Dao
public interface AccountDao {

    @Select
    Account findByAccountNumber(String accountNum);

    @Query("SELECT * FROM ${keyspaceId}.cust_acct_v1 WHERE account_number = :accountNum AND opco = :opcoParam")
    Account findByAccountNumberOpco(String accountNum, String opcoParam);

    @Select
    PagingIterable<Account> findAllByAccountNumber(String accountNum);

    @Select
    CompletableFuture<Account> findByAccountNumberAsync(String accountNum);

    @Query("SELECT * FROM ${keyspaceId}.cust_acct_v1 WHERE profile__account_type = :acctType ")
    PagingIterable<Account> findByProfileAccountType(String acctType);

    @Insert
    void save(Account account);

    @Insert
    BoundStatement batchSave(Account account);

    @Update
    void update(Account account);

    @Update
    BoundStatement batchUpdate(Account account);

    @Delete
    void delete(Account account);

    @Delete
    BoundStatement batchDelete(Account account);

    @Query("UPDATE ${keyspaceId}.cust_acct_v1 SET duty_tax_info = duty_tax_info + :mapEnt WHERE account_number = :acctNum AND opco = :opco") //TODO use common table ID instead of hard coded value
    void upsertDutyTaxInfoMapEntries(String acctNum, String opco, Map<String, String> mapEnt);

    @Query("UPDATE ${keyspaceId}.cust_acct_v1 SET profile__customer_type = :custType WHERE account_number = :acctNum AND opco = :opco") //TODO use common table ID instead of hard coded value
    void updateCustomerType(String acctNum, String opco, String custType);

    @Query("DELETE FROM ${keyspaceId}.cust_acct_v1 WHERE account_number = :accountNum")
    void deleteAllByAccountNumber(String accountNum);

    @Query("DELETE FROM ${keyspaceId}.cust_acct_v1 WHERE account_number = :accountNum")
    BoundStatement batchDeleteAllByAccountNumber(String accountNum);
}

