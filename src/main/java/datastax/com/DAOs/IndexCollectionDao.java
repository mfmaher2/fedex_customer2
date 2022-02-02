package datastax.com.DAOs;

import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.mapper.annotations.*;
import datastax.com.dataObjects.IndexCollection;

@Dao
public interface IndexCollectionDao {
    @Select
    IndexCollection findByAccountNumber(String accountNum);

    @Select
    PagingIterable<IndexCollection> findAllByAccountNumber(String accountNum);

    @Query("SELECT * FROM ${keyspaceId}.index_collection_test WHERE test_val_grams CONTAINS :substring")
    PagingIterable<IndexCollection> findByPartialText(String substring);

    @Insert
    void save(IndexCollection record);

    @Update
    void update(IndexCollection record);

    @Delete
    void delete(IndexCollection record);
}
