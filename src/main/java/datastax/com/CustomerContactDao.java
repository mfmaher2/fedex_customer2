package datastax.com;

import com.datastax.oss.driver.api.mapper.annotations.*;
import java.util.concurrent.CompletableFuture;

@Dao
public interface CustomerContactDao {

    @Select
    CustomerContact findByContactDocumentId(long id);

    @Select
    CompletableFuture<CustomerContact> findByContactDocumentIdAsync(long id);

    @Insert
    void save(CustomerContact contact);

    @Update
    void update(CustomerContact contact);

    @Delete
    void delete(CustomerContact contact);
}
