package datastax.com.dataObjects;

import com.datastax.oss.driver.api.mapper.annotations.*;

import java.nio.ByteBuffer;

@Entity
@CqlName("processing_cache_object")
public class ServiceProcessCache {

    @PartitionKey @CqlName("transaction_id") private String transactionID;
    @ClusteringColumn(0) @CqlName("table_name") private String tableName;
    @ClusteringColumn(1) @CqlName("table_primary_key_values") private String tableKeyValues;
    @CqlName("prevous_entry") private ByteBuffer previousEntry;
    @Computed("writetime(prevous_entry)") private long previousEntry_wrtm;

    public ServiceProcessCache() {};

    public String getTransactionID() { return transactionID; }
    public void setTransactionID(String val) { transactionID = val; }

    public String getTableName() { return tableName; }
    public void setTableName(String val) { tableName = val; }

    public String getTableKeyValues() { return tableKeyValues; }
    public void setTableKeyValues(String val) { tableKeyValues = val; }

    public ByteBuffer getPreviousEntry() { return previousEntry; }
    public void setPreviousEntry(ByteBuffer val) { previousEntry = val; }

    public long getPreviousEntry_wrtm() {return previousEntry_wrtm;}

    public void setPreviousEntry_wrtm(long previousEntry_wrtm) {
        this.previousEntry_wrtm = previousEntry_wrtm;
    }
}
