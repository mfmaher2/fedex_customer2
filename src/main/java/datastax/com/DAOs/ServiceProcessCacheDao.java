package datastax.com.DAOs;

import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Insert;
import datastax.com.dataObjects.ServiceProcessCache;

@Dao
public interface ServiceProcessCacheDao {
    @Insert
    void save(ServiceProcessCache account);
}
