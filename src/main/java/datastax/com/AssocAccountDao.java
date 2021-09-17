package datastax.com;

import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Select;

import java.util.concurrent.CompletableFuture;

@Dao
public interface AssocAccountDao {

    @Select
    AssocAccount findByAccountNumber(String accountNum);

    @Select
    CompletableFuture<AssocAccount> findByAccountNumberAsync(String accountNum);
}
