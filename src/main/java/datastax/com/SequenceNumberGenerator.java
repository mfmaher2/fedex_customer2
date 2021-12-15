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

        System.out.println("Running retry count=" + count);
        if(count >= RETRY_LIMIT){
            return false;
        }

        if((currentNum + blockSize) < endNumber){
            try{
                ResultSet results = session.execute(lwdUpdateCurNumberStmt.bind((currentNum + blockSize), domain, sequenceName, currentNum));
                Row resultDetails = results.one();
                if(resultDetails.getBoolean(0)){  //true if applied, otherwise false
                    System.out.println("Done");
                    for(int x = currentNum; x<(currentNum + blockSize); x++){
                        System.out.println("Current Number=" + x + " :: Block Size=" + blockSize + " host: " + hostName);
                    }
                    return true;
                }
                else{
                    int returnedNumVal = resultDetails.getInt(1);
                    System.out.println("Failed LWT Current Number=" + currentNum + ", Returned Number= " + returnedNumVal);
                    return(lwtCurrentNumberUpdate((count +1), returnedNumVal, domain, sequenceName));
                }
            }
            catch(Exception e){
                System.out.println(e.getMessage());
                return false;
            }
        }

        //if method reaches this point then an error has occurred
        return false;
    }

    public Boolean getSequenceNumbers(int blockSize, int repeatCount){

        //variable initialization
        startNumber = 0;
        endNumber = 500;
        currentSeq = 0;

        return lwtCurrentNumberUpdate(0, 1, "customer", "CAM_TEST_1");
    }


}
