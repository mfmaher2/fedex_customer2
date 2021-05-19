package datastax.com;

import com.datastax.oss.driver.api.core.MappedAsyncPagingIterable;
import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Query;
import com.datastax.oss.driver.api.mapper.annotations.Select;

import java.util.concurrent.CompletableFuture;

@Dao
public interface CustomerApplyDiscountDao {
    @Select
    CustomerApplyDiscount findByAccountNumber(String accountNum);

    @Select
    CompletableFuture<CustomerApplyDiscount> findByAccountNumberAsync(String accountNum);

    @Select
    CompletableFuture<MappedAsyncPagingIterable<CustomerApplyDiscount>> findAllByAccountNumberAsync(String accountNum);

//    @Select(customWhereClause = "account_number = :accountNum and solr_query = :solrParam")
//    CompletableFuture<MappedAsyncPagingIterable<CustomerApplyDiscount>> findAllByAccountSearchAsync(String accountNum, String solrParam);

//    @Query("select * from apply_discount_detail_v1 where account_number = '000001236'and solr_query = :solrParam")
//    CompletableFuture<MappedAsyncPagingIterable<CustomerApplyDiscount>> findAllByAccountSearchAsync(String solrParam);

    @Query("select * from apply_discount_detail_v1 where solr_query = :solrParam")
    CompletableFuture<MappedAsyncPagingIterable<CustomerApplyDiscount>> findAllByAccountSearchAsync(String solrParam);

}
