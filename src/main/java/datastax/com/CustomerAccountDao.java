package datastax.com;

import com.datastax.oss.driver.api.mapper.annotations.*;
import java.util.concurrent.CompletableFuture;

@Dao
public interface CustomerAccountDao {

    @Select
    CustomerAccount findByAccountNumber(String accountNum);

    @Select
    CompletableFuture<CustomerAccount> findByAccountNumberAsync(String accountNum);

    @Insert
    void save(CustomerAccount account);

    @Update
    void update(CustomerAccount account);

    @Delete
    void delete(CustomerAccount account);
}

