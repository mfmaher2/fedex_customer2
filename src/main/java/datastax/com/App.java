package datastax.com;

import com.datastax.oss.driver.api.core.CqlSession;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws InterruptedException {
        System.out.println("Starting sequence number automated generation");

        String keyspaceName = System.getProperty("keyspace", "customera");
        String seqNumTableName = System.getProperty("sequenceTable", "sequence_num_tbla");
        String domainName  = System.getProperty("domain", "customer");
        String sequenceName = System.getProperty("sequenceName", "CAM_TEST_1");
        String hostName = System.getProperty("hostReference", "localHost");  //used only for outputing results, not used to establish connection
        int blockSizeMin = Integer.valueOf(System.getProperty("blockSizeMin", "3"));
        int blockSizeMax = Integer.valueOf(System.getProperty("blockSizeMax", "15"));
        int repeatCount = Integer.valueOf(System.getProperty("repeatCount", "8"));

        System.out.println("Using following parameters:");
        System.out.println("keyspace = " + keyspaceName);
        System.out.println("sequenceTable = " + seqNumTableName);
        System.out.println("domain = " + domainName);
        System.out.println("sequenceName = " + sequenceName);
        System.out.println("hostReference = " + hostName);
        System.out.println("blockSizeMin = " + blockSizeMin);
        System.out.println("blockSizeMax = " + blockSizeMax);
        System.out.println("repeatCount = " + repeatCount);


        CqlSession session = CqlSession.builder()
//                    .addContactPoints("127.0.0.1") //should have multiple (2+) contactpoints listed
//                    .withQueryOptions(new QueryOptions().setConsistencyLevel(ConsistencyLevel.LOCAL_QUORUM))
//                    .withLoadBalancingPolicy(
//                            new TokenAwarePolicy(
//                                    LatencyAwarePolicy.builder(
//                                            DCAwareRoundRobinPolicy.builder()
//                                                    .withLocalDc("dc1")
//                                                    .build()
//                                    )
//                            )
//                        .withExclusionThreshold(2.0)
//                        .build()
//                    )
//                    .withCompression(ProtocolOptions.Compression.LZ4) //LZ4 jar needs to be in class path
                //auth information may also be needed
                .build();



        //initialize sequence table record
        String init =
                "UPDATE " + keyspaceName +  "." + seqNumTableName + "\n" +
                        "    SET \n" +
                        "        current_num = 100,\n" +
                        "        start_num = 0,\n" +
                        "        end_num = 5000\n" +
                        "    WHERE \n" +
                        "        domain = '" + domainName + "' AND \n" +
                        "        sequence_name = '" + sequenceName + "';";
        session.execute(init);

        SequenceNumberGenerator generator = new SequenceNumberGenerator(session, keyspaceName, seqNumTableName, hostName);
        Boolean results =  generator.getSequenceNumbers(blockSizeMin, blockSizeMax, repeatCount, domainName, sequenceName);

        if (session != null && !session.isClosed()) {
            session.close();
        }
    }
}
