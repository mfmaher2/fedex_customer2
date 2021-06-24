package datastax.com;

import com.datastax.oss.driver.api.mapper.annotations.*;
import java.util.concurrent.CompletableFuture;

import static com.datastax.oss.driver.api.mapper.entity.saving.NullSavingStrategy.DO_NOT_SET;
import static com.datastax.oss.driver.api.mapper.entity.saving.NullSavingStrategy.SET_TO_NULL;

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

    @Update(nullSavingStrategy = SET_TO_NULL)
    void updateNulls(CustomerAccount account);

    @Update(nullSavingStrategy = DO_NOT_SET)
    void updateNoSet(CustomerAccount account);

    @Delete
    void delete(CustomerAccount account);
}

