package datastax.com.DAOs;

import com.datastax.oss.driver.api.core.MappedAsyncPagingIterable;
import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.mapper.annotations.*;
import datastax.com.dataObjects.ApplyDiscount;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

@Dao
public interface ApplyDiscountDao {
    @Select
    ApplyDiscount findByAccountNumber(String accountNum);

    @Select
    PagingIterable<ApplyDiscount> findAllByAccountNumber(String accountNum);

    @Select
    CompletableFuture<ApplyDiscount> findByAccountNumberAsync(String accountNum);

    @Select (customWhereClause = "account_number = :accountNum and opco = :opco and apply_discount__effective_date_time = :effDT")
    ApplyDiscount findByKeys(String accountNum, String opco, Instant effDT);

    @Select
    CompletableFuture<MappedAsyncPagingIterable<ApplyDiscount>> findAllByAccountNumberAsync(String accountNum);

    @Select(customWhereClause = "account_number = :accountNum AND opco = :opco AND apply_discount__effective_date_time >= :startDateTime AND apply_discount__expiration_date_time <= :endDateTime")
    PagingIterable<ApplyDiscount> findByAccountNumDateTimeRange(String accountNum, String opco, Instant startDateTime, Instant endDateTime);

    @Select(customWhereClause = "account_number = :accountNum AND opco = :opco")
    CompletableFuture<MappedAsyncPagingIterable<ApplyDiscount>> findByAccountOpcoAsync(String accountNum, String opco);

    @Select(customWhereClause = "account_number = :accountNum AND opco = :opco AND apply_discount__effective_date_time >= :startDateTime AND apply_discount__expiration_date_time <= :endDateTime")
    CompletableFuture<MappedAsyncPagingIterable<ApplyDiscount>> findByAccountNumDateTimeRangeAsync(String accountNum, String opco, Instant startDateTime, Instant endDateTime);

    @Insert
    void save(ApplyDiscount applyDiscount);

    @Update
    void update(ApplyDiscount applyDiscount);

    @Delete
    void delete(ApplyDiscount applyDiscount);
}
