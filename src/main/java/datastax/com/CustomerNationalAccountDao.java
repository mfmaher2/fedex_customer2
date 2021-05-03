package datastax.com;

import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.core.cql.BoundStatementBuilder;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.mapper.annotations.*;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Dao
public interface CustomerNationalAccountDao {

//    @Query("select * from national_account_v1 where account_number = :accountNum")
    @Select
    PagingIterable<CustomerNationalAcccount> findByAccountNumber(String accountNum);

    @Select
    PagingIterable<CustomerNationalAcccount> findByAccountNumber(String accountNum, Function<BoundStatementBuilder, BoundStatementBuilder> setAttributes);

    @Select
    CompletableFuture<CustomerNationalAcccount> findByAccountNumberAsync(String accountNum);

    @GetEntity
    CustomerNationalAcccount asNationalAccount(Row row);


//    @Insert
//    void save(CustomerNationalAcccount account);

//    @Update
//    void update(CustomerNationalAcccount account);

//    @Delete
//    void delete(CustomerNationalAcccount account);
}
