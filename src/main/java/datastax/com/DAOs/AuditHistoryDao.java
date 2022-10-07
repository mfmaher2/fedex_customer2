package datastax.com.DAOs;

import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.mapper.annotations.*;
import datastax.com.dataObjects.AuditHistory;

import java.time.Instant;

@Dao
public interface AuditHistoryDao {

    @Select
    AuditHistory findByAccountNumber(String accountNum);

    @Select
    PagingIterable<AuditHistory> findAllByAccountNumber(String accountNum);

    @Query("SELECT * FROM ${qualifiedTableId} WHERE account_number = :accountNum AND last_update_tmstp >= :startDateTime AND last_update_tmstp <= :endDateTime")
    PagingIterable<AuditHistory> findByAccountNumDateTimeRange(String accountNum, Instant startDateTime, Instant endDateTime);

    @Select(customWhereClause = "solr_query = :solrQuery")
    @StatementAttributes(executionProfileName = "search")
    PagingIterable<AuditHistory> findBySearchQuery(String solrQuery);
    @Insert
    void save(AuditHistory account);

    @Update
    void update(AuditHistory account);

    @Delete
    void delete(AuditHistory account);

    @Query("DELETE FROM ${keyspaceId}.audit_history_v1 WHERE account_number = :accountNum")
    void deleteAllByAccountNumber(String accountNum);
}
