package datastax.com.schemaElements;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Environment {

    public enum AvailableEnviroments {L1, L4}

    private Map<DataCenter, CqlSession> sessionMap = new HashMap<>();
    private KeyspaceConfig ksConfig = null;
    private EnvironmentCustomize customizedEnvironment = null;

    public Environment(AvailableEnviroments envSelected) throws IOException {
        switch(envSelected){
            case L1:
                setupL1Environment();
                break;
            case L4:
                setupL4Environment();
                break;
            default:
                throw new RuntimeException("Unknown environment options provided");
        }
    }

    public Map<DataCenter, CqlSession> getSessionMap(){
        return sessionMap;
    }

    public KeyspaceConfig getKeyspaceConfig(){
        return ksConfig;
    }

    public EnvironmentCustomize getCustomizedEnvironment(){
        return customizedEnvironment;
    }

    public void setupL1Environment() throws IOException {
        //Setup for specific test environment - only one (L1 or L4) should be uncommented
        //**********
        //** Begin L1 environment config
        EnvironmentCustomizeParameters environmentParams = new EnvironmentCustomizeParameters();

        //common values
        environmentParams.sourceFilesPath = Paths.get("src/main/resources/genericSchema/").toAbsolutePath().toString();
        environmentParams.dataFilesPath = Paths.get("src/test/testData").toAbsolutePath().toString();
        environmentParams.cqlshPath = "/Users/michaeldownie/dse/dse-5.1.14/bin/cqlsh";
        environmentParams.bulkLoadPath = "/Users/michaeldownie/DSE/dsbulk-1.8.0/bin/dsbulk";

        //** End L1 environment config
        //**********
        ksConfig = new KeyspaceConfigSingleDC("SearchGraphAnalytics");

        environmentParams.environmentID = "l1";
        environmentParams.schemaCreateHost = "127.0.0.1";
        environmentParams.schemaCreatePort = "9042";
        environmentParams.searchIndexCreateHost = "127.0.0.1";
        environmentParams.searchIndexCreatePort = "9042";

        //create environment details based on parameters
        generateEnvironment(environmentParams);

        String sessionConf = "src/test/resources/L1/application.conf";
        String confFilePath = Paths.get(sessionConf).toAbsolutePath().toString();
        CqlSession commonSession = CqlSession.builder()
                .withConfigLoader(DriverConfigLoader.fromFile(new File(confFilePath)))
                .build();

        //in L1 only a single DC, assign each entry in session map to same 'common'
        //session value -- all operations will execute using same session
        sessionMap.put(DataCenter.CORE, commonSession);
        sessionMap.put(DataCenter.EDGE, commonSession);
        sessionMap.put(DataCenter.SEARCH, commonSession);
    }

    public void setupL4Environment() throws IOException {
        EnvironmentCustomizeParameters environmentParams = new EnvironmentCustomizeParameters();

        //** Begin L4 environment config
        ksConfig = new KeyspaceConfigMultiDC("core", "edge", "search");

        environmentParams.environmentID = "l4";
        environmentParams.schemaCreateHost = "127.0.0.1";
        environmentParams.schemaCreatePort = "9042";
        environmentParams.searchIndexCreateHost = "127.0.0.1";
        environmentParams.searchIndexCreatePort = "9044";

        //create environment details based on parameters
        generateEnvironment(environmentParams);

        //L4 - core DC session
        String coreSessionConf = "src/test/resources/L4/core-application.conf";
        String coreConfFilePath = Paths.get(coreSessionConf).toAbsolutePath().toString();
        CqlSession coreSession = CqlSession.builder()
                .withConfigLoader(DriverConfigLoader.fromFile(new File(coreConfFilePath)))
                .build();
        sessionMap.put(DataCenter.CORE, coreSession);

        //L4 - edge DC session
        String edgeSessionConf = "src/test/resources/L4/edge-application.conf";
        String edgeConfFilePath = Paths.get(edgeSessionConf).toAbsolutePath().toString();
        CqlSession edgeSession = CqlSession.builder()
                .withConfigLoader(DriverConfigLoader.fromFile(new File(edgeConfFilePath)))
                .build();
        sessionMap.put(DataCenter.EDGE, edgeSession);

        //L4 - search DC session
        String searchSessionConf = "src/test/resources/L4/search-application.conf";
        String searchConfFilePath = Paths.get(searchSessionConf).toAbsolutePath().toString();
        CqlSession searchSession = CqlSession.builder()
                .withConfigLoader(DriverConfigLoader.fromFile(new File(searchConfFilePath)))
                .build();
        sessionMap.put(DataCenter.SEARCH, searchSession);
    }

    private void generateEnvironment(EnvironmentCustomizeParameters envParams) throws IOException {
        customizedEnvironment = new EnvironmentCustomize(envParams);
        customizedEnvironment.generateCustomizedEnvironment();
    }
}
