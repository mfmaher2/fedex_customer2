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

import static org.apache.commons.lang3.SerializationUtils.serialize;

import com.github.javafaker.Faker;
import datastax.com.dataObjects.*;
import org.apache.commons.collections4.QueueUtils;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ServiceSimulator {

    ServiceEnvironmentDetails serviceEnvironment;

    Queue<AccountContact> circularQ =  QueueUtils.synchronizedQueue(new CircularFifoQueue<AccountContact>(1000));
    private static AtomicInteger transactionRefNum = new AtomicInteger();
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
        float updateVsNewRatio = 0.20F;

        for(int i=0; i<totalServiceCalls; i++) {

            //Get random number to determine if current call will update existing instead of creating new records
            int currentRandNum = random.nextInt(100);
            boolean updateExisting = (currentRandNum < (100*updateVsNewRatio)) ? true : false;

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
        int curTransactionRefNum = transactionRefNum.incrementAndGet();
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
        else{
            //store record for potential later use
            circularQ.add(newRec);
        }


        UUID transactionID = UUID.randomUUID();
        System.out.println("Begin processing transaction #" + curTransactionRefNum + " transaction ID=" + transactionID + startContactCall + " update mode=" + updateExisting);
        //Call contact service simulator
        ContactServiceSim.updateContactRecord(
                transactionID,
                newRec,
                serviceEnvironment
        );
        System.out.println("Complete processing transaaction #" + curTransactionRefNum + " duration= " + (System.currentTimeMillis()-startContactCall));

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
