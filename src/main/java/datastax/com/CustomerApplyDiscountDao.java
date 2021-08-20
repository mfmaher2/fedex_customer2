package datastax.com;

import com.datastax.oss.driver.api.core.MappedAsyncPagingIterable;
import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.mapper.annotations.*;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

@Dao
public interface CustomerApplyDiscountDao {
    @Select
    CustomerApplyDiscount findByAccountNumber(String accountNum);

    @Select
    CompletableFuture<CustomerApplyDiscount> findByAccountNumberAsync(String accountNum);

    @Select (customWhereClause = "account_number = :accountNum and opco = :opco and apply_discount__effective_date_time = :effDT and last_updt_tmstp = :updtDT")
    CustomerApplyDiscount findByKeys(String accountNum, String opco, Instant effDT, Instant updtDT);

    @Select
    CompletableFuture<MappedAsyncPagingIterable<CustomerApplyDiscount>> findAllByAccountNumberAsync(String accountNum);

    @Select(customWhereClause = "account_number = :accountNum and solr_query = :solrParam")
    CompletableFuture<MappedAsyncPagingIterable<CustomerApplyDiscount>> findAllByAccountSearchAsync(String accountNum, String solrParam);

    @Insert
    void save(CustomerApplyDiscount applyDiscount);

    @Update
    void update(CustomerApplyDiscount applyDiscount);

    @Delete
    void delete(CustomerApplyDiscount applyDiscount);
}
