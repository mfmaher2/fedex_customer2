package datastax.com.DAOs;

import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.mapper.annotations.*;
import datastax.com.dataObjects.Comment;

import java.time.Instant;

@Dao
public interface CommentDao {
    @Select
    Comment findByAccountNumber(String accountNum);

    @Select
    PagingIterable<Comment> findAllByAccountNumber(String accountNum);

    @Query("SELECT * FROM ${keyspaceId}.comment_v1 WHERE account_number = :accountNum AND opco = :opco AND comment__comment_date_time >= :startDateTime AND comment__comment_date_time <= :endDateTime")
    PagingIterable<Comment> findByAccountNumOpcoDateTimeRange(String accountNum, String opco, Instant startDateTime, Instant endDateTime);

    @Query("SELECT * FROM ${keyspaceId}.comment_v1 WHERE account_number = :accountNum AND comment__type = :type")
    PagingIterable<Comment> findByAccountNumType(String accountNum, String type);

    @Query("SELECT * FROM ${keyspaceId}.comment_v1 WHERE comment__type = :type")
    PagingIterable<Comment> findByCommentType(String type);

    @Insert
    void save(Comment account);

    @Update
    void update(Comment account);

    @Delete
    void delete(Comment account);

    @Query("DELETE FROM ${keyspaceId}.comment_v1 WHERE account_number = :accountNum")
    void deleteAllByAccountNumber(String accountNum);
}