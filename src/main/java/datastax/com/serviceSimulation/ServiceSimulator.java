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
import static org.apache.commons.lang3.SerializationUtils.serialize;

import com.github.javafaker.Faker;
import datastax.com.CustomerMapper;
import datastax.com.CustomerMapperEdge;
import datastax.com.dataObjects.*;
import datastax.com.DAOs.*;
import datastax.com.schemaElements.*;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static datastax.com.schemaElements.Keyspace.*;

public class ServiceSimulator {

    ServiceEnvironmentDetails serviceEnvironment;

    private static AtomicInteger transactionID = new AtomicInteger();
    Faker faker = new Faker();

    public ServiceSimulator(ServiceEnvironmentDetails servEnv) throws IOException {
        serviceEnvironment = servEnv;
        callAccountContactService();
    }

    private void callAccountContactService()
    {
        //generate contact data
        ContactServiceSim.updateContactRecord(
                String.valueOf(transactionID.incrementAndGet()),
                generateAccountContactRecord(),
                serviceEnvironment
        );

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
