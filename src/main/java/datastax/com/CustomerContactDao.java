package datastax.com;

import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.mapper.annotations.*;

import java.sql.ResultSet;

@Dao
public interface CustomerContactDao {

    @Select
    CustomerContact findByContactDocumentId(long id);

//    @Select
//    @Query("SELECT * FROM ${keyspaceId}.${tableId}\n" +
//            "WHERE\n" +
//            "    solr_query = :solr_query")  //'" +

//    @Query("SELECT * FROM contact\n" +
//            "WHERE\n" +
//            "    solr_query = :solr_stmt") ; //'" +
//            "{\"q\": \"{!tuple}address__secondary.unit::unit\"," +
//            "\"sort\": \"contact_document_id asc\"}'")
//    @Query("SELECT * FROM contact WHERE solr_query = '{\"q\": \"{!tuple}address__secondary.unit::unit\", \"sort\": \"contact_document_id asc\"}'")
//    PagingIterable<CustomerContact> findByContactBySolrSecAddrUnit(String solr_query);

//    @Query("SELECT * FROM ${keyspaceId}.${tableId}\n" +
//            "WHERE\n" +
//            "    solr_query = :solr_query")  //'" +

    @Query("SELECT * FROM ${keyspaceId}.${tableId} WHERE solr_query = :solr_query")
//    @Query("select * from contact where solr_query = :solr_query") // +
//            "'{\"q\": \"{!tuple}address__secondary.unit::unit\"," +
//            "\"sort\": \"contact_document_id asc\"}'")
    PagingIterable<CustomerContact> findByContactBySolrSecAddrUnit2(String solr_query);
//    ResultSet findByContactBySolrSecAddrUnit2(String solr_query);
//    ResultSet findByContactBySecAddrUnit2(String unit);

    @Select(customWhereClause = "solr_query = :solr_stmt")
    PagingIterable<CustomerContact> findByContactBySolrSecAddrUnit3(/*@CqlName("solr_query")*/ String solr_stmt);

    @Insert
    void save(CustomerContact contact);

    @Update
    void update(CustomerContact contact);

    @Delete
    void delete(CustomerContact contact);
}
