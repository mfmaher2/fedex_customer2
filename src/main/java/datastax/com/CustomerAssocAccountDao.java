package datastax.com;

import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Select;

import java.util.concurrent.CompletableFuture;

@Dao
public interface CustomerAssocAccountDao {

    @Select
    CustomerAssocAccount findByAccountNumber(String accountNum);

    @Select
    CompletableFuture<CustomerAssocAccount> findByAccountNumberAsync(String accountNum);
}
