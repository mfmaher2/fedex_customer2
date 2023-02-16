package datastax.com.DAOs;

import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.mapper.annotations.*;
import datastax.com.dataObjects.GroupInfo;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Dao
public interface GroupInfoDao {

    @Select
    GroupInfo findByAccountNumber(String accountNum);

    @Query("SELECT * FROM ${keyspaceId}.group_info_v1 WHERE account_number = :accountNum AND opco = :opcoParam AND group_id__code = :groupIdCode AND group_id__number = :groupIdNumber AND group_id__type = :groupIdType")
    GroupInfo findByAccountNumberOpco(String accountNum, String opcoParam);

    @Select
    PagingIterable<GroupInfo> findAllByAccountNumber(String accountNum);

    @Select
    CompletableFuture<GroupInfo> findByAccountNumberAsync(String accountNum);

//    @Query("SELECT * FROM ${keyspaceId}.group_info_v1 WHERE group_id__type = :acctType ")
//    PagingIterable<GroupInfo> findByGroupIdType(String idType);

    @Insert
    void save(GroupInfo account);

    @Insert
    BoundStatement batchSave(GroupInfo account);

    @Update
    void update(GroupInfo account);

//    @Update
//    BoundStatement batchUpdate(GroupInfo account);

    @Delete
    void delete(GroupInfo account);

    @Delete
    BoundStatement batchDelete(GroupInfo account);

//    @Query("UPDATE ${keyspaceId}.group_info_v1 SET duty_tax_info = duty_tax_info + :mapEnt WHERE account_number = :acctNum AND opco = :opco") //TODO use common table ID instead of hard coded value
//    void upsertDutyTaxInfoMapEntries(String acctNum, String opco, Map<String, String> mapEnt);
//
//    @Query("UPDATE ${keyspaceId}.group_info_v1 SET profile__customer_type = :custType WHERE account_number = :acctNum AND opco = :opco") //TODO use common table ID instead of hard coded value
//    void updateCustomerType(String acctNum, String opco, String custType);

    @Query("DELETE FROM ${keyspaceId}.group_info_v1 WHERE account_number = :accountNum")
    void deleteAllByAccountNumber(String accountNum);

    @Query("DELETE FROM ${keyspaceId}.group_info_v1 WHERE account_number = :accountNum")
    BoundStatement batchDeleteAllByAccountNumber(String accountNum);
}

