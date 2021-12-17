package datastax.com;

import com.datastax.oss.driver.api.core.ConsistencyLevel;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;

import java.time.Duration;

public class SequenceNumberGenerator {

    private CqlSession session;
    private String hostName;
    private final int RETRY_LIMIT = 3;

    private String keyspace;
    private String tableName;
    private PreparedStatement getCurNumberStmt;
    private PreparedStatement lwdUpdateCurNumberStmt;

    SequenceNumberGenerator(CqlSession session, String keyspace, String testTable, String hostName) {
        this.session = session;
        this.keyspace = keyspace;
        this.tableName = testTable;
        this.hostName = hostName;
        initializeStatements();
    };

    private void initializeStatements(){

        String getCurNumberCQL = "select currentNbr, startnbr, endnbr from " + keyspace + "." +tableName + " where domain=? and sequencename=?";
        String lwtUpdateCurNumberCQL = "update " + keyspace + "." + tableName + " set currentnbr = ? where domain = ? and sequencename = ? if currentnbr = ?";

        getCurNumberStmt = session.prepare(getCurNumberCQL);
        lwdUpdateCurNumberStmt = session.prepare(lwtUpdateCurNumberCQL);
    }

    private ResultSet executeLwtStatement(BoundStatement statement){
        return session.execute(statement
                                .setSerialConsistencyLevel(ConsistencyLevel.LOCAL_SERIAL)
                                .setTimeout(Duration.ofSeconds(15L))
                                );
    }

    private Boolean lwtCurrentNumberUpdate(int retryCount, int currentNum, int blockSize, int endNumber, String domain, String sequenceName){

        if(retryCount > 0) {
            //output retry count if error previously encountered
            System.out.println("Running with retry count=" + retryCount);
        }
        if(retryCount >= RETRY_LIMIT){
            return false;
        }

        if((currentNum + blockSize) < endNumber) {
            try {
                ResultSet results = session.execute(lwdUpdateCurNumberStmt.bind((currentNum + blockSize), domain, sequenceName, currentNum));
                Row resultDetails = results.one();
                if (resultDetails.getBoolean(0)) {  //true if applied, otherwise false
                    System.out.println("Done");
                    for (int x = currentNum; x < (currentNum + blockSize); x++) {
                        System.out.println("\tCurrent Number=" + x + " :: Block Size=" + blockSize + " host: " + hostName);
                    }
                    return true;
                } else {
                    int returnedNumVal = resultDetails.getInt(1);
                    System.out.println("Failed LWT Current Number=" + currentNum + ", Returned Number= " + returnedNumVal);
                    return (lwtCurrentNumberUpdate((retryCount + 1), returnedNumVal, blockSize, endNumber, domain, sequenceName));
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return false;
            }
        }
        else{
            System.out.println("** ERROR ** Requested sequence greater than allowed end number");
            System.out.println("\tCurrent Number=" + currentNum);
            System.out.println("\tBlock Size=" + blockSize);
            System.out.println("\tEndNumber=" + endNumber);

            return false;
        }
    }

    public Boolean getSequenceNumbers(int blockSize, int repeatCount, String domain, String sequenceName){
        //variable initialization
        int startNumber = 0;  //todo - cleanup what is member variable vs passed parameter
        int endNumber = 0;
        int currentSeqNum = 0;

        Boolean noFailures = true;

        for(int i=1; i<repeatCount; i++){
            ResultSet results = session.execute(getCurNumberStmt.bind(domain, sequenceName));
            Row resultDetails = results.one();
            currentSeqNum = resultDetails.getInt(0);
            startNumber = resultDetails.getInt(1);
            endNumber = resultDetails.getInt(2);

            noFailures = noFailures & lwtCurrentNumberUpdate(0, currentSeqNum, blockSize, endNumber , domain, sequenceName);
            //todo sleep for a random time
        }

        return noFailures;
    }
}
