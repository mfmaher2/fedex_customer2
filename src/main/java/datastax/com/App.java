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

        String keyspaceName = "customer";
        String seqNumTableName = "sequence_num_tbl";
        String domainName  = "customer";
        String sequence_name = "CAM_TEST_1";

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
                        "        sequence_name = '" + sequence_name + "';";
        session.execute(init);


        SequenceNumberGenerator generator = new SequenceNumberGenerator(session, keyspaceName, seqNumTableName, "localHost");
        Boolean results =  generator.getSequenceNumbers(3, 4, domainName, sequence_name);

        if (session != null && !session.isClosed()) {
            session.close();
        }
    }
}
