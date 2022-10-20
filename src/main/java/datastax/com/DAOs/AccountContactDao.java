package datastax.com.DAOs;

import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.mapper.annotations.*;
import datastax.com.dataObjects.AccountContact;

@Dao
public interface AccountContactDao {
    @Select
    AccountContact findByAccountNumber(String accountNum);

    @Select
    PagingIterable<AccountContact> findAllByAccountNumber(String accountNum);

    @Select(customWhereClause = "account_number = :accountNum AND opco = :opco AND contact_type_code = :contactTypeCode AND contact_business_id = :contactBusinessID")
    AccountContact findByKeys(String accountNum, String opco, String contactTypeCode, String contactBusinessID);

    @Select(customWhereClause = "person__last_name = :lastName")
    PagingIterable<AccountContact> findAllByLastName(String lastName);

    @Insert
    void save(AccountContact contact);

    @Insert
    BoundStatement batchSave(AccountContact contact);

    @Update
    void update(AccountContact contact);

    @Update
    BoundStatement batchUpdate(AccountContact contact);

    @Delete
    void delete(AccountContact contact);

    @Query("DELETE FROM ${keyspaceId}.account_contact WHERE account_number = :accountNum")
    void deleteAllByAccountNumber(String accountNum);
}
