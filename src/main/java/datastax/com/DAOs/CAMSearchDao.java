package datastax.com.DAOs;

import com.datastax.oss.driver.api.mapper.annotations.*;
import datastax.com.dataObjects.CAMSearch;

@Dao
public interface CAMSearchDao {
    @Select
    CAMSearch findByAccountNumber(String accountNum);

    @Insert
    void save(CAMSearch account);

    @Update
    void update(CAMSearch account);

    @Delete
    void delete(CAMSearch account);

    @Query("DELETE FROM ${keyspaceId}.cam_search_v1 WHERE account_number = :accountNum")
    void deleteAllByAccountNumber(String accountNum);
}
