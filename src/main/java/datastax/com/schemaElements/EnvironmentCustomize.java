package datastax.com.schemaElements;

import org.apache.commons.lang.text.StrSubstitutor;

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

    //output schema files, output files with customized values
    protected final String CAM_DDL_FILE = "cam_ddl.cql";
    protected final String CAM_SAI_FILE = "cam_sai_ddl.cql";
    protected final String CAM_SEARCH_FILE = "cam_search_ddl.cql";
    protected final String TEST_SCHEMA_FILE = "test_only_schema.cql";
    protected final String TEST_SEQ_NUM_FILE = "test_seq_num.cql";


    //script file related values
    protected final String SCHEMA_CREATE_FILE = "create_customer_schema.sh";
    protected final String DATA_LOAD_FILE = "load_customer_data.sh";

    protected String outputDirectory = "";
    protected EnvironmentCustomizeParameters envConfig;
    protected Map<String, String> mapSchemaSourceToDestFiles = new HashMap<>();
    protected Map<String, String> mapScriptSourceToDestFiles = new HashMap<>();
    protected Map<String, String> mapScriptReplaceContent = new HashMap<>();

    public EnvironmentCustomize(EnvironmentCustomizeParameters config) throws IOException {
        this.envConfig = config;

        //map schema files source -> destination
        mapSchemaSourceToDestFiles.put("cam_ddl_cql.sql", CAM_DDL_FILE);
        mapSchemaSourceToDestFiles.put("cam_sai_ddl_cql.sql", CAM_SAI_FILE);
        mapSchemaSourceToDestFiles.put("cam_search_ddl_cql.sql", CAM_SEARCH_FILE);
        mapSchemaSourceToDestFiles.put("test_only_schema_cql.sql", TEST_SCHEMA_FILE);
        mapSchemaSourceToDestFiles.put("test_seq_num_cql.sql", TEST_SEQ_NUM_FILE);

        //map script files source -> destination
        mapScriptSourceToDestFiles.put("create_customer_schema_template.sh", SCHEMA_CREATE_FILE);
        mapScriptSourceToDestFiles.put("load_customer_data_template.sh", DATA_LOAD_FILE);
    }

    protected void assignScriptReplacementValues(){
        mapScriptReplaceContent.put("CQLSH_PATH", envConfig.cqlshPath);
        mapScriptReplaceContent.put("SCHEMA_HOST", envConfig.schemaCreateHost);
        mapScriptReplaceContent.put("SCHEMA_PORT", envConfig.schemaCreatePort);
        mapScriptReplaceContent.put("SEARCH_HOST", envConfig.searchIndexCreateHost);
        mapScriptReplaceContent.put("SEARCH_PORT", envConfig.searchIndexCreatePort);

        mapScriptReplaceContent.put("DDL_FILE", CAM_DDL_FILE);
        mapScriptReplaceContent.put("SAI_DDL_FILE", CAM_SAI_FILE);
        mapScriptReplaceContent.put("SEARCH_DDL_FILE", CAM_SEARCH_FILE);
        mapScriptReplaceContent.put("TEST_SCHEMA", TEST_SCHEMA_FILE);
        mapScriptReplaceContent.put("SEQ_NUM_SCHEMA", TEST_SEQ_NUM_FILE);

        mapScriptReplaceContent.put("SCHEMA_FOLDER", outputDirectory);
    }

    public void generateCustomizedEnvironment() throws IOException {
        String camSchemaDirPrfix = "camSchema_";

        Path outputPath = Files.createTempDirectory(camSchemaDirPrfix);
        outputDirectory = outputPath.toString();
        System.out.println("Temporary folder created - " + outputDirectory);

        assignScriptReplacementValues();

        mapSchemaSourceToDestFiles.forEach((sourceFile, destinationFile) -> {
            String sourceFullPath = envConfig.sourcFilesPath + "/" + sourceFile;
            String destinationFullPath = outputPath.toAbsolutePath() + "/" + destinationFile ;
            try {
                translateSchemaFile(sourceFullPath, destinationFullPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } );

        mapScriptSourceToDestFiles.forEach((sourceFile, destinationFile) -> {
            String sourceFullPath = envConfig.sourcFilesPath + "/" + sourceFile;
            String destinationFullPath = outputPath.toAbsolutePath() + "/" + destinationFile ;
            try {
                translateScriptFile(sourceFullPath, destinationFullPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } );
    }

    protected void translateSchemaFile(String sourceFile, String destinationFile) throws IOException {
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(destinationFile));
            Stream<String> readStream = Files.lines(Paths.get(sourceFile));
                readStream.forEach(line -> {
                    try {
                        writer.write(line.replace(ENV_ID_PLACEHOLDER, envConfig.environmentID) + "\n");
                    } catch (IOException e) {
                        System.out.println("Error writing to temporary schema file " + destinationFile);
                        System.out.println(e.getMessage());
                        throw new RuntimeException(e);
                    }
                    catch(Exception e){
                        System.out.println("General exception writing to temporary schema file " + destinationFile);
                        System.out.println(e.getMessage());
                        throw new RuntimeException(e);
                    }
                });

            writer.close();

        } catch (IOException e) {
            System.out.println("Error processing schema file translation, source- " +  sourceFile + "  destination- " + destinationFile);
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        catch(Exception e){
            System.out.println("General exception processing schema file translation, source- " +  sourceFile + "  destination- " + destinationFile);
            System.out.println(e.getMessage());
            throw new RuntimeException(e);

        }
    }

    protected String replaceStringContent(String source, String placeHolder, String content){
        return source.replace(placeHolder, content);
    }
    protected void translateScriptFile(String sourceFile, String destinationFile) throws IOException {
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(destinationFile));
            Stream<String> readStream = Files.lines(Paths.get(sourceFile));
            StrSubstitutor substitutor = new  StrSubstitutor(mapScriptReplaceContent, "<", ">");
            System.out.println(mapScriptReplaceContent.toString());

            readStream.forEach(line -> {
                try {

//                    Str
//
//                    line = mapScriptReplaceContent.forEach((placeholder, envContent) -> {
//                        //line = line.replace(placeholder, envContent);
//                        replaceStringContent(line, placeholder, envContent);
//                    });
//                    writer.write(line.replace(ENV_ID_PLACEHOLDER, envConfig.environmentID) + "\n");
                    String outLine = substitutor.replace(line);
                    System.out.println("source line - " + line);
                    System.out.println("replaced line - " + outLine);
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
        return outputDirectory; //todo add file path
    }
}
