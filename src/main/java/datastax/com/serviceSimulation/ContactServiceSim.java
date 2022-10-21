package datastax.com.serviceSimulation;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BatchStatement;
import com.datastax.oss.driver.api.core.cql.BatchStatementBuilder;
import com.datastax.oss.driver.api.core.cql.BatchType;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import datastax.com.DAOs.AccountContactDao;
import datastax.com.DAOs.ServiceProcessCacheDao;
import datastax.com.dataObjects.AccountContact;
import datastax.com.dataObjects.ServiceProcessCache;
import datastax.com.schemaElements.DataCenter;
import org.apache.commons.lang3.SerializationUtils;

import java.nio.ByteBuffer;

public class ContactServiceSim {

    static private final String TABLE_NAME = "account_contact";
    static private final String SERVICE_NAME = "AccountContactService";
    static private final String KEY_DELIMITER = "|";

    public static boolean updateContactRecord(String transactionID, AccountContact updatedContact, ServiceEnvironmentDetails envDetails){

        boolean returnCode = true;

        //Retrieve current state of the record, if it exists
        AccountContact existingRecord = envDetails.daoAccountContact.findByKeys(
                updatedContact.getAccountNumber(),
                updatedContact.getOpco(),
                updatedContact.getContactTypeCode(),
                updatedContact.getContactBusinessID()
                );

        ByteBuffer bytesExistingRecord = null;
        if(null!=existingRecord)
        {
            byte[] existingRecordObj = SerializationUtils.serialize(existingRecord);
            bytesExistingRecord = ByteBuffer.wrap(existingRecordObj, 0, existingRecordObj.length);
        }

        //Prepare process cache entry
        ServiceProcessCache cacheAcctContact = new ServiceProcessCache();
        cacheAcctContact.setTransactionID(transactionID);
        cacheAcctContact.setTableName(TABLE_NAME);
        cacheAcctContact.setServiceName(SERVICE_NAME);
        cacheAcctContact.setTableKeyValues(
                updatedContact.getAccountNumber() + KEY_DELIMITER +
                updatedContact.getOpco() + KEY_DELIMITER +
                updatedContact.getContactTypeCode() + KEY_DELIMITER +
                updatedContact.getContactBusinessID()
            );
        cacheAcctContact.setPreviousEntry(bytesExistingRecord);

        //write Account Contact and Cache records
        BatchStatement batch = new BatchStatementBuilder(BatchType.LOGGED)
                .addStatement(envDetails.daoAccountContact.batchUpdate(updatedContact))
                .addStatement(envDetails.daoServiceProcess.batchSave(cacheAcctContact))
                .build();
        ResultSet resultSet = envDetails.sessionMap.get(DataCenter.CORE).execute(batch);

        //call downstream service(s) if batch write successful
        if(resultSet.wasApplied()){
            //call Account and Audit services
            //Account update
            //Set account profile__enterprise_source to the value of contact_business_id
            //Not a 'legitimate' use case but will test the mechanisms
            returnCode = returnCode && AccountServiceSim.updateAccountEnterpriseSource(
                    transactionID,
                    updatedContact.getAccountNumber(),
                    updatedContact.getOpco(),
                    updatedContact.getContactBusinessID(),
                    envDetails
            );
        }
        else{
            //failed to apply batch with account contact update and cache entry
            //return error indicator
            returnCode = false;
        }

        return returnCode;
    }

}
