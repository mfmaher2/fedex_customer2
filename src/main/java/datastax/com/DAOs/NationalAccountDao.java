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

    @Select
    PagingIterable<NationalAcccount> findByAccountNumber(String accountNum);

    @Select
    PagingIterable<NationalAcccount> findByAccountNumber(String accountNum, Function<BoundStatementBuilder, BoundStatementBuilder> setAttributes);

    @Select
    CompletableFuture<NationalAcccount> findByAccountNumberAsync(String accountNum);

    @Select(customWhereClause = "national_account_detail__national_account_nbr = :queryParam")
    PagingIterable<NationalAcccount> findBySAINationalAccount(String queryParam);

    @GetEntity
    NationalAcccount asNationalAccount(Row row);

//    @Insert
//    void save(CustomerNationalAcccount account);

//    @Update
//    void update(CustomerNationalAcccount account);

//    @Delete
//    void delete(CustomerNationalAcccount account);
}
