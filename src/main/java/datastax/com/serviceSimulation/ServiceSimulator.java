package datastax.com.serviceSimulation;

import com.datastax.oss.driver.api.core.CqlSession;
import com.github.javafaker.Faker;
import datastax.com.CustomerMapper;
import datastax.com.CustomerMapperEdge;
import datastax.com.DAOs.AccountContactDao;
import datastax.com.DAOs.AccountDao;
import datastax.com.DAOs.AuditHistoryDao;
import datastax.com.dataObjects.AccountContact;
import datastax.com.schemaElements.DataCenter;
import datastax.com.schemaElements.Environment;
import datastax.com.schemaElements.KeyspaceConfig;

import java.io.IOException;
import java.util.Map;

import static datastax.com.schemaElements.Keyspace.*;

public class ServiceSimulator {

    private static CustomerMapper customerMapper = null;
    private static CustomerMapperEdge customerMapperEdge = null;
    private AccountDao daoAccount = null;
    private AccountContactDao daoAccountContact = null;
    private AuditHistoryDao daoAuditHistory = null;

    private static Environment environment = null;
    private static Map<DataCenter, CqlSession> sessionMap = null;
    private static KeyspaceConfig ksConfig = null;


    Faker faker = new Faker();

    ServiceSimulator() throws IOException {

//        //Create environment and set local convenience variables based on environment
//        environment = new Environment(Environment.AvailableEnviroments.L1);
//        sessionMap = environment.getSessionMap();
//        ksConfig = environment.getKeyspaceConfig();
//
//
//        System.out.println("\tBeginning Mapper and DAO creation");
//        customerMapper = new CustomerMapperBuilder(sessionMap.get(DataCenter.CORE)).build();
//        customerMapperEdge = new CustomerMapperEdgeBuilder(sessionMap.get(DataCenter.EDGE)).build();
//
//        daoAccount = customerMapper.accountDao(ksConfig.getKeyspaceName(ACCOUNT_KS));
//        daoAccountContact = customerMapperEdge.accountContactDao(ksConfig.getKeyspaceName(ACCOUNT_CONTACT_KS));
//        daoAuditHistory = customerMapper.auditHistoryDao(ksConfig.getKeyspaceName(AUDIT_HISTORY_KS));
//        System.out.println("\tMapper and DAO creation complete");

    }

    private void callAccountContactService()
    {
        //generate contact data

//        AccountContact contact = generateAccountContactRecord();
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
