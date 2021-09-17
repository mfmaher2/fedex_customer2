package datastax.com.DAOs;

import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.core.cql.BoundStatementBuilder;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.mapper.annotations.*;
import datastax.com.dataObjects.NationalAcccount;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Dao
public interface NationalAccountDao {

//    @Query("select * from national_account_v1 where account_number = :accountNum")
    @Select
    PagingIterable<NationalAcccount> findByAccountNumber(String accountNum);

    @Select
    PagingIterable<NationalAcccount> findByAccountNumber(String accountNum, Function<BoundStatementBuilder, BoundStatementBuilder> setAttributes);

    @Select
    CompletableFuture<NationalAcccount> findByAccountNumberAsync(String accountNum);

    @Query("select * from national_account_v1 where solr_query = :nationalAcct")
    PagingIterable<NationalAcccount> findByNationalAccountNumberFullSolrParam(String nationalAcct);

    @Select(customWhereClause = "solr_query = :solrParam")
    PagingIterable<NationalAcccount> findBySearchQuery(String solrParam);

    @GetEntity
    NationalAcccount asNationalAccount(Row row);


//    @Insert
//    void save(CustomerNationalAcccount account);

//    @Update
//    void update(CustomerNationalAcccount account);

//    @Delete
//    void delete(CustomerNationalAcccount account);
}
