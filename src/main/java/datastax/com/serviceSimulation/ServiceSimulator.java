package datastax.com.serviceSimulation;

//import com.datastax.oss.driver.api.core.CqlSession;
//import com.github.javafaker.Faker;
//import datastax.com.CustomerMapper;
//import datastax.com.CustomerMapperEdge;
//import datastax.com.DAOs.AccountContactDao;
//import datastax.com.DAOs.AccountDao;
//import datastax.com.DAOs.AuditHistoryDao;
//import datastax.com.dataObjects.AccountContact;
//import datastax.com.schemaElements.DataCenter;
//import datastax.com.schemaElements.Environment;
//import datastax.com.schemaElements.KeyspaceConfig;
//
//import java.io.IOException;
//import java.util.Map;

import com.datastax.oss.driver.api.core.*;
import com.datastax.oss.driver.api.core.cql.*;
import com.datastax.oss.driver.api.core.data.ByteUtils;
import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.querybuilder.delete.Delete;
import com.datastax.oss.driver.api.querybuilder.insert.Insert;
import com.datastax.oss.driver.api.querybuilder.select.Select;
import com.datastax.oss.protocol.internal.util.Bytes;
import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.*;
import static datastax.com.dataObjects.AuditHistory.construcAuditEntryEntityStanzaSolrQuery;
import static datastax.com.schemaElements.Keyspace.*;
import static java.lang.System.currentTimeMillis;
import static org.apache.commons.lang3.SerializationUtils.serialize;

import com.github.javafaker.Faker;
import datastax.com.CustomerMapper;
import datastax.com.CustomerMapperEdge;
import datastax.com.dataObjects.*;
import datastax.com.DAOs.*;
import datastax.com.schemaElements.*;
import org.apache.commons.collections4.QueueUtils;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.commons.lang3.SerializationUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static datastax.com.schemaElements.Keyspace.*;

public class ServiceSimulator {

    ServiceEnvironmentDetails serviceEnvironment;

    Queue<AccountContact> circularQ =  QueueUtils.synchronizedQueue(new CircularFifoQueue<AccountContact>(1000));
    private static AtomicInteger transactionID = new AtomicInteger();
    Faker faker = new Faker();

    public ServiceSimulator(ServiceEnvironmentDetails servEnv) throws IOException {
        serviceEnvironment = servEnv;
    }

    public void simulationServiceCalls(int numConcurrent, long totalServiceCalls){

        //todo perform some validation on parameters
        exeuctAccountContactService(numConcurrent, totalServiceCalls);
    }

    private void exeuctAccountContactService(int numConcurrent, long totalServiceCalls){
        final int maxShutdowntime = 2000;  //in milliseconds
        ExecutorService executor = Executors.newFixedThreadPool(numConcurrent);
        Random random = new Random();
        float updateVsNewRation = 0.20F;

        for(int i=0; i<totalServiceCalls; i++) {

            //Randomly set a percentage of calls to update existing instead of creating new records
            int currentRandNum = random.nextInt(100);
            boolean updateExisting = (currentRandNum < (100*updateVsNewRation)) ? true : false;

            Runnable runTask = () -> {
                callAccountContactService(updateExisting);
            };
            executor.execute(runTask);
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(maxShutdowntime, TimeUnit.MILLISECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }

    private void callAccountContactService(boolean updateExisting)
    {
        int curTransactionID = transactionID.incrementAndGet();
        long startContactCall = System.currentTimeMillis();

        //generatenew account contact
        AccountContact newRec = generateAccountContactRecord();

        if(updateExisting){
            //Current execution is meant to update existing record instead of creating a new record
            AccountContact existingRec = circularQ.poll();

            //verify queue contained valid value
            if(existingRec != null){
                //assign key values from existing record -> non key values will be updated with new valuese
                newRec.setAccountNumber(existingRec.getAccountNumber());
                newRec.setOpco(existingRec.getOpco());
                newRec.setContactTypeCode(existingRec.getContactTypeCode());
                newRec.setContactBusinessID(existingRec.getContactBusinessID());
            }
        }

        //store record for potential later use
        circularQ.add(newRec);

        System.out.println("Begin processing transaction #" + curTransactionID + " at timestamp= " + startContactCall + " update mode=" + updateExisting);
        //Call contact service simulator
        ContactServiceSim.updateContactRecord(
                String.valueOf(curTransactionID),
                newRec,
                serviceEnvironment
        );
        System.out.println("Complete processing transaaction #" + curTransactionID + " duration= " + (System.currentTimeMillis()-startContactCall));

//        AccountContact contact = generateAccountContactRecord();
//        daoAccountContact.save(contact);
//
//        AccountContact contact2 = generateAccountContactRecord();
//        daoAccountContact.update(contact2);
    }

    private AccountContact generateAccountContactRecord(){
        AccountContact returnVal = new AccountContact();
        returnVal.setAccountNumber(faker.numerify("#########"));
        returnVal.setOpco("FX");  //todo randomly select from valid opcos
        returnVal.setContactTypeCode(faker.letterify("?????"));
        returnVal.setContactBusinessID(faker.bothify("??##"));
        returnVal.setPersonFirstName(faker.name().firstName());
        returnVal.setPersonLastName(faker.name().lastName());
        return returnVal;
    }
}
