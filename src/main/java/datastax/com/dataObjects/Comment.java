package datastax.com.dataObjects;

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;

import java.time.Instant;

@Entity
@CqlName("comment_v1")
public class Comment {
    @PartitionKey private String accountNumber;
    @ClusteringColumn(0) private String opco;
    @CqlName("last_update_tmstp") private Instant lastUpdated;
    @ClusteringColumn(2) @CqlName("comment__type") private String commentType;
    @ClusteringColumn(3) @CqlName("comment__comment_id") private String commentID;
    @CqlName("comment__comment_description") private String commentDesc;
    @CqlName("comment__requester") private String requester;
    @CqlName("comment__department_number") private int departmentNumber;
    @CqlName("comment__employee__opco") private String employeeOpco;
    @CqlName("comment__employee__number") private String employeeNumber;
    @ClusteringColumn(1) @CqlName("comment__comment_date_time") private Instant commentDateTime;

    public Comment() {};

    //one property defined to allow DSE mapper annotation to work correctly
    //todo determine method to allow lombok and DSE java mapper to work without manually defining one property
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String val) {accountNumber = val; }

    public String getOpco() { return opco; }
    public void setOpco(String val) {opco = val; }

    //also need to define one non-primary key field to allow for proper DAO generation
    public Instant getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Instant val) { lastUpdated = val;}

    public String getCommentType() { return commentType; }
    public void setCommentType(String val) {commentType = val; }

    public String getCommentID() { return commentID; }
    public void setCommentID(String val) {commentID = val; }

    public Instant getCommentDateTime() { return commentDateTime; }
    public void setCommentDateTime(Instant val) { commentDateTime = val;}
}