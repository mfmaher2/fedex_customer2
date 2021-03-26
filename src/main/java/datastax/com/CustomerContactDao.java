package datastax.com;

import com.datastax.oss.driver.api.mapper.annotations.*;

@Dao
public interface CustomerContactDao {

    @Select
    CustomerContact findByContactDocumentId(long id);

    @Insert
    void save(CustomerContact contact);

    @Update
    void update(CustomerContact contact);

    @Delete
    void delete(CustomerContact contact);
}
