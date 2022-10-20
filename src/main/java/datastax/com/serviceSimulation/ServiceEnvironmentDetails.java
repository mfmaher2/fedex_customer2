package datastax.com.serviceSimulation;

import com.datastax.oss.driver.api.core.CqlSession;
import datastax.com.CustomerMapper;
import datastax.com.CustomerMapperEdge;
import datastax.com.DAOs.AccountContactDao;
import datastax.com.DAOs.AccountDao;
import datastax.com.DAOs.AuditHistoryDao;
import datastax.com.DAOs.ServiceProcessCacheDao;
import datastax.com.schemaElements.DataCenter;
import datastax.com.schemaElements.Environment;
import datastax.com.schemaElements.KeyspaceConfig;

import java.util.Map;

import static datastax.com.schemaElements.Keyspace.*;
import static datastax.com.schemaElements.Keyspace.SYSTEM_OPERATIONS_KS;

public class ServiceEnvironmentDetails {

    public CustomerMapper customerMapper = null;
    public CustomerMapperEdge customerMapperEdge = null;
    public AccountDao daoAccount = null;
    public AccountContactDao daoAccountContact = null;
    public AuditHistoryDao daoAuditHistory = null;
    public ServiceProcessCacheDao daoServiceProcess = null;

    public Environment environment = null;
    public Map<DataCenter, CqlSession> sessionMap = null;
    public KeyspaceConfig ksConfig = null;

    public ServiceEnvironmentDetails(Environment env, Map<DataCenter, CqlSession> sessions, KeyspaceConfig keyspaces, CustomerMapper coreMapper, CustomerMapperEdge edgeMapper){
        //Assign member variables for later use
        environment = env;
        sessionMap = sessions;
        ksConfig = keyspaces;
        customerMapper = coreMapper;
        customerMapperEdge = edgeMapper;

        System.out.println("\tBeginning DAO creation");
        daoAccount = customerMapper.accountDao(ksConfig.getKeyspaceName(ACCOUNT_KS));
        daoAccountContact = customerMapperEdge.accountContactDao(ksConfig.getKeyspaceName(ACCOUNT_CONTACT_KS));
        daoAuditHistory = customerMapper.auditHistoryDao(ksConfig.getKeyspaceName(AUDIT_HISTORY_KS));
        daoServiceProcess = customerMapper.serviceProcessCacheDao(ksConfig.getKeyspaceName(SYSTEM_OPERATIONS_KS));
        System.out.println("\tDAO creation complete");

    }
}
