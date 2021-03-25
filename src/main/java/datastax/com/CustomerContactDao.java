package datastax.com;

import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Delete;
import com.datastax.oss.driver.api.mapper.annotations.Insert;
import com.datastax.oss.driver.api.mapper.annotations.Select;

@Dao
public interface CustomerContactDao {

    @Select
    CustomerContact findByContactDocumentId(long id);

    @Insert
    void save(CustomerContact contact);

    @Delete
    void delete(CustomerContact contact);
}
