package datastax.com.schemaElements;

import org.apache.commons.lang3.text.StrSubstitutor;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class EnvironmentCustomize
{
    //schema replacement values
    protected final String ENV_ID_PLACEHOLDER = "ENV_LEVEL_ID";

    //output schema files, output files with customized values
    protected final String CAM_DDL_FILE = "cam_ddl.cql";
    protected final String CAM_SAI_FILE = "cam_sai_ddl.cql";
    protected final String CAM_SEARCH_FILE = "cam_search_ddl.cql";
    protected final String CAM_SEARCH_SOLR_FILE = "cam_search_solr_ddl.cql";
    protected final String TEST_SCHEMA_FILE = "test_only_schema.cql";
    protected final String TEST_SEQ_NUM_FILE = "test_seq_num.cql";


    //script file related values
    protected final String SCHEMA_CREATE_FILE = "create_customer_schema.sh";
    protected final String DATA_LOAD_FILE = "load_customer_data.sh";

    protected String outputDirectory = "";
    protected EnvironmentCustomizeParameters envConfig;
    protected Map<String, String> mapSourceToDestFiles = new HashMap<>();
    protected Map<String, String> mapReplaceContent = new HashMap<>();

    public EnvironmentCustomize(EnvironmentCustomizeParameters config) throws IOException {
        this.envConfig = config;

        //map schema files source -> destination
        mapSourceToDestFiles.put("cam_ddl_cql.sql", CAM_DDL_FILE);
        mapSourceToDestFiles.put("cam_sai_ddl_cql.sql", CAM_SAI_FILE);
        mapSourceToDestFiles.put("cam_search_ddl_cql.sql", CAM_SEARCH_FILE);
        mapSourceToDestFiles.put("cam_search_solr_ddl_cql.sql", CAM_SEARCH_SOLR_FILE);
        mapSourceToDestFiles.put("test_only_schema_cql.sql", TEST_SCHEMA_FILE);
        mapSourceToDestFiles.put("test_seq_num_cql.sql", TEST_SEQ_NUM_FILE);

        //map script files source -> destination
        mapSourceToDestFiles.put("create_customer_schema_template.sh", SCHEMA_CREATE_FILE);
        mapSourceToDestFiles.put("load_customer_data_template.sh", DATA_LOAD_FILE);
    }

    protected void assignScriptReplacementValues(){
        mapReplaceContent.put("CQLSH_PATH", envConfig.cqlshPath);
        mapReplaceContent.put("DSBULK_PATH", envConfig.bulkLoadPath);
        mapReplaceContent.put("SCHEMA_HOST", envConfig.schemaCreateHost);
        mapReplaceContent.put("SCHEMA_PORT", envConfig.schemaCreatePort);
        mapReplaceContent.put("SEARCH_HOST", envConfig.searchIndexCreateHost);
        mapReplaceContent.put("SEARCH_PORT", envConfig.searchIndexCreatePort);

        mapReplaceContent.put("DDL_FILE", CAM_DDL_FILE);
        mapReplaceContent.put("SAI_DDL_FILE", CAM_SAI_FILE);
        mapReplaceContent.put("SEARCH_DDL_FILE", CAM_SEARCH_FILE);
        mapReplaceContent.put("TEST_SCHEMA", TEST_SCHEMA_FILE);
        mapReplaceContent.put("SEQ_NUM_SCHEMA", TEST_SEQ_NUM_FILE);

        mapReplaceContent.put(ENV_ID_PLACEHOLDER, envConfig.environmentID);

        mapReplaceContent.put("SCHEMA_FOLDER", outputDirectory);
        mapReplaceContent.put("DATA_FILES_PATH", envConfig.dataFilesPath);
    }

    public void generateCustomizedEnvironment() throws IOException {
        String camSchemaDirPrfix = "camSchema_";

        Path outputPath = Files.createTempDirectory(camSchemaDirPrfix);
        outputDirectory = outputPath.toString();
        System.out.println("Temporary folder created - " + outputDirectory);


        //assign translation values and create common substitutor to be used for all files
        assignScriptReplacementValues();
        StrSubstitutor substitutor = new  StrSubstitutor(mapReplaceContent, "<", ">");

        mapSourceToDestFiles.forEach((sourceFile, destinationFile) -> {
            String sourceFullPath = envConfig.sourceFilesPath + "/" + sourceFile;
            String destinationFullPath = outputPath.toAbsolutePath() + "/" + destinationFile ;
            try {
                translateFile(sourceFullPath, destinationFullPath, substitutor);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } );

        setFileAsExecutable(outputDirectory + "/" + SCHEMA_CREATE_FILE);
        setFileAsExecutable(outputDirectory + "/" + DATA_LOAD_FILE);

        setCleanupOnExit();
    }

    protected void setFileAsExecutable(String fileLocation){
        try {
            Path executableFile = Paths.get(fileLocation);
            executableFile.toFile().setExecutable(true);
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    protected void setCleanupOnExit(){

        Path tempDirPath = Paths.get(outputDirectory);

        try (DirectoryStream<Path> tempDirStream = Files.newDirectoryStream(tempDirPath)){
            tempDirPath.toFile().deleteOnExit();

            for(Path tempFilePath : tempDirStream){
                tempFilePath.toFile().deleteOnExit();
            }

        } catch(IOException e){
            System.out.println(e.getMessage());
        }
    }

    protected void translateFile(String sourceFile, String destinationFile, StrSubstitutor substitutor) throws IOException {
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(destinationFile));
            Stream<String> readStream = Files.lines(Paths.get(sourceFile));

            readStream.forEach(line -> {
                try {
                    String outLine = substitutor.replace(line);
                    writer.write(outLine + "\n");
                } catch (IOException e) {
                    System.out.println("Error writing to temporary script file " + destinationFile);
                    System.out.println(e.getMessage());
                    throw new RuntimeException(e);
                }
                catch(Exception e){
                    System.out.println("General exception writing to temporary script file " + destinationFile);
                    System.out.println(e.getMessage());
                    throw new RuntimeException(e);
                }
            });

            writer.close();

        } catch (IOException e) {
            System.out.println("Error processing script file translation, source- " +  sourceFile + "  destination- " + destinationFile);
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        catch(Exception e){
            System.out.println("General exception processing script file translation, source- " +  sourceFile + "  destination- " + destinationFile);
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public String getSchemaCreationFilePath(){
        return outputDirectory + "/" + SCHEMA_CREATE_FILE;
    }
    public String getLoadDataFilePath(){
        return outputDirectory + "/" + DATA_LOAD_FILE;
    }
}
