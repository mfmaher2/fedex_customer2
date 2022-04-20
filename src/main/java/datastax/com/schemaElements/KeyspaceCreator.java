package datastax.com.schemaElements;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.querybuilder.schema.CreateKeyspace;
import com.datastax.oss.driver.shaded.guava.common.collect.ImmutableMap;

import java.util.Map;

import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.createKeyspace;

public class KeyspaceCreator {

    public static void createKeyspacesFromConfig(KeyspaceConfig config, CqlSession session){

        Map<Keyspaces, Map<String, Integer>> keyspacMap = config.getDataCenterMapping();

        for(Keyspaces ks : keyspacMap.keySet()){
//            String ksName = ks.keyspaceName();
            String ksName = config.getKeyspaceName(ks);
            System.out.println("Creating keyspace - " + ksName);
            CreateKeyspace create = createKeyspace(ksName).ifNotExists()
//                        .withNetworkTopologyStrategy(ImmutableMap.of("SearchGraphAnalytics", 1))
//                    .withNetworkTopologyStrategy(ImmutableMap.of("core", 1))
                    .withNetworkTopologyStrategy(keyspacMap.get(ks))
                    .withDurableWrites(true);

            System.out.println(create.asCql()); //optional output of complete keyspace creation CQL statement
            session.execute(create.build());

        }

    }
}
