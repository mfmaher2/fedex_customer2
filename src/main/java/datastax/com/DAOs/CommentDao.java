package datastax.com.DAOs;

import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.mapper.annotations.*;
import datastax.com.dataObjects.Comment;

import java.time.Instant;

@Dao
public interface CommentDao {
     @Select
    Comment findByAccountNumber(String accountNum);

    @Select
    PagingIterable<Comment> findAllByAccountNumber(String accountNum);

    @Query("SELECT * FROM ${keyspaceId}.comment_v1 WHERE comment__comment_id = :commentID")
    PagingIterable<Comment> findByCommentID(String commentID);

//    @Query("SELECT * FROM ${keyspaceId}.comment_v1 WHERE account_number = :accountNum AND opco in ('FX','FDFR') AND comment__type in ('GI') AND comment__comment_date_time >= :startDateTime AND comment__comment_date_time <= :endDateTime")
//    PagingIterable<Comment> findByAccountNumDateTimeRange(String accountNum, Instant startDateTime, Instant endDateTime);

    //@Query("SELECT * FROM ${keyspaceId}.comment_v1 WHERE account_number = :accountNum AND comment__comment_date_time >= :startDateTime AND comment__comment_date_time <= :endDateTime AND comment__type = :type ")
    //PagingIterable<Comment> findByAccountNumTypeDateTimeRange(String accountNum, Instant startDateTime, Instant endDateTime, String type);

//    @Query("SELECT * FROM ${keyspaceId}.comment_v1 WHERE account_number = :accountNum AND opco = :opco AND comment__type in ('GI') AND comment__comment_date_time >= :startDateTime AND comment__comment_date_time <= :endDateTime")
//    PagingIterable<Comment> findByAccountNumOpcoDateTimeRange(String accountNum, String opco, Instant startDateTime, Instant endDateTime);

//    @Query("SELECT * FROM ${keyspaceId}.comment_v1 WHERE account_number = :accountNum AND opco = :opco AND comment__comment_date_time >= :startDateTime AND comment__comment_date_time <= :endDateTime AND comment__type = :type ")
//    PagingIterable<Comment> findByAccountNumOpcoTypeDateTimeRange(String accountNum, String opco, Instant startDateTime, Instant endDateTime, String type);

    //@Query("SELECT * FROM ${keyspaceId}.comment_v1 WHERE account_number = :accountNum AND comment__type = :type")
    //PagingIterable<Comment> findByAccountNumType(String accountNum, String type);

    //@Query("SELECT * FROM ${keyspaceId}.comment_v1 WHERE comment__type = :type")
    //PagingIterable<Comment> findByCommentType(String type);

    @Query("SELECT * FROM ${keyspaceId}.comment_v1 WHERE comment__comment_id = :commentId")
    Comment findByCommentId(String commentId);

    @Insert
    void save(Comment account);

    @Update
    void update(Comment account);

    @Delete
    void delete(Comment account);

    // YOU WILL NEED EXPLICIT DELETE STATEMENTS IF WE WANT TO DELETE BY NUMBER AND TYPE
    // COMMENT USUALLY NEVER HAS A DELETE SCENARIO UNLESS SYSTEM REQUESTED DUE TO ISSUE.
    @Query("DELETE FROM ${keyspaceId}.comment_v1 WHERE account_number = :accountNum")
    void deleteAllByAccountNumber(String accountNum);

//    @Query("DELETE FROM ${keyspaceId}.comment_v1 WHERE comment__comment_id = :commentId")
//    void deleteByCommentId(String commentId);

    @Query("DELETE FROM ${keyspaceId}.comment_v1 WHERE account_number = :accountNum AND opco = :opco AND comment__type = :type AND comment__comment_date_time = :commentDT")
    void deleteByKeys(String accountNum, String opco, String type, Instant commentDT);

 // BATCH STATEMENTS
    @Insert
    BoundStatement batchSave(Comment comment);

    @Delete
    BoundStatement batchDelete(Comment comment);
}

