package datastax.com.dataObjects;

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;

import java.util.Map;
import java.util.Set;

@Entity
@CqlName("index_collection_test")
public class IndexCollection {

    @PartitionKey private String accountNumber;
    @ClusteringColumn private String opco;
    @CqlName("name_line") private Map<String, String> nameLine;
    @CqlName("test_val") private String testVal;
    @CqlName("test_val_grams") private Set<String> testValGrams;

    public IndexCollection() {};

    public String getAccountNumber() { return accountNumber;}
    public void setAccountNumber(String val) { accountNumber = val;}

    public String getOpco() {return opco;}
    public void setOpco(String val) {opco = val;}

    public Map<String, String> getNameLine() { return nameLine; }
    public void setNameLine(Map<String, String> val) { nameLine = val; }

    public String getTestVal() {return testVal;}
    public void setTestVal(String val) {testVal = val;}

    public Set<String> getTestValGrams() { return  testValGrams; }
    public void setTestValGrams(Set<String> val) {testValGrams = val; }
}
