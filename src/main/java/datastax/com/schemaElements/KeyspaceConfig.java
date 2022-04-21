package datastax.com.schemaElements;

import java.util.HashMap;
import java.util.Map;

public abstract class KeyspaceConfig {

    protected Map<Keyspace, String> keyspaceNames = new HashMap<>();
    protected Map<Keyspace, Map<String, Integer>> dataCenterMapping = new HashMap<>();

    KeyspaceConfig(){
    }

    protected void AssignKeyspaceNames(){
        //todo throw exception
    }

    protected void AssignDataCenterMappings(){
        //todo throw exception
    }

    protected void AddKeyspaceDataCenter(Keyspace keyspace, String datacenter, Integer replicationFactor){
        Map<String, Integer> keyspaceEntry = dataCenterMapping.getOrDefault(keyspace, new HashMap<>());
        keyspaceEntry.put(datacenter, replicationFactor);
        dataCenterMapping.put(keyspace, keyspaceEntry);
    }

    public String getKeyspaceName(Keyspace keyspace){
        return keyspaceNames.getOrDefault(keyspace, "");
    }

    public Map<Keyspace, Map<String, Integer>> getDataCenterMapping(){
        return dataCenterMapping;
    }


}
