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

    @Select(customWhereClause = "account_number = :accountNum and solr_query = :solrParam")
    CompletableFuture<MappedAsyncPagingIterable<ApplyDiscount>> findAllByAccountSearchAsync(String accountNum, String solrParam);

    @Insert
    void save(ApplyDiscount applyDiscount);

    @Update
    void update(ApplyDiscount applyDiscount);

    @Delete
    void delete(ApplyDiscount applyDiscount);
}
