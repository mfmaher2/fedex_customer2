package datastax.com.schemaElements;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.querybuilder.schema.CreateKeyspace;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.createKeyspace;

public class KeyspaceCreator {

    public static void createKeyspacesFromConfig(KeyspaceConfig config, CqlSession session){

        Map<Keyspace, Map<String, Integer>> keyspacMap = config.getDataCenterMapping();

        for(Keyspace ks : keyspacMap.keySet()){
            String ksName = config.getKeyspaceName(ks);
            System.out.println("Creating keyspace - " + ksName);
            CreateKeyspace create = createKeyspace(ksName).ifNotExists()
                    .withNetworkTopologyStrategy(keyspacMap.get(ks))
                    .withDurableWrites(true);

            System.out.println(create.asCql()); //optional output of complete keyspace creation CQL statement
            session.execute(create.build());
        }
    }

    public static void outputKeyspacesCreationCQL(KeyspaceConfig config, String outputFilePath) throws IOException {

        try(FileWriter writer = new FileWriter(Paths.get(outputFilePath).toFile())) {
            Map<Keyspace, Map<String, Integer>> keyspacMap = config.getDataCenterMapping();

            for (Keyspace ks : keyspacMap.keySet()) {
                String ksName = config.getKeyspaceName(ks);
                System.out.println("Creating keyspace - " + ksName);
                CreateKeyspace create = createKeyspace(ksName).ifNotExists()
                        .withNetworkTopologyStrategy(keyspacMap.get(ks))
                        .withDurableWrites(true);

                System.out.println(create.asCql()); //optional output of complete keyspace creation CQL statement
                writer.write(create.asCql() + "\n");
            }
        }
    }
}
