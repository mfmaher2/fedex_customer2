package datastax.com.DAOs;

import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Insert;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import datastax.com.dataObjects.Account;
import datastax.com.dataObjects.ServiceProcessCache;

@Dao
public interface ServiceProcessCacheDao {
    @Insert
    void save(ServiceProcessCache cacheEntry);

    @Insert
    BoundStatement batchSave(ServiceProcessCache cacheEntry);

    @Select
    ServiceProcessCache findByTransactionId(String transID);
}
