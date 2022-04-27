package datastax.com.schemaElements;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class EnvironmentCustomize
{
    //schema replacement values
    protected final String ENV_ID_PLACEHOLDER = "<ENV_LEVEL_ID>";

    //script file replacement values
    protected final String CAM_DDL_FILE = "cam_ddl.cql";
    protected final String CAM_SAI_FILE = "cam_sai_ddl.cql";
    protected final String CAM_SEARCH_FILE = "cam_search_ddl.cql";
    protected final String TEST_SCHEMA_FILE = "test_only_schema.cql";
    protected final String TEST_SEQ_NUM_FILE = "test_seq_num.cql";

    protected String outputDirectory = "";
    protected EnvironmentCustomizeParameters envConfig;
    protected Map<String, String> mapSourceToDestFiles = new HashMap<>();
    protected Map<String, String> mapScriptReplace = new HashMap<>();

    public EnvironmentCustomize(EnvironmentCustomizeParameters config) throws IOException {
        this.envConfig = config;

        mapSourceToDestFiles.put("cam_ddl_cql.sql", CAM_DDL_FILE);
        mapSourceToDestFiles.put("cam_sai_ddl_cql.sql", CAM_SAI_FILE);
        mapSourceToDestFiles.put("cam_search_ddl_cql.sql", CAM_SEARCH_FILE);
        mapSourceToDestFiles.put("test_only_schema_cql.sql", TEST_SCHEMA_FILE);
        mapSourceToDestFiles.put("test_seq_num_cql.sql", TEST_SEQ_NUM_FILE);
    }

    protected void assignScriptReplacementValues(){
        mapScriptReplace.put("<CQLSH_PATH>", envConfig.cqlshPath);
        mapScriptReplace.put("<SCHEMA_HOST>", envConfig.schemaCreateHost);
        mapScriptReplace.put("<SCHEMA_PORT>", envConfig.schemaCreatePort);
        mapScriptReplace.put("<SEARCH_HOST>", envConfig.schemaCreateHost);
        mapScriptReplace.put("<SEARCH_PORT>", envConfig.searchIndexCreatePort);
        mapScriptReplace.put("<SCHEMA_FOLDER>", outputDirectory);
    }

    public void generateCustomizeEnvironment() throws IOException {
        String camSchemaDirPrfix = "camSchema_";

        Path outputPath = Files.createTempDirectory(camSchemaDirPrfix);
        outputDirectory = outputPath.toString();
        System.out.println("Temporary folder created - " + outputDirectory);

        assignScriptReplacementValues();

        mapSourceToDestFiles.forEach((source,destination) -> {
            String sourceFullPath = envConfig.schemaSourceFilesPath + "/" + source;
            String destinationFullPath = outputPath.toAbsolutePath() + "/" + destination ;
            try {
                translateEnvironmentFile(sourceFullPath, destinationFullPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } );
    }

    protected void translateEnvironmentFile(String sourceFile, String destinationFile) throws IOException {

            BufferedWriter writer = new BufferedWriter(new FileWriter(destinationFile));
            Stream<String> readStream = Files.lines(Paths.get(sourceFile));
            readStream.forEach(line -> {
                try {
                    writer.write(line.replace(ENV_ID_PLACEHOLDER, envConfig.environmentID) + "\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            writer.close();


//        try{
//            BufferedWriter writer = new BufferedWriter(new FileWriter(destinationFile));
//            Stream<String> readStream = Files.lines(Paths.get(sourceFile));
//                readStream.forEach(line -> {
//                    try {
//                        writer.write(line.replace(ENV_ID_PLACEHOLDER, envConfig.environmentID) + "\n");
//                    } catch (IOException e) {
//                        System.out.println("Error writing to temporary file " + destinationFile);
//                        System.out.println(e.getMessage());
//                        throw new RuntimeException(e);
//                    }
//                    catch(Exception e){
//                        System.out.println("General exception writing to temporary file " + destinationFile);
//                        System.out.println(e.getMessage());
//                        throw new RuntimeException(e);
//                    }
//                    finally {
//                        writer.close();
//                    }
//                });
//        } catch (IOException e) {
//            System.out.println("Error processing file translation, source- " +  sourceFile + "  destination- " + destinationFile);
//            System.out.println(e.getMessage());
//            throw new RuntimeException(e);
//        }
//        catch(Exception e){
//            System.out.println("General exception processing file translation, source- " +  sourceFile + "  destination- " + destinationFile);
//            System.out.println(e.getMessage());
//            throw new RuntimeException(e);
//
//        }
    }

    public String getSchemaCreationFilePath(){
        return outputDirectory; //todo add file path
    }
}
