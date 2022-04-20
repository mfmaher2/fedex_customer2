package datastax.com.schemaElements;

import java.util.HashMap;
import java.util.Map;

public abstract class KeyspaceConfig {

    protected Map<Keyspaces, String> keyspaceNames = new HashMap<>();
    protected Map<Keyspaces, Map<String, Integer>> dataCenterMapping = new HashMap<>();

    KeyspaceConfig(){
    }

    protected void AssignKeyspaceNames(){
        //todo throw exception
    }

    protected void AssignDataCenterMappings(){
        //todo throw exception
    }

    protected void AddKeyspaceDataCenter(Keyspaces keyspace, String datacenter, Integer replicationFactor){
        Map<String, Integer> keyspaceEntry = dataCenterMapping.getOrDefault(keyspace, new HashMap<>());
        keyspaceEntry.put(datacenter, replicationFactor);
        dataCenterMapping.put(keyspace, keyspaceEntry);
    }

    public String getKeyspaceName(Keyspaces keyspace){
        return keyspaceNames.getOrDefault(keyspace, "");
    }

    public Map<Keyspaces, Map<String, Integer>> getDataCenterMapping(){
        return dataCenterMapping;
    }


}
