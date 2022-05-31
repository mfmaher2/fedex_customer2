package datastax.com.schemaElements;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.querybuilder.select.Select;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.bindMarker;
import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.selectFrom;
import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.dropKeyspace;
import static com.datastax.oss.driver.api.querybuilder.SchemaBuilder.dropTable;

public class EnvironmentHelpers {

    public static void loadEnvironmentSchema(Environment environment) throws IOException, InterruptedException {
        System.out.println("\tBeginning keypspace creation");
        KeyspaceCreator.createKeyspacesFromConfig(
                environment.getKeyspaceConfig(),
                environment.getSessionMap().get(DataCenter.SEARCH)
                );
        System.out.println("\tKeyspace creation complete");

        System.out.println("\tBeginning table and index creation");
        runScript(environment.getCustomizedEnvironment().getSchemaCreationFilePath());
        System.out.println("\tTable and index creation complete");
    }

    public static void loadEnvironmentData(Environment environment) throws IOException, InterruptedException {
        runScript(environment.getCustomizedEnvironment().getLoadDataFilePath());

        //sleep for a time to allow Solr indexes to update completely
        System.out.println("Completed data load.  Pausing to allow indexes to update...");
        Thread.sleep(11000);
    }

    public static void dropEnvironmentKeyspaces(Environment environment, boolean dropTables) throws InterruptedException {
        CqlSession localSession = environment.getSessionMap().get(DataCenter.SEARCH); //**Note use search for all functionality

        for (Keyspace ks : Keyspace.values()) {
            String ksName = environment.getKeyspaceConfig().getKeyspaceName(ks);

            if (dropTables) {
                System.out.println("Dropping tables from keyspace - " + ksName);
                long startTables = System.currentTimeMillis();

                Select select = selectFrom("system_schema", "tables")
                        .column("table_name")
                        .whereColumn("keyspace_name").isEqualTo(bindMarker());

                ResultSet resultSet = localSession.execute(select.build(ksName));
                List<Row> foundRows = resultSet.all();
                for (Row curRow : foundRows) {
                    String curTable = curRow.getString("table_name");
                    localSession.execute(dropTable(ksName, curTable).ifExists().build());
                    System.out.println("\tdrop table - " + curTable);
                }
                long stopTables = System.currentTimeMillis();
                System.out.println("Time for dropping all tables from keyspace " + ksName + "- " + ((stopTables - startTables) / 1000.0) + "s");
            }

            long startKeySpace = System.currentTimeMillis();
            System.out.println("Dropping keyspace - " + ksName);
            localSession.execute(dropKeyspace(ksName).ifExists().build());
            long stopKeyspace = System.currentTimeMillis();
            System.out.println("Time for dropping keyspace - " + ((stopKeyspace - startKeySpace) / 1000.0) + "s");

            Thread.sleep(150L);
        }
    }

    static void runScript(String scriptPath) throws InterruptedException, IOException {
        ProcessBuilder processBuild = new ProcessBuilder(Paths.get(scriptPath).toAbsolutePath().toString());
        Process process = processBuild.start();

        int exitValue = process.waitFor();
        if (exitValue != 0) {
            // check for errors
            new BufferedInputStream(process.getErrorStream());
            throw new RuntimeException("execution of script failed!");
        }
    }
}
