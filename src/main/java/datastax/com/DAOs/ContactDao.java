package datastax.com.DAOs;

import com.datastax.oss.driver.api.mapper.annotations.*;
import datastax.com.dataObjects.Contact;

import java.util.concurrent.CompletableFuture;

@Dao
public interface ContactDao {

    @Select
    Contact findByContactDocumentId(long id);

    @Select
    CompletableFuture<Contact> findByContactDocumentIdAsync(long id);

    @Insert
    void save(Contact contact);

    @Update
    void update(Contact contact);

    @Delete
    void delete(Contact contact);
}
