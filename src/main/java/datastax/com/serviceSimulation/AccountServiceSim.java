package datastax.com.serviceSimulation;

import com.datastax.oss.driver.api.core.cql.BatchStatement;
import com.datastax.oss.driver.api.core.cql.BatchStatementBuilder;
import com.datastax.oss.driver.api.core.cql.BatchType;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import datastax.com.dataObjects.Account;
import datastax.com.dataObjects.ServiceProcessCache;
import datastax.com.schemaElements.DataCenter;
import org.apache.commons.lang3.SerializationUtils;

import java.nio.ByteBuffer;
import java.util.UUID;

public class AccountServiceSim {

    static private final String TABLE_NAME = "cust_acct_v1";
    static private final String SERVICE_NAME = "AccountService";
    static private final String KEY_DELIMITER = "|";
    public static boolean updateAccountEnterpriseSource(UUID transactionID, String accountNum, String opco, String newEntSource, ServiceEnvironmentDetails envDetails){
        boolean returnCode = true;

        //Prepare record update
        Account updatedAccount = new Account();
        updatedAccount.setAccountNumber(accountNum);
        updatedAccount.setOpco(opco);
        updatedAccount.setProfileEnterpriseSource(newEntSource);

        //Retrieve current state of record if it exists
        System.out.println("\t\tupdateAccountEnterpriseSource() - retrieving record for accountNum=" + accountNum + ", opco="+opco + ", transactionID="+transactionID);
        Account existingRecord = envDetails.daoAccount.findByAccountNumberOpco(accountNum, opco);

        ByteBuffer bytesExistingRecord = null;
        if(null!=existingRecord)
        {
            byte[] existingRecordObj = SerializationUtils.serialize(existingRecord);
            bytesExistingRecord = ByteBuffer.wrap(existingRecordObj, 0, existingRecordObj.length);
        }

        //Prepare process cache entry
        ServiceProcessCache cacheAcct = new ServiceProcessCache();
        cacheAcct.setTransactionID(transactionID);
        cacheAcct.setTableName(TABLE_NAME);
        cacheAcct.setServiceName(SERVICE_NAME);
        cacheAcct.setTableKeyValues(
                updatedAccount.getAccountNumber() + KEY_DELIMITER +
                updatedAccount.getOpco()
        );
        cacheAcct.setPreviousEntry(bytesExistingRecord);

        //write Account Contact and Cache records
        BatchStatement batch = new BatchStatementBuilder(BatchType.LOGGED)
                .addStatement(envDetails.daoAccount.batchUpdate(updatedAccount))
                .addStatement(envDetails.daoServiceProcess.batchSave(cacheAcct))
                .build();
        ResultSet resultSet = envDetails.sessionMap.get(DataCenter.CORE).execute(batch);
        //check for success
        if(resultSet.wasApplied()){
            //call Audit services

        }
        else{
            //failed to apply batch with account contact update and cache entry
            //return error indicator
            returnCode = false;
        }


        return returnCode;
    }
}
