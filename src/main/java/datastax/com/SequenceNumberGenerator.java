package datastax.com;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;

public class SequenceNumberGenerator {

    private CqlSession session;
    private int blockSize = 1;
    private int repeatCount;
    private String hostName;
    private final int RETRY_LIMIT = 3;

    private int startNumber;
    private int endNumber;
    private int currentSeq;
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

//    SequenceNumberGenerator(CqlSession session, int blockSize, int repeatCount) {
//        this.session = session;
//        this.blockSize = blockSize;
//        this.repeatCount = repeatCount;
//        initializeStatements();
//    };

    private void initializeStatements(){

        String getCurNumberCQL = "select currentNbr, startnbr, endnbr from " + keyspace + "." +tableName + " where domain=? and sequencename=?";
        String lwtUpdateCurNumberCQL = "update " + keyspace + "." + tableName + " set currentnbr = ? where domain = ? and sequencename = ? if currentnbr = ?";

        getCurNumberStmt = session.prepare(getCurNumberCQL);
        lwdUpdateCurNumberStmt = session.prepare(lwtUpdateCurNumberCQL);
    }

    private Boolean lwtCurrentNumberUpdate(int count, int currentNum, String domain, String sequenceName){

        if(count > 0) {
            //output retry count if error previously encountered
            System.out.println("Running retry count=" + count);
        }
        if(count >= RETRY_LIMIT){
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
                    return (lwtCurrentNumberUpdate((count + 1), returnedNumVal, domain, sequenceName));
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

        //if method reaches this point then an error has occurred
//        return false;
    }

    public Boolean getSequenceNumbers(int blockSize, int repeatCount){
        //variable initialization
        startNumber = 0;  //todo - cleanup what is member variable vs passed parameter
        endNumber = 500;
        currentSeq = 0;
        this.blockSize = blockSize;
        String domain  = "customer";
        String sequenceName = "CAM_TEST_1";

        Boolean noFailures = true;

        for(int i=1; i<repeatCount; i++){
            ResultSet results = session.execute(getCurNumberStmt.bind(domain, sequenceName));
            Row resultDetails = results.one();
            currentSeq = resultDetails.getInt(0);
            startNumber = resultDetails.getInt(1);
            endNumber = resultDetails.getInt(2);

            noFailures = noFailures & lwtCurrentNumberUpdate(0, currentSeq, domain, sequenceName);
            //todo sleep for a random time
        }

        return noFailures;
    }
}
