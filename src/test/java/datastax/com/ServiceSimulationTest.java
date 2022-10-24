package datastax.com;

import com.datastax.oss.driver.api.core.CqlSession;
import datastax.com.schemaElements.DataCenter;
import datastax.com.schemaElements.Environment;
import datastax.com.schemaElements.KeyspaceConfig;
import datastax.com.serviceSimulation.ServiceEnvironmentDetails;
import datastax.com.serviceSimulation.ServiceSimulator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

public class ServiceSimulationTest {

    private static Environment environment = null;
    private static Map<DataCenter, CqlSession> sessionMap = null;
    private static KeyspaceConfig ksConfig = null;

    private static CustomerMapper customerMapper = null;
    private static CustomerMapperEdge customerMapperEdge = null;

    @BeforeClass
    public static void init() throws IOException {
        System.out.println("\nStarting CAM Service Simulation Testing");

        //Create environment and set local convenience variables based on environment
        environment = new Environment(Environment.AvailableEnviroments.L1);
        sessionMap = environment.getSessionMap();
        ksConfig = environment.getKeyspaceConfig();

        System.out.println("\tBeginning Mapper creation");
        customerMapper = new CustomerMapperBuilder(sessionMap.get(DataCenter.CORE)).build();
        customerMapperEdge = new CustomerMapperEdgeBuilder(sessionMap.get(DataCenter.EDGE)).build();

        System.out.println("\tMapper creation complete");
    }


    @Test
    public void simulationTestNoFailures() throws IOException {
        ServiceEnvironmentDetails envDetails = new ServiceEnvironmentDetails(environment, sessionMap, ksConfig, customerMapper, customerMapperEdge);
        ServiceSimulator sim = new ServiceSimulator(envDetails);
        sim.simulationServiceCalls(10, 25);
        assert(true);
    }

    @AfterClass
    public static void close() throws InterruptedException {
        System.out.println("\nCAM Service Simulation Testing Complete");
    }
}
